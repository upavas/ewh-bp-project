package com.ewhoxford.android.bloodpressure.signalProcessing;

import com.ewhoxford.android.bloodpressure.model.BloodPressureValue;
import com.ewhoxford.android.bloodpressure.utils.FileManager;

/**
 * @author user Determines systolic, diastolic and mean arterial blood pressure
 *         values
 */
public class PressureValues {

	/**
	 * 
	 * @param oscillations
	 * @param curve
	 * @return SP, DP, MAP, and interpolated signal
	 * @throws ArrayIsNullException
	 * @see bloodPressure
	 */
	public BloodPressureValue pressureValues(TimeSeriesMod signalOscillations,
			TimeSeriesMod signalIn, int indexUp, int indexDown) {
		
		BloodPressureValue bloodPressure = new BloodPressureValue();
		
		try {
			
	
		/**
		 * Trunk signal between 240 and 40
		 */
		// initialize variables 1
		int l1 = indexDown - indexUp + 1;
		double[] curve = new double[l1];
		double[] oscillations = new double[l1];
		float[] time = new float[l1];
		int j = 0;
		//determine heart rate;
		FileManager.createVectors("total.m", signalOscillations.pressure);
		// select curve correspondent to the indexes 240 to 40
		for (int i = indexUp; i <= indexDown; ++i) {
			curve[j] = signalIn.pressure[i];
			oscillations[j] = signalOscillations.pressure[i];
			time[j] = signalIn.time[i];
			j = j + 1;
		}
				
		// initialize variables 2
		
		int defaultSize = 100;
		double[] maxOscillations = new double[defaultSize];
		float[] maxTime = new float[defaultSize];
		int[] maxTimeInt = new int[defaultSize];
		double[] minOscillations = new double[defaultSize];
		float[] minTime = new float[defaultSize];
		double[] maxOscillationsInterp = new double[oscillations.length];
		double mn = 1;
		double mx = -1;
		double value = 0;
		int mnPos = 0;
		int mxPos = 0;
		double delta = (double) (5 * Math.pow(10, -6));
		boolean lookForMax = true;
		int jmx = 0;
		int jmn = 0;

		// detect oscillation peaks (max and min)
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

		float[] maxTimeMod = new float[indx];
		int[] maxTimeIntMod = new int[indx];
		double[] maxOscillationsMod = new double[indx];

		for (int i = 0; i < indx; ++i) {
			maxTimeMod[i] = maxTime[i];
			maxTimeIntMod[i] = maxTimeInt[i];
			maxOscillationsMod[i] = maxOscillations[i];
		}

		// GET MEAN ARTERIAL PRESSURE
		boolean FLAG = false;
		int MAPPos = 0;
		double MAP = 0;

		while (FLAG == false) {
			MaxResult maxMAP = ArrayOperator.maxValue(maxOscillationsMod);
			if (curve[maxTimeInt[maxMAP.index]] > 160) {
				maxOscillationsMod[maxMAP.index] = 0;
			} else {
				FLAG = true;
				MAPPos = maxTimeInt[maxMAP.index];
				MAP = curve[maxTimeInt[maxMAP.index]];
			}
		}

		// perform linear interpolation to find the other pressure values
		maxOscillationsInterp = LinearInterpolation.linearInterpolation(
				maxTimeMod, maxOscillationsMod, time);

		// FIND DIASTOLIC BLOOD PRESSURE
		FLAG = false;
		// int DPPos = 0;
		double DP = 0;
		int i = MAPPos + 1;
		while (i < maxOscillationsInterp.length && FLAG == false) {
			if ((double) (maxOscillationsInterp[i] / maxOscillationsInterp[MAPPos]) < 0.7) {
				FLAG = true;
				DP = curve[i];
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
			if ((double) (maxOscillationsInterp[i] / maxOscillationsInterp[MAPPos]) < 0.35) {
				FLAG = true;
				SP = curve[i];
				// SPPos = i;
			}
			i = i - 1;
		}

		FileManager.createVectors("osc.m", oscillations);
		
	
		FFT fft= new FFT(oscillations);
		float[] spectrum= fft.fft();

		FileManager.createVectors("spectrum1.m", spectrum);
		MaxResult maxMAP = ArrayOperator.maxValue(spectrum);
		int heartRate=Math.round(maxMAP.getIndex()*60);
		
		// bloodPressure.setPressureSignal(maxOscillationsMod);
		// bloodPressure.setTimeSignal(maxTimeMod);

		// output
		bloodPressure.setPressureSignal(maxOscillationsInterp);
		bloodPressure.setTimeSignal(time);
		bloodPressure.setDiastolicBP(DP);
		bloodPressure.setSystolicBP(SP);
		bloodPressure.setMeanArterialBP(MAP);
		bloodPressure.setHeartRate(heartRate);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return bloodPressure;
	}
}
