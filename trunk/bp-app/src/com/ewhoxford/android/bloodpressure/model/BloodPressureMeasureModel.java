package com.ewhoxford.android.bloodpressure.model;

import java.text.SimpleDateFormat;

public class BloodPressureMeasureModel {

	public int systolicPressure;
	public int dyastolicPressure;
	public int pulse;
	public String notes;
	public String createdDate;
	public int id;

	public BloodPressureMeasureModel(int pulse, int systolicPressure, int dyastolicPressure,
			String notes, int createdDate, int id) {
		super();
		this.systolicPressure = systolicPressure;
		this.dyastolicPressure = dyastolicPressure;
		this.notes = notes;

		java.text.DateFormat dateFormat = SimpleDateFormat
				.getDateInstance(SimpleDateFormat.SHORT);
		this.createdDate = dateFormat.format(createdDate);
		this.id = id;
	}

	public int getSystolicPressure() {
		return systolicPressure;
	}

	public void setSystolicPressure(int systolicPressure) {
		this.systolicPressure = systolicPressure;
	}

	public int getDyastolicPressure() {
		return dyastolicPressure;
	}

	public void setDyastolicPressure(int dyastolicPressure) {
		this.dyastolicPressure = dyastolicPressure;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(int createdDate) {

		java.text.DateFormat dateFormat = SimpleDateFormat
				.getDateInstance(SimpleDateFormat.SHORT);
		this.createdDate = dateFormat.format(createdDate);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPulse() {
		return pulse;
	}

	public void setPulse(int pulse) {
		this.pulse = pulse;
	}

}