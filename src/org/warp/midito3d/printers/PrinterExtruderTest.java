package org.warp.midito3d.printers;

import java.io.IOException;
import java.util.Locale;

import org.warp.midito3d.PrinterArea;

public class PrinterExtruderTest implements Printer {
	private Motor[] motors;
	private final PrinterArea printerArea;
	double[] motorsPosition = new double[2];
	double[] motorsDirection = new double[] {1d, 1d};
	
	public PrinterExtruderTest(Motor z, Motor e, PrinterArea printerArea) {
		motors = new Motor[]{z,e};
		this.printerArea = printerArea;
	}

	@Override
	public final int getMotorsCount() {
		return 2;
	}

	@Override
	public void initialize(GCodeOutput po) throws IOException {
		po.writeLine("G21");
		goTo(po, 4000, 0, 0);
	}

	@Override
	public void wait(GCodeOutput po, double time) throws IOException {
		po.writeLine(String.format(Locale.US, "G04 P%.4f", time));
	}

	@Override
	public void move(GCodeOutput po, double distanceFactor, double... feed) throws IOException {
		double speedTot = Math.sqrt(Math.pow(feed[0], 2d)+Math.pow(feed[1], 2d));
		double speedPart = Math.sqrt(Math.pow(feed[0], 2d));
		
		for (int i = 0; i < 2; i++) {
			motorsPosition[i] += (feed[i] * distanceFactor * motorsDirection[i]);
			if (isBiggerThanMax(i, motorsPosition[i])) {
				motorsDirection[i] = -1d;
			}
			if (isSmallerThanMin(i, motorsPosition[i])) {
				motorsDirection[i] = 1d;
			}
		}
		
		po.writeLine(String.format(Locale.US, "G01 X0 Y0 Z%.10f E%.10f F%.10f", motorsPosition[0], motorsPosition[1], speedTot));
	}

	@Override
	public void goTo(GCodeOutput po, double speed, double... position) throws IOException {
		po.writeLine(String.format(Locale.US, "G00 X0 Y0 Z%.10f E%.10f F%.10f", position[0], position[1], speed));
	}

	@Override
	public void stop(GCodeOutput po) throws IOException {
	}

	@Override
	public Motor getMotor(int number) {
		return motors[number];
	}

	@Override
	public boolean isBiggerThanMax(int motor, double val) {
		if (motor == 0) {
			return val > printerArea.maxZ;
		} else {
			return false;
		}
	}

	@Override
	public boolean isSmallerThanMin(int motor, double val) {
		if (motor == 0) {
			return val < printerArea.minZ;
		} else {
			return false;
		}
	}
}
