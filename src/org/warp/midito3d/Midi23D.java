package org.warp.midito3d;

import java.io.IOException;
import java.util.Locale;

import org.warp.midito3d.music.Music;
import org.warp.midito3d.music.Note;
import org.warp.midito3d.printers.GCodeOutput;
import org.warp.midito3d.printers.Printer;

public final class Midi23D {

	public final Printer printer;
	public final Music music;
	public final GCodeOutput output;
	public final boolean motorTest;
	
	public Midi23D(Printer printer, Music music, GCodeOutput output, boolean motorTest) {
		this.printer = printer;
		this.music = music;
		this.output = output;
		this.motorTest = motorTest;
	}
	
	public void run() throws IOException {
		
		final int motorsCount = printer.getMotorsCount();
		
		output.openAndLock();
		
		music.setOutputChannelsCount(motorTest?1:motorsCount);
		music.reanalyze();

		long previousTick;
		long currentTick = -1;
		
		printer.initialize(output);
		
		while(music.hasNext()) {
			music.findNext();
			previousTick = currentTick;
			currentTick = music.getCurrentTick();

			double[] frequency = new double[motorsCount];
			double[] speed = new double[motorsCount];
			boolean didSomething = false;
			String frequenciesString = "";
			
			for (int channel = 0; channel < motorsCount; channel++) {
				Note note = music.getCurrentNote(channel);
				
				if (note != null) {
					frequency[channel] = note.calculateFreq(music.getChannelPitch(channel));
					speed[channel] = (frequency[channel] * note.velocity /* * ((double)music.getChannelVolume(channel)) */) / (((double)printer.getMotor(channel).getPPI()) / music.getToneMultiplier());
					
					didSomething = speed[channel] > 0;
				}
				
				frequenciesString += String.format(Locale.US, ", %.3fHz", frequency[channel]);
			}
			
			System.out.println(String.format("Chord: [%s] for %d deltas", frequenciesString.substring(2), (currentTick-previousTick)));
			
			if (didSomething) {
				
				printer.move(output, (((currentTick-previousTick)/music.getDivision()) * music.getTempo()), speed);
			} else {
				printer.wait(output, (((currentTick-previousTick)/music.getDivision()) / music.getTempo()));
			}
		}
		
		printer.stop(output);
		
		output.close();
		
		System.out.println("Done.");
	}
	
}
