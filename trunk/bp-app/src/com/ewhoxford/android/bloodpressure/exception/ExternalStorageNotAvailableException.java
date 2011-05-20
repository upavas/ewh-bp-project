package com.ewhoxford.android.bloodpressure.exception;

import android.util.Log;

public class ExternalStorageNotAvailableException extends Exception {

	/**
	  * @author mauro
	 */
	private static final long serialVersionUID = 1L;
	
	public ExternalStorageNotAvailableException(String tag) {
		Log.i(tag, "External Storage not available, cannot save file.");
	}

}
