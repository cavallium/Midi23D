package org.warp.midito3d.gui.printers;

import org.warp.midito3d.printers.Printer3Axes;

public class Model3Axes implements PrinterModel {
	private MotorSetting[] motors;

	public Model3Axes() {
		motors = new MotorSetting[]{new MotorSetting(),new MotorSetting(),new MotorSetting(800)};
	}
	
	public Model3Axes(MotorSetting x, MotorSetting y, MotorSetting z) {
		motors = new MotorSetting[]{x,y,z};
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
		return "XYZ Axes";
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
