package org.warp.midito3d.music.midi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.sound.midi.Sequence;
import org.jfugue.ChannelPressure;
import org.jfugue.Controller;
import org.jfugue.KeySignature;
import org.jfugue.Layer;
import org.jfugue.Measure;
import org.jfugue.MidiParser;
import org.jfugue.ParserListener;
import org.jfugue.ParserProgressListener;
import org.jfugue.PitchBend;
import org.jfugue.PolyphonicPressure;
import org.jfugue.Tempo;
import org.jfugue.Time;
import org.jfugue.Voice;
import org.warp.midito3d.music.DoneListener;
import org.warp.midito3d.music.Music;
import org.warp.midito3d.music.Note;

class MidiMusic implements Music, ParserListener, ParserProgressListener {
	
	private final Sequence sequence;
	private MidiParser parser;
	private List<DoubleSequence> frequencySequences;
	private DoubleSequence tempoSequence;
	private double speedMultiplier = 1.0d;
	private float toneMultiplier = 1.0f;
	private boolean[] blacklistedChannel = new boolean[16];
	private DoneListener doneEvent;
	private MidiNote[] currentNotes;
	private double currentTempo;
	private final double DEFAULT_TEMPO = 120;

	private long currentTick = -1;
	
	private int channelsCount = 16;
	
	private PrintStream out;

    MidiMusic(Sequence sequence){
    	this(sequence, false);
	}
    
    MidiMusic(Sequence sequence, boolean debug){
    	setDebugOutput(debug);
		this.parser = new org.jfugue.MidiParser();
		this.sequence = sequence;
	}
	
	@Override
	public void reanalyze(DoneListener doneEvent) {
		this.parser = new org.jfugue.MidiParser();
		parser.addParserListener(this);
		parser.addParserProgressListener(this);
		this.frequencySequences = new ArrayList<DoubleSequence>();
		this.frequencySequences.add(new DoubleSequence());
		this.tempoSequence = new DoubleSequence();
		this.ignoredTracks.add(false);
		currentTime = 0L;
		this.doneEvent = doneEvent;
		parser.parse(sequence);
	}

	public int getChannelsCount() {
		return channelsCount;
	}
	
	@Override
	public void findNext() {
		long initialTick = this.currentTick;
		MidiNote[] initialSituation = currentNotes;
		double initialTempo = this.currentTempo;
		if (currentNotes == null) {
			initialSituation = new MidiNote[channelsCount];
			for (int i = 0; i < channelsCount; i++) {
				initialSituation[0] = new MidiNote(0, 96d, initialTick);
			}
		}
		long currentTick = this.currentTick;
		MidiNote[] currentSituation = initialSituation;
		double currentTempo = initialTempo;
		boolean changed = false;
		do {
			currentTick++;
			if (currentTick < sequence.getTickLength()) {
				currentTempo = tempoSequence.getNextTick();
				if (currentTempo == 0) {
					currentTempo = DEFAULT_TEMPO;
				}
				if (initialTempo != currentTempo) changed = true;
				for (int i = 0; i < channelsCount; i++) {
					if (i < frequencySequences.size()) {
						double freq = frequencySequences.get(i).getNextTick();
						if (currentSituation[i] == null || currentSituation[i].getFrequency() != freq) {
							currentSituation[i] = new MidiNote(freq, 96d, currentTick);
							changed = true;
						}
					} else {
						currentSituation[i] = null;
					}
				}
			}
		} while(currentTick < sequence.getTickLength() && !changed);
		this.currentTick = currentTick >= sequence.getTickLength() ? sequence.getTickLength() - 1 : currentTick;
		this.currentNotes = currentSituation;
		this.currentTempo = currentTempo;
	}
	
	@Override
	public MidiNote getCurrentNote(int channel) {
		return currentNotes[channel];
	}
	
	@Override
	public void setSpeedMultiplier(double val) {
		speedMultiplier = val;
	}

	@Override
	public void setToneMultiplier(float val) {
		toneMultiplier = val;
	}

	@Override
	public boolean hasNext() {
		return currentTick+1 < sequence.getTickLength();
	}

	@Override
	public long getCurrentTick() {
		return currentTick;
	}

	@Override
	public double getDivision() {
		return sequence.getResolution()*18d;
	}

	@Override
	public double getCurrentTempo() {
		return currentTempo/DEFAULT_TEMPO;
	}

	@Override
	public double getSpeedMultiplier() {
		return speedMultiplier;
	}

