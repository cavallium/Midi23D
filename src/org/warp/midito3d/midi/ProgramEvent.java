package org.warp.midito3d.midi;

public class ProgramEvent implements MidiMusicEvent {
	public final int program;
	
	public ProgramEvent(int program) {
		this.program = program;
	}
}
