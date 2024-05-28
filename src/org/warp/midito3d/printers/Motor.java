package org.warp.midito3d.printers;

public class Motor {
	private final int stepsPerMillimeter;

	/**
	 * @param stepsPerMillimeter steps/mm, usually taken from DEFAULT_AXIS_STEPS_PER_UNIT
	 */
	public Motor(int stepsPerMillimeter) {
		this.stepsPerMillimeter = stepsPerMillimeter;
	}

	/**
	 * steps/mm
	 */
	public int getStepsPerMillimeter() {
		return stepsPerMillimeter;
	}
}
