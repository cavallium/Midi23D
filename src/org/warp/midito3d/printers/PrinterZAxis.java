package org.warp.midito3d.printers;

import java.io.IOException;
import java.util.Locale;

import org.warp.midito3d.PrinterArea;

public class PrinterZAxis implements Printer {
	public static final int modelID = 0x01;
	
	private Motor motor;
	private final PrinterArea printerArea;
	double motorPosition = 0;
	double motorDirection = 1d;
	
	public PrinterZAxis(Motor z, PrinterArea printerArea) {
		motor = z;
		this.printerArea = printerArea;
	}

	@Override
	public final int getMotorsCount() {
		return 1;
	}

	@Override
	public void initialize(GCodeOutput po) throws IOException {
		po.writeLine("G21");
		po.writeLine("M302");
		goTo(po, 8000, printerArea.minX, printerArea.minY, printerArea.minZ, 0);
	}

	@Override
	public void wait(GCodeOutput po, double time) throws IOException {
		po.writeLine(String.format(Locale.US, "G04 P%.4f", time));
	}

	@Override
	public void move(GCodeOutput po, double time, double... speed) throws IOException {
		time/=60d;
		double motorDelta = ((speed[0] * time) *motorDirection);
		motorPosition += motorDelta;
		if (isBiggerThanMax(0, motorPosition)) {
			motorDirection = -1d;
		}
		if (isSmallerThanMin(0, motorPosition)) {
			motorDirection = 1d;
		}

		po.writeLine(String.format(Locale.US, "G01 F%.10f", speed[0]));
		po.writeLine(String.format(Locale.US, "G01 X0 Y0 Z%.10f E0 F%.10f", motorPosition, speed[0]));
	}

	@Override
	public void goTo(GCodeOutput po, double speed, double... position) throws IOException {
		motorPosition = position[0];
		po.writeLine(String.format(Locale.US, "G00 F%.10f", speed));
		po.writeLine(String.format(Locale.US, "G00 X0 Y0 Z%.10f E0 F%.10f", position[0], position[1], position[2], speed));
	}

	@Override
	public void stop(GCodeOutput po) throws IOException {
	}

	@Override
	public Motor getMotor(int number) {
		return motor;
	}

	@Override
	public boolean isBiggerThanMax(int motor, double val) {
		return val > printerArea.maxZ;
	}

	@Override
	public boolean isSmallerThanMin(int motor, double val) {
		return val < printerArea.minZ;
	}
}
