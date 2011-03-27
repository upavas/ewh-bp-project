// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.androidplot.Plot;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.ewhoxford.android.mouseInputDevice.MiceStreamActivityListener;
import com.ewhoxford.android.mouseInputDevice.SampleDynamicXYDatasource;

//Class Measure : activity that pops when the user wants to start taking blood pressure
public class Measure extends Activity {

	private static final int HISTORY_SIZE = 500;

	boolean saveFile = false;
	private XYPlot bpMeasureXYPlot;
	private LinkedList<Number> bpMeasureHistory;
	private MyPlotUpdater plotUpdater;
	int count = 0;
	MiceStreamActivityListener miceListener;
	SampleDynamicXYDatasource data;
	private SimpleXYSeries bpMeasureSeries = null;
	boolean maxPressureReached = false;
	boolean minPressureReached = false;
	ProgressDialog myProgressDialog;

	{
		bpMeasureHistory = new LinkedList<Number>();
		bpMeasureSeries = new SimpleXYSeries("");

	}

	private class MyPlotUpdater implements Observer {
		Plot plot;
		float pressureValue = 0;

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
					o.deleteObservers();
					startSignalProcessing();
				}

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

		// miceListener = new MiceStreamActivityListener();
		// miceListener.addObserver(this);
		// Timer updateTimer = new Timer("real time pressure");
		// updateTimer.scheduleAtFixedRate(new TimerTask() {
		// @Override
		// public void run() {
		// updateGui();
		// }
		// }, 0, 30);

		// #### End of char real time update code

		// kick off the data generating thread:
		new Thread(data).start();
	}

	// private void updateGui() {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	//
	// // bpMeasureHistory.add(100);
	// bpMeasureSeries.setModel(bpMeasureHistory,
	// SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
	// bpMeasureXYPlot.redraw();
	// }
	// });
	// }

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

		myProgressDialog = ProgressDialog.show(Measure.this, "Please wait...",
				"Doing Extreme Calculations...", true);

		new Thread() {
			public void run() {
				try {
					// Do some Fake-Work
					sleep(400);

					ValuesView valuesView = (ValuesView) findViewById(R.id.results);
					valuesView.requestFocus();
					valuesView.setSPressure(180);
					valuesView.setDPressure(40);
					valuesView.setPulseRate(80);
					valuesView.invalidate();

				} catch (Exception e) {
				}
				// Dismiss the Dialog
				myProgressDialog.dismiss();
			}
		}.start();

	}

}