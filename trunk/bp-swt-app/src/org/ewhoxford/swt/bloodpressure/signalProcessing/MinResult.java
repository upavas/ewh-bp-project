package org.ewhoxford.swt.bloodpressure.signalProcessing;

/**
 * 
 * @author mpimentel
 * Structure for minimum and index of arrays
 */
public class MinResult {

	/**
	 * minimum value of array
	 */
	double min;
	
	/**
	 * index where minimum of array occurs
	 */
	float index;

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public float getIndex() {
		return index;
	}

	public void setIndex(float index) {
		this.index = index;
	}
	
}
