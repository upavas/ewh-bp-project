package com.microchip.android.BasicUSBDeviceDemo;

/**
 * 
 * @author msantos
 * Convert digital in pressure units (mm Hg)
 */
public class ConvertTommHg {
	
		
	public static double convertTommHg(long x, long y) {

		float aux1 = 0, aux2 = 0;

		double pressureValue = 0;

		if (x < 0)
			x += 256;
		if (y < 0)
			y += 256;

		aux1 = (float) ((x << 8) | y) / (1024 * 5);

		aux2 = (float) ((3.72 * aux1) - 0.04);

		pressureValue = (double) (aux2 * 7.50061683 / (0.018));

		return pressureValue;

	}

}
