// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.androidplot.Plot;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.ewhoxford.android.bloodpressure.signalProcessing.BloodPressureValues;
import com.ewhoxford.android.bloodpressure.signalProcessing.SignalProcessing;
import com.ewhoxford.android.bloodpressure.signalProcessing.TimeSeriesMod;
import com.ewhoxford.android.mouseInputDevice.MiceStreamActivityListener;
import com.ewhoxford.android.mouseInputDevice.SampleDynamicXYDatasource;

//Class Measure : activity that pops when the user wants to start taking blood pressure
public class Measure extends Activity {

	private static final int DIALOG_PROCESSING_SIGNAL_ID = 0;
	boolean saveFile = false;
	private XYPlot bpMeasureXYPlot;

	private MyPlotUpdater plotUpdater;
	int count = 0;
	MiceStreamActivityListener miceListener;
	SampleDynamicXYDatasource data;
	private SimpleXYSeries bpMeasureSeries = null;
	boolean maxPressureReached = false;
	boolean minPressureReached = false;
	ProgressDialog myProgressDialog;
	AlertDialog.Builder builder;
	BloodPressureValues bloodPressureValues;
	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();
	// Create runnable for posting
	final Runnable runSignalProcessing = new Runnable() {
		public void run() {
			startSignalProcessing();
		}
	};
	final Runnable updataBPResultView = new Runnable() {
		public void run() {
			int dPressure = (int) bloodPressureValues.getDiastolicBP();
			int sPressure = (int) bloodPressureValues.getSystolicBP();
			int pulse = (int) bloodPressureValues.getMeanArterialBP();
			ValuesView valuesView = (ValuesView) findViewById(R.id.results);
			valuesView.requestFocus();
			valuesView.setSPressure(sPressure);
			valuesView.setDPressure(dPressure);
			valuesView.setPulseRate(pulse);
			valuesView.invalidate();
		}
	};

	{
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

			if (pressureValue > 180)
				maxPressureReached = true;

			if (maxPressureReached)
				if (pressureValue < 20) {
					minPressureReached = true;
					// o.deleteObservers();
					data.setActive(false);

					mHandler.post(runSignalProcessing);
				} else {

					bpMeasureSeries.setModel(data.getBpMeasure(),
							SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
					try {
						plot.postRedraw();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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

		final EditText edittext = (EditText) findViewById(R.id.edittext);
		edittext.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					// Toast.makeText(HelloFormStuff.this, edittext.getText(),
					// Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

		final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
		checkbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks, depending on whether it's now
				// checked
				if (((CheckBox) v).isChecked()) {
					saveFile = true;
				}
			}
		});

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

		Button saveButton = (Button) findViewById(R.id.button_save);
		saveButton.setEnabled(false);

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				String fileLocation = "";

				if (saveFile) {

					boolean mExternalStorageAvailable = false;
					boolean mExternalStorageWriteable = false;
					String state = Environment.getExternalStorageState();

					if (Environment.MEDIA_MOUNTED.equals(state)) {
						// We can read and write the media
						mExternalStorageAvailable = mExternalStorageWriteable = true;
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY
							.equals(state)) {
						// We can only read the media
						mExternalStorageAvailable = true;
						mExternalStorageWriteable = false;
					} else {
						// Something else is wrong. It may be one of many other
						// states, but all we need
						// to know is we can neither read nor write
						mExternalStorageAvailable = mExternalStorageWriteable = false;
					}
					
					if(mExternalStorageAvailable && mExternalStorageWriteable){
//						int i=O;

					}

				}

			}
		});

		// #### End of Set up click listeners for all the buttons

		// initialize our XYPlot reference and real time update code:

		// getInstance and position datasets:
		data = new SampleDynamicXYDatasource();
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
				try {
					// Do some Fake-Work
					int l = data.getBpMeasure().size();
					float[] arrayTime = new float[l];
					double[] arrayPressure = new double[l];
					int i = 0;
					int fs = 100;
					while (i < l) {
						arrayPressure[i] = data.getBpMeasure().get(i)
								.doubleValue();
						arrayTime[i] = ((float) i / (float) fs);
						i++;
					}

					TimeSeriesMod signal = new TimeSeriesMod();
					signal.setPressure(arrayPressure);
					signal.setTime(arrayTime);
					bloodPressureValues = new BloodPressureValues();
					SignalProcessing r = new SignalProcessing();
					bloodPressureValues = r.signalProcessing(signal, fs);

				} catch (Exception e) {
				}
				// Dismiss the Dialog
				myProgressDialog.dismiss();
				mHandler.post(updataBPResultView);
			}
		}.start();

	}

}