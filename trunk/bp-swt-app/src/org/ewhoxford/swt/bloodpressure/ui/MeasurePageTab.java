package org.ewhoxford.swt.bloodpressure.ui;

/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ewhoxford.swt.bloodpressure.exception.BadMeasureException;
import org.ewhoxford.swt.bloodpressure.exception.TempBadMeasureException;
import org.ewhoxford.swt.bloodpressure.model.BloodPressureValue;
import org.ewhoxford.swt.bloodpressure.pressureInputDevice.TestDatasource;
import org.ewhoxford.swt.bloodpressure.signalProcessing.SignalProcessing;
import org.ewhoxford.swt.bloodpressure.signalProcessing.TimeSeriesMod;
import org.ewhoxford.swt.bloodpressure.utils.FileManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

class MeasurePageTab extends Tab {
	/* Controls for setting layout parameters */
	private Button newMeasure;
	/* The example layout instance */
	private FillLayout fillLayout;

	// plot that shows real time data
	private static XYPlot bpMeasureXYPlot;
	// save measure button
	private Button saveButton;
	// Observer object that is notified by pressure data stream observable file
	private MyPlotUpdater plotUpdater;
	// Observable object that notifies observer that new values were acquired.
	private TestDatasource data;
	// pressure time series shown in the real time chart
	private TimeSeries bpMeasureSeries = null;
	// array with time points
	private float[] arrayTime;
	// array with pressure points
	private double[] arrayPressure;
	// auxiliary variable to control measurement.
	private boolean maxPressureReached = false;
	// signal processing progress dialog
	// private ProgressDialog myProgressDialog;
	// discard measure alert dialog
	// private AlertDialog.Builder builder;
	// discard measure alert dialog
	// private AlertDialog.Builder saveAlert;
	// Structure that holds blood pressure values result
	private BloodPressureValue bloodPressureValue;
	// Need handler for callbacks to the UI thread
	// private final Handler mHandler = new Handler();
	// Checkbox to save measure csv file
	private Button createCSV;
	// user notes
	private Text notesText;
	// number of points in X axis
	public static int BOUNDARY_NUMBER_OF_POINTS = 100;
	// max pressure value for measure
	private int maxPressureValueForMeasure = 200;
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
	private Text hrTextbox;
	private Text dbpTextbox;
	private Text sbpTextbox;
	private Label messageLabel;
	private ChartComposite frame;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 * 
	 * @wbp.parser.entryPoint
	 */
	MeasurePageTab(BPMainWindow instance) {
		super(instance);
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	void createChildWidgets() {
		/* Add common controls */
		super.createChildWidgets();

	}

	private class MyPlotUpdater implements Observer {
		Plot plot;
		double pressureValue = 0;

		public MyPlotUpdater(Plot plot) {
			this.plot = plot;
		}

		@Override
		public void update(Observable o, Object arg) {

			pressureValue = data.getPressureValue();

			if (data.getBpMeasure().size() == measureSize
					&& measureSize < BOUNDARY_NUMBER_OF_POINTS) {
				Display.getDefault().syncExec(connectedSensorText);

			} else {
				measureSize = data.getBpMeasure().size();
				// check if operator has reached reasonable cuff pressure
				if (!maxPressureReached) {
					if (pressureValue > maxPressureValueForMeasure) {
						maxPressureReached = true;
						Display.getDefault().syncExec(changeTextMessage);
					} else {
						Display.getDefault().syncExec(changeTextMessagePump);

					}
				}
			}
			if (maxPressureReached) {
				// if max pressure reached, check if measurement is now over
				if (pressureValue < minPressureReached) {
					// o.deleteObservers();
					data.setActive(false);
					// measurement is over, we are prepared to determine blood
					// pressure
					Display.getDefault().syncExec(runSignalProcessing);
				} else {
					updatePlot();
				}
			} else {
				updatePlot();
			}

		}

		private void updatePlot() {
			bpMeasureSeries.add(new Millisecond(), data.getBpMeasure()
					.getLast());

		}
	}

	/**
	 * Creates the control widgets.
	 */
	void createControlWidgets() {

		/* Controls the type of FillLayout */
		Group bpGroup = new Group(controlGroup, SWT.NONE);
		bpGroup.setText(BPMainWindow.getResourceString("Options"));
		bpGroup.setLayout(new GridLayout(2, true));
		bpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		messageLabel = new Label(controlGroup, SWT.NONE);
		messageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
		messageLabel.setText("");

		Label label1 = new Label(bpGroup, SWT.NONE);
		label1.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label1.setText("SBP");

		sbpTextbox = new Text(bpGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		sbpTextbox
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		sbpTextbox.setText("0");
		sbpTextbox.setEditable(false);

		Label label2 = new Label(bpGroup, SWT.NONE);
		label2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label2.setText("DBP");

		dbpTextbox = new Text(bpGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		dbpTextbox
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dbpTextbox.setText("0");
		dbpTextbox.setEditable(false);

		Label label3 = new Label(bpGroup, SWT.NONE);
		label3.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label3.setText("HR");

		hrTextbox = new Text(bpGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		hrTextbox
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		hrTextbox.setText("0");
		hrTextbox.setEditable(false);

		Label label5 = new Label(controlGroup, SWT.NONE);
		label5.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label5.setText("");

		Label label4 = new Label(controlGroup, SWT.NONE);
		label4.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label4.setText("Notes:");

		notesText = new Text(controlGroup, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.H_SCROLL);
		notesText
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		notesText.setText("Comment on the blood pressure");
		notesText.computeSize(100, 500);
		notesText.setEnabled(true);
		final Group optionGroup = new Group(controlGroup, SWT.NONE);
		optionGroup.setText(BPMainWindow.getResourceString("Options"));
		optionGroup.setLayout(new GridLayout());
		optionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		newMeasure = new Button(optionGroup, SWT.BUTTON1);
		newMeasure.setText("New Measure");
		newMeasure.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		newMeasure.setSelection(true);
		newMeasure.addSelectionListener(selectionListener);

		createCSV = new Button(optionGroup, SWT.CHECK);
		createCSV.setText("create CSV File");
		createCSV.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		saveButton = new Button(optionGroup, SWT.BUTTON1);
		saveButton.setText("Save");
		saveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		saveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String savedFileName = "";
				String notes = "";

				if (notesText.getText().length() != 0) {
					notes = notesText.getText();
				}

				Long time = System.currentTimeMillis();

				if (createCSV.getSelection()) {

					try {
						savedFileName = FileManager.saveFile(
								bloodPressureValue, arrayPressure, arrayTime,
								time, notes);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						addNewMeasureAndFile(savedFileName, bloodPressureValue,
								notes, time);
					}

				} else {
					addNewMeasureAndFile(savedFileName, bloodPressureValue,
							notes, time);

					if (createCSV.getSelection()) {

						MessageDialog
								.openInformation(
										optionGroup.getShell(),
										"Saved",
										BPMainWindow
												.getResourceString("alert_dialog_data_saved_to_database_and_csv"));

					} else {

						MessageDialog
								.openInformation(
										optionGroup.getShell(),
										"Saved",
										BPMainWindow
												.getResourceString("alert_dialog_data_saved_to_database"));
					}
				}
			}
		});
		// #### End of Set up click listeners for all the buttons
	}

	/**
	 * Creates the bp measure layout.
	 */
	void createLayout() {
		fillLayout = new FillLayout();
		layoutComposite.setLayout(fillLayout);

		final JFreeChart chart = createChart(createDataset(), bpMeasureXYPlot);

		frame = new ChartComposite(layoutComposite, SWT.NONE, chart, false,
				true, true, false, false);
		// frame.setRangeZoomable(false);
		// ChartComposite frame = new ChartComposite(layoutComposite, SWT.NONE,
		// chart, true);
		frame.setDisplayToolTips(true);
		frame.setHorizontalAxisTrace(false);
		frame.setVerticalAxisTrace(false);
		frame.setRangeZoomable(false);
		// frame.chartChanged(new );
		// initialize our XYPlot reference and real time update code:

		// getInstance and position datasets:
		data = new TestDatasource();
		// SampleDynamicSeries signalSeries = new SampleDynamicSeries(data, 0,
		// "Blood Pressure");

		// register plot with plot updater observer
		plotUpdater = new MyPlotUpdater(bpMeasureXYPlot);

		data.addObserver(plotUpdater);
		// start observable datasource thread
		new Thread(data).start();

	}

	/**
	 * Disposes the editors without placing their contents into the table.
	 */
	void disposeEditors() {
		;
	}

	/**
	 * Returns the layout data field names.
	 */
	String[] getLayoutDataFieldNames() {
		return new String[] { "", "Control" };
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	String getTabText() {
		return "FillLayout";
	}

	/**
	 * Sets the state of the layout.
	 */
	void setLayoutState() {
		if (saveButton.getSelection()) {
			fillLayout.type = SWT.VERTICAL;
		} else {
			fillLayout.type = SWT.HORIZONTAL;
		}
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return A chart.
	 */
	private static JFreeChart createChart(XYDataset dataset,
			XYPlot bpMeasureXYPlot) {

		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Blood Pressure Measure", // title
				"time(s)", // x-axis label
				"Pressure(mmHg)", // y-axis label
				dataset, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		bpMeasureXYPlot = (XYPlot) chart.getPlot();
		bpMeasureXYPlot.setBackgroundPaint(Color.lightGray);
		bpMeasureXYPlot.setDomainGridlinePaint(Color.white);
		bpMeasureXYPlot.setRangeGridlinePaint(Color.white);

		// bpMeasureXYPlot.setAxisOffset(new RectangleInsets(300, 0, 0,
		// BOUNDARY_NUMBER_OF_POINTS));
		// bpMeasureXYPlot.setDomainCrosshairVisible(true);
		// bpMeasureXYPlot.setRangeCrosshairVisible(true);

		XYItemRenderer r = bpMeasureXYPlot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}

		ValueAxis axis = bpMeasureXYPlot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(100000.0); // 60 seconds
		axis = bpMeasureXYPlot.getRangeAxis();
		axis.setRange(0.0, 300.0);

		// DateAxis axis = (DateAxis) plot.getDomainAxis();
		// axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		// axis.setRange(0, 1000);

		return chart;

	}

	/**
	 * Creates a dataset, consisting of two series of monthly data.
	 * 
	 * @return The dataset.
	 */
	private static XYDataset createDataset() {

		// ReadCSV r= new ReadCSV();

		// int[][] pressureValues =r.readCSV("./", "bp.txt");
		// int l = pressureValues.length;
		// TimeSeriesMod pressureValuesMod = ConvertTommHg.convertArrayTommHg(
		// pressureValues, 100);
		// double[] pressureValuesFloat = pressureValuesMod.getPressure();
		// int k = 1;
		TimeSeries s1 = new TimeSeries("BP signal");
		// while (k < l) {
		// s1.add(new Millisecond(k,new Date().getSeconds(),new
		// Date().getMinutes(),new Date().getHours(),new Date().getDay(),new
		// Date().getMonth(),2011), pressureValuesFloat[k]);
		// k = k + 1;
		// }

		// TimeSeries s2 = new TimeSeries("Max Blood Pressure");

		// while (k < l) {
		// s1.add(new Millisecond(k,new Date().getSeconds(),new
		// Date().getMinutes(),new Date().getHours(),new Date().getDay(),new
		// Date().getMonth(),new Date().getYear()), 200);
		// k = k + 1;
		// }

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);
		// dataset.addSeries(s2);

		return dataset;
	}

	// Create runnable for signal processing
	final Runnable runSignalProcessing = new Runnable() {
		public void run() {
			startSignalProcessing();
		}
	};
	final Runnable updataBPResultView = new Runnable() {

		public void run() {
			// Display blood pressure algorithm result in the Measure layout
			// bloodPressureValue.setDiastolicBP(76);
			// bloodPressureValue.setSystolicBP(128);
			// bloodPressureValue.setMeanArterialBP(78);

			int dPressure = (int) bloodPressureValue.getDiastolicBP();
			int sPressure = (int) bloodPressureValue.getSystolicBP();
			int pulse = (int) bloodPressureValue.getHeartRate();

			sbpTextbox.setText("" + sPressure);
			dbpTextbox.setText("" + dPressure);
			hrTextbox.setText("" + pulse);

			saveButton.setEnabled(true);
			saveButton.redraw();
			// mHandler.post(disconnectedSensor);

		}
	};

	{
		// initialized time series
		bpMeasureSeries = new TimeSeries("BP time series");

	}

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable changeTextMessage = new Runnable() {
		public void run() {

			messageLabel.setText(BPMainWindow.getResourceString("stop_pump"));
			// messageLabel.setTextColor(Color.YELLOW);
			messageLabel.redraw();
		}
	};

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable changeTextMessagePump = new Runnable() {
		public void run() {
			// TextView textMessage = (TextView)
			// findViewById(R.id.text_message);
			messageLabel.setText(BPMainWindow.getResourceString("pump"));
			// textMessage.setTextColor(Color.GREEN);
			messageLabel.redraw();
		}
	};

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable connectedSensorText = new Runnable() {
		public void run() {
			// TextView textMessage = (TextView)
			// findViewById(R.id.text_message);
			messageLabel.setText(BPMainWindow
					.getResourceString("connect_sensor"));
			// textMessage.setTextColor(Color.RED);
			messageLabel.redraw();
		}
	};

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable disconnectedSensor = new Runnable() {
		public void run() {
			messageLabel.setText(BPMainWindow
					.getResourceString("disconnect_sensor"));
			// textMessage.setTextColor(Color.RED);
			messageLabel.redraw();
		}
	};

	/**
	 * Determination of BP and pulse algorithm
	 */
	private void startSignalProcessing() {

		// myProgressDialog = ProgressDialog.show(MeasureActivity.this,
		// getResources().getText(R.string.alert_dialog_processing_data),
		// getResources().getText(R.string.alert_dialog_determine_bp),
		// true);

		// Runnable runnable = new Runnable() {
		// @Override
		// public void run() {
		// // Imagine something useful here
		// try {
		// Thread.sleep(10000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		//				
		// }
		// };
		//		
		// BusyIndicator.showWhile(controlGroup.getShell().getDisplay(),
		// runnable);

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
					bloodPressureValue = r.signalProcessing(signal, fs);
				} catch (BadMeasureException e) {
					// myProgressDialog.dismiss();
					Display.getDefault().syncExec(discardMeasure);

				} catch (TempBadMeasureException e) {
					// myProgressDialog.dismiss();
					Display.getDefault().syncExec(discardTemBadMeasure);
				}

				// Dismiss the Dialog
				// myProgressDialog.dismiss();
				Display.getDefault().syncExec(updataBPResultView);
			}
		}.start();

	}

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable discardMeasure = new Runnable() {
		public void run() {
			MessageDialog
					.openInformation(
							controlGroup.getShell(),
							"Saved",
							BPMainWindow
									.getResourceString("alert_dialog_discard_bad_measure"));

		}
	};

	// Create runnable for chaging messages while pressure is being acquired
	final Runnable discardTemBadMeasure = new Runnable() {
		public void run() {
			MessageDialog
					.openInformation(
							controlGroup.getShell(),
							"Saved",
							BPMainWindow
									.getResourceString("alert_dialog_discard_temp_bad_measure"));

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
		// ContentResolver cr = getContentResolver();
		// ContentValues values = new ContentValues();
		// values.put(BPMeasure.CREATED_DATE, time);
		// values.put(BPMeasure.MODIFIED_DATE, time);
		// values.put(BPMeasure.DP, bloodPressureValue.getDiastolicBP());
		// values.put(BPMeasure.SP, bloodPressureValue.getSystolicBP());
		// values.put(BPMeasure.MAP, bloodPressureValue.getMeanArterialBP());
		// values.put(BPMeasure.PULSE, bloodPressureValue.getHeartRate());
		// values.put(BPMeasure.NOTE, note);
		// values.put(BPMeasure.MEASUREMENT_SYNC, false);
		// if (savedFileName != null) {
		// if (savedFileName.length() == 0) {
		// values.put(BPMeasure.MEASUREMENT_FILE_EXIST, false);
		// } else {
		// values.put(BPMeasure.MEASUREMENT_FILE_EXIST, true);
		// values.put(BPMeasure.MEASUREMENT_FILE, savedFileName);
		// }
		// }
		// cr.insert(BPMeasure.CONTENT_URI, values);
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
