package com.ewhoxford.android.bloodpressure.signalProcessing;

/**
 * 
 * @author mpimentel
 * Structure for pressure signal and time series
 */
public class TimeSeriesMod {
	
	/**
	 * pressure array
	 */
	double[] pressure;
	
	/**
	 * oscillations array 
	 */
	double[] oscill;

	/**
	 * time array (based on the sampling frequency)
	 */
	float[] time;

	public double[] getPressure() {
		return pressure;
	}

	public void setPressure(double[] pressure) {
		this.pressure = pressure;
	}

	public double[] getOscill() {
		return oscill;
	}

	public void setOscill(double[] oscill) {
		this.oscill = oscill;
	}
	
	public float[] getTime() {
		return time;
	}

	public void setTime(float[] time) {
		this.time = time;
	}
	
	

	
	
	
}
