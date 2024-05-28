package org.warp.midito3d.gui.printers;

import java.util.Arrays;
import org.warp.midito3d.printers.PrinterZAxis;

public class ModelZAxis implements PrinterModel {

	private final String modelName;
	private final MotorSetting[] motors;

	public ModelZAxis(String modelName, MotorSetting[] defaultMotorSetting) {
		this.modelName = modelName;
		motors = Arrays.copyOfRange(defaultMotorSetting, 2, 3);
	}
	
	@Override
	public int getMotorsCount() {
		return 1;
	}

	@Override
	public MotorSetting getMotor(int number) {
		if (number == 0) {
			return motors[0];
		} else {
			throw new java.lang.IndexOutOfBoundsException();
		}
	}

	@Override
	public String getName() {
		return "Z Axis (" + modelName + ")";
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

	@Override
	public PrinterZAxis createPrinterObject(PrinterModelArea printerModelArea) {
		return new PrinterZAxis(motors[0].createMotorObject(), printerModelArea.createAreaObject());
	}
}
