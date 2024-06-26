package org.warp.midito3d.gui.printers;

import java.util.Arrays;
import org.warp.midito3d.printers.Printer3Axes;

public class Model3Axes implements PrinterModel {

	private final String modelName;
	private final MotorSetting[] motors;

	public Model3Axes(String modelName, MotorSetting[] defaultMotorSetting) {
		this.modelName = modelName;
		motors = Arrays.copyOf(defaultMotorSetting, 3);
	}
	
	@Override
	public int getMotorsCount() {
		return 3;
	}

	@Override
	public MotorSetting getMotor(int number) {
		return motors[number];
	}

	@Override
	public String getName() {
		return "XYZ Axes (" + modelName+ ")";
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
			case 2:
				return "Motor Z";
			default:
				return "err";
		}
	}

	@Override
	public Printer3Axes createPrinterObject(PrinterModelArea printerModelArea) {
		return new Printer3Axes(motors[0].createMotorObject(), motors[1].createMotorObject(), motors[2].createMotorObject(), printerModelArea.createAreaObject());
	}

}
