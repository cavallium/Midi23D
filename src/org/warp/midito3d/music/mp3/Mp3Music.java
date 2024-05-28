package org.warp.midito3d.music.mp3;

import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.sound.sampled.AudioInputStream;

import org.warp.midito3d.music.DoneListener;
import org.warp.midito3d.music.Music;
import org.warp.midito3d.music.Note;

import javazoom.jl.decoder.Bitstream;

public class Mp3Music implements Music {

	private double[][] freqs;
	
	private double tempo = 500000;
	private double speedMultiplier = 1.0d;
	private float toneMultiplier = 1.0f;

	private long currentTick = -1;
	
	private int channelsCount = 4;

	private PrintStream out;
	private PrintStream err;

	private boolean errored = false;

	private float samplesPerSecond;

	private double currentNotes[];
	
	Mp3Music(double[][] freqs, float samplesPerSecond, boolean debug) {
    	setDebugOutput(debug);
		this.freqs = freqs;
		this.channelsCount = freqs.length;
		this.samplesPerSecond = samplesPerSecond; //52f/12f;
	}

	@Override
	public void setOutputChannelsCount(int i) {
		this.err.print("Not implemented function: setOutputChannelsCount("+i+")");
	}

	@Override
	public void reanalyze(DoneListener l) {
		try {

//			if (mus.available()) {
//				currentNote = mus.read();
//				mus.getFormat().getFrameSize()
//				System.out.println(mus.getCustomTag());
//				System.exit(1);
//			}
		} catch (Exception ex) {
			ex.printStackTrace();
			errored = true;
		}
		
        currentTick = -1;
	}

	@Override
	public boolean hasNext() {
		if (errored) {
			return false;
		}
		return currentTick + 1 < freqs[0].length;
	}

	@Override
	public void findNext() {
		int delta = 1;
		boolean allEquals = false;
		do {
			currentNotes = new double[channelsCount];
			for (int i = 0; i < channelsCount; i++) {
				currentNotes[i] = this.freqs[i][(int) currentTick+delta];
			}
			delta++;
			if (currentTick+delta >= this.freqs[0].length) {
				break;
			}
			allEquals = true;
			for (int i = 0; i < this.channelsCount; i++) {
				if (currentNotes[i] != this.freqs[i][(int) currentTick+delta]) {
					allEquals = false;
					break;
				}
			}
		} while(allEquals);
		currentTick += delta;
	}

	@Override
	public long getCurrentTick() {
		return currentTick;
	}

	@Override
	public double getDivision() {
		return samplesPerSecond;
	}

	@Override
	public double getCurrentTempo() {
		return 1;//0.00037409711d;//12.69d/299d;//15d/856d;
	}

	@Override
	public Note getCurrentNote(int channel) {
		return new Mp3Note(currentNotes[channel], 69d, currentTick);
	}

	@Override
	public double getChannelPitch(int channel) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public double getSpeedMultiplier() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void setSpeedMultiplier(double f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getToneMultiplier() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void setToneMultiplier(float f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlacklistedChannels(List<Integer> blacklistedChannels) {
		this.err.print("Not implemented function: setBlacklistedChannels("+blacklistedChannels+")");
	}

	@Override
	public void setDebugOutput(boolean debug) {
    	if (debug) {
    		this.out = System.out;
    		this.err = System.err;
    	} else {
    		this.out = new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					
				}
			});
    		this.err = this.out;
    	}
	}

	@Override
	public long getLength() {
		if (this.freqs.length > 0) {
			return this.freqs[0].length;
		} else {
			return 0;
		}
	}

}
