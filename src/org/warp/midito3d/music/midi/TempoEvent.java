package org.warp.midito3d.music.midi;

class TempoEvent implements MidiMusicEvent {
	public final double tempo;
	
	public TempoEvent(double tempo) {
		this.tempo = tempo;
	}
}
