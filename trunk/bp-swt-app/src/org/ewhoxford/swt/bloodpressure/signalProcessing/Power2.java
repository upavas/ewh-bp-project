package org.ewhoxford.swt.bloodpressure.signalProcessing;

public class Power2 {

	/**
	 * @param args
	 */
	public static int determine(int number) {
	
		long a = new Long(number).longValue();
		if (a < 0)
		{
		return 0;
		}
		String bits= Long.toBinaryString(a);
		int power;
		if (bits.indexOf("1") == bits.lastIndexOf("1"))
		{
		power = bits.length() - bits.indexOf("1") - 1;
		}
		else
		{
		power = bits.length() - bits.indexOf("1");
		}
		Double f=Math.pow(2,power);
		return f.intValue(); 
	}

}
