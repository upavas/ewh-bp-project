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
import org.ewhoxford.swt.bloodpressure.signalProcessing.ConvertTommHg;
import org.ewhoxford.swt.bloodpressure.signalProcessing.TimeSeriesMod;
import org.ewhoxford.swt.bloodpressure.utils.ReadCSV;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
	Button horizontal, vertical;
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
//		super.createChildWidgets ();
//		
//		/* Add TableEditors */
//		comboEditor = new TableEditor (table);
//		table.addSelectionListener (new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				resetEditors ();
//				index = table.getSelectionIndex ();
//				if (index == -1) return;
//				TableItem oldItem = comboEditor.getItem ();
//				newItem = table.getItem (index);
//				if (newItem == oldItem || newItem != lastSelected) {
//					lastSelected = newItem;
//					return;
//				}
//				table.showSelection ();
//				
//				combo = new CCombo (table, SWT.READ_ONLY);
//				createComboEditor (combo, comboEditor);
//			}
//		});
//		
//		
//		/* Add listener to add an element to the table */
//		add.addSelectionListener(new SelectionAdapter () {
//			public void widgetSelected(SelectionEvent e) {
//				TableItem item = new TableItem (table, 0);
//				item.setText (0, String.valueOf (table.indexOf (item)));
//				item.setText (1, "Button");
//				data.addElement ("Button");
//				resetEditors ();
//			}
//		});
	}
	
	/**
	 * Creates the control widgets.
	 */
	void createControlWidgets () {
		/* Controls the type of FillLayout */
		Group typeGroup = new Group (controlGroup, SWT.NONE);
		typeGroup.setText (BPMainWindow.getResourceString ("Type"));
		typeGroup.setLayout (new GridLayout ());
		typeGroup.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));
		horizontal = new Button (typeGroup, SWT.RADIO);
		horizontal.setText ("SWT.HORIZONTAL");
		horizontal.setLayoutData(new GridData (GridData.FILL_HORIZONTAL));
		horizontal.setSelection(true);
		horizontal.addSelectionListener (selectionListener);
		vertical = new Button (typeGroup, SWT.RADIO);
		vertical.setText ("SWT.VERTICAL");
		vertical.setLayoutData(new GridData (GridData.FILL_HORIZONTAL));
		vertical.addSelectionListener (selectionListener); 
		
		/* Add common controls */
		//super.createControlWidgets ();
		
		/* Position the sash */
		sash.setWeights (new int [] {4,1});
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
	 * Takes information from TableEditors and stores it.
	 */
	void resetEditors () {
		TableItem oldItem = comboEditor.getItem ();
		comboEditor.setEditor (null, null, -1);
		if (oldItem != null) {
			int row = table.indexOf (oldItem);
			data.insertElementAt (combo.getText (), row);
			oldItem.setText (1, data.elementAt (row).toString ());
			combo.dispose ();
		}
		setLayoutState ();
		refreshLayoutComposite ();
		layoutComposite.layout (true);
		layoutGroup.layout (true);
	}
	
	/**
	 * Sets the state of the layout.
	 */
	void setLayoutState () {
		if (vertical.getSelection()) {
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
		
		TimeSeries s2 = new TimeSeries("Max Blood Pressure");
		
		while (k < l) {
			s1.add(new Millisecond(k,new Date().getSeconds(),new Date().getMinutes(),new Date().getHours(),new Date().getDay(),new Date().getMonth(),new Date().getYear()), 200);
				k = k + 1;
		}
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);

		return dataset;
	}
	
	
}
