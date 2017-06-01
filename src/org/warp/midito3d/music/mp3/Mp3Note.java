package org.warp.midito3d.music.mp3;

import org.warp.midito3d.music.Note;

public class Mp3Note extends Note {

	public Mp3Note(double note, double velocity, long startTick) {
		super(note, velocity, startTick);
	}

	@Override
	public double calculateFreq(double channelPitch) {
		return note*channelPitch;
	}

}
