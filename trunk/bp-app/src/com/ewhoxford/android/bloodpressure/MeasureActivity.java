// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.ewhoxford.android.bloodpressure.database.BloodPressureMeasureTable.BPMeasure;
import com.ewhoxford.android.bloodpressure.model.BloodPressureValue;
import com.ewhoxford.android.bloodpressure.pressureInputDevice.TestDatasource;
import com.ewhoxford.android.bloodpressure.signalProcessing.SignalProcessing;
import com.ewhoxford.android.bloodpressure.signalProcessing.TimeSeriesMod;
import com.ewhoxford.android.bloodpressure.utils.FileManager;

/**
 * Class Measure : activity that pops when the user wants to start taking blood
 * pressure
 * 
 * @author mauro
 * 
 */
public class MeasureActivity extends Activity {

	private MeasureActivity measureContext = this;
	// plot that shows real time data
	private XYPlot bpMeasureXYPlot;
	// save measure button
	private Button saveButton;
	// Observer object that is notified by pressure data stream observable file
	private MyPlotUpdater plotUpdater;
	// Observable object that notifies observer that new values were acquired.
	private TestDatasource data;
	// pressure time series shown in the real time chart
	private SimpleXYSeries bpMeasureSeries = null;
	// array with time points
	private float[] arrayTime;
	// array with pressure points
	private double[] arrayPressure;
	// auxiliary variable to control measurement.
	private boolean maxPressureReached = false;
	// signal processing progress dialog
	private ProgressDialog myProgressDialog;
	// discard measure alert dialog
	private AlertDialog.Builder builder;
	// discard measure alert dialog
	private AlertDialog.Builder saveAlert;
	// Structure that holds blood pressure values result
	private BloodPressureValue bloodPressureValue;
	// Need handler for callbacks to the UI thread
	private final Handler mHandler = new Handler();
	// Checkbox to save measure csv file
	private CheckBox checkBox;
	// user notes
	private EditText notesText;
	// number of points in X axis
	public static int BOUNDARY_NUMBER_OF_POINTS = 100;
	// max pressure value for measure
	private int maxPressureValueForMeasure = 240;
	// signal frequency
	int signalFreq = 100;
	// measure finished
	private boolean measureFinished;
	// measure successfull
	private boolean measureSuccessful;

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
			valuesView.setSPressure(128);
			valuesView.setDPressure(76);
			valuesView.setPulseRate(78);
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

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable changeTextMessage = new Runnable() {
		public void run() {
			TextView textMessage = (TextView) findViewById(R.id.text_message);
			textMessage.setText(R.string.stop_pump);
			textMessage.setTextColor(Color.YELLOW);
			textMessage.postInvalidate();
		}
	};

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
			if (pressureValue > maxPressureValueForMeasure) {
				maxPressureReached = true;
				mHandler.post(changeTextMessage);
			}

