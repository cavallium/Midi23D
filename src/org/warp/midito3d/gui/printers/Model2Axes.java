package org.warp.midito3d.gui.printers;

import java.util.Arrays;
import org.warp.midito3d.printers.Printer2Axes;

public class Model2Axes implements PrinterModel {

	private final String modelName;
	private final MotorSetting[] motors;

	public Model2Axes(String modelName, MotorSetting[] defaultMotorSetting) {
		this.modelName = modelName;
		motors = Arrays.copyOf(defaultMotorSetting, 2);
	}
	
	@Override
	public int getMotorsCount() {
		return 2;
	}

	@Override
	public MotorSetting getMotor(int number) {
		return motors[number];
	}

	@Override
	public String getName() {
		return "XY Axes (" + modelName + ")";
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getMotorName(int i) {
		switch (i) {
			case 0:
				return "Motor X";
			case 1:
				return "Motor Y";
			default:
				return "err";
		}
	}

	@Override
	public Printer2Axes createPrinterObject(PrinterModelArea printerModelArea) {
		return new Printer2Axes(motors[0].createMotorObject(), motors[1].createMotorObject(), printerModelArea.createAreaObject());
	}

}
