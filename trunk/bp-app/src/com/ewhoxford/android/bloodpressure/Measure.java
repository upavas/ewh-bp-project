// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.androidplot.Plot;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.ewhoxford.android.bloodpressure.BloodPressureMeasureTable.BPMeasure;
import com.ewhoxford.android.bloodpressure.exception.ExternalStorageNotAvailableException;
import com.ewhoxford.android.bloodpressure.model.BloodPressureValue;
import com.ewhoxford.android.bloodpressure.signalProcessing.SignalProcessing;
import com.ewhoxford.android.bloodpressure.signalProcessing.TimeSeriesMod;
import com.ewhoxford.android.pressureInputDevice.TestDatasource;

//Class Measure : activity that pops when the user wants to start taking blood pressure
public class Measure extends Activity {

	Measure measureContext = this;
	// save file option is false
	boolean saveFile = false;
	// plot that shows real time data
	private XYPlot bpMeasureXYPlot;
	// save measure button
	private Button saveButton;
	// Observer object that is notified by pressure data stream observable file
	private MyPlotUpdater plotUpdater;
	// Observable object that notifies observer that new values were acquired.
	TestDatasource data;
	// pressure time series shown in the real time chart
	private SimpleXYSeries bpMeasureSeries = null;
	// array with time points
	float[] arrayTime;
	// array with pressure points
	double[] arrayPressure;
	// auxiliary variable to control measurement.
	boolean maxPressureReached = false;
	// auxiliary variable to control measurement.
	boolean minPressureReached = false;
	// signal processing progress dialog
	ProgressDialog myProgressDialog;
	// discard measure alert dialgo
	AlertDialog.Builder builder;
	// Structure that holds blood pressure values result
	BloodPressureValue bloodPressureValue;
	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();
	// Checkbox to save measure csv file
	CheckBox checkBox;
	// user notes
	EditText notesText;

	// Create runnable for signal processing
	final Runnable runSignalProcessing = new Runnable() {
		public void run() {
			startSignalProcessing();
		}
	};
	final Runnable updataBPResultView = new Runnable() {

		public void run() {
			// Display blood pressure algorithm result in the Measure layout
			int dPressure = (int) bloodPressureValue.getDiastolicBP();
			int sPressure = (int) bloodPressureValue.getSystolicBP();
			int pulse = (int) bloodPressureValue.getMeanArterialBP();
			ValuesView valuesView = (ValuesView) findViewById(R.id.results);
			valuesView.requestFocus();
			valuesView.setSPressure(sPressure);
			valuesView.setDPressure(dPressure);
			valuesView.setPulseRate(pulse);
			valuesView.invalidate();
			// activate save button
			saveButton.setEnabled(true);
			saveButton.invalidate();

		}
	};

	{
		// initialized time series
		bpMeasureSeries = new SimpleXYSeries("");

	}

	private class MyPlotUpdater implements Observer, Callback {
		Plot plot;
		double pressureValue = 0;

		public MyPlotUpdater(Plot plot) {
			this.plot = plot;
		}

		@Override
		public void update(Observable o, Object arg) {

			pressureValue = data.getPressureValue();
			// check if operator has reached reasonable cuff pressure
			if (pressureValue > 180)
				maxPressureReached = true;

			if (maxPressureReached) {
				// if max pressure reached, check if measurement is now over
				if (pressureValue < 20) {
					// this will probably be erased.
					minPressureReached = true;
					// o.deleteObservers();
					data.setActive(false);
					// measurement is over, we are prepared to determine blood
					// pressure
					mHandler.post(runSignalProcessing);
				} else {
					updatePlot();
				}
			} else {
				updatePlot();
			}

		}

