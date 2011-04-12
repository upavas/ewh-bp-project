package com.ewhoxford.android.bloodpressure;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Main Blood pressure activity. When the app is launched, this activity runs,
 * allowing the user to either run a measure, or view saved blood pressure
 * measures.
 * 
 * @author mauro
 */
public class MainActivity extends Activity implements View.OnClickListener {
	public static final String TAG = MainActivity.class.toString();

	// Option menu codes
	private static final int OPTION_SETTINGS = 1;
	private static final int OPTION_SYNC = 2;

	// Activity request codes
	public static final int SETTINGS = 6;

	private MainActivity context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		View newMeasure = findViewById(R.id.new_measure_button);
		newMeasure.setOnClickListener(this);

		View measureList = findViewById(R.id.measure_list);
		measureList.setOnClickListener(this);

	}

	private void newMeasure() {
		Intent i = new Intent(this, MeasureActivity.class);
		startActivity(i);
	}

	private void measuresList() {
		Intent i = new Intent(this, MeasureListActivity.class);
		startActivity(i);
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.new_measure_button:
			newMeasure();
			break;
		case R.id.measure_list:
			measuresList();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, OPTION_SETTINGS, 1, "Settings");
		menu.add(0, OPTION_SYNC, 2, "Sync");
		return true;
	}

	private ProgressDialog progressDialog;

	private void doUpdateBloodPressureDatabase() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Updating Blood Pressure database.");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();

		new Thread() {
			public void run() {
				// TODO: syng with blood pressure database
				// try {
				// MocaUtil.updateProcedureDatabase(context,
				// getContentResolver());
				// } catch (APIException e) {
				// // TODO(XXX) check error code
				// Log.e(TAG, "updateProcedureDatabase threw APIException "
				// + e);
				// showDialog(DIALOG_NO_CONNECTIVITY);
				// } finally {
				// if (progressDialog != null) {
				// progressDialog.dismiss();
				// progressDialog = null;
				// }
				// }
			}
		}.start();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTION_SETTINGS:
			Intent i = new Intent(Intent.ACTION_PICK);
			i.setClass(this, Settings.class);
			startActivityForResult(i, SETTINGS);
			return true;
		case OPTION_SYNC:
			doUpdateBloodPressureDatabase();
			return true;
		}

		return false;
	}
}