package org.warp.midito3d.music.midi;

class ProgramEvent implements MidiMusicEvent {
	public final int program;
	
	public ProgramEvent(int program) {
		this.program = program;
	}
}
