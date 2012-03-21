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

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class ListMeasureTab extends Tab {
	/* Controls for setting layout parameters */

	/* The example layout instance */
	GridLayout gridLayout;
	/* TableEditors and related controls */
	TableEditor comboEditor, widthEditor, heightEditor;
	TableEditor vAlignEditor, hAlignEditor, hIndentEditor;
	TableEditor hSpanEditor, vSpanEditor, hGrabEditor, vGrabEditor;
	// CCombo combo, vAlign, hAlign, hGrab, vGrab;
	// Text widthText, heightText, hIndent, hSpan, vSpan;
	Button newMeasure, delete, clear;
	Group childGroup;

	Vector data = new Vector();

	/* Constants */
	final int COMBO_COL = 1;
	final int WIDTH_COL = 2;
	final int HEIGHT_COL = 3;
	final int HALIGN_COL = 4;
	final int VALIGN_COL = 5;
	final int HINDENT_COL = 6;
	final int HSPAN_COL = 7;
	final int VSPAN_COL = 8;
	final int HGRAB_COL = 9;
	final int VGRAB_COL = 10;

	final int TOTAL_COLS = 11;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	ListMeasureTab(BPMainWindow instance) {
		super(instance);
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	void createChildWidgets() {
		/* Create the TraverseListener */
		final TraverseListener traverseListener = new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN
						|| e.detail == SWT.TRAVERSE_TAB_NEXT)
					resetEditors();
				if (e.detail == SWT.TRAVERSE_ESCAPE)
					disposeEditors();
			}
		};

		/* Add common controls */
		super.createChildWidgets();

		/* Add TableEditors */
		comboEditor = new TableEditor(table);
		widthEditor = new TableEditor(table);
		heightEditor = new TableEditor(table);
		vAlignEditor = new TableEditor(table);
		hAlignEditor = new TableEditor(table);
		hIndentEditor = new TableEditor(table);
		hSpanEditor = new TableEditor(table);
		vSpanEditor = new TableEditor(table);
		hGrabEditor = new TableEditor(table);
		vGrabEditor = new TableEditor(table);

		/* Add listener to add an element to the table */
		newMeasure.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem(table, 0);
				String[] insert = new String[] {
						String.valueOf(table.indexOf(item)), "Button", "-1",
						"-1", "BEGINNING", "CENTER", "0", "1", "1", "false",
						"false" };
				item.setText(insert);
				data.addElement(insert);
				resetEditors();
			}
		});
	}

	/**
	 * Creates the control widgets.
	 */
	void createControlWidgets() {
		/* Controls for adding and removing children */

		childGroup = new Group(controlGroup, SWT.NONE);
		childGroup.setText(BPMainWindow.getResourceString("Children"));
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		childGroup.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		childGroup.setLayoutData(data);

		newMeasure = new Button(childGroup, SWT.PUSH);
		newMeasure.setText(BPMainWindow.getResourceString("New Measure"));
		newMeasure.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		delete = new Button(childGroup, SWT.PUSH);
		delete.setText(BPMainWindow.getResourceString("Delete"));
		delete.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				resetEditors();
				int[] selected = table.getSelectionIndices();
				table.remove(selected);
				/* Refresh the control indices of the table */
				for (int i = 0; i < table.getItemCount(); i++) {
					table.getItem(i).setText(0, String.valueOf(i));
				}
				refreshLayoutComposite();
				layoutComposite.layout(true);
				layoutGroup.layout(true);
			}
		});
		clear = new Button(childGroup, SWT.PUSH);
		clear.setText(BPMainWindow.getResourceString("Clear"));
		clear.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				resetEditors();
				children = layoutComposite.getChildren();
				for (int i = 0; i < children.length; i++) {
					children[i].dispose();
				}
				table.removeAll();
				//data.clear();?????????????
				children = new Control[0];
				layoutGroup.layout(true);
			}
		});
		/* Create the "children" table */
		table = new Table(childGroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 150;
		table.setLayoutData(gridData);
		table.addTraverseListener(traverseListener);

		/* Add columns to the table */
		String[] columnHeaders = getLayoutDataFieldNames();
		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnHeaders[i]);
			if (i == 0)
				column.setWidth(20);
			else if (i == 1)
				column.setWidth(80);
			else
				column.pack();
		}

		/* Add common controls */
		super.createControlWidgets();
		controlGroup.pack();
	}

	/**
	 * Creates the example layout.
	 */
	void createLayout() {
		gridLayout = new GridLayout();
		layoutComposite.setLayout(gridLayout);
	}

	/**
	 * Disposes the editors without placing their contents into the table.
	 */
	void disposeEditors() {
		comboEditor.setEditor(null, null, -1);

	}

	/**
	 * Returns the layout data field names.
	 */
	String[] getLayoutDataFieldNames() {
		return new String[] { "No.", "DATE/TIME", "SYS", "DIA", "PULSE",
				"NOTES" };
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	String getTabText() {
		return "List Measures";
	}

	
	/**
	 * Sets the layout data for the children of the layout.
	 */
	void setLayoutData() {
		Control[] children = layoutComposite.getChildren();
		TableItem[] items = table.getItems();
		GridData data;
		int hIndent, hSpan, vSpan;
		String vAlign, hAlign, vGrab, hGrab;
		for (int i = 0; i < children.length; i++) {
			data = new GridData();
			/* Set widthHint and heightHint */
			data.widthHint = new Integer(items[i].getText(WIDTH_COL))
					.intValue();
			data.heightHint = new Integer(items[i].getText(HEIGHT_COL))
					.intValue();
			/* Set vertical alignment and horizontal alignment */
			hAlign = items[i].getText(HALIGN_COL);
			if (hAlign.equals("CENTER")) {
				data.horizontalAlignment = GridData.CENTER;
			} else if (hAlign.equals("END")) {
				data.horizontalAlignment = GridData.END;
			} else if (hAlign.equals("FILL")) {
				data.horizontalAlignment = GridData.FILL;
			} else {
				data.horizontalAlignment = GridData.BEGINNING;
			}
			vAlign = items[i].getText(VALIGN_COL);
			if (vAlign.equals("BEGINNING")) {
				data.verticalAlignment = GridData.BEGINNING;
			} else if (vAlign.equals("END")) {
				data.verticalAlignment = GridData.END;
			} else if (vAlign.equals("FILL")) {
				data.verticalAlignment = GridData.FILL;
			} else {
				data.verticalAlignment = GridData.CENTER;
			}
			/* Set indents and spans */
			hIndent = new Integer(items[i].getText(HINDENT_COL)).intValue();
			data.horizontalIndent = hIndent;
			hSpan = new Integer(items[i].getText(HSPAN_COL)).intValue();
			data.horizontalSpan = hSpan;
			vSpan = new Integer(items[i].getText(VSPAN_COL)).intValue();
			data.verticalSpan = vSpan;
			/* Set grabbers */
			hGrab = items[i].getText(HGRAB_COL);
			if (hGrab.equals("true")) {
				data.grabExcessHorizontalSpace = true;
			} else {
				data.grabExcessHorizontalSpace = false;
			}
			vGrab = items[i].getText(VGRAB_COL);
			if (vGrab.equals("true")) {
				data.grabExcessVerticalSpace = true;
			} else {
				data.grabExcessVerticalSpace = false;
			}
			children[i].setLayoutData(data);
		}
	}

	/**
	 * Sets the state of the layout.
	 */
	void setLayoutState() {
		/* Set the columns for the layout */
		
	}
}
