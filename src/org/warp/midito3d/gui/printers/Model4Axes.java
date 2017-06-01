package org.warp.midito3d.gui.printers;

import org.warp.midito3d.printers.Printer4Axes;

public class Model4Axes implements PrinterModel {
	private MotorSetting[] motors;

	public Model4Axes() {
		motors = new MotorSetting[]{new MotorSetting(),new MotorSetting(),new MotorSetting(800),new MotorSetting()};
	}
	
	public Model4Axes(MotorSetting x, MotorSetting y, MotorSetting z, MotorSetting e) {
		motors = new MotorSetting[]{x,y,z,e};
	}
	
	@Override
	public int getMotorsCount() {
		return 4;
	}

	@Override
	public MotorSetting getMotor(int number) {
		return motors[number];
	}
	
	@Override
	public String getName() {
		return "XYZ Axes + Extruder";
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
			case 3:
				return "Extruder";
			default:
				return "err";
		}
	}

	@Override
	public Printer4Axes createPrinterObject(PrinterModelArea printerModelArea) {
		return new Printer4Axes(motors[0].createMotorObject(), motors[1].createMotorObject(), motors[2].createMotorObject(), motors[3].createMotorObject(), printerModelArea.createAreaObject());
	}
}
