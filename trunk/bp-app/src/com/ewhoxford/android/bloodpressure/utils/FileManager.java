package com.ewhoxford.android.bloodpressure.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.ewhoxford.android.bloodpressure.exception.ExternalStorageNotAvailableException;
import com.ewhoxford.android.bloodpressure.model.BloodPressureValue;

/**
 * 
 * @author mauro
 */
public class FileManager {

	public static final String DIRECTORY = "com_ewhoxford_android_bloodpressure";

	public static String saveFile(Context context, BloodPressureValue values,
			double[] arrayPressure, float[] arrayTime, long createdDate,
			String note) throws ExternalStorageNotAvailableException {

		String fileName = "";

		String state = Environment.getExternalStorageState();
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other
			// states, but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			fileName = createExternalStoragePublicBPMeasureFile(context,
					values, arrayPressure, arrayTime, createdDate, note);
		} else {
			throw new ExternalStorageNotAvailableException(context.getClass()
					.getName());
		}

		return fileName;

	}

	public static boolean checkExternalStorage() {

		String state = Environment.getExternalStorageState();
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other
			// states, but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			return true;
		} else {
			return false;
		}

	}

	public static String createExternalStoragePublicBPMeasureFile(
			Context context, BloodPressureValue values, double[] arrayPressure,
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
		File path;
		String fileName = "bp_measure_" + createdDate + ".csv";
		File file;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
			path = Environment.getExternalStoragePublicDirectory(DIRECTORY);
			file = new File(path, fileName);
		} else {
			path = Environment.getExternalStorageDirectory();
			file = new File(path, "/" + DIRECTORY + "/" + fileName);
		}

		try {
			// Make sure the Pictures directory exists.
			path.mkdirs();

			FileWriter writer = new FileWriter(file);
			int dPressure = (int) values.getDiastolicBP();
			int sPressure = (int) values.getSystolicBP();
			int pulse = (int) values.getHeartRate();
			writer.append("Systolic_Pressure, Dyastolic_Pressure,Pulse, note\n");
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

			if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
				MediaScannerConnection.scanFile(context, new String[] { file
						.toString() }, new String[] { MimeTypeMap
						.getFileExtensionFromUrl(file.toString()) },
						new MediaScannerConnection.OnScanCompletedListener() {
							public void onScanCompleted(String path, Uri uri) {
								Log.i("ExternalStorage", "Scanned " + path
										+ ":");
								Log.i("ExternalStorage", "-> uri=" + uri);
							}
						});
			} else {

			}

		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.w("ExternalStorage", "Error writing " + file, e);
		}
		return fileName;
	}

	void deleteExternalStoragePublicFile(String fileName) {
		// Create a path where we will place our picture in the user's
		// public pictures directory and delete the file. If external
		// storage is not currently mounted this will fail.

		File path;
		File file;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
			path = Environment.getExternalStoragePublicDirectory(DIRECTORY);
			file = new File(path, fileName);
		} else {
			path = Environment.getExternalStorageDirectory();
			file = new File(path, "/" + DIRECTORY + "/" + fileName);
		}

		file.delete();
	}

	boolean hasExternalStoragePublicFile(String fileName) {
		// Create a path where we will place our picture in the user's
		// public pictures directory and check if the file exists. If
		// external storage is not currently mounted this will think the
		// picture doesn't exist.
		File path;

		File file;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
			path = Environment.getExternalStoragePublicDirectory(DIRECTORY);
			file = new File(path, fileName);
		} else {
			path = Environment.getExternalStorageDirectory();
			file = new File(path, "/" + DIRECTORY + "/" + fileName);
		}

		return file.exists();
	}

	public static String createVectors(String name, double[] arrayPressure)
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
		File path;
		File file;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
			path = Environment.getExternalStoragePublicDirectory(DIRECTORY);
			file = new File(path, name);
		} else {
			path = Environment.getExternalStorageDirectory();
			file = new File(path, "/" + DIRECTORY + "/" + name);
		}

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
			Log.w("ExternalStorage", "Error writing " + file, e);
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
		File path;
		File file;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
			path = Environment.getExternalStoragePublicDirectory(DIRECTORY);
			file = new File(path, name);
		} else {
			path = Environment.getExternalStorageDirectory();
			file = new File(path, "/" + DIRECTORY + "/" + name);
		}

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
			Log.w("ExternalStorage", "Error writing " + file, e);
		}
		return name;
	}
	
	
	public static String createVectors(String name, LinkedList<Number> arrayPressure)
			throws IllegalArgumentException {

		if (arrayPressure.size() == 0) {
			String detailMessage = "illegal argument in input";
			throw new IllegalArgumentException(detailMessage);
		}

		// Create a path where we will place our file in the user's
		// public pictures directory. Note that you should be careful about
		// what you place here, since the user often manages these files. For
		// pictures and other media owned by the application, consider
		// Context.getExternalMediaDir().
		// String uuid = UUID.randomUUID().toString();
		File path;
		File file;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO) {
			path = Environment.getExternalStoragePublicDirectory(DIRECTORY);
			file = new File(path, name);
		} else {
			path = Environment.getExternalStorageDirectory();
			file = new File(path, "/" + DIRECTORY + "/" + name);
		}

		try {
			// Make sure the Pictures directory exists.
			path.mkdirs();

			FileWriter writer = new FileWriter(file);
			writer.append("y=[");

			int i = 0;
			while (i < arrayPressure.size()) {

				if (i == arrayPressure.size()) {
					writer.append(arrayPressure.get(i) + "");
				}
				writer.append(arrayPressure.get(i) + ",");
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
			Log.w("ExternalStorage", "Error writing " + file, e);
		}
		return name;
	}

}
