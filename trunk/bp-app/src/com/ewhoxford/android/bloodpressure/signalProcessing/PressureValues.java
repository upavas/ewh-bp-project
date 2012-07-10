package com.ewhoxford.android.bloodpressure.signalProcessing;

import java.util.Arrays;

import com.ewhoxford.android.bloodpressure.model.BloodPressureValue;
import com.ewhoxford.android.bloodpressure.utils.FileManager;

/**
 * @author user Determines systolic, diastolic and mean arterial blood pressure
 *         values
 */
public class PressureValues {

	/**
	 * 
	 * @param signalInFrequency
	 * @param oscillations
	 * @param curve
	 * @return SP, DP, MAP, and interpolated signal
	 * @throws ArrayIsNullException
	 * @see bloodPressure
	 * 
	 */
	
	public BloodPressureValue pressureValues(TimeSeriesMod signal, int indexUp, 
			int indexDown, float signalInFrequency) {

		BloodPressureValue bloodPressure = new BloodPressureValue();
		
		int l1 = indexDown - indexUp + 1;
		double[] cuffpress = new double[l1];
		double[] oscillations = new double[l1];
		double sumOscill = 0.0;
		float[] time = new float[l1];
		
		int j = 0;
		
		// Define arrays with the different time series
		for (int i = indexUp; i <= indexDown; ++i) {
			cuffpress[j] = signal.pressure[i];
			oscillations[j] = signal.oscill[i];
			time[j] = signal.time[i];
			sumOscill = sumOscill + oscillations[j];	// used to remove DC component of the Oscillations signal
			j = j + 1;
		}
		
		sumOscill = sumOscill / oscillations.length;
		
		// Now let's remove the DC component of the oscillations signal
		for (int j1 = 0; j1 < oscillations.length; ++j1){
			oscillations[j1] = oscillations[j1] - sumOscill;
			if (oscillations[j1] < 0)
				oscillations[j1] = 0;
		}
		
		/*** Detect peaks of the oscillations ***/
		// Initialize variables and pre-allocate space for variables
		int defaultSize = 200;	// there won't be more than 200 peaks in the oscillations
		
		// Maximums
		double[] maxOscillations = new double[defaultSize];
		float[] maxTime = new float[defaultSize];
		int[] maxTimeInt = new int[defaultSize];
		
		// Minimums
		double[] minOscillations = new double[defaultSize];
		float[] minTime = new float[defaultSize];
		
		// Array with the result of a linear interpolation procedure
		double[] maxOscillationsInterp = new double[oscillations.length];
		
		double mn = 1;
		double mx = -1;
		double value = 0;
		int mnPos = 0;
		int mxPos = 0;
		double delta = 0.05;
		boolean lookForMax = true;
		int jmx = 0;
		int jmn = 0;
		
		// Run peak detector
		for (int i = 0; i < oscillations.length; ++i) {
			value = oscillations[i];
			if (value > mx) {
				mx = value;
				mxPos = i;
			}
			if (value < mn) {
				mn = value;
				mnPos = i;
			}
			if (lookForMax == true) {
				if (value < (mx - delta)) {
					maxOscillations[jmx] = mx;
					maxTime[jmx] = time[mxPos];
					maxTimeInt[jmx] = mxPos;
					mn = value;
					mnPos = i;
					jmx = jmx + 1;
					lookForMax = false;
				}
			} else {
				if (value > (mn + delta)) {
					minOscillations[jmn] = mn;
					minTime[jmn] = time[mnPos];
					mx = value;
					mxPos = i;
					jmn = jmn + 1;
					lookForMax = true;
				}
			}
		}
		
		// remove extra positions that were not occupied
		boolean Flag = false;
		int indx = 0;

		for (int i = 0; i < maxTime.length; ++i) {
			if (maxTime[i] == 0 && Flag == false) {
				indx = i;
				Flag = true;
			}
		}

		float[] meanTimeMod = new float[indx];
		int[] meanTimeIntMod = new int[indx];
		double[] meanOscillationsMod = new double[indx];
		float rr[] = new float[indx];
		
		for (int i = 0; i < indx; ++i) {
			meanTimeMod[i] = maxTime[i];
			meanTimeIntMod[i] = maxTimeInt[i];
			meanOscillationsMod[i] = maxOscillations[i];
			if (i < indx - 1)
				rr[i] = maxTime[i+1] - maxTime[i];
		}
		
		meanOscillationsMod[0] = 0;
		meanOscillationsMod[meanOscillationsMod.length - 1] = 0;
		
		/*** CALCULATE BLOOD PRESSURE AND HEART RATE ***/
		// RATIOS TO BE USED
		double ratioSBP = 0.75; double ratioDBP = 0.75;
		
		// GET MEAN ARTERIAL PRESSURE
		boolean FLAG = false;
		int MAPPos = 0;
		double MAP = 0;

		while (FLAG == false) {
			MaxResult maxMAP = ArrayOperator.maxValue(meanOscillationsMod);
			if (cuffpress[meanTimeIntMod[maxMAP.index]] > 180) {	// Un-necessary step for the new signal
				meanOscillationsMod[maxMAP.index] = 0;
			} else {
				FLAG = true;
				MAPPos = meanTimeIntMod[maxMAP.index];
				MAP = cuffpress[meanTimeIntMod[maxMAP.index]];
			}
		}
		
		// perform linear interpolation to find the other pressure values
		maxOscillationsInterp = LinearInterpolation.linearInterpolation(
				meanTimeMod, meanOscillationsMod, time);

		// FIND DIASTOLIC BLOOD PRESSURE
		FLAG = false;
		// int DPPos = 0;
		double DP = 0;
		int i = MAPPos + 1;
		while (i < maxOscillationsInterp.length && FLAG == false) {
			if ((double) (maxOscillationsInterp[i] / maxOscillationsInterp[MAPPos]) < ratioDBP) {
				FLAG = true;
				DP = cuffpress[i];
				// DPPos = i;
			}
			i = i + 1;
		}

		// FIND SYSTOLIC BLOOD PRESSURE
		FLAG = false;
		// int SPPos = 0;
		double SP = 0;
		i = MAPPos - 1;
		while (i > 0 && FLAG == false) {
			if ((double) (maxOscillationsInterp[i] / maxOscillationsInterp[MAPPos]) < ratioSBP) {
				FLAG = true;
				SP = cuffpress[i];
				// SPPos = i;
			}
			i = i - 1;
		}
		
		// Calculate Heart Rate (OPTION 1: using the FFT)
		
		//FileManager.createVectors("osc.m", oscillations);
		int nfft = Power2.determine(Math.round(oscillations.length));
		double[] newOscillations = new double[nfft];
		for (int k = 0; k < nfft; k++) {
			if (k < oscillations.length)
				newOscillations[k] = oscillations[k];
			else
				newOscillations[k] = 0;
		}
		FFT fft = new FFT(newOscillations, nfft);
		float[] spectrum = fft.fft();
		
		boolean hrFound = false; 
		int heartRate = 0;
		
		while (hrFound == false) {
			// FileManager.createVectors("spectrum1.m", spectrum);
			MaxResult maxMAP = ArrayOperator.maxValue(spectrum);
			// System.out.println("mR:" + maxMAP);
			int index2 = maxMAP.getIndex();
			// float spectrumL=spectrum.length;
			float freq = (index2) * signalInFrequency / nfft;
			heartRate = Math.round(freq * 60);
			if (heartRate < 31) {
				spectrum[index2] = 0;
			} else
				hrFound = true;
		}
		
		// Calculate Heart Rate (OPTION 2: using the oscillation peaks detected)
		
//		 Arrays.sort(rr);
//		int middle = rr.length/2;
//	    if (rr.length%2 == 1) {
//	        heartRate = Math.round(60/rr[middle]);
//	    } else {
//	    	heartRate = Math.round(60/((rr[middle-1]+rr[middle])/2));
//	    }
		
		// output
		bloodPressure.setPressureSignal(maxOscillationsInterp);
		bloodPressure.setTimeSignal(time);
		bloodPressure.setDiastolicBP(DP);
		bloodPressure.setSystolicBP(SP);
		bloodPressure.setMeanArterialBP(MAP);
		bloodPressure.setHeartRate(heartRate);

		return bloodPressure;
	}
}
