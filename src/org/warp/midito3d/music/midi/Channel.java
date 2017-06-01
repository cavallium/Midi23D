package org.warp.midito3d.music.midi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

class Channel {
	public final HashMap<Long, ArrayList<MidiMusicEvent>> events; //Tick, NoteEvent
	public HashMap<Integer, MidiNote> activeNotes; //Note, Note
	public double currentVolume;
	public double currentPitch;
	public int currentProgram;
	
	public Channel() {
		events = new HashMap<>();
		activeNotes = new HashMap<>();
		currentVolume = 1d;
		currentProgram = 0;
		currentPitch = 1;
	}

	public boolean isSilent() {
		boolean silent = true;
		search : for (Entry<Long, ArrayList<MidiMusicEvent>> eventEntry : events.entrySet()) {
			ArrayList<MidiMusicEvent> events = eventEntry.getValue();
			for (MidiMusicEvent event : events) {
				if (event instanceof NoteEvent) {
					if (((NoteEvent)event).state == true) {
						silent = false;
						break search;
					}
				}
			}
		}
		return silent;
	}
}
