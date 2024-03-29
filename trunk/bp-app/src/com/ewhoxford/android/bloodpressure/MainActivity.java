package com.ewhoxford.android.bloodpressure;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * Main Blood pressure activity. When the app is launched, this activity runs,
 * allowing the user to either run a measure, or view saved blood pressure
 * measures.
 * 
 * @author mauro
 */
public class MainActivity extends Activity implements View.OnClickListener {
	public static final String TAG = MainActivity.class.toString();

	private MainActivity context = this;

	private static final int OPTION_SYNC = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		View newMeasure = findViewById(R.id.new_measure_button);
		newMeasure.setOnClickListener(this);

		View measureList = findViewById(R.id.measure_list);
		measureList.setOnClickListener(this);


		// Help button
		Button helpButton = (Button) findViewById(R.id.help_button);
		helpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, HelpActivity.class);
				startActivity(i);

			}
		});

		Process p;
		try {
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Code to run in input/output exception
			System.out.println("not root");
		}

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

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//
//		menu.add(0, OPTION_SYNC, 1, "Sync");
//
//		return true;
//	}

	private ProgressDialog progressDialog;

	private void doUpdateBloodPressureDatabase() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		progressDialog = new ProgressDialog(context);
		// TODO substitute this string for 1 string in values folder
		// (localization)
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

//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case OPTION_SYNC:
//			Intent i = new Intent(this, MeasureListSyncActivity.class);
//			startActivity(i);
//			return true;
//		}
//
//		return false;
//	}
}