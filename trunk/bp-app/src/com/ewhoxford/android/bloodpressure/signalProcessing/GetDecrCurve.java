package com.ewhoxford.android.bloodpressure.signalProcessing;

/**
 * 
 * @author user Get decreasing part of the signal
 */
public class GetDecrCurve {

	/**
	 * 
	 * @param signal
	 * @return array with curve correspondent to the decreasing part of the
	 *         signal
	 * @throws ArrayIsNullException
	 * @see curve
	 */
	
	private int indxup = 0;
	private int indxdown = 0;
	
	public TimeSeriesMod getDecrCurve(TimeSeriesMod signal) {
		
		// get max and index values of the original signal
		MaxResult maxval = ArrayOperator.maxValue(signal.pressure);
		
		// initialize variables
		TimeSeriesMod signalOut = new TimeSeriesMod();
		int l = signal.pressure.length - maxval.index;
		double[] curve = new double[signal.pressure.length];
		float[] time = new float[signal.pressure.length];
		
		// apply additional filter - remove transitions/jumps higher than 50 mmHg
		curve = signal.pressure;
		time = signal.time;
		//curve[0] = signal.pressure[maxval.index];
		//time[0] = signal.time[maxval.index];
		for (int i = 1; i < l; ++i) {
			int j = i + maxval.index;
			if (Math.abs(signal.pressure[j - 1] - signal.pressure[j]) > 50) {
				signal.pressure[j] = signal.pressure[j - 1];
				curve[j] = signal.pressure[j - 1];
			} else
				curve[j] = signal.pressure[j];
			time[j] = signal.time[j];
		}
		
		// define window for median filter
		double[] window = new double[5];

		// APPLY MEDIAN FILTER TO ENTIRE SIGNAL
		for (int i = 2; i < curve.length - 2; ++i) {

			// pick up window elements
			for (int j = 0; j < 5; ++j)
				window[j] = (double) curve[i - 2 + j];

			// order elements (only half of them)
			for (int j = 0; j < 3; ++j) {

				// find position of minimum element
				int min = j;
				for (int k = j + 1; k < 5; ++k)
					if (window[k] < window[min])
						min = k;

				// put found minimum element in its place
				double temp = window[j];
				window[j] = window[min];
				window[min] = temp;
			}

			// get result - the middle element
			curve[i - 2] = window[2];
		}

		// SELECT VALUES FROM 240 TO 40 mm Hg
		// initialize variables
		boolean FLAG1 = false;
		boolean FLAG2 = false;
		//int indxup = 0;
		//int indxdown = 0;

		// get indexes
		for (int i = maxval.index + 1; i < curve.length; ++i) {
			if (curve[i] <= 200 && FLAG1 == false) {
				indxup = i;
				FLAG1 = true;
			}
			if (curve[i] <= 40 && FLAG2 == false) {
				indxdown = i;
				FLAG2 = true;
			}
		}
		
		/*
		int l1 = indxdown - indxup + 1;
		double[] curvef = new double[l1];
		float[] timef = new float[l1];
		int j = 0;

		// select curve correspondent to the indexes 240 to 40
		for (int i = indxup; i <= indxdown; ++i) {
			curvef[j] = curve[i];
			timef[j] = time[i];
			j = j + 1;
		}
		*/
		// update indxup and indxdown
		//indxup = indxup + maxval.index;
		//indxdown = indxdown + maxval.index;
		
		// output
		signalOut.setPressure(curve);
		signalOut.setTime(time);
		return signalOut;
	}
	
	
	public int getDecrCurveIndexUp() {
		return indxup;
	}
	
	
	public int getDecrCurveIndexDown(){
		return indxdown;
	}

	
	public boolean isBadMeasure(TimeSeriesMod signal, int timeTreshold) {
		float time = signal.time[indxdown] - signal.time[indxup]; 
		System.out.println("initial:"+signal.time[indxdown]+"s, end:"+signal.time[indxup]+"s");
		if (time < timeTreshold) {
			return true;
		}
		return false;
	}
	
	

}
