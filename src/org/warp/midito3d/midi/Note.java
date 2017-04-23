package org.warp.midito3d.midi;

public class Note {
	public final int note;
	public final int velocity;
	public final long startTick;
	
	public Note(int note, int velocity, long startTick) {
		this.note = note;
		this.velocity = velocity;
		this.startTick = startTick;
	}
}
