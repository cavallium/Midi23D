package org.warp.midito3d.music.mp3;

import java.util.Arrays;
import java.util.logging.Logger;

public class VorbisWindowFunction
implements WindowFunction {
    private static final Logger logger = Logger.getLogger(VorbisWindowFunction.class.getName());
    private final double[] scalars;
    private static final double PI = 3.141592653589793;

    public VorbisWindowFunction(int size) {
        this.scalars = new double[size];
        for (int i = 0; i < size; ++i) {
            double xx = Math.sin(3.141592653589793 / (2.0 * (double)size) * (2.0 * (double)i));
            this.scalars[i] = Math.sin(1.5707963267948966 * (xx * xx));
        }
        logger.finest(String.format("VorbisWindowFunction scalars (size=%d): %s\n", this.scalars.length, Arrays.toString(this.scalars)));
    }

    public void applyWindow(double[] data) {
        if (data.length != this.scalars.length) {
            throw new IllegalArgumentException("Invalid array size (required: " + this.scalars.length + "; given: " + data.length + ")");
        }
        for (int i = 0; i < data.length; ++i) {
            double[] arrd = data;
            int n = i;
            arrd[n] = arrd[n] * this.scalars[i];
        }
    }
}