	@Override
	public float getToneMultiplier() {
		return toneMultiplier;
	}

	public double getChannelVolume(int channel) {
		return 1;
	}

	@Override
	public double getChannelPitch(int channel) {
		return 1;
	}
	
	@Override
	public void setBlacklistedChannels(List<Integer> blacklist) {
		
		boolean[] blacklistedChannelsBool = new boolean[16];
		
		for (int i : blacklist) {
			if (i < 16) {
				blacklistedChannelsBool[i] = true;
			}
		}
		
		this.blacklistedChannel = blacklistedChannelsBool;
	}

	@Override
	public void setOutputChannelsCount(int i) {
		this.channelsCount = i;
	}

	@Override
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

	@Override
	public long getLength() {
		return sequence.getTickLength();
	}

	

	private long currentTime = 0L;
	private int currentChannel = 0;
	private List<Boolean> ignoredTracks = new ArrayList<>();
	
	private void compressChannels() {
		removeUnusedChannels();
		int finalChannels = channelsCount;
		int initialChannels = frequencySequences.size();
		
		for (int ch = 0; ch < frequencySequences.size(); ch++) {
			frequencySequences.get(ch).resetCounter();
		}
		
		while (frequencySequences.size() > finalChannels) {
			System.out.println((initialChannels - frequencySequences.size() + 1) + "/" + (initialChannels - finalChannels));
			DoubleSequence lastSequence = frequencySequences.remove(frequencySequences.size()-1);
			long currentTick = 0;
			int perc = 0;
			while(currentTick < sequence.getTickLength()) {
				double freq = lastSequence.getComputedValueAt(currentTick);
				double minFreq = Double.MAX_VALUE;
				int minFreqCh = 0;
				for (int ch = 0; ch < frequencySequences.size(); ch++) {
					double chFreq = frequencySequences.get(ch).getComputedValueAt(currentTick);
					if (chFreq < minFreq) {
						minFreq = chFreq;
						minFreqCh = ch;
					}
				}
				if (freq >= minFreq) {
					frequencySequences.get(minFreqCh).putEvent(currentTick, freq);
					if (!frequencySequences.get(minFreqCh).hasEventAt(currentTick+1)) {
						frequencySequences.get(minFreqCh).putEvent(currentTick+1, minFreq);
					}
				}
				if (currentTick % (sequence.getTickLength() / 10) == 0) {
					perc = (int) (currentTick * 100d / sequence.getTickLength());
					System.out.println(perc + "%");
				}
				currentTick++;
			}
		}
	}
	
	private void removeUnusedChannels() {
		Iterator<DoubleSequence> it = frequencySequences.iterator();
		while (it.hasNext()) {
			DoubleSequence sequence = it.next();
			if (sequence.isEmpty()) it.remove();
		}
	}

	@Override
	public void voiceEvent(Voice voice) {
//		System.out.println("voiceEvent: " + voice.getMusicString());
		currentChannel = voice.getVoice();
		while (frequencySequences.size() <= currentChannel) {
			frequencySequences.add(new DoubleSequence());
			ignoredTracks.add(false);
		}
		ignoredTracks.set(currentChannel, ignoredTracks.get(currentChannel) || (blacklistedChannel.length > currentChannel && blacklistedChannel[currentChannel]));
	}

	@Override
	public void tempoEvent(Tempo tempo) {
		long currentTick = this.currentTick < 0 ? 0 : this.currentTick;
		System.out.println("tempoEvent: " + "(tick " + currentTick + ")" + tempo.getTempo());
		this.tempoSequence.putEvent(currentTick, tempo.getTempo());
	}

	@Override
	public void instrumentEvent(org.jfugue.Instrument instrument) {
		ignoredTracks.set(currentChannel, ignoredTracks.get(currentChannel) || (instrument.getInstrument() >= 112 && instrument.getInstrument() < 120));
		System.out.println("instrumentEvent: " + instrument.getMusicString());
	}

	@Override
	public void layerEvent(Layer layer) {
		System.out.println("layerEvent: " + layer.getMusicString());
	}

	@Override
	public void measureEvent(Measure measure) {
		System.out.println("measureEvent: " + measure.getMusicString());
	}

	@Override
	public void timeEvent(Time time) {
		System.out.println("timeEvent: " + time.getTime());
		currentTime = time.getTime();
	}

	@Override
	public void keySignatureEvent(KeySignature keySig) {
		System.out.println("keySignatureEvent: " + keySig.getMusicString());
	}

