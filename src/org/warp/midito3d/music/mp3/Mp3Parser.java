package org.warp.midito3d.music.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

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

	public static Mp3Music loadFrom(String file, boolean debug) throws UnsupportedAudioFileException, IOException, DecoderException, BitstreamException {
		File filename = new File(file);
		AudioInputStream in= AudioSystem.getAudioInputStream(filename);
		AudioInputStream din = null;
		AudioFormat baseFormat = in.getFormat();
		double sampleRate = 48000;
		AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
													(int)sampleRate,
		                                            16,
		                                            1,
		                                            2,
		                                            (int)sampleRate,
		                                            true);
		din = AudioSystem.getAudioInputStream(decodedFormat, in);
		
		ReadableByteChannel inCh = Channels.newChannel(din);
	    ByteBuffer inBuf=ByteBuffer.allocate(256);
	    final double factor=2.0/(1<<16);
	    double[] input = new double[0];
	    while(inCh.read(inBuf) != -1) {
	        inBuf.flip();
	        double[] convertedData=new double[inBuf.remaining()/2];
	        DoubleBuffer outBuf=DoubleBuffer.wrap(convertedData);
	        while(inBuf.remaining()>=2) {
	            outBuf.put(inBuf.getShort()*factor);
	        }
	        assert !outBuf.hasRemaining();
	        inBuf.compact();
	        
	        input = concatArrays(input, convertedData);
	    }
	    
	    double durationInSeconds = (input.length+0.0) / sampleRate;  
		
	    /*
		double[] input = new double[din.available()];
		
		double data;
		int i = 0;
		while((data = din.read()) != -1) {
			input[i] = data;
			i++;
		}
		*/
		
		/*
		Bitstream bitStream = new Bitstream(new FileInputStream(file));
		Header frm;
		double[] input = new double[0];
	    System.out.println();
		while((frm = bitStream.readFrame()) != null){
		    Decoder decoder = new Decoder();
		    short[] samples = ((SampleBuffer) decoder.decodeFrame(frm, bitStream)).getBuffer(); //returns the next 2304 samples
		    double[] smpls = new double[samples.length];
		    bitStream.closeFrame();
		    
		    for (int i = 0; i < samples.length; i++) {
		    	smpls[i] = samples[i];
		    }
		    input = concatArrays(input, smpls);
		}
		*/
		
	    sampleRate*=2d;
	    
	    double[] freqs = convertToFreq(input, sampleRate);
	    float samplesPerSecond = (float) (((double)freqs.length)/durationInSeconds);
	    System.out.println("Song duration: "+durationInSeconds+"s; ");
		return new Mp3Music(freqs, samplesPerSecond,/*baseFormat.getSampleRate(), baseFormat.getChannels()*/1, debug);
		
	}
	
	static double[] convertToFreq(double[] input, double sampleRate) {
		int index = 0;
		final int step = 1024;
		
		double[] freqs = new double[(input.length+step-1)/step];
		
		int indx = 0;
		while (input.length - index > 0) {
			int curStep;
			if (index + step < input.length) {
				curStep = step;
			} else {
				curStep = input.length - index;
			}
			
			double[] inputSegment = Arrays.copyOfRange(input, index, index+curStep);
			freqs[indx] = getFreq(inputSegment, sampleRate);
			indx++;
			index+=curStep;
			
		}
		return freqs;
	}
	
	static double getFreq(double[] input, double sampleRate) {
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
		double max_magnitude = Double.NEGATIVE_INFINITY;
		double max_index = -1;
		for (int i = 0; i < n/2; i++) {
			if (magnitude[i] > max_magnitude) {
				max_magnitude = magnitude[i];
				max_index = i;
			}
		}
		
		return max_index * sampleRate / ((double)n);
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
