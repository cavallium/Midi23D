package org.warp.midito3d.music.mp3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Clip {
    private static final Logger logger = Logger.getLogger(Clip.class.getName());
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100.0f, 16, 1, true, true);
    private final List<Frame> frames = new ArrayList<Frame>();
    private int frameSize = 1024;
    private int overlap = 2;
    private double spectralScale = 10000.0;

    public Clip(File file) throws UnsupportedAudioFileException, IOException {
        int n;
        WindowFunction windowFunc = new NullWindowFunction();//new VorbisWindowFunction(this.frameSize);
        AudioFormat desiredFormat = AUDIO_FORMAT;
        BufferedInputStream in = new BufferedInputStream(AudioSystem.getAudioInputStream(desiredFormat, AudioSystem.getAudioInputStream(file)));
        byte[] buf = new byte[this.frameSize * 2];
        in.mark(buf.length * 2);
        while ((n = in.read(buf)) != -1) {
            logger.fine("Read " + n + " bytes");
            double[] samples = new double[this.frameSize];
            for (int i = 0; i < this.frameSize; ++i) {
                byte hi = buf[2 * i];
                int low = buf[2 * i + 1] & 255;
                int sampVal = hi << 8 | low;
                samples[i] = (double)sampVal / this.spectralScale;
            }
            this.frames.add(new Frame(samples, windowFunc));
            in.reset();
            in.skip(this.frameSize * 2 / this.overlap);
            in.mark(buf.length * 2);
        }
        logger.info(String.format("Read %d frames from %s (%d bytes)\n", this.frames.size(), file.getAbsolutePath(), this.frames.size() * buf.length));
    }

    public int getFrameTimeSamples() {
        return this.frameSize;
    }

    public int getFrameFreqSamples() {
        return this.frameSize;
    }

    public double getSpectralScale() {
        return this.spectralScale;
    }

    public int getFrameCount() {
        return this.frames.size();
    }

    public Frame getFrame(int i) {
        return this.frames.get(i);
    }

    public AudioInputStream getAudio() {
        InputStream audioData = new InputStream(){
            int nextFrame;
            OverlapBuffer overlapBuffer;
            int currentSample;
            boolean currentByteHigh;
            int emptyFrameCount;

            public int available() throws IOException {
                return Integer.MAX_VALUE;
            }

            public int read() throws IOException {
                if (this.overlapBuffer.needsNewFrame()) {
                    if (this.nextFrame < Clip.this.frames.size()) {
                        Frame f = (Frame)Clip.this.frames.get(this.nextFrame++);
                        this.overlapBuffer.addFrame(f.asTimeData());
                    } else {
                        this.overlapBuffer.addEmptyFrame();
                        ++this.emptyFrameCount;
                    }
                }
                if (this.emptyFrameCount >= Clip.this.overlap) {
                    return -1;
                }
                if (this.currentByteHigh) {
                    this.currentSample = (int)(this.overlapBuffer.next() * Clip.this.spectralScale);
                    this.currentByteHigh = false;
                    return this.currentSample >> 8 & 255;
                }
                this.currentByteHigh = true;
                return this.currentSample & 255;
            }
        };
        int length = this.getFrameCount() * this.getFrameTimeSamples() * (AUDIO_FORMAT.getSampleSizeInBits() / 8) / this.overlap;
        return new AudioInputStream(audioData, AUDIO_FORMAT, length);
    }

}

