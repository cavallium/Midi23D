package org.warp.midito3d.midi;

public class Note {
	public final double note;
	public final double velocity;
	public final long startTick;
	
	public Note(double note, double velocity, long startTick) {
		this.note = note;
		this.velocity = velocity;
		this.startTick = startTick;
	}
}
