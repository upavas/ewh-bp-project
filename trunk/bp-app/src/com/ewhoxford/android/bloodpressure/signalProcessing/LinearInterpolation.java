package com.ewhoxford.android.bloodpressure.signalProcessing;


/**
 * 
 * @author user
 * Interpolates (linearly) and get re-sampled signal (Similar to function'interp1' from MatLab - DOES NOT VALIDATE INPUTS)
 */
public class LinearInterpolation {

	/**
	 * Performs LINEAR INTERPOLATION
	 * @param xi
	 * @param yi
	 * @param x
	 * @return re-sampled array with the same dimensions as the argument3 'x'
	 * @see y
	 */
	public static double[] linearInterpolation(float[] xi, double[] yi, float[] x){
		
		// initialize variables
		double[] y = new double[x.length];
		float x0 = x[0];
		double aux1 = 0;
		double aux2 = 0;
		double y0 = 0;
		double y1 = yi[0];
		float x1 = xi[0];
		int j = 0;
		y[0] = 0;
		
		// LINEAR INTERPOLATION
		for (int i = 1; i < x.length; ++i){
			// auxiliary variables
			aux1 = (double) (x[i] - x0)/(x1 - x0);
			aux2 = (double) (y1 - y0)*aux1;
			
			// final result
			y[i] = (double) (y0 + aux2);
			
			// change boundaries
			if (x[i] == x1){
				if (j == xi.length - 1){     // last position is reached
					x0 = x1;
					x1 = x[x.length - 1];
					y0 = y1;
					y1 = 0;
				}
				else{						// change boundaries
					j = j + 1;
					x0 = x1;
					x1 = xi[j];
					y0 = y1;
					y1 = yi[j];
				}
			}
			Double y2 = null;
			try {
				 y2=new Double(y[i]);			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			if(y2.isNaN()||y2==null){
				y[i]=y[i];
						
			}
			
			if(Double.isNaN(y[i])){
				y[i]=y[i];
			}
				
		}
		
		// output
		return y;
	}
}
