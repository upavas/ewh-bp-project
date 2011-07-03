package com.ewhoxford.android.bloodpressure.signalProcessing;

import org.hermit.dsp.FFTTransformer;

public class FFT {

	float[] signal;
	int windowSize;

	public FFT(double[] signalDouble,int windowSize) {
		

		this.signal =new float[signalDouble.length];
		this.windowSize =windowSize;


		
		for (int i = 0; i <= signalDouble.length-1; ++i) {
			this.signal[i]=new Float(signalDouble[i]);
	
		}
		
	}

	float[] fft(){
		int fftBlock=windowSize*2;
		
	        FFTTransformer fft = new FFTTransformer(fftBlock);
	        float[] out = new float[fftBlock / 2];
		
	        fft.setInput(signal, 0, out.length * 2);
	        fft.transform();
	        fft.getResults(out);
		
		
		return out;
		
	}
}
