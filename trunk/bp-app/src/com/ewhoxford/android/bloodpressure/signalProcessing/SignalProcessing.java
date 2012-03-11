package com.ewhoxford.android.bloodpressure.signalProcessing;

import com.ewhoxford.android.bloodpressure.exception.BadMeasureException;
import com.ewhoxford.android.bloodpressure.exception.TempBadMeasureException;
import com.ewhoxford.android.bloodpressure.model.BloodPressureValue;

/**
 * 
 * @author mpimentel Compile the entire signal processing code by calling the
 *         implemented routines
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
			float samplingFrequency) throws BadMeasureException, TempBadMeasureException {

		// initialize variables
		BloodPressureValue values = new BloodPressureValue();
		TimeSeriesMod filtSignal = new TimeSeriesMod();
		TimeSeriesMod oscillations = new TimeSeriesMod();
		boolean badMeasure;
		// TimeSeriesMod filteredOscillations = new TimeSeriesMod();
		int indexUp = 0;
		int indexDown = 0;

		/**
		 * GET CUFF DEFLATION get indexes from the decreasing part of the signal
		 * in the range 220 to 40 mm Hg
		 */
		GetDecrCurve r2 = new GetDecrCurve();
		filtSignal = r2.getDecrCurve(aux5);
		indexUp = r2.getDecrCurveIndexUp();
		indexDown = r2.getDecrCurveIndexDown();
		badMeasure = r2.isBadMeasure(filtSignal, 20);

		if (badMeasure) {
			throw new BadMeasureException(TAG);
		}
		// /**
		// * CURVE FITTING get oscillations by applying exponential LSF
		// */
		// Detrend r3 = new Detrend();
		// originalOscillations = r3.detrend(cuffDeflation);

		/**
		 * APPLY BAND-PASS FILTER get filtered signal by applying a Butterworth
		 * band-pass filter
		 */
		FilterButter r4 = new FilterButter();
		oscillations = r4.filterButter(filtSignal);

		/**
		 * CALCULATE BLOOD PRESSURE VALUES get systolic, diastolic and mean
		 * arterial blood pressure values
		 */
		PressureValues r5 = new PressureValues();
		values = r5.pressureValues(oscillations, filtSignal, indexUp,
				indexDown, samplingFrequency);

//		if (values.getDiastolicBP() == 0 || values.getSystolicBP() == 0) {
//			throw new TempBadMeasureException(TAG);
//		}
//
//		if (values.getDiastolicBP() > 95 || values.getSystolicBP() < 105) {
//			throw new TempBadMeasureException(TAG);
//		}
		// output
		return values;
	}
}
