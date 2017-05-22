package org.warp.midito3d.gui.printers;

import org.warp.midito3d.printers.Motor;

public class ModelZAxis implements PrinterModel {
	private MotorSetting motor;
	
	public ModelZAxis(MotorSetting z) {
		motor = z;
	}
	
	public ModelZAxis() {
		motor = new MotorSetting(800);
	}
	
	@Override
	public int getMotorsCount() {
		return 1;
	}

	@Override
	public MotorSetting getMotor(int number) {
		if (number == 0) {
			return motor;
		} else {
			throw new java.lang.IndexOutOfBoundsException();
		}
	}

	@Override
	public String getName() {
		return "Z Axis";
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getMotorName(int i) {
		switch (i) {
			case 0:
				return "Motor Z";
			default:
				return "err";
		}
	}
}
