package org.warp.midito3d.music.midi;

import java.io.File;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

public class MidiParser {
	
	public static MidiMusic loadFrom(String string) throws InvalidMidiDataException, IOException {
		return loadFrom(string, false);
	}
	
	public static MidiMusic loadFrom(String string, boolean debug) throws InvalidMidiDataException, IOException {
		Sequence sequence = MidiSystem.getSequence(new File(string));
		MidiMusic m = new MidiMusic(sequence, debug);
		return m;
	}
	
}
