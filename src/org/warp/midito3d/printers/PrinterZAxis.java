package org.warp.midito3d.printers;

import java.io.IOException;
import java.util.Locale;
import org.warp.midito3d.PrinterArea;

public class PrinterZAxis extends PrinterNAxes {

	public PrinterZAxis(Motor z, PrinterArea printerArea) {
		super(new Motor[]{z}, printerArea);
	}

	@Override
	protected void writeGMove(GCodeOutput po, boolean fastMove, double feed, double[] motorsPosition) throws IOException {
		if (!fastMove) {
			po.writeLine(String.format(Locale.US, "G01 F%.10f", feed));
		}
		po.writeLine(String.format(Locale.US, "G0%s Z%.10f", fastMove ? "0" : "1", feed, motorsPosition[0]));
	}
}
