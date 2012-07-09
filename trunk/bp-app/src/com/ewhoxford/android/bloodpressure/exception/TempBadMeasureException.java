package com.ewhoxford.android.bloodpressure.exception;

import android.util.Log;

public class TempBadMeasureException extends Exception {

	/**
	  * @author mauro
	 */
	private static final long serialVersionUID = 1L;
	
	public TempBadMeasureException(String tag) {
		Log.i(tag, "Bad Measure. Repeat procedure.");
	}

}
