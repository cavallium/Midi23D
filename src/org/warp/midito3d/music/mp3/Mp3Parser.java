package org.warp.midito3d.music.mp3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import org.jtransforms.fft.DoubleFFT_1D;
import org.warp.midito3d.music.Music;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.Obuffer;
import javazoom.jl.decoder.SampleBuffer;

public class Mp3Parser {

	private final static int actuatorsCount = 4;
	
	public static Mp3Music loadFrom(String file, boolean debug) throws UnsupportedAudioFileException, IOException, DecoderException, BitstreamException {
		File filename = new File(file);
		Clip c = new Clip(filename);
		final int length = c.getFrameCount();
		final int freqSamples = c.getFrameFreqSamples();
	    
	    double durationInSeconds = c.getAudio().getFrameLength() / c.getAudio().getFormat().getFrameRate()/c.getAudio().getFormat().getFrameSize();  
		
	    double[][] freqs = new double[actuatorsCount][length];

	    double max_frequency_power = 0;
	    double max_frequency_index = 0;
	    for (int frameIndex = 0; frameIndex < length; frameIndex++) {
			Frame f = c.getFrame(frameIndex);
			for (int frequencyIndex = 0; frequencyIndex < freqSamples; frequencyIndex++) {
				double frequencyPower = Math.abs(f.getReal(frequencyIndex));
				if (frequencyPower > max_frequency_power) {
					max_frequency_power = frequencyPower;
				}
				if (frequencyIndex > max_frequency_index) {
					max_frequency_index = frequencyIndex;
				}
			}
	    }
	    System.out.println("MAX Frequency: " + ((double)max_frequency_index * (double)c.getSpectralScale() / (double)freqSamples));
	    System.out.println("MAX Frequency Power: " + max_frequency_power);
	    final double thresold_frequency_power = max_frequency_power * 0.6d;
	    
	    for (int frameIndex = 0; frameIndex < length; frameIndex++) {
			//Get max frequencies
			double[] max_freqs_power = new double[actuatorsCount];
			double[] max_freqs_index = new double[actuatorsCount];
			for (int i = 0; i < actuatorsCount; i++) {
				max_freqs_power[i] = 0;
				max_freqs_index[i] = 0;
			}
			Frame f = c.getFrame(frameIndex);
			/*
			for (int frequencyIndex = 0; frequencyIndex < freqSamples; frequencyIndex++) {
				for (int k = 0; k < actuatorsCount; k++) {
					double frequencyPower = Math.abs(f.getReal(frequencyIndex));
					if (frequencyPower > thresold_frequency_power) {
						if (frequencyPower > max_freqs_power[k]) {
							boolean done = false;
							for (int l = 0; l < actuatorsCount; l++) {
								if ((double)Math.abs(frequencyIndex - max_freqs[l]) * (double)c.getSpectralScale() / ((double)freqSamples) <= 10d) { // se le note sono simili (+-10Hz) fai la media e trattale come una sola
									System.out.println("Joining frequencies " + ((double)frequencyIndex * (double)c.getSpectralScale() / ((double)freqSamples)) + "Hz and " + ((double)max_freqs[l] * (double)c.getSpectralScale() / ((double)freqSamples)) + "Hz");
									if (frequencyIndex > max_freqs[l]) {
										max_freqs[l] = frequencyIndex;
										max_freqs_power[l] = frequencyPower > max_freqs_power[l] ? frequencyPower : max_freqs_power[l];
									}
									
									//alternativa:
									//max_freqs[l] = (frequencyIndex * frequencyPower + max_freqs[l] * max_freqs_power[l]) / (frequencyPower + max_freqs_power[l]); // media tra le due note simili
									//max_freqs_power[l] = (frequencyPower + max_freqs_power[l]) / 2; // media tra le due note simili
									
									done = true;
									break;
								}
							}
							if (!done) {
								for (int j = actuatorsCount - 2; j >= k; j--) {
									max_freqs_power[j] = max_freqs_power[j+1];
									max_freqs[j] = max_freqs[j+1];
								}
								max_freqs_power[k] = frequencyPower;
								max_freqs[k] = ((double)frequencyIndex) * c.getSpectralScale() / ((double)freqSamples);
								break;
							}
						}
					}
				}
			}
			*/
			for (int frequencyIndex = 0; frequencyIndex < freqSamples; frequencyIndex++) {
				for (int k = 0; k < actuatorsCount; k++) {
					double frequencyPower = Math.abs(f.getReal(frequencyIndex));
					if (frequencyPower > thresold_frequency_power) {
						if (frequencyIndex > max_freqs_index[k]) {
							boolean done = false;
							for (int l = 0; l < actuatorsCount; l++) {
								if ((double)Math.abs(frequencyIndex - max_freqs_index[l]) * (double)c.getSpectralScale() / ((double)freqSamples) <= 10d) { // se le note sono simili (+-10Hz) fai la media e trattale come una sola
									System.out.println("Joining frequencies " + ((double)frequencyIndex * (double)c.getSpectralScale() / ((double)freqSamples)) + "Hz and " + ((double)max_freqs_index[l] * (double)c.getSpectralScale() / ((double)freqSamples)) + "Hz");
									if (frequencyIndex > max_freqs_index[l]) {
										max_freqs_index[l] = frequencyIndex;
										max_freqs_power[l] = frequencyPower > max_freqs_power[l] ? frequencyPower : max_freqs_power[l];
									}
									done = true;
									break;
								}
							}
							if (!done) {
								for (int j = actuatorsCount - 2; j >= k; j--) {
									max_freqs_power[j] = max_freqs_power[j+1];
									max_freqs_index[j] = max_freqs_index[j+1];
								}
								max_freqs_power[k] = frequencyPower;
								max_freqs_index[k] = frequencyIndex;
								break;
							}
						}
					}
				}
			}
			for (int i = 0; i < actuatorsCount; i++) {
				freqs[i][frameIndex] = (double)max_freqs_index[i] * (double)c.getSpectralScale() / (double)freqSamples;
			}
	    }

	    freqsArrToImg(freqs, false, 2d, 64, "freqs");
	    float samplesPerSecond = (float) (((double)freqs[0].length)/durationInSeconds);
	    System.out.println("Song duration: "+durationInSeconds+"s; ");

	    debugMusic(freqs, durationInSeconds);
	    
		return new Mp3Music(freqs, samplesPerSecond, debug);
	}
	
