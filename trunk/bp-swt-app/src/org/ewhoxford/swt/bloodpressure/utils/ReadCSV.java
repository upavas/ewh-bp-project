package org.ewhoxford.swt.bloodpressure.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


/**
 * class to read a csv pressure file and test the application
 * 
 * @author mauro
 * 
 */
public class ReadCSV {

//	public int[][] readCSV(Activity activity) {
//
//		// int[] x= new int[20000];
//		// int[] y=new int[20000];
//
//		int[][] numbers = new int[10000][2];
//
//		InputStream is = activity.getResources().openRawResource(R.raw.bp);
//		BufferedReader bufRdr;
//
//		try {
//			bufRdr = new BufferedReader(new InputStreamReader(is));
//
//			String line = null;
//			int row = 0;
//			int col = 0;
//
//			// read each line of text file
//			while ((line = bufRdr.readLine()) != null && row < 10000) {
//				StringTokenizer st = new StringTokenizer(line, ",");
//				while (st.hasMoreTokens()) {
//					// get next token and store it in the array
//					numbers[row][col] = new Integer(st.nextToken());
//					col++;
//				}
//				col = 0;
//				row++;
//			}
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// Structure s1=new Structure(x, y);
//
//		return numbers;
//	}

	public static int[][] readCSV(String directory, String fileName) {

		// int[] x= new int[20000];
		// int[] y=new int[20000];

		
		
		int[][] numbers = new int[10000][2];
		File file = new File(directory + "/" + fileName);
		//InputStream is = activity.getResources().openRawResource(R.raw.bp);
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
					numbers[row][col] = new Integer(st.nextToken());
					col++;
				}
				col = 0;
				row++;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Structure s1=new Structure(x, y);

		return numbers;
		
	
	}

}
