package com.ewhoxford.android.bloodpressure;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.ewhoxford.android.bloodpressure.model.BloodPressure;

public class SimpleBPAdapter extends SimpleAdapter {

	private int[] colors = new int[] { 0x30ffffff, 0x30808080 };

	private List<BloodPressure> bpList;

	@SuppressWarnings("unchecked")
	public SimpleBPAdapter(Context context,
			List<? extends Map<String, String>> items, int resource,
			String[] from, int[] to) {
		super(context, items, resource, from, to);
		this.bpList = (List<BloodPressure>) items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);
		return view;
	}

	@Override
	public void setViewBinder(ViewBinder viewBinder) {
		// TODO Auto-generated method stub
		super.setViewBinder(viewBinder);
	}

	
	
}
