package com.ewhoxford.android.bloodpressure.signalProcessing;

import android.util.Log;

import com.ewhoxford.android.bloodpressure.exception.BadMeasureException;
import com.ewhoxford.android.bloodpressure.exception.TempBadMeasureException;
import com.ewhoxford.android.bloodpressure.model.BloodPressureValue;

/**
 * 
 * @author Compile the entire signal processing code by calling the implemented
 *         methods
 */
public class SignalProcessing {

	private static String TAG = "Signal Processing";

	/**
	 * 
	 * @param digitalSignal
	 * @param samplingFrequency
	 * @return final blood pressure values
	 * @throws BadMeasureException
	 * @throws TempBadMeasureException
	 * @throws ArrayIsNullException
	 * @see values
	 */
	public BloodPressureValue signalProcessing(TimeSeriesMod aux5,
			float samplingFrequency) {

		// initialize variables
		BloodPressureValue values = new BloodPressureValue();
		TimeSeriesMod newSignal = new TimeSeriesMod();
		//TimeSeriesMod oscillations = new TimeSeriesMod();
		// boolean badMeasure;
		
		// TimeSeriesMod filteredOscillations = new TimeSeriesMod();
		int indexUp = 0;
		int indexDown = 0;

		/**
		 * 
		 * GET CUFF DEFLATION 
		 * Get indexes from the decreasing part of the signal
		 * 
		 */
		try {
			
			Log.v("MAURO & MARCO:", "BEFORE SELECTING CURVE");
			GetDecrCurve r2 = new GetDecrCurve();
			newSignal = r2.getDecrCurve(aux5);
			indexUp = r2.getDecrCurveIndexUp();
			indexDown = r2.getDecrCurveIndexDown();
			// badMeasure = r2.isBadMeasure(filtSignal, 20);

			Log.v("MAURO & MARCO:", "AFTER SELECTING CURVE");
			/**
			 * 
			 * if (badMeasure) { throw new BadMeasureException(TAG); }
			 */
			// /**
			// * CURVE FITTING get oscillations by applying exponential LSF
			// */
			// Detrend r3 = new Detrend();
			// originalOscillations = r3.detrend(cuffDeflation);

			/**
			 * APPLY BAND-PASS FILTER get filtered signal by applying a
			 * Butterworth band-pass filter
			 */
			//FilterButter r4 = new FilterButter();
			//oscillations = r4.filterButter(filtSignal);

			/**
			 * CALCULATE BLOOD PRESSURE VALUES get systolic, diastolic and mean
			 * arterial blood pressure values
			 */
			PressureValues r5 = new PressureValues();
			values = r5.pressureValues(newSignal, indexUp, indexDown, 
					samplingFrequency);
			
			Log.v("MAURO & MARCO:", "AFTER CALCULATING BLOOD PRESSURE VALUES");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// if (values.getDiastolicBP() == 0 || values.getSystolicBP() == 0) {
		// throw new TempBadMeasureException(TAG);
		// }
		//
		// if (values.getDiastolicBP() > 95 || values.getSystolicBP() < 105) {
		// throw new TempBadMeasureException(TAG);
		// }
		// output
		return values;
	}
}
