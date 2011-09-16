package org.ewhoxford.swt.bloodpressure.exception;

public class ExternalStorageNotAvailableException extends Exception {

	/**
	 * @author mauro
	 */
	private static final long serialVersionUID = 1L;

	public ExternalStorageNotAvailableException(String tag) {
		// Log.i(tag, "External Storage not available, cannot save file.");
		System.out.println(tag
				+ ":External Storage not available, cannot save file.");
	}

}
