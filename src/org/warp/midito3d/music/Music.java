package org.warp.midito3d.music;

import java.util.List;

public interface Music {

	void setOutputChannelsCount(int i);

	void reanalyze();

	boolean hasNext();

	void findNext();

	long getCurrentTick();
	
	long getLength();

	double getDivision();

	double getCurrentTempo();

	Note getCurrentNote(int channel);

	double getChannelPitch(int channel);

	double getSpeedMultiplier();
	
	void setSpeedMultiplier(double f);

	float getToneMultiplier();

	void setToneMultiplier(float f);

	void setBlacklistedChannels(List<Integer> blacklistedChannels);

	void setDebugOutput(boolean b);

}
