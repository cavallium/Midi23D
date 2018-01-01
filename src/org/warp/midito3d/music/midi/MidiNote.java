package org.warp.midito3d.music.midi;

import org.warp.midito3d.music.Note;

class MidiNote extends Note {
	
	public MidiNote(double frequency, double velocity, long startTick) {
		super(frequency, velocity, startTick);
	}
	
	@Override
	public double getFrequency() {
		return note;
	}
}
