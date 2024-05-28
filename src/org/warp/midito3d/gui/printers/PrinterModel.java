package org.warp.midito3d.gui.printers;

import org.warp.midito3d.printers.Printer;

public interface PrinterModel {
	public int getMotorsCount();
	// take the motor configuration (steps/mm) from DEFAULT_AXIS_STEPS_PER_UNIT
	public MotorSetting getMotor(int number);
	public String getName();
	public String getMotorName(int i);
	public Printer createPrinterObject(PrinterModelArea printerModelArea);
}
