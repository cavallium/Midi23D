package org.warp.midito3d;

import java.io.IOException;
import java.util.Locale;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.warp.midito3d.music.DoneListener;
import org.warp.midito3d.music.Music;
import org.warp.midito3d.music.Note;
import org.warp.midito3d.printers.GCodeOutput;
import org.warp.midito3d.printers.Printer;

public final class Midi23D implements DoneListener {

	public final Printer printer;
	public final Music music;
	public final GCodeOutput output;
	public final boolean motorTest;
	private int motorsCount;
	
	public Midi23D(Printer printer, Music music, GCodeOutput output, boolean motorTest) {
		this.printer = printer;
		this.music = music;
		this.output = output;
		this.motorTest = motorTest;
	}
	
	public void run() throws IOException {
		
		this.motorsCount = printer.getMotorsCount();
		
		output.openAndLock();
		
		music.setOutputChannelsCount(motorTest?1:motorsCount);
		music.reanalyze(this);
	}
	
	@Override
	public void done() {
		try {
			Note[] lastNotes = null;
			double lastTempo = 0;
			long lastTick = -1;
			double lastToneMultiplier = 0;
			double lastDivision = 0;
			printer.initialize(output);
			
			final double[][] debugFreqs = new double[printer.getMotorsCount()][(int) music.getLength()];
			double songDuration = 0;

			// Wait 2 seconds at the start
			printer.wait(output, 2);

			do {
				long currentTick = music.getCurrentTick();
				if (lastTick != -1) {
					double deltaTime = (((currentTick-lastTick) * lastDivision) * lastTempo) / music.getSpeedMultiplier();
					
					double[] frequency = new double[motorsCount];
					double[] speed = new double[motorsCount];
					boolean didSomething = false;
					String frequenciesString = "";
					
					for (int channel = 0; channel < motorsCount; channel++) {
						Note note = lastNotes[channel];
						
						if (note != null) {
							frequency[channel] = note.getFrequency() * lastToneMultiplier;
							for (int ii = (int) lastTick + 1; ii <= currentTick; ii++) {
								debugFreqs[channel][ii] = frequency[channel];
							}
							speed[channel] = frequency[channel] * 60d / (double)printer.getMotor(channel).getStepsPerMillimeter(); // mm/min
							
							if (didSomething == false) {
								didSomething = speed[channel] > 0d;
							}
						}
						
						frequenciesString += String.format(Locale.US, ", %.3fHz", frequency[channel]);
					}
					
					songDuration+=deltaTime;
					System.out.println(String.format("Chord: [%s] for %d deltas (%.2f seconds)", frequenciesString.substring(2), currentTick-lastTick, deltaTime));
					
					
					if (didSomething) {
						printer.move(output, deltaTime, speed);
					} else {
						printer.wait(output, deltaTime);
					}	
				}
				lastTick = currentTick;
				lastNotes = new Note[motorsCount];
				for (int i = 0; i < motorsCount; i++) {
					lastNotes[i] = music.getCurrentNote(i);
				}
				lastTempo = music.getCurrentTempo();
				lastToneMultiplier = music.getToneMultiplier();
				lastDivision = music.getDivision();
				music.findNext();
			} while(music.hasNext());

			// Wait 5 seconds at the end
			printer.wait(output, 5);
			
			System.out.println("Song duration: "+ songDuration + " seconds.");
			
			printer.stop(output);
			
			output.close();
			
			System.out.println("Done.");

//			debugMusic(debugFreqs, songDuration, printer.getMotorsCount());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
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
