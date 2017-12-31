package org.warp.midito3d;

import java.io.IOException;
import java.util.Locale;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.warp.midito3d.music.Music;
import org.warp.midito3d.music.Note;
import org.warp.midito3d.music.mp3.Mp3Music;
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

		long firstTick;
		long lastTick = -1;
		
		printer.initialize(output);
		
		final double[][] debugFreqs = new double[printer.getMotorsCount()][(int) music.getLength()];
		double songDuration = 0;
		
		while(music.hasNext()) {
			music.findNext();
			firstTick = lastTick;
			lastTick = music.getCurrentTick();

			double[] frequency = new double[motorsCount];
			double[] speed = new double[motorsCount];
			boolean didSomething = false;
			String frequenciesString = "";
			
			for (int channel = 0; channel < motorsCount; channel++) {
				Note note = music.getCurrentNote(channel);
				
				if (note != null) {
					frequency[channel] = note.calculateFreq(music.getChannelPitch(channel)) * music.getToneMultiplier();
					for (int ii = (int) firstTick + 1; ii <= lastTick; ii++) {
						debugFreqs[channel][ii] = frequency[channel];
					}
					speed[channel] = frequency[channel] * note.velocity / (double)printer.getMotor(channel).getPPI();
					
					if (didSomething == false) {
						didSomething = speed[channel] > 0d;
					}
				}
				
				frequenciesString += String.format(Locale.US, ", %.3fHz", frequency[channel]);
			}
			
			double deltaTime = (((lastTick-firstTick)/music.getDivision()) * music.getCurrentTempo())*60d;
			songDuration+=deltaTime;
			System.out.println(String.format("Chord: [%s] for %d deltas (%.2f seconds)", frequenciesString.substring(2), lastTick-firstTick, deltaTime));
			
			
			if (didSomething) {
				printer.move(output, deltaTime/60d, speed);
			} else {
				for (int m = 0; m < motorsCount; m++) {
					speed[m] = 13 / (double)printer.getMotor(m).getPPI();
				}
				printer.move(output, deltaTime/60d, speed);
				/*
				if (music instanceof Mp3Music) {
					printer.wait(output, deltaTime);
				} else {
					printer.wait(output, deltaTime);
				}*/
				
			}
		}
		
		printer.stop(output);
		
		output.close();
		
		System.out.println("Done.");
		
//		debugMusic(debugFreqs, songDuration, printer.getMotorsCount());
	}
	
	private static void debugMusic(final double[][] freqs, final double songDuration, final double actuatorsCount) {
		System.out.println("Debugging music. Duration=" + String.format("%.2f", songDuration) + " seconds. " + actuatorsCount + " channels.");
		for (int chan = 0; chan < 1; chan++) {
			final int chanF = chan;
			new Thread(() -> {
				try {
					createToneList();
					for (int idx = 0; idx < freqs[chanF].length; idx++) {
						writeTone((int) (freqs[chanF][idx]), (int) (songDuration*1000d/freqs[0].length), 0.5d / actuatorsCount * (actuatorsCount - chanF));
						//writeTone(265, 23, 0.5d);
					}
					startToneList();
					System.out.println("track ended.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
	}
	
	private static float SAMPLE_RATE = 8000f;
	private static void writeTone(int hz, int msecs) throws LineUnavailableException {
		writeTone(hz, msecs, 0.5d);
    }
	private static SourceDataLine sdl;
	private static void createToneList() throws LineUnavailableException {
        AudioFormat af = new AudioFormat(SAMPLE_RATE,8,1,true,false);     
        sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
	}
	private static void startToneList() throws LineUnavailableException {
        sdl.drain();
        sdl.stop();
        sdl.close();
	}
	
	private static void writeTone(int hz, int msecs, double vol) throws LineUnavailableException {
        byte[] buf = new byte[1];
        for (int i=0; i < msecs*8; i++) {
              double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
              buf[0] = (byte)(Math.sin(angle) * 127.0 * vol);
              sdl.write(buf,0,1);
        }
    }

	
}
