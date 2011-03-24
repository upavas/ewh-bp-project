package com.ewhoxford.android.bloodpressure.model;

/**
 * 
 * @author MD Santos
 * represents data coming from pressure sensor
 *
 */


public class PressureDataPoint {

	/**
	 * pressure point
	 */
	char[] mouseData;
	
	/**
	 * time of pressure point aquisition in miliseconds
	 */
	long time;
	
	
	public char[] getMouseData() {
		return mouseData;
	}
	public void setMouseData(char[] mouseData) {
		this.mouseData = mouseData;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}
