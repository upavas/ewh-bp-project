package com.ewhoxford.android.bloodpressure.model;

import java.util.HashMap;

import com.ewhoxford.android.bloodpressure.database.BloodPressureMeasureTable.BPMeasure;

public class BloodPressure extends HashMap<Object, Object> {

	public BloodPressure(String id, String createdDate, String sp, String dp,
			String pulse, String phrProviderProfile) {
		super();
		this.id = id;
		this.created_date = createdDate;
		this.sp = sp;
		this.dp = dp;
		this.pulse = pulse;
		this.phr_provider_profile = phrProviderProfile;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String KEY_ID = BPMeasure._ID;
	public static String KEY_CREATED_DATE = BPMeasure.CREATED_DATE;
	public static String KEY_SP = BPMeasure.SP;
	public static String KEY_DP = BPMeasure.DP;
	public static String KEY_PULSE = BPMeasure.PULSE;
	public static String KEY_PHR_PROVIDER_PROFILE = BPMeasure.PHR_PROVIDER_PROFILE;

	private String id;
	private String created_date;
	private String sp;
	private String dp;
	private String pulse;
	private String phr_provider_profile;

	@Override
	public String get(Object k) {
		String key = (String) k;
		if (KEY_ID.equals(key))
			return id;
		else if (KEY_CREATED_DATE.equals(key))
			return created_date;
		else if (KEY_SP.equals(key))
			return sp;
		else if (KEY_DP.equals(key))
			return dp;
		else if (KEY_PULSE.equals(key))
			return pulse;
		else if (KEY_PHR_PROVIDER_PROFILE.equals(key))
			return phr_provider_profile;
		else
			return null;
	}

	public String getId() {
		return id;
	}

	public String getCreated_date() {
		return created_date;
	}

	public String getSp() {
		return sp;
	}

	public String getDp() {
		return dp;
	}

	public String getPulse() {
		return pulse;
	}

	public String getPhr_provider_profile() {
		return phr_provider_profile;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreated_date(String createdDate) {
		this.created_date = createdDate;
	}

	public void setSp(String sp) {
		this.sp = sp;
	}

	public void setDp(String dp) {
		this.dp = dp;
	}

	public void setPulse(String pulse) {
		this.pulse = pulse;
	}

	public void setPhr_provider_profile(String phrProviderProfile) {
		this.phr_provider_profile = phrProviderProfile;
	}
}
