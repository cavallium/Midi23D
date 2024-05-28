package org.warp.midito3d;

public class PrinterArea {
	/**
	 * x, y, z, ...
	 */
	public final int[] min;
	/**
	 * x, y, z, ...
	 */
	public final int[] max;

	public PrinterArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this(3);
		this.min[0] = minX;
		this.min[1] = minY;
		this.min[2] = minZ;
		this.max[0] = maxX;
		this.max[1] = maxY;
		this.max[2] = maxZ;
	}

	public PrinterArea(int nd) {
		this.min = new int[nd];
		this.max = new int[nd];
	}

	public PrinterArea(int[] min, int[] max) {
		assert min.length == max.length;
		this.min = min;
		this.max = max;
	}
}