		private void updatePlot() {
			bpMeasureSeries.setModel(data.getBpMeasure(),
					SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
			try {
				plot.postRedraw();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public boolean handleMessage(Message arg0) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	// To be performed on the creation
	public void onCreate(Bundle savedInstanceState) {
		// Parent's method
		super.onCreate(savedInstanceState);

		// Layout
		setContentView(R.layout.measure);

		// #### Set up click listeners for all the buttons

		notesText = (EditText) findViewById(R.id.edittext);

		// initialize checkbox variable
		checkBox = (CheckBox) findViewById(R.id.checkbox);

		// Help button
		View HelpButton = findViewById(R.id.button_help);
		// HelpButton.setOnClickListener(this);

		Button discardButton = (Button) findViewById(R.id.button_discard);

		discardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to discard measure?")
				.setCancelable(false).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Measure.this.finish();
							}
						}).setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		saveButton = (Button) findViewById(R.id.button_save);
		saveButton.setEnabled(false);

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BPMeasureProvider mProvider = new BPMeasureProvider();
				String savedFileName = "";
				String notes = "";
				if (notesText.getText().length() != 0) {
					notes = notesText.getText().toString();
				}
				if (checkBox.isChecked()) {

					try {
						savedFileName = FileManager.saveFile(measureContext,
								bloodPressureValue, arrayPressure, arrayTime);
					} catch (ExternalStorageNotAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						addNewMeasureAndFile(savedFileName, bloodPressureValue,
								notes);
					}

				} else {
					addNewMeasureAndFile(savedFileName, bloodPressureValue,
							notes);
				}

			}

		});

		// #### End of Set up click listeners for all the buttons

		// initialize our XYPlot reference and real time update code:

		// getInstance and position datasets:
		data = new TestDatasource();
		// SampleDynamicSeries signalSeries = new SampleDynamicSeries(data, 0,
		// "Blood Pressure");

		bpMeasureXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		// register plot with plot updater observer
		plotUpdater = new MyPlotUpdater(bpMeasureXYPlot);
		// freeze the range boundaries:
		bpMeasureXYPlot.setRangeBoundaries(0, 300, XYPlot.BoundaryMode.FIXED);
		bpMeasureXYPlot
				.setDomainBoundaries(0, 12000, XYPlot.BoundaryMode.FIXED);
		bpMeasureXYPlot
				.addSeries(bpMeasureSeries, LineAndPointRenderer.class,
						new LineAndPointFormatter(Color.rgb(100, 100, 200),
								Color.BLACK));
		bpMeasureXYPlot.setDomainStepValue(3);
		bpMeasureXYPlot.setTicksPerRangeLabel(3);
		bpMeasureXYPlot.setDomainLabel("Time (s)");
		bpMeasureXYPlot.getDomainLabelWidget().pack();
		bpMeasureXYPlot.setRangeLabel("Pressure(mmHg)");
		bpMeasureXYPlot.getRangeLabelWidget().pack();
		bpMeasureXYPlot.disableAllMarkup();

		// hook up the plotUpdater to the data model:
		data.addObserver(plotUpdater);
		// start observable datasource thread
		new Thread(data).start();
	}

	// event : click on something
	public void onClick(View V) {
		// let's find what has been clicked
		switch (V.getId()) {
		// Start button
		case R.id.button_help:
			finish(); // kill this activity, so going back to the previous one
			break;

		}
	}

	void printSamples(MotionEvent ev) {
		final int historySize = ev.getHistorySize();
		final int pointerCount = ev.getPointerCount();
		for (int h = 0; h < historySize; h++) {
			System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
			for (int p = 0; p < pointerCount; p++) {
				System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p),
						ev.getHistoricalX(p, h), ev.getHistoricalY(p, h));
			}
		}
		System.out.printf("At time %d:", ev.getEventTime());
		for (int p = 0; p < pointerCount; p++) {
			System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p), ev
					.getX(p), ev.getY(p));
		}
	}

	private void startSignalProcessing() {

		myProgressDialog = ProgressDialog.show(Measure.this,
				"Processing Data...", "Determining Blood Pressure...", true);

		new Thread() {
			public void run() {

				// Do some Fake-Work
				int l = data.getBpMeasure().size();
				float[] arrayTime = new float[l];
				double[] arrayPressure = new double[l];
				int i = 0;
				int fs = 100;
				while (i < l) {
					arrayPressure[i] = data.getBpMeasure().get(i).doubleValue();
					arrayTime[i] = ((float) i / (float) fs);
					i++;
				}

				TimeSeriesMod signal = new TimeSeriesMod();
				signal.setPressure(arrayPressure);
				signal.setTime(arrayTime);
				bloodPressureValue = new BloodPressureValue();
				SignalProcessing r = new SignalProcessing();

				try {
					bloodPressureValue = r.signalProcessing(signal, fs);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					myProgressDialog.dismiss();
					mHandler.post(updataBPResultView);
				}

				// Dismiss the Dialog
				myProgressDialog.dismiss();
				mHandler.post(updataBPResultView);
			}
		}.start();

	}

	/**
	 * Add new blood pressure result to database and create file in sdcard
	 * 
	 * @param savedFileName
	 * @param bloodPressureValue
	 * @param note
	 */
	private void addNewMeasureAndFile(String savedFileName,
			BloodPressureValue bloodPressureValue, String note) {

		ContentResolver cr = getContentResolver();

		ContentValues values = new ContentValues();

		Long time = System.currentTimeMillis();
		values.put(BPMeasure.CREATED_DATE, time);
		values.put(BPMeasure.MODIFIED_DATE, time);
		values.put(BPMeasure.DP, bloodPressureValue.getDiastolicBP());
		values.put(BPMeasure.SP, bloodPressureValue.getSystolicBP());
		// correct this value @MARCO
		values.put(BPMeasure.PULSE, bloodPressureValue.getMeanArterialBP());
		values.put(BPMeasure.NOTE, note);
		values.put(BPMeasure.MEASUREMENT_FILE_SYNC, false);
		if (savedFileName != null) {
			if (savedFileName.length() == 0) {
				values.put(BPMeasure.MEASUREMENT_FILE_EXIST, false);
			} else {
				values.put(BPMeasure.MEASUREMENT_FILE_EXIST, true);
				values.put(BPMeasure.MEASUREMENT_FILE, savedFileName);

			}

		}

		cr.insert(BPMeasure.CONTENT_URI, values);
	}

}