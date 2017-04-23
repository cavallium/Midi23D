package org.warp.midito3d.midi;

public class NoteEvent implements MidiMusicEvent {
	public final boolean state;
	public final int note;
	public final int velocity;
	
	public NoteEvent(boolean state, int note, int velocity) {
		if (velocity == 0) {
			state = false;
		}	
		this.state = state;
		this.note = note;
		this.velocity = velocity;
	}
}
