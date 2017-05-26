package org.warp.midito3d.midi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.midi.Instrument;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

public class MidiMusic {
	
	private final Sequence sequence;
	private double tempo = 500000;
	private double speedMultiplier = 1.0d;
	private float toneMultiplier = 1.0f;
	private Channel[] channels;
	private boolean[] blacklistedChannel = new boolean[16];

	private long currentTick = -1;
	
	private int channelsCount = 16;

    private static final int NOTE_ON = 0x90;
	private static final int NOTE_OFF = 0x80;
	private static final int SET_TEMPO = 0x51;
	private static final int PROGRAM_CHANGE = 192;
	private static final int CONTROL_CHANGE = 176;
	private static final int PITCH_WHEEL = 224;
	private static final int TEXT = 0x01;
	private static final int END_OF_TRACK = 0x2F;
	
	private PrintStream out;

    MidiMusic(Sequence sequence){
    	this(sequence, false);
	}
    
    MidiMusic(Sequence sequence, boolean debug){
    	setDebugOutput(debug);
		this.sequence = sequence;
		reanalyze();
	}
	
	public void reanalyze() {
		channels = new Channel[channelsCount];
		
		for (int ch = 0; ch < channelsCount; ch++) {
			channels[ch] = new Channel();
		}
		
		printPrograms();
		
		for (Patch p : sequence.getPatchList()) {
			this.out.print("Found new patch! Program:");
			this.out.print(p.getProgram());
			this.out.println(" Bank:");
			this.out.println(p.getBank());
		}
		
        int trackNumber = 0;
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    final int channelNumber = sm.getChannel();
                    
                    if (!blacklistedChannel[channelNumber]) {
                    	int chnumbfixtmp = channelNumber;
                    	final int k = chnumbfixtmp-1;
                    	for (int j = 0; j < k; j++) {
                    		if (blacklistedChannel[j]) {
                    			chnumbfixtmp--;
                    		}
                    	}
                        int bufferNumber = chnumbfixtmp%channelsCount;
                        
                    	ArrayList<MidiMusicEvent> al;
                    	if (!channels[bufferNumber].events.containsKey(event.getTick())) {
                    		al = new ArrayList<>();
                        	channels[bufferNumber].events.put(event.getTick(), al);
                    	} else {
                    		al = channels[bufferNumber].events.get(event.getTick());
                    	}
                    	
                        if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF) {
                            al.add(new NoteEvent(sm.getCommand() == NOTE_ON, sm.getData1(), sm.getData2()));
                        } else if (sm.getCommand() == CONTROL_CHANGE) {
                        	int control = sm.getData1();
                        	if (control == 7 /* 7: Main Volume */) {
                                al.add(new MainVolumeEvent(((double)sm.getData2())/127d));
                            	this.out.println("[Channel "+channelNumber+" buf."+bufferNumber+"] (CC07) Volume value: "+((double)sm.getData2())/127d);
                        	} else if (control == 11 /* 11: Main Volume during track */) {
                                al.add(new MainVolumeEvent(((double)sm.getData2())/127d));
                            	this.out.println("[Channel "+channelNumber+" buf."+bufferNumber+"] (CC11) Volume value: "+((double)sm.getData2())/127d);
                        	} else if (control == 0 /* 0 Bank Select (followed by 32) */) {
                            	this.out.println("[Channel "+channelNumber+" buf."+bufferNumber+"] (CC00) Bank select (not implemented)");
                        	} else if (control == 32 /* 32 Bank Select */) {
                            	this.out.println("[Channel "+channelNumber+" buf."+bufferNumber+"] (CC32) Bank select (not implemented)");
                        	} else {
                            	this.out.println("[Channel "+channelNumber+" buf."+bufferNumber+"] Control change: "+sm.getData1());
                        	}
                        } else if (sm.getCommand() == PROGRAM_CHANGE) {
                            al.add(new ProgramEvent(sm.getData1()));
                        	this.out.println("[Channel "+channelNumber+" buf."+bufferNumber+"] Program selected: "+sm.getData1());
                        } else if (sm.getCommand() == PITCH_WHEEL) {
                        	int data1 = sm.getData2();
                        	int data2 = sm.getData1();
                        	double pitchVal = 1+(((double)(((data1 << 7) | data2)-8192))/8192d);
                            al.add(new PitchEvent(pitchVal));
                        	this.out.println("[Channel "+channelNumber+" buf."+bufferNumber+"] Pitch wheel: "+(pitchVal));
                        } else {
                            this.out.println("Unknown command:" + sm.getCommand());
                        }
                    }
                } else if (message instanceof MetaMessage) {
                	MetaMessage mm = (MetaMessage) event.getMessage();
                	if (mm.getType() == SET_TEMPO) {
                    	byte[] data = mm.getData();
                    	this.out.println("Tempo change: "+ ((data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff)));
                    	tempo = ((double)((data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff)))/speedMultiplier;
                    } else if (mm.getType() == TEXT) {
                        this.out.println("Title: " + new String(mm.getData()));
                    } else if (mm.getType() == END_OF_TRACK) {
                        this.out.println("End of track.");
                    } else {
                        this.out.println("Unknown meta message: " + mm.getType());
                    }
                } else {
                    this.out.println("Unknown other message: " + message.getClass());
                }
            }
        }
        
        int lastReusedChannel = -1;
        //Fill empty channels
        for (int ch = 0; ch < channelsCount; ch++) {
        	if (channels[ch].isSilent()) {
        		if (lastReusedChannel >= channelsCount-1) {
        			lastReusedChannel = -1;
        		}
        		for (int chn = 0; chn < channelsCount; chn++) {
        			if (chn > lastReusedChannel) {
    					lastReusedChannel = chn;
        				if (channels[chn].isSilent() == false) {
        					channels[ch].activeNotes = channels[chn].activeNotes;
        					this.out.println("Using notes of channel "+ chn + " on channel "+ ch);
        					break;
        				}
        			}
        		}
        	}
        }
        
        currentTick = -1;
	}
	
	private void printPrograms() {
		try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            Instrument[] instruments = synth.getAvailableInstruments();
            if( synth.loadAllInstruments( synth.getDefaultSoundbank() ) ) {
                this.out.println( "There are " + instruments.length + " instruments." );
            }
            for( int i = 0; i < instruments.length; i++ ) {
                this.out.print( instruments[i].getName() + " >> ");
                this.out.println( instruments[i].getPatch().getProgram() + "::" +
                                    instruments[i].getPatch().getBank() );
            }
        } catch( MidiUnavailableException mue ) {
            // ignore
        }
	}

	public int getChannelsCount() {
		return channelsCount;
	}
	
	public void findNext() {
		long delta = 0;
		int changes = 0;
		boolean stop = false;
		while(!stop && changes == 0 && sequence.getTickLength() - (currentTick+delta+1) >= 0) {
			changes = 0;
			delta++;
			for (int ch = 0; ch < channelsCount; ch++) {
				ArrayList<MidiMusicEvent> events = channels[ch].events.get(currentTick+delta);
				
				if (events == null) {
					
				} else {
					for (MidiMusicEvent e : events) {
						if (e instanceof NoteEvent) {
							NoteEvent ne = (NoteEvent) e;
							
//							boolean isDrum = false;
//							for (int drum = 0; drum < drumPrograms.length; drum++) {
//								if (channels[ch].currentProgram == drumPrograms[drum]) {
//									isDrum = true;
//									break;
//								}
//							}
							if (ne.state) {
								if (delta != 1 && !channels[ch].activeNotes.containsKey(ne.note)) {
									delta--;
									stop = true;
									break;
								}
								channels[ch].activeNotes.put(ne.note, new Note(ne.note, ne.velocity, currentTick+delta));
							} else {
								if (delta != 1 && channels[ch].activeNotes.containsKey(ne.note)) {
									delta--;
									stop = true;
									break;
								}
								channels[ch].activeNotes.remove(ne.note);
							}
						} else if (e instanceof MainVolumeEvent) {
							MainVolumeEvent mve = (MainVolumeEvent) e;
							channels[ch].currentVolume = mve.volume;
						} else if (e instanceof PitchEvent) {
							PitchEvent pe = (PitchEvent) e;
							channels[ch].currentPitch = pe.pitch;
						} else if (e instanceof ProgramEvent) {
							ProgramEvent pe = (ProgramEvent) e;
							channels[ch].currentProgram = pe.program;
						}
					}
					for (MidiMusicEvent e : events) {
						if (e instanceof NoteEvent) {
							NoteEvent ne = (NoteEvent) e;
							
//							boolean isDrum = false;
//							for (int drum = 0; drum < drumPrograms.length; drum++) {
//								if (channels[ch].currentProgram == drumPrograms[drum]) {
//									isDrum = true;
//									break;
//								}
//							}
							if (ne.state) {
								if (delta != 1 && !channels[ch].activeNotes.containsKey(ne.note)) {
									delta--;
									stop = true;
									break;
								}
								channels[ch].activeNotes.put(ne.note, new Note(ne.note, ne.velocity, currentTick+delta));
							} else {
								if (delta != 1 && channels[ch].activeNotes.containsKey(ne.note)) {
									delta--;
									stop = true;
									break;
								}
								channels[ch].activeNotes.remove(ne.note);
							}
						} else if (e instanceof MainVolumeEvent) {
							MainVolumeEvent mve = (MainVolumeEvent) e;
							channels[ch].currentVolume = mve.volume;
						} else if (e instanceof PitchEvent) {
							PitchEvent pe = (PitchEvent) e;
							channels[ch].currentPitch = pe.pitch;
						} else if (e instanceof ProgramEvent) {
							ProgramEvent pe = (ProgramEvent) e;
							channels[ch].currentProgram = pe.program;
						}
					}
				}

				
				for(Iterator<Entry<Integer, Note>> it = channels[ch].activeNotes.entrySet().iterator(); it.hasNext(); ) {
					Entry<Integer, Note> noteEntry = it.next();
					Note note = noteEntry.getValue();
					MidiProgram program = getProgram(channels[ch].currentProgram);
					if (program.duration >= 0) {
						if (note.startTick + program.duration < currentTick+delta) {
							if (delta != 1) {
								delta--;
								stop = true;
								break;
							}
							it.remove();
						}
					}
				}
			}
		}
		currentTick+=delta;
	}
	
	private static final int duplicateMode = 0;
	
	public Note getCurrentNote(int channel) {
		if (channel >= channelsCount) {
			channel = channel%channelsCount;
		}
		int notes = 0;
		Note maxNote = null;
		for (Entry<Integer, Note> noteEntry : channels[channel].activeNotes.entrySet()) {
			Note note = noteEntry.getValue();
			
			switch(duplicateMode) {
				case 0:
					if (maxNote == null || ((maxNote.velocity == note.velocity && note.startTick == maxNote.startTick) && maxNote.note < note.note) || (note.startTick == maxNote.startTick && maxNote.velocity < note.velocity) || (maxNote.startTick < note.startTick)) {
						maxNote = note;
					}
					break;
				case 1:
					if (maxNote == null) {
						maxNote = note;
					} else {
						long startTick = note.startTick;
						if (note.startTick < maxNote.startTick) {
							startTick = maxNote.startTick;
						}
						maxNote = new Note(note.note + maxNote.note, note.velocity + maxNote.velocity, startTick);
					}
					notes++;
					break;
			}
		}
			
		switch(duplicateMode) {
			case 1:
				if (notes > 1) {
					maxNote = new Note((double)maxNote.note / (double)notes, (double)maxNote.velocity / (double)notes, maxNote.startTick);
				}
				break;
		}
		
		return maxNote;
	}
	
	public void setSpeedMultiplier(float val) {
		speedMultiplier = val;
		tempo/=speedMultiplier;
	}

	public void setToneMultiplier(float val) {
		toneMultiplier = val;
	}

	public boolean hasNext() {
		return sequence.getTickLength() - (currentTick+1) >= 0;
	}

	public long getCurrentTick() {
		return currentTick;
	}

	public double getDivision() {
		return sequence.getResolution();
	}

	public double getTempo() {
		return tempo;
	}

	public double getToneMultiplier() {
		return toneMultiplier;
	}

	public double getChannelVolume(int channel) {
		if (channel >= channelsCount) {
			channel = channel%channelsCount;
		}
		return channels[channel].currentVolume;
	}

	public double getChannelPitch(int channel) {
		if (channel >= channelsCount) {
			channel = channel%channelsCount;
		}
		return channels[channel].currentPitch;
	}
	
	private static MidiProgram getProgram(int id) {
		switch(id) {
			case 0: //Piano
				return new MidiProgram(id, -1);
			case 47: //TIMPANI
				return new MidiProgram(id, 20);
			case 34: //PICKLED BASS
				return new MidiProgram(id, -1);
			default:
				return new MidiProgram(id, -1);
		}
	}
	
	public void setBlacklistedChannels(List<Integer> blacklist) {
		
		boolean[] blacklistedChannelsBool = new boolean[16];
		
		for (int i : blacklist) {
			if (i < 16) {
				blacklistedChannelsBool[i] = true;
			}
		}
		
		this.blacklistedChannel = blacklistedChannelsBool;
	}

	public void setOutputChannelsCount(int i) {
		this.channelsCount = i;
	}

	public void setDebugOutput(boolean debug) {
    	if (debug) {
    		this.out = System.out;
    	} else {
    		this.out = new PrintStream (new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					
				}
			});
    	}
	}
}
