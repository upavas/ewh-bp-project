package com.ewhoxford.android.bloodpressure.signalProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * 
 * @author mpimentel
 * Read digital signal from CSV file (values are saved in a 2 column matrix) 
 */
public class ReadCSVfile {
	
	/**
	 * Read file from sdcard
	 * @return two column matrix with X and Y mouse displacements
	 * @see numbers
	 */
	public int[][] readCSV() {
		
		// initialize variable
		int[][] numbers = new int[10000][2];

		// get file from sdcard
		File file = new File("sdcard/bp-joao-1.csv");

		BufferedReader bufRdr;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		
			String line = null;
			int row = 0;
			int col = 0;

			// read each line of text file
			while ((line = bufRdr.readLine()) != null && row < 10000) {
				StringTokenizer st = new StringTokenizer(line, ",");
				while (st.hasMoreTokens()) {
					// get next token and store it in the array
					numbers[row][col] =new Integer(st.nextToken());
					col++;
				}
				col = 0;
				row++;
			}
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// output	
		return numbers;
	}
}
