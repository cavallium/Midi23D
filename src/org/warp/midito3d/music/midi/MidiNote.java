package org.warp.midito3d.music.midi;

import org.warp.midito3d.music.Note;

class MidiNote extends Note {
	
	public MidiNote(double note, double velocity, long startTick) {
		super(note, velocity, startTick);
	}
	
	public double getNote() {
		return note;
	}

	@Override
	public double calculateFreq(double channelPitch) {
		return Math.pow(2d, ((note*channelPitch)-69d)/12d)*440d;
	}
}
