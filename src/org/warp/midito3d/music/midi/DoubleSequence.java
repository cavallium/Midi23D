package org.warp.midito3d.music.midi;

import java.util.HashMap;

public class DoubleSequence {
	
	private HashMap<Long, Double> frequencyChanges;
	private Long currentTick;
	private Double currentFrequency;
	
	public DoubleSequence() {
		frequencyChanges = new HashMap<>();
		resetCounter();
	}
	
	public void resetCounter() {
		currentTick = 0L;
		currentFrequency = 0d;
	}
	
	public void putEvent(long tick, double frequency) {
		frequencyChanges.put(tick, frequency);
	}
	
	public boolean hasEventAt(Long tick) {
		return frequencyChanges.containsKey(tick);
	}

	public double getEventAt(long tick) {
		return frequencyChanges.get(tick);
	}

	public double getComputedValueAt(long tick) {
		Double frequency;
		long currentTick = tick;
		do {
			frequency = frequencyChanges.getOrDefault(currentTick, null);
			currentTick--;
		} while (frequency == null && currentTick >= 0);
		if (frequency == null) {
			return 0;
		} else {
			return frequency;
		}
	}
	
	public double getNextTick() {
		if (frequencyChanges.containsKey(currentTick)) {
			currentFrequency = frequencyChanges.get(currentTick);
		}
		currentTick++;
		return currentFrequency==null?0:currentFrequency;
	}

	public boolean isEmpty() {
		return frequencyChanges.isEmpty();
	}
}
