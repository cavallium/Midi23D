package org.warp.midito3d.midi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.SoundbankResource;
import javax.sound.midi.spi.MidiFileReader;

public class MidiParser {
	
	public static MidiMusic loadFrom(String string) throws InvalidMidiDataException, IOException {
		Sequence sequence = MidiSystem.getSequence(new File(string));
		
		MidiMusic m = new MidiMusic(sequence);
		
		return m;
	}
	
}
