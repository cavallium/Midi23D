package org.warp.midito3d.gui.printers;

import org.warp.midito3d.printers.Motor;

public class MotorSetting {

	/**
	 * steps/mm, usually taken from DEFAULT_AXIS_STEPS_PER_UNIT
	 */
	public int ppi;

	/**
	 * @param ppi steps/mm, usually taken from DEFAULT_AXIS_STEPS_PER_UNIT
	 */
	public MotorSetting(int ppi) {
		this.ppi = ppi;
	}
	
	public MotorSetting() {
		this.ppi = 100;
	}

	public Motor createMotorObject() {
		return new Motor(ppi);
	}
}