	@Override
	public void controllerEvent(Controller controller) {
//		System.out.println("controllerEvent: " + controller.getMusicString());
	}

	@Override
	public void channelPressureEvent(ChannelPressure channelPressure) {
		System.out.println("channelPressureEvent: " + channelPressure.getMusicString());
	}

	@Override
	public void polyphonicPressureEvent(PolyphonicPressure polyphonicPressure) {
		System.out.println("polyphonicPressureEvent: " + polyphonicPressure.getMusicString());
	}

	@Override
	public void pitchBendEvent(PitchBend pitchBend) {
		System.out.println("pitchBendEvent: " + pitchBend.getMusicString());
	}

	@Override
	public void noteEvent(org.jfugue.Note note) {
		System.out.println("noteEvent: " + note.getVerifyString());
		if (!ignoredTracks.get(currentChannel)) {
//			int currentChannel = 0;
//			while (frequencySequences.get(currentChannel).getFrequencyAt(currentTime) != 0.0d) {
//				if (currentChannel+1 == frequencySequences.size()) {
//					frequencySequences.add(new FrequencySequence());
//					ignoredTracks.add(false);
//				}
//				currentChannel++;
//			}
			frequencySequences.get(currentChannel).putEvent(currentTime, midiNoteToHertz(note.getValue()));
			if (note.getDuration() > 0) {
				frequencySequences.get(currentChannel).putEvent(currentTime+note.getDuration(), 0.0d);
			}
		}
		
	}

	@Override
	public void sequentialNoteEvent(org.jfugue.Note note) {
		System.out.println("sequentialNoteEvent: " + note.getMusicString());
	}

	@Override
	public void parallelNoteEvent(org.jfugue.Note note) {
		System.out.println("parallelNoteEvent: " + note.getMusicString());
	}

	@Override
	public void progressReported(String description, long partCompleted, long whole) {
		if (partCompleted == whole) {
			System.out.println("Compressing channels from " + frequencySequences.size() + " to " + channelsCount);
			compressChannels();
			System.out.println("Compressed.");
			
		    BufferedImage debugAudioRawData = new BufferedImage((int) sequence.getTickLength(), 512, BufferedImage.TYPE_INT_RGB);
		    Graphics g = debugAudioRawData.getGraphics();
	    	g.setColor(Color.black);
	    	g.clearRect(0, 0, debugAudioRawData.getWidth(), debugAudioRawData.getHeight());
	    	g.setColor(Color.white);
	    	double min = Double.MAX_VALUE;
	    	double max = Double.MIN_VALUE;
	    	for (int j = 0; j < frequencySequences.size(); j++) {
	    		frequencySequences.get(j).resetCounter();
	    	}
		    for (int i = 0; i < sequence.getTickLength(); i++) {
		    	for (int j = 0; j < frequencySequences.size(); j++) {
		    		double val = frequencySequences.get(j).getNextTick();
			    	if (val > max) {
			    		max = val;
			    	}
			    	if (val < min) {
			    		min = val;
			    	}
		    	}
		    }
	    	for (int j = 0; j < frequencySequences.size(); j++) {
	    		frequencySequences.get(j).resetCounter();
	    	}
		    System.out.println("min: "+ min + ", max: " + max);
		    
		    final int[] colors = {0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0x00FFFF, 0xFF00FF, 0xFFFFFF};
		    for (int i = 0; i < sequence.getTickLength(); i++) {
			    for (int j = 0; j < frequencySequences.size(); j++) {
			    	g.setColor(new Color(colors[j%7]));
			    	final int y = (int) (((frequencySequences.get(j).getNextTick() - min) / (max-min)) * (double)(debugAudioRawData.getHeight() - 1));
			    	g.fillRect((int)(i), (debugAudioRawData.getHeight() - 1) - y, 1, 2);
			    }
		    }
		    g.dispose();
		    try {
				ImageIO.write(debugAudioRawData, "bmp", new File("N:\\TimedTemp\\example.bmp"));
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	for (int j = 0; j < frequencySequences.size(); j++) {
	    		frequencySequences.get(j).resetCounter();
	    	}
			if (doneEvent != null) doneEvent.done();
		}
//		System.out.println("progressReported: " + description + "," + partCompleted + "," + whole);
	}
	
	private static double midiNoteToHertz(int note) {
		return 440d * (Math.pow(2, ((((double) note)-69d)/12d)));
	}
}
