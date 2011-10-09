package org.ewhoxford.swt.bloodpressure.exception;

public class NoDeviceException extends Exception {
	
	/**
	 * @author mauro
	 */
	private static final long serialVersionUID = 1L;

	public NoDeviceException(String tag) {
		// Log.i(tag, "External Storage not available, cannot save file.");
		System.out.println(tag
				+ ":No blood pressure device found. Please connect the device again.");
	}
	
}
