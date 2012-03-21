package org.ewhoxford.swt.bloodpressure.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ewhoxford.swt.bloodpressure.exception.ExternalStorageNotAvailableException;
import org.ewhoxford.swt.bloodpressure.model.BloodPressureValue;
import org.ewhoxford.swt.bloodpressure.ui.BPMainWindow;

/**
 * 
 * @author mauro
 */
public class FileManager {

	static String userHome = System.getProperty("user.home");
	static String directory = BPMainWindow.getResourceString("Directory");

	public static final String DIRECTORY = userHome + "/" + directory + "/";

	public static String saveFile(BloodPressureValue values,
			double[] arrayPressure, float[] arrayTime, long createdDate,
			String note) throws ExternalStorageNotAvailableException {

		String fileName = "";

		fileName = createExternalStoragePublicBPMeasureFile(values,
				arrayPressure, arrayTime, createdDate, note);
		return fileName;

	}

	public static String createExternalStoragePublicBPMeasureFile(
			BloodPressureValue values, double[] arrayPressure,
			float[] arrayTime, long createdDate, String note)
			throws IllegalArgumentException {

		if (arrayPressure.length == 0 || arrayTime.length == 0) {
			String detailMessage = "illegal argument in input";
			throw new IllegalArgumentException(detailMessage);
		}

		// Create a path where we will place our file in the user's
		// public pictures directory. Note that you should be careful about
		// what you place here, since the user often manages these files. For
		// pictures and other media owned by the application, consider
		// Context.getExternalMediaDir().
		// String uuid = UUID.randomUUID().toString();
		File path = new File(DIRECTORY);
		String fileName = "bp_measure_" + createdDate + ".csv";
		File file = new File(DIRECTORY + "/" + fileName);

		try {
			// Make sure the Pictures directory exists.
			path.mkdirs();

			FileWriter writer = new FileWriter(file);
			int dPressure = (int) values.getDiastolicBP();
			int sPressure = (int) values.getSystolicBP();
			int pulse = (int) values.getHeartRate();
			writer
					.append("Systolic_Pressure, Dyastolic_Pressure,Pulse, note\n");
			writer.append(sPressure + "," + dPressure + "," + pulse + ","
					+ note + "\n");
			writer.append("time,pressure(mmHg)\n");

			int i = 0;
			while (i < arrayPressure.length) {

				writer.append(arrayTime[i] + "," + arrayPressure[i] + "\n");
				i = i + 1;
			}

			// generate whatever data you want

			writer.flush();
			writer.close();

			// // Tell the media scanner about the new file so that it is
			// // immediately available to the user.

		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			e.printStackTrace();
		}
		return fileName;
	}

	void deleteExternalStoragePublicFile(String fileName) {
		// Create a path where we will place our picture in the user's
		// public pictures directory and delete the file. If external
		// storage is not currently mounted this will fail.
		File file = new File(fileName);

		file.delete();
	}

	boolean hasExternalStoragePublicFile(String fileName) {
		// Create a path where we will place our picture in the user's
		// public pictures directory and check if the file exists. If
		// external storage is not currently mounted this will think the
		// picture doesn't exist.

		File file = new File(fileName);

		return file.exists();
	}

	public static String createVectors(String name, double[] arrayPressure)
			throws IllegalArgumentException {

		if (arrayPressure.length == 0) {
			String detailMessage = "illegal argument in input";
			throw new IllegalArgumentException(detailMessage);
		}

		File path = new File(DIRECTORY);
		File file = new File(DIRECTORY + "/" + name);

		try {
			// Make sure the Pictures directory exists.
			path.mkdirs();

			FileWriter writer = new FileWriter(file);
			writer.append("y=[");

			int i = 0;
			while (i < arrayPressure.length) {

				if (i == arrayPressure.length) {
					writer.append(arrayPressure[i] + "");
				}
				writer.append(arrayPressure[i] + ",");
				i = i + 1;
			}
			writer.append("];");
			// generate whatever data you want

			writer.flush();
			writer.close();

			// // Tell the media scanner about the new file so that it is
			// // immediately available to the user.

			// if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
			// MediaScannerConnection.scanFile(context, new String[] { file
			// .toString() }, new String[] { MimeTypeMap
			// .getFileExtensionFromUrl(file.toString()) },
			// new MediaScannerConnection.OnScanCompletedListener() {
			// public void onScanCompleted(String path, Uri uri) {
			// Log.i("ExternalStorage", "Scanned " + path + ":");
			// Log.i("ExternalStorage", "-> uri=" + uri);
			// }
			// });
			// } else{
			//
			//
			// }

		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			e.printStackTrace();

		}
		return name;
	}

	public static String createVectors(String name, float[] arrayPressure)
			throws IllegalArgumentException {

		if (arrayPressure.length == 0) {
			String detailMessage = "illegal argument in input";
			throw new IllegalArgumentException(detailMessage);
		}

		// Create a path where we will place our file in the user's
		// public pictures directory. Note that you should be careful about
		// what you place here, since the user often manages these files. For
		// pictures and other media owned by the application, consider
		// Context.getExternalMediaDir().
		// String uuid = UUID.randomUUID().toString();
		File path = new File(DIRECTORY);
		File file = new File(DIRECTORY + "/" + name);

		try {
			// Make sure the Pictures directory exists.
			path.mkdirs();

			FileWriter writer = new FileWriter(file);
			writer.append("y=[");

			int i = 0;
			while (i < arrayPressure.length) {

				if (i == arrayPressure.length) {
					writer.append(arrayPressure[i] + "");
				}
				writer.append(arrayPressure[i] + ",");
				i = i + 1;
			}
			writer.append("];");
			// generate whatever data you want

			writer.flush();
			writer.close();

			// // Tell the media scanner about the new file so that it is
			// // immediately available to the user.

			// if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
			// MediaScannerConnection.scanFile(context, new String[] { file
			// .toString() }, new String[] { MimeTypeMap
			// .getFileExtensionFromUrl(file.toString()) },
			// new MediaScannerConnection.OnScanCompletedListener() {
			// public void onScanCompleted(String path, Uri uri) {
			// Log.i("ExternalStorage", "Scanned " + path + ":");
			// Log.i("ExternalStorage", "-> uri=" + uri);
			// }
			// });
			// } else{
			//
			//
			// }

		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			e.printStackTrace();
		}
		return name;
	}

}