			if (maxPressureReached) {
				// if max pressure reached, check if measurement is now over
				if (pressureValue < 20) {
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
		Button helpButton = (Button) findViewById(R.id.button_help);
		helpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(measureContext, HelpActivity.class);
				startActivity(i);

			}
		});

		Button discardButton = (Button) findViewById(R.id.button_discard);

		discardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.alert_dialog_discard_measure)
				.setCancelable(false).setPositiveButton(
						R.string.alert_dialog_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								MeasureActivity.this.finish();
							}
						}).setNegativeButton(R.string.alert_dialog_no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		saveButton = (Button) findViewById(R.id.button_save);
		saveButton.setEnabled(false);

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String savedFileName = "";
				String notes = "";

				if (notesText.getText().length() != 0) {
					notes = notesText.getText().toString();
				}

				Long time = System.currentTimeMillis();

				if (checkBox.isChecked()) {

					try {
						savedFileName = FileManager.saveFile(measureContext,
								bloodPressureValue, arrayPressure, arrayTime,
								time, notes);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						addNewMeasureAndFile(savedFileName, bloodPressureValue,
								notes, time);
					}

				} else {
					addNewMeasureAndFile(savedFileName, bloodPressureValue,
							notes, time);
				}
				AlertDialog alert = saveAlert.create();

				if (checkBox.isChecked()) {
					if (FileManager.checkExternalStorage()) {
						alert
								.setMessage(getResources()
										.getText(
												R.string.alert_dialog_data_saved_file_and_database));
					} else {
						alert.setMessage(getResources().getText(
								R.string.alert_dialog_data_saved_to_database));
					}
				} else {
					alert.setMessage(getResources().getText(
							R.string.alert_dialog_data_saved_to_database));
				}
				alert.show();
			}

		});
		saveAlert = new AlertDialog.Builder(this);
		saveAlert.setCancelable(false).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent i = new Intent(measureContext,
								MeasureListActivity.class);
						startActivity(i);
					}
				});

		// #### End of Set up click listeners for all the buttons

		// initialize our XYPlot reference and real time update code:

		// getInstance and position datasets:
		data = new TestDatasource(this);
		// SampleDynamicSeries signalSeries = new SampleDynamicSeries(data, 0,
		// "Blood Pressure");

		bpMeasureXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		// register plot with plot updater observer
		plotUpdater = new MyPlotUpdater(bpMeasureXYPlot);
		// freeze the range boundaries:
		bpMeasureXYPlot.setRangeBoundaries(0, 300, XYPlot.BoundaryMode.FIXED);

		bpMeasureXYPlot.setDomainBoundaries(0, BOUNDARY_NUMBER_OF_POINTS,
				XYPlot.BoundaryMode.FIXED);
		bpMeasureXYPlot
				.addSeries(bpMeasureSeries, LineAndPointRenderer.class,
						new LineAndPointFormatter(Color.rgb(100, 100, 200),
								Color.BLACK));

		List<Number> pressureListArray = new ArrayList<Number>();
		for (int i = 0; i < BOUNDARY_NUMBER_OF_POINTS; i++) {
			pressureListArray.add(maxPressureValueForMeasure);
		}

		SimpleXYSeries pressureLimit = new SimpleXYSeries(pressureListArray,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
		bpMeasureXYPlot.addSeries(pressureLimit, LineAndPointRenderer.class,
				new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.RED));
		bpMeasureXYPlot.setDomainStepValue(3);
		bpMeasureXYPlot.setTicksPerRangeLabel(3);
		bpMeasureXYPlot.setDomainLabel(getResources().getText(
				R.string.pressure_x_legend).toString());
		bpMeasureXYPlot.getDomainLabelWidget().pack();
		bpMeasureXYPlot.setRangeLabel(getResources().getText(
				R.string.pressure_y_legend).toString());
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

	/**
	 * Determination of BP and pulse algorithm
	 */
	private void startSignalProcessing() {

		myProgressDialog = ProgressDialog.show(MeasureActivity.this,
				getResources().getText(R.string.alert_dialog_processing_data),
				getResources().getText(R.string.alert_dialog_determine_bp),
				true);

		new Thread() {
			public void run() {

				int l = data.getBpMeasureHistory().size();
				arrayTime = new float[l];
				arrayPressure = new double[l];
				int i = 0;
				int fs = 100;
				while (i < l) {
					arrayPressure[i] = data.getBpMeasureHistory().get(i)
							.doubleValue();
					arrayTime[i] = ((float) i / (float) fs);
					i++;
				}

				TimeSeriesMod signal = new TimeSeriesMod();
				signal.setPressure(arrayPressure);
				signal.setTime(arrayTime);
				bloodPressureValue = new BloodPressureValue();
				SignalProcessing r = new SignalProcessing();

				try {
					// bloodPressureValue = r.signalProcessing(signal, fs);
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
			BloodPressureValue bloodPressureValue, String note, long time) {

		ContentResolver cr = getContentResolver();

		ContentValues values = new ContentValues();

		values.put(BPMeasure.CREATED_DATE, time);
		values.put(BPMeasure.MODIFIED_DATE, time);
		values.put(BPMeasure.DP, bloodPressureValue.getDiastolicBP());
		values.put(BPMeasure.SP, bloodPressureValue.getSystolicBP());
		// TODO correct this value @MARCO,
		// TODO put MAP VAlUE in database as a separate value,
		values.put(BPMeasure.PULSE, bloodPressureValue.getMeanArterialBP());
		values.put(BPMeasure.NOTE, note);
		values.put(BPMeasure.MEASUREMENT_SYNC, false);
		// values.put(BPMeasure., value)
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

	public boolean isMeasureFinished() {
		return measureFinished;
	}

	public boolean isMeasuresuccessful() {

		return measureSuccessful;
	}

	public void setMeasureFinished(boolean measureFinished) {
		this.measureFinished = measureFinished;
	}

	public void setMeasuresuccessful(boolean measureSuccessful) {
		this.measureSuccessful = measureSuccessful;
	}

}