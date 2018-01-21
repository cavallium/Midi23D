package org.warp.midito3d.printers;

public class Motor {
	private final int stepsPerMillimeter;
	
	public Motor(int stepsPerMillimeter) {
		this.stepsPerMillimeter = stepsPerMillimeter;
	}
	
	public int getStepsPerMillimeter() {
		return stepsPerMillimeter;
	}
}
