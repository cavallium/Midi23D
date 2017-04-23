package org.warp.midito3d;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.warp.midito3d.midi.MidiMusic;
import org.warp.midito3d.midi.Note;
import org.warp.midito3d.printers.GCodeOutput;
import org.warp.midito3d.printers.Printer;

public final class Midi23D {

	public final Printer printer;
	public final MidiMusic music;
	public final GCodeOutput output;
	public final boolean motorTest;
	
	public Midi23D(Printer printer, MidiMusic music, GCodeOutput output, boolean motorTest) {
		this.printer = printer;
		this.music = music;
		this.output = output;
		this.motorTest = motorTest;
	}
	
	public void run() throws IOException {
		
		final int motorsCount = printer.getMotorsCount();
		
		output.openAndLock();
		
		music.setOutputChannelsCount(motorTest?1:motorsCount);

		long previousTick;
		long currentTick = -1;
		
		printer.initialize(output);
		
		while(music.hasNext()) {
			music.findNext();
			previousTick = currentTick;
			currentTick = music.getCurrentTick();

			double[] frequency = new double[motorsCount];
			double[] speed = new double[motorsCount];
			double time = ((((double)(currentTick-previousTick))/music.getDivision()) * (music.getTempo()/60000000d));
			boolean didSomething = false;
			String frequenciesString = "";
			
			for (int channel = 0; channel < motorsCount; channel++) {
				Note note = music.getCurrentNote(channel);
				
				if (note != null) {
					frequency[channel] = Math.pow(2d, ((((double)note.note)*music.getChannelPitch(channel))-69d)/12d)*440d;
					speed[channel] = (frequency[channel] * ((double)note.velocity) /* * ((double)music.getChannelVolume(channel)) */) / (((double)printer.getMotor(channel).getPPI()) / music.getToneMultiplier());
					
					didSomething = speed[channel] > 0;
				}
				
				frequenciesString += String.format(Locale.US, ", %.3f", speed[channel]);
			}
			
			System.out.println(String.format("Chord: [%s] for %d deltas", frequenciesString.substring(2), (currentTick-previousTick)));
			
			if (didSomething) {
				
				printer.move(output, time, speed);
			} else {
				printer.wait(output, time);
			}
		}
		
		printer.stop(output);
		
		output.close();
		
		System.out.println("Done.");
	}
	
}
