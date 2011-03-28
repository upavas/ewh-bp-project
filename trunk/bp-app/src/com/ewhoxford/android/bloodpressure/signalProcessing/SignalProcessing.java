package com.ewhoxford.android.bloodpressure.signalProcessing;

/**
 * 
 * @author mpimentel
 * Compile the entire signal processing code by calling the implemented routines
 */
public class SignalProcessing {

	/**
	 * 
	 * @param digitalSignal
	 * @param samplingFrequency
	 * @return final blood pressure values
	 * @see values
	 */
	public BloodPressureValues signalProcessing(TimeSeriesMod aux5, int samplingFrequency){
		
		// initialize variables
		BloodPressureValues values = new BloodPressureValues();
		TimeSeriesMod cuffDeflation = new TimeSeriesMod();
		TimeSeriesMod originalOscillations = new TimeSeriesMod();
		TimeSeriesMod filteredOscillations = new TimeSeriesMod();
				
		/**
		 *  FILTER 1
		 *  remove zeros ('crazy jumps') from digital signal
		 */
//		RmZeros r1 = new RmZeros();
//	    int vals1[][]= r1.rmZeros(digitalSignal); 
//		
//	    /**
//	     * CONVERSION
//	     * convert digital signal to pressure values (in mm Hg)
//	     */
//		TimeSeriesMod aux5 = ConvertTommHg.convertTommHg(vals1, samplingFrequency);
		
		/**
		 * GET CUFF DEFLATION
		 * get the decreasing part of the signal in the range 220 to 40 mm Hg
		 */
		GetDecrCurve r2 = new GetDecrCurve();
		cuffDeflation = r2.getDecrCurve(aux5);
		
		/**
		 * CURVE FITTING
		 * get oscillations by applying exponential LSF
		 */
		ExpFitting r3 = new ExpFitting();
		originalOscillations = r3.expFitting(cuffDeflation);
		
		/**
		 * APPLY BAND-PASS FILTER
		 * get filtered signal by applying a Butterworth band-pass filter
		 */
		FilterButter r4 = new FilterButter();
		filteredOscillations = r4.filterButter(originalOscillations);
		
		/**
		 * CALCULATE BLOOD PRESSURE VALUES
		 * get systolic, diastolic and mean arterial blood pressure values
		 */
		PressureValues r5 = new PressureValues();
		values = r5.pressureValues(filteredOscillations, cuffDeflation);
		
		// output
		return values;
	} 
}
