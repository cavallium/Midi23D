package org.warp.midito3d.printers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import org.warp.midito3d.PrinterArea;

public abstract class PrinterNAxes implements Printer {

	private Motor[] motors;
	private final PrinterArea printerArea;
	double[] motorsPosition;
	double[] motorsDirection;

	PrinterNAxes(Motor[] motors, PrinterArea printerArea) {
		this.motors = motors;
		this.printerArea = printerArea;
		motorsPosition = new double[motors.length];
		motorsDirection = new double[motors.length];
		Arrays.fill(motorsDirection, 1d);
	}

	@Override
	public final int getMotorsCount() {
		return motors.length;
	}

	@Override
	public void initialize(GCodeOutput po) throws IOException {
		po.writeLine("M82");
		po.writeLine("G21");
		po.writeLine("G90");
		po.writeLine("G28 X0 Y0");
		po.writeLine("G0 Z0 F8000");
		po.writeLine("M302 S0 P1");
	}

	@Override
	public void wait(GCodeOutput po, double time) throws IOException {
		po.writeLine(String.format(Locale.US, "G04 S%.4f", time));
	}

	@Override
	public void move(GCodeOutput po, double time, double... speed) throws IOException {
		// mm
		double[] initialPosition = Arrays.copyOf(motorsPosition, motorsPosition.length);
		// mm
		double[] finalPosition = Arrays.copyOf(motorsPosition, motorsPosition.length);

		// calculate final position
		for (int i = 0; i < initialPosition.length; i++) {
			double motorDelta = ((speed[i] / 60d * time) * motorsDirection[i]);
			finalPosition[i] += motorDelta;
		}

		goToInternal(po, time, initialPosition, finalPosition);
	}

	@Override
	public void goTo(GCodeOutput po, double time, double... position) throws IOException {
		// mm
		double[] initialPosition = Arrays.copyOf(motorsPosition, motorsPosition.length);
		// mm
		double[] finalPosition = Arrays.copyOf(position, motorsPosition.length);

		goToInternal(po, time, initialPosition, finalPosition);
	}

	/**
	 * @param time seconds
	 * @param initialPosition mm
	 * @param finalPosition mm
	 */
	private void goToInternal(GCodeOutput po, double time, double[] initialPosition, double[] finalPosition) throws IOException {
		double distance = euclideanDistance(initialPosition, finalPosition);

		// mm/min
		double aggregateSpeed = distance / (time / 60d);

		// update global state
		for (int i = 0; i < finalPosition.length; i++) {
			if (isBiggerThanMax(i, finalPosition[i])) {
				motorsDirection[i] = -1d;
			}
			if (isSmallerThanMin(i, finalPosition[i])) {
				motorsDirection[i] = 1d;
			}
		}
		System.arraycopy(finalPosition, 0, motorsPosition, 0, finalPosition.length);

		writeGMove(po, false, aggregateSpeed, finalPosition);
	}

	protected abstract void writeGMove(GCodeOutput po, boolean fastMove, double feed, double[] motorsPosition) throws IOException;

	@Override
	public void stop(GCodeOutput po) throws IOException {
	}

	@Override
	public Motor getMotor(int number) {
		return motors[number];
	}

	@Override
	public boolean isBiggerThanMax(int motor, double val) {
		return val > printerArea.max[motor];
	}

	@Override
	public boolean isSmallerThanMin(int motor, double val) {
		return val < printerArea.min[motor];
	}
}
