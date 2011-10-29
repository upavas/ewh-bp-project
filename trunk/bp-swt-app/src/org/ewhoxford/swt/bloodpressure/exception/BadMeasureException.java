package org.ewhoxford.swt.bloodpressure.exception;



public class BadMeasureException extends Exception {

	/**
	  * @author mauro
	 */
	private static final long serialVersionUID = 1L;
	
	public BadMeasureException(String tag) {
		System.out.println(tag+":Bad Measure. Repeat procedure.");
	}

}
