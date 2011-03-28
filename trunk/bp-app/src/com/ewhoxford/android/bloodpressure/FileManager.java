package com.ewhoxford.android.bloodpressure;

import java.io.File;

import android.os.Environment;

public class FileManager {

	public boolean saveFile() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

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

		}

		return true;

	}

	void createExternalStoragePublicPicture() {
		// Create a path where we will place our picture in the user's
		// public pictures directory. Note that you should be careful about
		// what you place here, since the user often manages these files. For
		// pictures and other media owned by the application, consider
		// Context.getExternalMediaDir().
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(path, "DemoPicture.jpg");

		// try {
		// // Make sure the Pictures directory exists.
		// path.mkdirs();

			// Very simple code to copy a picture from the application's
			// resource into the external file. Note that this code does
			// no error checking, and assumes the picture is small (does not
			// try to copy it in chunks). Note that if external storage is
			// not currently mounted this will silently fail.
//			InputStream is = getResources()
//					.openRawResource(R.drawable.balloons);
//			OutputStream os = new FileOutputStream(file);
//			byte[] data = new byte[is.available()];
//			is.read(data);
//			os.write(data);
//			is.close();
//			os.close();
//
//			// Tell the media scanner about the new file so that it is
//			// immediately available to the user.
//			MediaScannerConnection.scanFile(this, new String[] { file
//					.toString() }, null,
//					new MediaScannerConnection.OnScanCompletedListener() {
//						public void onScanCompleted(String path, Uri uri) {
//							Log.i("ExternalStorage", "Scanned " + path + ":");
//							Log.i("ExternalStorage", "-> uri=" + uri);
//						}
//					});
//		} catch (IOException e) {
//			// Unable to create file, likely because external storage is
//			// not currently mounted.
//			Log.w("ExternalStorage", "Error writing " + file, e);
//		}
	}

	void deleteExternalStoragePublicPicture() {
		// Create a path where we will place our picture in the user's
		// public pictures directory and delete the file. If external
		// storage is not currently mounted this will fail.
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(path, "DemoPicture.jpg");
		file.delete();
	}

	boolean hasExternalStoragePublicPicture() {
		// Create a path where we will place our picture in the user's
		// public pictures directory and check if the file exists. If
		// external storage is not currently mounted this will think the
		// picture doesn't exist.
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(path, "DemoPicture.jpg");
		return file.exists();
	}

}
