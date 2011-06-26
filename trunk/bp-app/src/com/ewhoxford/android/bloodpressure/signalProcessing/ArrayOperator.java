package com.ewhoxford.android.bloodpressure.signalProcessing;

/**
 * 
 * @author mpimentel Does basic mathematical operations over arrays: maximum
 *         calculation, time series
 */
public class ArrayOperator {

	/**
	 * 
	 * @param array
	 * @return max and index values of an array
	 * @throws ArrayIsNullException
	 * @see mr
	 */
	public static MaxResult maxValue(double[] array) {

		if (array.length == 0) {

			throw new IllegalArgumentException(
					"Array is Null, cannot calculate Maximum value and Index value");

		} else {

			// initialize variables
			double maximum = array[0];
			int index = 0;

			// get max value
			for (int i = 1; i < array.length; i++) {
				if (array[i] > maximum) {
					maximum = array[i];
					index = i;
				}
			}

			// output
			MaxResult mr = new MaxResult();
			mr.setIndex(index);
			mr.setMax(maximum);
			return mr;
		}

	}
	
	/**
	 * 
	 * @param array
	 * @return max and index values of an array
	 * @throws ArrayIsNullException
	 * @see mr
	 */
	public static MaxResult maxValue(float[] array) {

		if (array.length == 0) {

			throw new IllegalArgumentException(
					"Array is Null, cannot calculate Maximum value and Index value");

		} else {

			// initialize variables
			double maximum = array[0];
			int index = 0;

			// get max value
			for (int i = 1; i < array.length; i++) {
				if (array[i] > maximum) {
					maximum = array[i];
					index = i;
				}
			}

			// output
			MaxResult mr = new MaxResult();
			mr.setIndex(index);
			mr.setMax(maximum);
			return mr;
		}

	}

	/**
	 * 
	 * @param array
	 * @return min and index values of an array
	 * @see mr
	 */
	public static MinResult minValue(double[] array) {
		// initialize variables
		double minimum = array[0];
		int index = 0;

		// get min value
		for (int i = 1; i < array.length; i++) {
			if (array[i] < minimum) {
				minimum = array[i];
				index = i;
			}
		}

		// output
		MinResult mr = new MinResult();
		mr.setIndex(index);
		mr.setMin(minimum);
		return mr;
	}

	/**
	 * 
	 * @param signal
	 * @return result of some sum operations for exponential LSF
	 * @see mr
	 */
	public static SumsResults sumModified(TimeSeriesMod signal) {
		// initialize variables
		SumsResults mr = new SumsResults();
		double y = 0, x = 0, xy = 0, xylny = 0, x2y = 0, ylny = 0;
		double aux1, aux2, aux3, aux4;

		// calculate different sums
		for (int i = 0; i < signal.pressure.length; ++i) {
			x = x + signal.time[i];
			y = (double) y + signal.pressure[i];
			aux1 = (double) signal.time[i] * signal.pressure[i];
			xy = xy + aux1;
			aux2 = (double) (aux1 * Math.log(signal.pressure[i]));
			xylny = xylny + aux2;
			aux3 = aux1 * signal.time[i];
			x2y = x2y + aux3;
			aux4 = (double) (signal.pressure[i] * Math.log(signal.pressure[i]));
			ylny = ylny + aux4;
		}

		// output
		mr.setSum_x(x);
		mr.setSum_y(y);
		mr.setSum_xy(xy);
		mr.setSum_x2y(x2y);
		mr.setSum_xylny(xylny);
		mr.setSum_ylny(ylny);
		return mr;
	}
}
