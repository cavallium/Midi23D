package org.warp.midito3d.printers;

import java.io.IOException;
import java.util.Locale;
import org.warp.midito3d.PrinterArea;

public class Printer4Axes extends PrinterNAxes {

	public Printer4Axes(Motor x, Motor y, Motor z, Motor e, PrinterArea printerArea) {
		super(new Motor[]{x, y, z, e}, printerArea);
	}

	@Override
	protected void writeGMove(GCodeOutput po, boolean fastMove, double feed, double[] motorsPosition) throws IOException {
		if (!fastMove) {
			po.writeLine(String.format(Locale.US, "G01 F%.10f", feed));
		}
		po.writeLine(String.format(Locale.US, "G0%s F%.10f X%.10f Y%.10f Z%.10f E%.10f", fastMove ? "0" : "1", feed, motorsPosition[0], motorsPosition[1], motorsPosition[2], motorsPosition[3]));
	}
}
