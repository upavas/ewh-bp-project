package com.ewhoxford.android.bloodpressure;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ewhoxford.android.bloodpressure.model.BloodPressureMeasureModel;

class MeasureListView extends ListView {

	BloodPressureMeasureModel bloodPressureModel;

	// Constructor 1/3
	public MeasureListView(Context context) {
		// Parent's constructor
		super(context);

		// Initialization
		initMeasureListView(context, bloodPressureModel);
	}

	// Constructor 2/3
	public MeasureListView(Context context, AttributeSet attrs) {
		// Parent's constructor
		super(context, attrs);

		// Initialization
		initMeasureListView(context, bloodPressureModel);
	}

	// Constructor 3/3
	public MeasureListView(Context context, AttributeSet ats, int defaultStyle) {
		// Parent's constructor
		super(context, ats, defaultStyle);

		// Initialization
		initMeasureListView(context, bloodPressureModel);
	}

	private void initMeasureListView(Context context,
			BloodPressureMeasureModel bloodPressureModel2) {
		// this.setOrientation(HORIZONTAL);
		// created id column
		if (bloodPressureModel != null) {
			LinearLayout.LayoutParams idParams = new LinearLayout.LayoutParams(
					20, LayoutParams.WRAP_CONTENT);
			idParams.setMargins(1, 1, 1, 1);

			TextView id = new TextView(context);
			id.setText(Integer.toString(bloodPressureModel.id));
			id.setTextSize(14f);
			id.setTextColor(Color.WHITE);
			addView(id, idParams);

			// created date column
			LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
					100, LayoutParams.WRAP_CONTENT);
			dateParams.setMargins(1, 1, 1, 1);
			// created date column
			TextView createdDate = new TextView(context);
			createdDate.setText(bloodPressureModel.getCreatedDate());
			createdDate.setTextSize(14f);
			createdDate.setTextColor(Color.WHITE);
			addView(createdDate, dateParams);
			// created sp column
			LinearLayout.LayoutParams systolicParams = new LinearLayout.LayoutParams(
					20, LayoutParams.WRAP_CONTENT);
			systolicParams.setMargins(1, 1, 1, 1);

			TextView systolicPressure = new TextView(context);
			systolicPressure.setText(Integer
					.toString(bloodPressureModel.systolicPressure));
			systolicPressure.setTextSize(14f);
			systolicPressure.setTextColor(Color.WHITE);
			addView(systolicPressure, systolicParams);

			// created dp column
			LinearLayout.LayoutParams dyastolicParams = new LinearLayout.LayoutParams(
					20, LayoutParams.WRAP_CONTENT);
			dyastolicParams.setMargins(1, 1, 1, 1);

			TextView dyastolicPressure = new TextView(context);
			dyastolicPressure.setText(Integer
					.toString(bloodPressureModel.dyastolicPressure));
			dyastolicPressure.setTextSize(14f);
			dyastolicPressure.setTextColor(Color.WHITE);
			addView(dyastolicPressure, dyastolicParams);

			// created pulse column
			LinearLayout.LayoutParams pulseParams = new LinearLayout.LayoutParams(
					20, LayoutParams.WRAP_CONTENT);
			dyastolicParams.setMargins(1, 1, 1, 1);

			TextView pulse = new TextView(context);
			pulse.setText(Integer.toString(bloodPressureModel.pulse));
			pulse.setTextSize(14f);
			pulse.setTextColor(Color.WHITE);
			addView(pulse, pulseParams);

			// created note column
			LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
					20, LayoutParams.WRAP_CONTENT);
			noteParams.setMargins(1, 1, 1, 1);

			TextView note = new TextView(context);
			note.setText(bloodPressureModel.notes);
			note.setTextSize(14f);
			note.setTextColor(Color.WHITE);
			addView(note, noteParams);
		}
	}

	public MeasureListView(Context context,
			BloodPressureMeasureModel bloodPressureModel) {
		super(context);
		initMeasureListView(context, bloodPressureModel);
	}

	public BloodPressureMeasureModel getBpModel() {
		return bloodPressureModel;
	}

	public void setBpModel(BloodPressureMeasureModel bloodPressureModel) {
		this.bloodPressureModel = bloodPressureModel;
	}
}
