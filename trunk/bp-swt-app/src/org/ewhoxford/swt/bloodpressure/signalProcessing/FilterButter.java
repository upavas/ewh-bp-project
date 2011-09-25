package org.ewhoxford.swt.bloodpressure.signalProcessing;

/**
 * 
 * @author mpimentel
 * Apply bandpass filter and get filtered signal
 */
public class FilterButter {
	
	/**
	 * A Low- and High-pass filters are applied based on the respective coefficients obtained in MatLab 
	 * @param signalin
	 * @return filtered signal
	 * @see signalout
	 */
	public TimeSeriesMod filterButter(TimeSeriesMod signalin){
		
		// initialize variables
		int l = signalin.pressure.length;
		TimeSeriesMod signalout = new TimeSeriesMod();
		double[] y = new double[l + 6];
		double[] x = new double[l + 6];
		double[] z = new double[l];
		double[] a = new double[7];
		double[] b = new double[7];
		double aux0, aux1, aux2, aux3, aux4, aux5, aux6;
		
		/**** LOW PASS FILTER ****/
		
		// define filter coefficients a and b
		aux0 = (double) (-3.33066907387547 * Math.pow(10, -16));
		a[0] = (double) 1.0; a[1] = (double) ((double) -1*5.715523948648149); a[2] = (double) (13.617800960004807);
		a[3] = (double) ((double) -1*17.312394973020645); a[4] = (double) (12.385827978528630); a[5] = (double) ((double) -1*4.728047449606683); 
		a[6] = (double) (0.752337571338128 + aux0);
		
		aux1 = (double) (4.440892098500626 * Math.pow(10, -16));
		b[0] = (double) (0.867373951267923 - aux1); b[1] = (double) ((double) -1*5.204243707607535); b[2] = (double) (13.010609269018840); 
		b[3] = (double) ((double) -1*17.347479025358453); b[4] = (double) (13.010609269018840); b[5] = (double) ((double) -1*5.204243707607535); 
		b[6] = (double) (0.867373951267923 - aux1);
		
		// introduce zeros in the beginning of the array
		for (int i = 0; i < x.length; ++i){
			if (i < 6){
				x[i] = 0.0;
				y[i] = 0.0;
			}
			else{
				x[i] = signalin.pressure[i-6];
				y[i] = signalin.pressure[i-6];
			}
		}
		
		// apply filter
		for (int n = 6; n < x.length; ++n){
			y[n] = b[0]*x[n] + b[1]*x[n-1] + b[2]*x[n-2] + b[3]*x[n-3] + b[4]*x[n-4] + b[5]*x[n-5] + b[6]*x[n-6] - a[1]*y[n-1] - a[2]*y[n-2] - a[3]*y[n-3] - a[4]*y[n-4] - a[5]*y[n-5] - a[6]*y[n-6];
		}
		

		/**** HIGH PASS FILTER ****/
		
		// update array: x = y
		for (int i = 0; i < x.length; ++i){
			x[i] = y[i];
		}
		
		// define filter coefficients a and b
		aux2 = (double) (2.220446049250313 * Math.pow(10, -16));
		a[0] = 1.0; a[1] = (double) ((double) -1*5.952585285236121); a[2] = (double) 14.764049196974659;
		a[3] = (double) ((double) -1*19.530327102659257); a[4] = (double) 14.532539146455779; a[5] = (double) ((double) -1*5.767367345591576); 
		a[6] = (double) (0.953691390059853 + aux2);
		
		aux3 = (double) (5.048709793414476 * Math.pow(10, -29));
		aux4 = (double) (3.129163594906004 * Math.pow(10, -13));
		aux5 = (double) (1.043054531635335 * Math.pow(10, -12));
		aux6 = (double) (4.038967834731580 * Math.pow(10, -28));
		b[0] = (double) (5.215272658176673 * Math.pow(10, -14)); b[1] = (double) (aux4 - aux3); b[2] = (double) (7.822908987265009 * Math.pow(10, -13)); 
		b[3] = (double) (aux5 - aux6); b[4] = (double) (7.822908987265009 * Math.pow(10, -13)); b[5] = (double) (aux4 - aux3); 
		b[6] = (double) (5.215272658176673 * Math.pow(10, -14));
		
		// apply filter and remove the zeros introduced before in the beginning of the array 
		for (int n = 6; n < x.length; ++n){
			y[n] = b[0]*x[n] + b[1]*x[n-1] + b[2]*x[n-2] + b[3]*x[n-3] + b[4]*x[n-4] + b[5]*x[n-5] + b[6]*x[n-6] - a[1]*y[n-1] - a[2]*y[n-2] - a[3]*y[n-3] - a[4]*y[n-4] - a[5]*y[n-5] - a[6]*y[n-6];
			z[n-6] = y[n];
		}
		
		/**** Compute FINAL variable ****/
		
		//output
		signalout.setPressure(z);
		signalout.setTime(signalin.time);
		
		return signalout;
	}
}
