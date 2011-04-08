package com.ewhoxford.android.bloodpressure;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ewhoxford.android.bloodpressure.model.BloodPressureMeasureModel;

public class BloodPressureAdapter extends BaseAdapter {

	private Context context;
	private List<BloodPressureMeasureModel> bloodPressureModelList;

	public BloodPressureAdapter(Context context,
			List<BloodPressureMeasureModel> bloodPressureModelList) {
		this.context = context;
		this.bloodPressureModelList = bloodPressureModelList;
	}

	public int getCount() {
		return bloodPressureModelList.size();
	}

	public Object getItem(int position) {
		return bloodPressureModelList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		BloodPressureMeasureModel bloodPressureM = bloodPressureModelList
				.get(position);
		return new MeasureListView(this.context, bloodPressureM);
	}

}