	private static void debugMusic(final double[][] freqs, final double songDuration) {
		System.out.println("Debugging music");
		for (int chan = 0; chan < 1; chan++) {
			final int chanF = chan;
			new Thread(() -> {
				try {
					createToneList();
					for (int idx = 0; idx < freqs[chanF].length; idx++) {
						writeTone((int) (freqs[chanF][idx]), (int) (songDuration*1000d/freqs[0].length), 0.5d / (double)actuatorsCount * ((double)actuatorsCount - chanF));
						//writeTone(265, 23, 0.5d);
					}
					startToneList();
					System.out.println("track ended.");
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}
	
	private static float SAMPLE_RATE = 8000f;
	private static void writeTone(int hz, int msecs) throws LineUnavailableException {
		writeTone(hz, msecs, 0.5d);
    }
	private static SourceDataLine sdl;
	private static void createToneList() throws LineUnavailableException {
        AudioFormat af = new AudioFormat(SAMPLE_RATE,8,1,true,false);     
        sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
	}
	private static void startToneList() throws LineUnavailableException {
        sdl.drain();
        sdl.stop();
        sdl.close();
	}
	
	private static void writeTone(int hz, int msecs, double vol) throws LineUnavailableException {
        byte[] buf = new byte[1];
        for (int i=0; i < msecs*8; i++) {
              double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
              buf[0] = (byte)(Math.sin(angle) * 127.0 * vol);
              sdl.write(buf,0,1);
        }
    }

	private static void arrToImg(double[] input, boolean reverseY, double widthmultiplier, int height, String name) {
	    BufferedImage debugAudioRawData = new BufferedImage((int)(input.length*widthmultiplier), height, BufferedImage.TYPE_INT_RGB);
	    Graphics g = debugAudioRawData.getGraphics();
    	g.setColor(Color.black);
    	g.clearRect(0, 0, (int)(input.length*widthmultiplier), height);
    	g.setColor(Color.white);
    	double min = Double.MAX_VALUE;
    	double max = Double.MIN_VALUE;
	    for (int i = 0; i < input.length; i++) {
	    	if (input[i] > max) {
	    		max = input[i];
	    	}
	    	if (input[i] < min) {
	    		min = input[i];
	    	}
	    }
	    System.out.println("min: "+ min + ", max: " + max);
	    for (int i = 0; i < input.length; i++) {
	    	final int y = (int) (((input[i] - min) / (max-min)) * (double)(height - 1));
	    	g.fillRect((int)(i*widthmultiplier), reverseY?y:((height - 1) - y), widthmultiplier>1?(int)(1*widthmultiplier):1, 1);
	    }
	    g.dispose();
	    try {
			ImageIO.write(debugAudioRawData, "bmp", new File("C:\\Users\\Andrea Cavalli\\Videos\\Desktop\\"+name+".bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void freqsArrToImg(double[][] input, boolean reverseY, double widthmultiplier, int height, String name) {
	    BufferedImage debugAudioRawData = new BufferedImage((int)(input[0].length*widthmultiplier), height, BufferedImage.TYPE_INT_RGB);
	    Graphics g = debugAudioRawData.getGraphics();
    	g.setColor(Color.black);
    	g.clearRect(0, 0, (int)(input[0].length*widthmultiplier), height);
    	g.setColor(Color.white);
    	double min = Double.MAX_VALUE;
    	double max = Double.MIN_VALUE;
    	for (int i = 0; i < input.length; i++) {
    	    for (int j = 0; j < input[i].length; j++) {
    	    	if (input[i][j] > max) {
    	    		max = input[i][j];
    	    	}
    	    	if (input[i][j] < min) {
    	    		min = input[i][j];
    	    	}
    	    }
    	}
	    System.out.println("freq min: "+ min + ", freq max: " + max);
    	for (int i = 0; i < input.length; i++) {
    	    for (int j = 0; j < input[i].length; j++) {
    	    	final int y = (int) (((input[i][j] - min) / (max-min)) * (double)(height - 1));
    	    	g.fillRect((int)(j*widthmultiplier), reverseY?y:((height - 1) - y), widthmultiplier>1?(int)(1*widthmultiplier):1, 1);
    	    }
    	}
	    g.dispose();
	    try {
			ImageIO.write(debugAudioRawData, "bmp", new File("C:\\Users\\Andrea Cavalli\\Videos\\Desktop\\"+name+".bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static double[][] convertToFreq(double[] input, double sampleRate, final int simultaneousFrequencies) {
		int index = 0;
		final int step = (int)(sampleRate/2d); //2 steps per second
		
		double[][] freqs = new double[simultaneousFrequencies][(input.length+step-1)/step];
		
		int indx = 0;
		while (input.length - index > 0) {
			int curStep;
			if (index + step < input.length) {
				curStep = step;
			} else {
				curStep = input.length - index;
			}
			
			double[] inputSegment = Arrays.copyOfRange(input, index, index+curStep);
			double[] freqsInstant = getFreq(inputSegment, sampleRate, simultaneousFrequencies);
			for (int i = 0; i < simultaneousFrequencies; i++) {
				freqs[i][indx] = freqsInstant[i];
			}
			indx++;
			index+=curStep;
			
		}
		return freqs;
	}
	
	static double[] getFreq(double[] input, double sampleRate, final int simultaneousFrequencies) {
		final int n = input.length;
		
		//Calculate FFT
		DoubleFFT_1D fft = new DoubleFFT_1D(n);
		double[] fftData = new double[n*2];
		double[] magnitude = new double[n/2];
		System.arraycopy(input, 0, fftData, 0, n);
		fft.realForwardFull(fftData);
		
		//Calculate magnitude;
		for (int i = 0; i < n/2; i++) {
			double re = fftData[2*i];
			double im = fftData[2*i+1];
			magnitude[i] = Math.sqrt(re*re+im*im);
		}

		//Get frequency
		double[] max_magnitudes = new double[simultaneousFrequencies];
		double[] max_indexes = new double[simultaneousFrequencies];
		for (int i = 0; i < simultaneousFrequencies; i++) {
			max_magnitudes[i] = Double.NEGATIVE_INFINITY;
			max_indexes[i] = -1;
		}
		for (int i = 0; i < n/2; i++) {
			for (int k = 0; k < simultaneousFrequencies; k++) {
				if (magnitude[i] > max_magnitudes[k]) {
					for (int j = simultaneousFrequencies - 2; j >= k; j--) {
						max_magnitudes[j] = max_magnitudes[j+1];
						max_indexes[j] = max_indexes[j+1];
					}
					max_magnitudes[k] = magnitude[i];
					max_indexes[k] = i;
					break;
				}
			}
		}

		//System.out.println(max_indexes * sampleRate / ((double)n));
		double[] result = new double[simultaneousFrequencies];
		for (int i = 0; i < simultaneousFrequencies; i++) {
			result[i] = max_indexes[i] * sampleRate / ((double)n);
		}
		return result;
	}
	
    public static double[][] realToComplex(double[] real) {
        double[][] complex = new double[real.length][2];
        for (int i = 0; i < real.length; ++i) {
            complex[i][0] = real[i];
        }
        return complex;
    }

    static double[] concatArrays(double[] input, double[] smpls) {

        int aLen = input.length;
        int bLen = smpls.length;
        double[] C= new double[aLen+bLen];

        System.arraycopy(input, 0, C, 0, aLen);
        System.arraycopy(smpls, 0, C, aLen, bLen);

        return C;
    }

}
