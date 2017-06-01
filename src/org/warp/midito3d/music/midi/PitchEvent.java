package org.warp.midito3d.music.midi;

class PitchEvent implements MidiMusicEvent {
	public final double pitch;

	public PitchEvent(double pitch) {
		this.pitch = pitch;
	};
}
