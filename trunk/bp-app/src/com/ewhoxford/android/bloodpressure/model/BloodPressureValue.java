package com.ewhoxford.android.bloodpressure.model;

/**
 * 
 * @author mpimentel Final structure: includes the BP signal, time series, and
 *         the values for MAP, SP and DP
 */
public class BloodPressureValue {

	/**
	 * pressure array - important part of the signal
	 */
	double[] pressureSignal;

	/**
	 * time array (based on the sampling frequency)
	 */
	float[] timeSignal;

	/**
	 * Value of systolic BP
	 */
	double systolicBP;

	/**
	 * Value of diastolic BP
	 */
	double diastolicBP;

	/**
	 * Value of mean arterial pressure
	 */
	double meanArterialBP;

	public double[] getPressureSignal() {
		return pressureSignal;
	}

	public void setPressureSignal(double[] pressureSignal) {
		this.pressureSignal = pressureSignal;
	}

	public float[] getTimeSignal() {
		return timeSignal;
	}

	public void setTimeSignal(float[] timeSignal) {
		this.timeSignal = timeSignal;
	}

	public double getSystolicBP() {
		return systolicBP;
	}

	public void setSystolicBP(double systolicBP) {
		this.systolicBP = systolicBP;
	}

	public double getDiastolicBP() {
		return diastolicBP;
	}

	public void setDiastolicBP(double diastolicBP) {
		this.diastolicBP = diastolicBP;
	}

	public double getMeanArterialBP() {
		return meanArterialBP;
	}

	public void setMeanArterialBP(double meanArterialBP) {
		this.meanArterialBP = meanArterialBP;
	}

}
