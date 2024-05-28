package org.warp.midito3d.gui.printers;

import org.warp.midito3d.PrinterArea;

public class PrinterModelArea {
	public int[] size;
	public int[] margins;
	
	/**
	 * @param size
	 * @param margins
	 */
	public PrinterModelArea(int[] size, int[] margins) {
		this.size = size;
		this.margins = margins;
	}

	public PrinterArea createAreaObject() {
		return new PrinterArea(new int[] {margins[0], margins[1], margins[2], margins[3]}, new int[] {size[0]-margins[0], size[1]-margins[1], size[2]-margins[2], size[3]-margins[3]});
	}
}
