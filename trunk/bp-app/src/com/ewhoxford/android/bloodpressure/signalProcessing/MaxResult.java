package com.ewhoxford.android.bloodpressure.signalProcessing;

/**
 * 
 * @author mpimentel
 * Structure for maximum and index of arrays
 */
public class MaxResult {
	
	/**
	 * maximum value of array
	 */
	double max;
	
	/**
	 * index where maximum of array occurs
	 */
	int index;

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
