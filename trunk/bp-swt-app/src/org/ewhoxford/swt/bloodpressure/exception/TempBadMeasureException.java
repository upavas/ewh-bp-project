package org.ewhoxford.swt.bloodpressure.exception;

public class TempBadMeasureException extends Exception {

	/**
	 * @author mauro
	 */
	private static final long serialVersionUID = 1L;

	public TempBadMeasureException(String tag) {
		// Log.i(tag, "Bad Measure. Repeat procedure.");
		System.out.println(tag + ":Bad Measure. Repeat procedure.");
	}

}
