package com.ewhoxford.android.bloodpressure.signalProcessing;

import com.androidplot.series.XYSeries;

public class SimpleXYSeries implements XYSeries {
	
	private final BloodPressureValues vals;
	
	public SimpleXYSeries(int[][] vals2, int fs) {
		
		// call signal processing routine 
	    SignalProcessing r = new SignalProcessing();
	    vals = r.signalProcessing(vals2, fs);
	    
	}

	// f(x) = x
	@Override
	public Number getX(int index) {
		return vals.timeSignal[index];
	}

	// domain begins at 0
	@Override
	public Number getMinX() {
		return vals.timeSignal[0];
	}

	// domain ends at the end of time vector
	@Override
	public Number getMaxX() {
		return vals.timeSignal[vals.timeSignal.length-1];
	}

	@Override
	public String getTitle() {
		return "";
	}

	// range consists of all the values in vals
	@Override
	public int size() {
		return vals.pressureSignal.length;
	}

	/*
	@Override
	public void onReadBegin() {

	}

	@Override
	public void onReadEnd() {

	}
	 */
	// return vals[index]
	@Override
	public Number getY(int index) {
		// make sure index isnt something unexpected:
		// if (index < 0 || index > 9) {
		// throw new IllegalArgumentException(
		// "Only values between 0 and 9 are allowed.");
		// }
		return vals.pressureSignal[index];
	}

	// smallest value in vals is 0
	@Override
	public Number getMinY() {
		MinResult minval = ArrayOperator.minValue(vals.pressureSignal);
		return minval.min;
	}

	// largest value in vals is 99
	@Override
	public Number getMaxY() {
		MaxResult maxval = ArrayOperator.maxValue(vals.pressureSignal);
		return maxval.max;
	}
}



/*

android:id="@+id/results" android:layout_width="fill_parent"
			android:layout_height="110dip" android:layout_marginTop="10dip"
			android:layout_marginBottom="25dip" android:layout_marginLeft="50dip"
			android:layout_marginRight="50dip" android:layout_below="@+id/graph" /> 
			*/
