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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.ewhoxford.swt.bloodpressure.exception.BadMeasureException;
import org.ewhoxford.swt.bloodpressure.exception.TempBadMeasureException;
import org.ewhoxford.swt.bloodpressure.model.BloodPressureValue;
import org.ewhoxford.swt.bloodpressure.signalProcessing.ConvertTommHg;
import org.ewhoxford.swt.bloodpressure.signalProcessing.SignalProcessing;
import org.ewhoxford.swt.bloodpressure.signalProcessing.TimeSeriesMod;
import org.ewhoxford.swt.bloodpressure.utils.ReadCSV;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

class MeasurePageTab extends Tab {
	/* Controls for setting layout parameters */
	Button newMeasure, saveMeasure, createCSV;
	/* The example layout instance */
	FillLayout fillLayout;
	/* TableEditors and related controls*/
	TableEditor comboEditor;
	CCombo combo;
	
	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 * @wbp.parser.entryPoint
	 */
	MeasurePageTab(BPMainWindow instance) {
		super(instance);
	}
	
	/**
	 * Creates the widgets in the "child" group.
	 */
	void createChildWidgets () {
		/* Add common controls */
		super.createChildWidgets ();
		
		/* Add TableEditors */
		comboEditor = new TableEditor (table);
		table.addSelectionListener (new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				resetEditors ();
				index = table.getSelectionIndex ();
				if (index == -1) return;
				TableItem oldItem = comboEditor.getItem ();
				newItem = table.getItem (index);
				if (newItem == oldItem || newItem != lastSelected) {
					lastSelected = newItem;
					return;
				}
				table.showSelection ();
				
				combo = new CCombo (table, SWT.READ_ONLY);
				createComboEditor (combo, comboEditor);
			}
		});
		
		
		/* Add listener to add an element to the table */
		add.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem (table, 0);
				item.setText (0, String.valueOf (table.indexOf (item)));
				item.setText (1, "Button");
				data.addElement ("Button");
				resetEditors ();
			}
		});
	}
	
	/**
	 * Creates the control widgets.
	 */
	void createControlWidgets () {

		/* Controls the type of FillLayout */
		Group bpGroup = new Group (controlGroup, SWT.NONE);
		bpGroup.setText (BPMainWindow.getResourceString ("Options"));
		bpGroup.setLayout (new GridLayout (2,true));
		bpGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));
		
		Label label1 = new Label(bpGroup, SWT.NONE);
		label1.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label1.setText("SBP");
		
		final Text textSBP = new Text(bpGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		textSBP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textSBP.setText("0");
		textSBP.setEditable(false);
		
		Label label2 = new Label(bpGroup, SWT.NONE);
		label2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label2.setText("DBP");
		
		final Text textDBP = new Text(bpGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		textDBP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textDBP.setText("0");
		textDBP.setEditable(false);
		
		
		Label label3 = new Label(bpGroup, SWT.NONE);
		label3.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label3.setText("HR");
		
		
		final Text textHR = new Text(bpGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		textHR.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textHR.setText("0");
		textHR.setEditable(false);

		Label label5 = new Label(controlGroup, SWT.NONE);
		label5.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label5.setText("");
		
		
		
		Label label4 = new Label(controlGroup, SWT.NONE);
		label4.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label4.setText("Notes:");
		
		Text textNotes = new Text(controlGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		textNotes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textNotes.setText("Comment on the blood pressure");
	    textNotes.computeSize(100,500);
	    //textNotes.setSize(1000, 500);
		textNotes.setEnabled(true);
				
		Group optionGroup = new Group (controlGroup, SWT.NONE);
		optionGroup.setText (BPMainWindow.getResourceString ("Options"));
		optionGroup.setLayout (new GridLayout ());
		optionGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));
				
		newMeasure = new Button (optionGroup, SWT.BUTTON1);
		newMeasure.setText("New Measure");
		newMeasure.setLayoutData(new GridData (GridData.FILL_HORIZONTAL));
		newMeasure.setSelection(true);
		newMeasure.addSelectionListener (selectionListener);
		newMeasure.addSelectionListener (selectionListener);
		newMeasure.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				//read CSV File and create TimeSeriesMod
				ReadCSV r= new ReadCSV();
				
				int[][] pressureValues =r.readCSV("./", "bp.txt");
				int l = pressureValues.length;
				
				//Conversion
				TimeSeriesMod pressureValuesMod = ConvertTommHg.convertArrayTommHg(
						pressureValues, 100);
				
				//Apply Initial Filter: if difference between one value and the next is higher than 20, put the next the same as the previous.
				double[] pressureValuesFloat = pressureValuesMod.getPressure();
				//float[] arrayTime = new float[l];
				double[] arrayPressure = new double[l];
				arrayPressure[0] = pressureValuesFloat[0];
				int i = 1;
				while (i<l){
					if (Math.abs(pressureValuesFloat[i]-pressureValuesFloat[i-1]) > 20) 
						arrayPressure[i]=pressureValuesFloat[i-1];
					else
						arrayPressure[i]=pressureValuesFloat[i];
				}
				
				TimeSeriesMod signal = new TimeSeriesMod();
				signal.setPressure(arrayPressure);
				signal.setTime(pressureValuesMod.getTime());
				BloodPressureValue bloodPressureValue = new BloodPressureValue();
				SignalProcessing r1 = new SignalProcessing();
				try {
					bloodPressureValue = r1.signalProcessing(signal, 100);
				} catch (BadMeasureException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (TempBadMeasureException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
						
				textSBP.setText(Integer.toString((int) bloodPressureValue.getSystolicBP()));
				textDBP.setText(Integer.toString((int) bloodPressureValue.getDiastolicBP()));
				textHR.setText(Integer.toString((int) bloodPressureValue.getHeartRate()));
			}
		});
		
		createCSV = new Button (optionGroup, SWT.CHECK);
		createCSV.setText ("create CSV File");
		createCSV.setLayoutData(new GridData (GridData.FILL_HORIZONTAL));
			
		saveMeasure = new Button (optionGroup, SWT.BUTTON1);
		saveMeasure.setText ("Save");
		saveMeasure.setLayoutData(new GridData (GridData.FILL_HORIZONTAL));
		saveMeasure.addSelectionListener (selectionListener); 
			
		/* Add common controls */
		//super.createControlWidgets ();
		
		/* Position the sash */
		//sash.setWeights (new int [] {4,1});
	}
	
	/**
	 * Creates the bp measure layout.
	 */
	void createLayout () {
		fillLayout = new FillLayout ();
		layoutComposite.setLayout (fillLayout);
		final JFreeChart chart = createChart(createDataset());
		ChartComposite frame = new ChartComposite(layoutComposite, SWT.NONE, chart, true);
		frame.setDisplayToolTips(true);
		frame.setHorizontalAxisTrace(false);
		frame.setVerticalAxisTrace(false);	
		
	}
	
	/** 
	 * Disposes the editors without placing their contents
	 * into the table.
	 */
	void disposeEditors () {
		comboEditor.setEditor (null, null, -1);
		combo.dispose ();
	}

	
	/**
	 * Generates code for the example layout.
	 */
	StringBuffer generateLayoutCode () {
		StringBuffer code = new StringBuffer ();
		code.append ("\t\tFillLayout fillLayout = new FillLayout ();\n");
		if (fillLayout.type == SWT.VERTICAL) {
			code.append ("\t\tfillLayout.type = SWT.VERTICAL;\n");
		}
		code.append ("\t\tshell.setLayout (fillLayout);\n");
		for (int i = 0; i < children.length; i++) {
			Control control = children [i];
			code.append (getChildCode (control, i));
		}
		return code;
	}
	
	/**
	 * Returns the layout data field names.
	 */
	String [] getLayoutDataFieldNames() {
		return new String [] {"","Control"};
	}
	
	/**
	 * Gets the text for the tab folder item.
	 */
	String getTabText () {
		return "FillLayout";
	}
	
	
	/**
	 * Sets the state of the layout.
	 */
	void setLayoutState () {
		if (saveMeasure.getSelection()) {
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
	private static JFreeChart createChart(XYDataset dataset) {

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

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(300, 0,0 ,60));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}
		
		Axis axis= plot.getDomainAxis();

//		DateAxis axis = (DateAxis) plot.getDomainAxis();
		//axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		//axis.setRange(0, 1000);
	
		return chart;

	}
	
	
	/**
	 * Creates a dataset, consisting of two series of monthly data.
	 * 
	 * @return The dataset.
	 */
	private static XYDataset createDataset() {

		ReadCSV r= new ReadCSV();
		
		int[][] pressureValues =r.readCSV("./", "bp.txt");
		int l = pressureValues.length;
		TimeSeriesMod pressureValuesMod = ConvertTommHg.convertArrayTommHg(
				pressureValues, 100);
		double[] pressureValuesFloat = pressureValuesMod.getPressure();
		int k = 1;
		TimeSeries s1 = new TimeSeries("BP signal");
		
		while (k < l) {
			s1.add(new Millisecond(k,new Date().getSeconds(),new Date().getMinutes(),new Date().getHours(),new Date().getDay(),new Date().getMonth(),2011), pressureValuesFloat[k]);
			k = k + 1;
		}
		
		k = 1;
		
		TimeSeries s2 = new TimeSeries("Max Blood Pressure");
		
		while (k < l) {
			s2.add(new Millisecond(k,new Date().getSeconds(),new Date().getMinutes(),new Date().getHours(),new Date().getDay(),new Date().getMonth(),2011), 200.0);
				k = k + 1;
		}
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);
						
		return dataset;
	}
	
	
}
