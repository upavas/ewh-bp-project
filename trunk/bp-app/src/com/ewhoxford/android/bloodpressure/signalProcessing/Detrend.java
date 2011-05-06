package com.ewhoxford.android.bloodpressure.signalProcessing;

import Jama.Matrix;

/**
 * 
 * @author mpimentel
 * Get detrended signal (eliminate DC component) 
 */

public class Detrend {
	
	public TimeSeriesMod detrend(TimeSeriesMod signalin){
		TimeSeriesMod signalout = new TimeSeriesMod();
		double[] signalaux = new double[signalin.pressure.length];
		double[][] a = new double[signalin.pressure.length][2]; 
		double[][] x = new double[signalin.pressure.length][1];
		
		// build regressor with linear pieces + DC
		for (int i = 0; i < signalin.pressure.length - 1; ++i){
			a[i][0] = 1/signalin.pressure.length;
			a[i][1] = 1;
			x[i][0] = signalin.pressure[i];
		}
		// definition of matrices
		Matrix A = new Matrix(a);
		Matrix X = new Matrix(x);
		Matrix PA = A.inverse();
		Matrix Y = X.minus(A.times(PA.times(X))); //remove the best fit
		// get array from matrix
		double[][] y = Y.getArray();
		for (int i = 0; i < signalin.pressure.length - 1; ++i){
			signalaux[i] = y[i][0];
		}
		
		signalout.setPressure(signalaux);
		signalout.setTime(signalin.time);		
		return signalout;
	}
}
