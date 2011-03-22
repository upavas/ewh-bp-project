package com.ewhoxford.android.bloodpressure;

import com.androidplot.series.XYSeries;

public class SimpleXYSeries implements XYSeries {

	private final float[] vals;

	public SimpleXYSeries(int l, int[][] vals1) {
		int valsx = 0;
		int valsy = 0;
		float aux1 = 0;
		float aux2 = 0;
		vals = new float[l];

		int i = 0;

		while (i < l) {
			valsy = Math.abs(vals1[i][1] - 255);
			if (vals1[i][0] == 1) {
				valsx = 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2;
			} else if (vals1[i][0] == 2) {
				valsx = 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2;
			} else if (vals1[i][0] == 3) {
				valsx = (2 * 2 * 2 * 2 * 2 * 2 * 2 * 2)
						+ (2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2);
			} else {
				valsx = 0;
			}
			aux1 = (float) (valsx + valsy) / 1024;
			aux2 = (float) (aux1 - 0.04);
			vals[i] = (float) (aux2 * 7.50061683 / 0.018);
			i++;
		}

	}

	// f(x) = x
	@Override
	public Number getX(int index) {
		return index;
	}

	// range begins at 0
	@Override
	public Number getMinX() {
		return 0;
	}

	// range ends at 9
	@Override
	public Number getMaxX() {
		return 10000;
	}

	@Override
	public String getTitle() {
		return "";
	}

	// range consists of all the values in vals
	@Override
	public int size() {
		return vals.length;
	}

	// return vals[index]
	@Override
	public Number getY(int index) {
		// make sure index isnt something unexpected:
		// if (index < 0 || index > 9) {
		// throw new IllegalArgumentException(
		// "Only values between 0 and 9 are allowed.");
		// }
		return vals[index];
	}

	// smallest value in vals is 0
	@Override
	public Number getMinY() {
		return 0;
	}

	// largest value in vals is 99
	@Override
	public Number getMaxY() {
		return 350;
	}
}
