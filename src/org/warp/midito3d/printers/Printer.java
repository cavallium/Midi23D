package org.warp.midito3d.printers;

import java.io.IOException;

public interface Printer {
	public int getMotorsCount();
	public void initialize(GCodeOutput po) throws IOException;

	/**
	 * @param time seconds
	 */
	public void wait(GCodeOutput po, double time) throws IOException;

	/**
	 *
	 * @param time seconds
	 * @param motorSpeed mm/min
	 */
	public void move(GCodeOutput po, double time, double... motorSpeed) throws IOException;

	/**
	 * @param time seconds
	 */
	public void goTo(GCodeOutput po, double time, double... position) throws IOException;
	public void stop(GCodeOutput po) throws IOException;
	public Motor getMotor(int number);
	public boolean isBiggerThanMax(int motor, double val);
	public boolean isSmallerThanMin(int motor, double val);

	default double euclideanDistance(double[] a, double[] b) {
		double x = 0, s;
		for (int i = 0; i < a.length; x += s * s) {
			s = a[i] - b[i++];
		}
		return Math.sqrt(x);
	}
}
