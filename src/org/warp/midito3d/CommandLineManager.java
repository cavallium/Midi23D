package org.warp.midito3d;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;

import org.warp.midito3d.music.Music;
import org.warp.midito3d.music.midi.MidiParser;
import org.warp.midito3d.printers.GCodeOutput;
import org.warp.midito3d.printers.Motor;
import org.warp.midito3d.printers.Printer;
import org.warp.midito3d.printers.Printer2Axes;
import org.warp.midito3d.printers.Printer3Axes;
import org.warp.midito3d.printers.Printer4Axes;
import org.warp.midito3d.printers.PrinterZAxis;

public class CommandLineManager {
	public static void execute(String[] args) {
		Scanner scanner = null;
		try {
			GCodeOutput output = new GCodeOutput(args[1]);
			boolean motorTest = (args.length >= 5)?Boolean.parseBoolean(args[5-1]):false;
			List<Integer> blacklistedChannels = new ArrayList<Integer>();
			if (args.length >= 6) {
				final String[] blacklistedChannelsStr = args[6-1].split("\\s*,\\s*");
				for (int i = 0; i < blacklistedChannelsStr.length; i++)
					blacklistedChannels.add(Integer.parseInt(blacklistedChannelsStr[i]));
			}

			float speedMultiplier = Float.parseFloat(args[2]);
			float toneMultiplier = Float.parseFloat(args[3]);
			
			scanner = new Scanner(System.in);
			System.out.println("Choose the number of axes to use:\n1:\tOnly axis Z\n3:\tX,Y,Z axes\n4:\tX,Y,Z axes and extruder (WARNING: The extruder must be over 150 degrees to move)");
			int printerAxes = scanner.nextInt();
			
			if ((printerAxes != 1) & (printerAxes != 2) & (printerAxes != 3) & (printerAxes != 4)) {
				System.err.println("Please pick one of the possible answers!");
				System.exit(1);
			}

			Music music = MidiParser.loadFrom(args[0], true);
			
			music.setBlacklistedChannels(blacklistedChannels);
			music.setSpeedMultiplier(speedMultiplier);
			music.setToneMultiplier(toneMultiplier);
			
			Printer printer = null;
			
			String[] axisName = new String[]{"x", "y", "z", "extruder"};
			if (printerAxes == 1) {
				axisName[0] = "z";
			}
			Motor[] motors = new Motor[printerAxes];
			for (int m = 0; m < printerAxes; m++) {
				System.out.println("Select the speed of the "+axisName[m]+"-axis motor:\n(Usually it's 100 for the x,y,z,extruder axis and 800 for the z axis)");
				motors[m] = new Motor(scanner.nextInt());
			}

			switch (printerAxes) {
				case 1:
					printer = new PrinterZAxis(motors[0], new PrinterArea(10, 10, 10, 50, 50, 40));
					break;
				case 2:
					printer = new Printer2Axes(motors[0], motors[1], new PrinterArea(new int[]{10, 10}, new int[]{50, 50}));
					break;
				case 3:
					printer = new Printer3Axes(motors[0], motors[1], motors[2], new PrinterArea(10, 10, 10, 50, 50, 40));
					break;
				case 4:
					printer = new Printer4Axes(motors[0], motors[1], motors[2], motors[3], new PrinterArea(new int[] {10, 10, 10, 0}, new int[] {50, 50, 40, 100000000}));
					break;
			}
			
			Midi23D Midi23D = new Midi23D(printer, music, output, motorTest);
			Midi23D.run();
		} catch (InvalidMidiDataException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		if (scanner != null) {
			scanner.close();
		}
	}
}
