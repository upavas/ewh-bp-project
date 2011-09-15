package org.ewhoxford.swt.bloodpressure.signalProcessing;

/**
 * 
 * @author mpimentel
 * Structure for sum operation results (over 2 arrays, X and Y) for exponential LSF
 */
public class SumsResults {

	/**
	 * sum result of xi
	 */
	double sum_x;
	
	/**
	 * sum result of yi
	 */
	double sum_y;
	
	/**
	 * sum result of xi*yi
	 */
	double sum_xy;
	
	/**
	 * sum result of xi*yi*lnyi
	 */
	double sum_xylny;
	
	/**
	 * sum result of yi*lnyi
	 */
	double sum_ylny;
	
	/**
	 * sum result of xi*xi*yi
	 */
	double sum_x2y;

	public double getSum_x() {
		return sum_x;
	}

	public void setSum_x(double sum_x) {
		this.sum_x = sum_x;
	}

	public double getSum_y() {
		return sum_y;
	}

	public void setSum_y(double sum_y) {
		this.sum_y = sum_y;
	}

	public double getSum_xy() {
		return sum_xy;
	}

	public void setSum_xy(double sum_xy) {
		this.sum_xy = sum_xy;
	}

	public double getSum_xylny() {
		return sum_xylny;
	}

	public void setSum_xylny(double sum_xylny) {
		this.sum_xylny = sum_xylny;
	}

	public double getSum_ylny() {
		return sum_ylny;
	}

	public void setSum_ylny(double sum_ylny) {
		this.sum_ylny = sum_ylny;
	}

	public double getSum_x2y() {
		return sum_x2y;
	}

	public void setSum_x2y(double sum_x2y) {
		this.sum_x2y = sum_x2y;
	}
	
}
