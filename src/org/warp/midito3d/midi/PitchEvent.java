package org.warp.midito3d.midi;

public class PitchEvent implements MidiMusicEvent {
	public final double pitch;

	public PitchEvent(double pitch) {
		this.pitch = pitch;
	};
}
