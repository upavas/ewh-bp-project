// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.ewhoxford.android.bloodpressure.database.BloodPressureMeasureTable.BPMeasure;
import com.ewhoxford.android.bloodpressure.model.BloodPressureValue;
import com.ewhoxford.android.bloodpressure.pressureInputDevice.TestDemoCustomHID;
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
	// private TestDatasource data;
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
	private final Handler messageHandler = new Handler();
	// Checkbox to save measure csv file
	private CheckBox checkBox;
	// user notes
	private EditText notesText;
	// number of points in X axis
	public static int BOUNDARY_NUMBER_OF_POINTS = 100;
	// max pressure value for measure
	private int maxPressureValueForMeasure = 140;
	// min pressure value for measure
	private double minPressureReached = 35;
	// signal frequency
	int signalFreq = 100;
	// measure finished
	private boolean measureFinished;
	// measure successfull
	private boolean measureSuccessful;
	// measure size
	private int measureSize = 0;
	// event count
	private int totalCount = 0;
	// event count
	private boolean testMode = true;
	TestDemoCustomHID demo = null;
	PendingIntent pendingIntent = null;

	private PowerManager.WakeLock wl;

	LinkedList<Number> plotData = new LinkedList<Number>();
	double pressureValue = 0;

	// redraws a plot whenever an update is received:
	private class MyPlotUpdater implements Observer {
		Plot plot;

		public MyPlotUpdater(Plot plot) {
			this.plot = plot;
		}

		@Override
		public void update(Observable o, Object arg) {
			plotData.add(demo.getPressureValue());

			if (plotData.size() == measureSize
					&& measureSize < BOUNDARY_NUMBER_OF_POINTS) {
				// messageHandler.post(connectedSensorText);
			} else {
				measureSize = plotData.size();
				// check if operator has reached reasonable cuff pressure
				if (!maxPressureReached) {
					if (plotData.getLast().doubleValue() > maxPressureValueForMeasure) {
						maxPressureReached = true;
						// messageHandler.post(changeTextMessage);

					} else {
						// messageHandler.post(changeTextMessagePump);
					}
				}
			}

			if (maxPressureReached) {
				// if max pressure reached, check if measurement is now over
				if (plotData.getLast().doubleValue() < minPressureReached) {
					// o.deleteObservers();
					demo.close();
					// startSignalProcessing();
					messageHandler.post(runSignalProcessing);
				} else {
					updatePlot(plot);
				}
			} else {
				updatePlot(plot);
			}

		}
	}

	private void updatePlot(Plot plot) {

		try {
			bpMeasureSeries.setModel(plotData,
					SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

			plot.postRedraw();
		} catch (InterruptedException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}

	}

	// Create runnable for signal processing
	final Runnable runSignalProcessing = new Runnable() {
		public void run() {
			startSignalProcessing();
		}
	};
	// final Runnable updataBPResultView = new Runnable() {
	//
	// public void run() {
	// Log.d("MARCO", bloodPressureValue.toString());
	// int dPressure = (int) bloodPressureValue.getDiastolicBP();
	// int sPressure = (int) bloodPressureValue.getSystolicBP();
	// int pulse = (int) bloodPressureValue.getHeartRate();
	// // TODO debugging!
	// ValuesView valuesView = (ValuesView) findViewById(R.id.results);
	// valuesView.requestFocus();
	// valuesView.setSPressure(sPressure);
	// valuesView.setDPressure(dPressure);
	// valuesView.setPulseRate(pulse);
	// valuesView.invalidate();
	// saveButton.setEnabled(true);
	// saveButton.invalidate();
	// // messageHandler.post(disconnectedSensor);
	// }
	// };

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

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable changeTextMessagePump = new Runnable() {
		public void run() {
			TextView textMessage = (TextView) findViewById(R.id.text_message);
			textMessage.setText(R.string.pump);
			textMessage.setTextColor(Color.GREEN);
			textMessage.postInvalidate();
		}
	};

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable connectedSensorText = new Runnable() {
		public void run() {
			TextView textMessage = (TextView) findViewById(R.id.text_message);
			textMessage.setText(R.string.connect_sensor);
			textMessage.setTextColor(Color.RED);
			textMessage.postInvalidate();
		}
	};

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable disconnectedSensor = new Runnable() {
		public void run() {
			TextView textMessage = (TextView) findViewById(R.id.text_message);
			textMessage.setText(R.string.disconnect_sensor);
			textMessage.setTextColor(Color.RED);
			textMessage.postInvalidate();
		}
	};

	/**
	 * Handler for receiving messages from the USB Manager thread or the LED
	 * control modules
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}

	}; // handler

	// To be performed on the creation
	public void onCreate(Bundle savedInstanceState) {
		// Parent's method
		super.onCreate(savedInstanceState);

		// Layout
		setContentView(R.layout.measure);

		// #### Set up click listeners for all the buttons

		notesText = (EditText) findViewById(R.id.edittext);
		notesText.setFocusable(false);
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
				.setCancelable(false)
				.setPositiveButton(R.string.alert_dialog_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								MeasureActivity.this.finish();
							}
						})
				.setNegativeButton(R.string.alert_dialog_no,
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
						alert.setMessage(getResources()
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
		// data = new TestDatasource(this);
		// SampleDynamicSeries signalSeries = new SampleDynamicSeries(data, 0,
		// "Blood Pressure");plotUpdater = new MyPlotUpdater(bpMeasureXYPlot);

		bpMeasureXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		// register plot with plot updater observer
		plotUpdater = new MyPlotUpdater(bpMeasureXYPlot);
		// freeze the range boundaries:
		bpMeasureXYPlot.setRangeBoundaries(0, 300, BoundaryMode.FIXED);

		bpMeasureXYPlot.setDomainBoundaries(0, BOUNDARY_NUMBER_OF_POINTS,
				BoundaryMode.FIXED);
		bpMeasureSeries = new SimpleXYSeries("Pressure(mmHg)");
		bpMeasureSeries.setModel(plotData,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
		bpMeasureXYPlot.addSeries(bpMeasureSeries, LineAndPointRenderer.class,
				new LineAndPointFormatter(Color.rgb(100, 100, 200),
						Color.BLACK, null));

		List<Number> pressureListArray = new ArrayList<Number>();
		for (int i = 0; i < BOUNDARY_NUMBER_OF_POINTS; i++) {
			pressureListArray.add(maxPressureValueForMeasure);
		}

		SimpleXYSeries pressureLimit = new SimpleXYSeries(pressureListArray,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
		bpMeasureXYPlot.addSeries(pressureLimit, LineAndPointRenderer.class,
				new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.RED,
						null));
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
		// data.addObserver(plotUpdater);
		// start observable datasource thread
		// new Thread(data).start();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

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

	// /**
	// * Determination of BP and pulse algorithm
	// */
	// private void startSignalProcessing() {
	//
	// // myProgressDialog = ProgressDialog.show(MeasureActivity.this,
	// // getResources().getText(R.string.alert_dialog_processing_data),
	// // getResources().getText(R.string.alert_dialog_determine_bp),
	// // true);
	//
	// new Thread() {
	// public void run() {
	// Log.v("MAURO:", "BEFORE SIGNAL PROCESSING");
	// int l = demo.getBpMeasureHistory().size();
	// arrayTime = new float[l];
	// arrayPressure = new double[l];
	// int i = 0;
	// int fs = 100;
	// while (i < l) {
	// arrayPressure[i] = demo.getBpMeasureHistory().get(i)
	// .doubleValue();
	// arrayTime[i] = ((float) i / (float) fs);
	// i++;
	// }
	//
	// TimeSeriesMod signal = new TimeSeriesMod();
	// signal.setPressure(arrayPressure);
	//
	// signal.setTime(arrayTime);
	// bloodPressureValue = new BloodPressureValue();
	// //SignalProcessing r = new SignalProcessing();
	//
	// try {
	// bloodPressureValue = r.signalProcessing(signal, fs);
	// } catch (Exception e) {
	// myProgressDialog.dismiss();
	// messageHandler.post(discardTemBadMeasure);
	// e.printStackTrace();
	// }
	//
	// Log.v("MAURO:", "AFTER CALCULATE BLOOD PRESSURE VALUES");
	// // Dismiss the Dialog
	// //myProgressDialog.dismiss();
	// messageHandler.post(updataBPResultView);
	// }
	// }.start();
	//
	// }

	/**
	 * Determination of BP and pulse algorithm
	 */
	private void startSignalProcessing() {

		// Log.v("MAURO:", "BEFORE SIGNAL PROCESSING");
		int l = demo.getBpMeasureHistory().size();
		arrayTime = new float[l];
		arrayPressure = new double[l];
		int i = 0;
		int fs = 100;
		while (i < l) {
			arrayPressure[i] = demo.getBpMeasureHistory().get(i).doubleValue();
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
			// myProgressDialog.dismiss();
			// messageHandler.post(discardTemBadMeasure);
			e.printStackTrace();
		}

		// Log.v("MAURO:", "AFTER CALCULATE BLOOD PRESSURE VALUES");
		// Dismiss the Dialog
		// myProgressDialog.dismiss();
		// messageHandler.post(updataBPResultView);

		// Log.d("MARCO", bloodPressureValue.toString());
		int dPressure = (int) bloodPressureValue.getDiastolicBP();
		int sPressure = (int) bloodPressureValue.getSystolicBP();
		int pulse = (int) bloodPressureValue.getHeartRate();
		// TODO debugging!
		ValuesView valuesView = (ValuesView) findViewById(R.id.results);
		valuesView.requestFocus();
		valuesView.setSPressure(sPressure);
		valuesView.setDPressure(dPressure);
		valuesView.setPulseRate(pulse);
		valuesView.invalidate();
		saveButton.setEnabled(true);
		saveButton.invalidate();
		// messageHandler.post(disconnectedSensor);
	}

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable discardMeasure = new Runnable() {
		public void run() {

			builder = new AlertDialog.Builder(measureContext);
			builder.setMessage(R.string.alert_dialog_discard_bad_measure)
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									MeasureActivity.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable discardTemBadMeasure = new Runnable() {
		public void run() {

			builder = new AlertDialog.Builder(measureContext);
			builder.setMessage(R.string.alert_dialog_discard_temp_bad_measure)
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									MeasureActivity.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};

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
		values.put(BPMeasure.MAP, bloodPressureValue.getMeanArterialBP());
		values.put(BPMeasure.PULSE, bloodPressureValue.getHeartRate());
		values.put(BPMeasure.NOTE, note);
		values.put(BPMeasure.MEASUREMENT_SYNC, false);
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

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this.getBaseContext(), MainActivity.class);
		startActivity(i);
		return;
	}

	@Override
	public void onPause() {
		/* If there is a demo running, close it */
		if (demo != null) {
			demo.close();
		}
		demo = null;

		/* unregister any receivers that we have */
		unregisterReceiver(receiver);

		super.onPause();
		wl.release();
	}

	@Override
	public void onResume() {
		super.onResume();
		wl.acquire();
		/*
		 * Check to see if it was a USB device attach that caused the app to
		 * start or if the user opened the program manually.
		 */
		Intent intent = getIntent();
		String action = intent.getAction();

		if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
			/*
			 * This application is starting as a result of a device being
			 * attached. Get the device information that caused the app opening
			 * from the intent, and load the demo that corresponds to that
			 * device.
			 */
			// Log.v("MAURO", "APP WAS RESUMED AUTOMATICALLY");
			UsbDevice device = (UsbDevice) intent
					.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			demo = new TestDemoCustomHID(this.getApplicationContext(), device,
					handler);
			demo.addObserver(plotUpdater);
		} else {
			/*
			 * This application is starting up by a user opening the app
			 * manually. We need to look through to see if there are any devices
			 * that are already attached.
			 */
			// Log.v("MAURO", "APP WAS RESUMED,BY USER");
			UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
			HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
			Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

			while (deviceIterator.hasNext()) {
				/*
				 * For each device that we found attached, see if we are able to
				 * load a demo for that device.
				 */
				demo = new TestDemoCustomHID(this.getApplicationContext(),
						deviceIterator.next(), handler);
				demo.addObserver(plotUpdater);
				if (demo != null) {
					break;
				}
			}
		}

		if (testMode) {
			demo = new TestDemoCustomHID(this.getApplicationContext(), null,
					handler);
			demo.addObserver(plotUpdater);
			// break;
		}

		// Create a new filter to detect USB device events
		IntentFilter filter = new IntentFilter();

		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

		registerReceiver(receiver, filter);

		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				getPackageName() + ".USB_PERMISSION"), 0);
	}

	/***********************************************************************/
	/** Private section **/
	/***********************************************************************/

	/**
	 * New BroadcastReceiver object that will handle all of the USB device
	 * attach and detach events.
	 */
	BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			/* Get the information about what action caused this event */
			String action = intent.getAction();
			try {

				if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
					/*
					 * If it was a USB device detach event, then get the USB
					 * device that cause the event from the intent.
					 */
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					// Log.v("MAURO", "index=" + "DEVICE ATTACHED");
					// System.out.printf("e do Measure Activity: circulation");

					if (device != null) {
						/*
						 * Synchronize to demo here to make sure that the main
						 * GUI isn't doing something with the demo at the
						 * moment.
						 */
						synchronized (demo) {
							/* If the demo exists, close it down and free it up */
							if (demo != null) {
								demo.close();
								demo = null;
							}
						}
						// messageHandler.post(connectedSensorText);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

}