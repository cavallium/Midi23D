package org.warp.midito3d.printers;

import java.io.IOException;

public interface Printer {
	public int getMotorsCount();
	public void initialize(GCodeOutput po) throws IOException;
	public void wait(GCodeOutput po, double time) throws IOException;
	public void move(GCodeOutput po, double time, double... motorSpeed) throws IOException;
	public void goTo(GCodeOutput po, double speed, double... position) throws IOException;
	public void stop(GCodeOutput po) throws IOException;
	public Motor getMotor(int number);
	public boolean isBiggerThanMax(int motor, double val);
	public boolean isSmallerThanMin(int motor, double val);
}
