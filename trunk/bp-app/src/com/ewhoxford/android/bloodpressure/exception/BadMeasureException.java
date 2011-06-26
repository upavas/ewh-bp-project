package com.ewhoxford.android.bloodpressure.exception;

import android.util.Log;

public class BadMeasureException extends Exception {

	/**
	  * @author mauro
	 */
	private static final long serialVersionUID = 1L;
	
	public BadMeasureException(String tag) {
		Log.i(tag, "Bad Measure. Repeat procedure.");
	}

}
