package org.warp.midito3d.music.mp3;

import java.util.LinkedList;

public class OverlapBuffer {
    private final LinkedList<double[]> buffers;
    private final double[] emptyFrame;
    private final int offset;
    private int current;

    public OverlapBuffer(int frameSize, int overlap) {
        this.offset = frameSize / overlap;
        this.emptyFrame = new double[frameSize];
        this.buffers = new LinkedList();
        for (int i = 0; i < overlap; ++i) {
            this.buffers.add(this.emptyFrame);
        }
    }

    public double next() {
        int myOffset = this.current;
        double val = 0.0;
        for (double[] buf : this.buffers) {
            val += buf[myOffset];
            myOffset += this.offset;
        }
        ++this.current;
        return val;
    }

    public void addFrame(double[] frame) {
        this.buffers.addFirst(frame);
        this.buffers.removeLast();
        this.current = 0;
    }

    public void addEmptyFrame() {
        this.addFrame(this.emptyFrame);
    }

    public boolean needsNewFrame() {
        return this.current == this.offset;
    }
}

