package com.ewhoxford.android.bloodpressure.signalProcessing;

/**
 *  
 * @author mpimentel
 * Get signal that results from the subtraction between the observation and the Fit model
 */
public class ExpFitting {

	/**
	 * Three steps method:
	 * First: Calculate best Fit Model parameters 'a' and 'b': f(x)=A*exp(x*B), where A = exp(a) and B = b
	 * Second: Determine Curve correspondent to the best Fit Model
	 * Third: Calculate subtraction between observed signal and best fit model curve in order to get the OSCILLATIONS
	 * @param signalin
	 * @return signal containing oscillations 
	 * @see signalout
	 */
	public TimeSeriesMod expFitting(TimeSeriesMod signalin){
		
		// initialize variables (including auxiliary variables)
		TimeSeriesMod signalout = new TimeSeriesMod();
		double a1, b1, aux1, aux2, aux3, aux4, aux5, aux6, A, B;
		double[] signalaux = new double[signalin.pressure.length];
		
		// calculate necessary array sums 
		SumsResults s = ArrayOperator.sumModified(signalin);
		
		// calculate parameter a
		aux1 = s.sum_x2y * s.sum_ylny;
		aux2 = s.sum_xy * s.sum_xylny;
		aux3 = s.sum_y * s.sum_x2y;
		aux4 = s.sum_xy * s.sum_xy;
		a1 = (aux1 - aux2)/(aux3 - aux4);
		
		// calculate parameter b
		aux1 = s.sum_y * s.sum_xylny;
		aux2 = s.sum_xy * s.sum_ylny;
		aux3 = s.sum_y * s.sum_x2y;
		aux4 = s.sum_xy * s.sum_xy;
		b1 = (aux1 - aux2)/(aux3 - aux4);
		
		// calculate A and B
		A = (double) Math.exp(a1);
		B = b1;
		
		// get oscillations
		for (int i = 0; i < signalin.pressure.length; ++i){
			aux5 = signalin.time[i]*B;
			aux6 = A * (double) Math.exp(aux5);
			signalaux[i] = signalin.pressure[i] - aux6;
		}
			
		// output
		signalout.setPressure(signalaux);
		signalout.setTime(signalin.time);
		
		return signalout;
	}
}
