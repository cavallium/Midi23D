package org.warp.midito3d.gui.printers;

import java.io.IOException;

import org.warp.midito3d.printers.GCodeOutput;
import org.warp.midito3d.printers.Motor;

public interface PrinterModel {
	public int getMotorsCount();
	public MotorSetting getMotor(int number);
	public String getName();
	public String getMotorName(int i);
}
