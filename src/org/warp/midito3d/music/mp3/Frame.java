package org.warp.midito3d.music.mp3;

import java.io.PrintStream;
import java.util.Arrays;

import org.jtransforms.dct.DoubleDCT_1D;

public class Frame {
    private double[] data;
    private static DoubleDCT_1D dct;
    private final WindowFunction windowFunc;

    public Frame(double[] timeData, WindowFunction windowFunc) {
        this.windowFunc = windowFunc;
        if (dct == null) {
            dct = new DoubleDCT_1D(timeData.length);
        }
        windowFunc.applyWindow(timeData);
        dct.forward(timeData, true);
        this.data = new double[timeData.length];
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = timeData[i];
        }
    }

    public int getLength() {
        return this.data.length;
    }

    public double getReal(int idx) {
        return this.data[idx];
    }

    public double getImag(int idx) {
        return 0.0;
    }

    public void setReal(int idx, double d) {
        this.data[idx] = d;
    }

    public double[] asTimeData() {
        double[] timeData = new double[this.data.length];
        System.arraycopy(this.data, 0, timeData, 0, this.data.length);
        dct.inverse(timeData, true);
        this.windowFunc.applyWindow(timeData);
        return timeData;
    }

    public static void main(String[] args) {
        double[] orig = new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 0.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 7.0};
        System.out.println(Arrays.toString(orig));
        Frame f = new Frame(orig, new NullWindowFunction());
        System.out.println(Arrays.toString(f.data));
        System.out.println(Arrays.toString(f.asTimeData()));
    }
}

