// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import java.io.File;
import java.io.FileInputStream;

import com.androidplot.ui.layout.AnchorPosition;
import com.androidplot.ui.layout.XLayoutStyle;
import com.androidplot.ui.layout.YLayoutStyle;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.XYPlot;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.EditText;

//Class Measure : activity that pops when the user wants to start taking blood pressure
public class Measure extends Activity implements OnClickListener {

	GraphView graph;
	BPSignalProcessing signalProcessing;
	boolean saveFile = false;
	private XYPlot mySimpleXYPlot;

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
		HelpButton.setOnClickListener(this);

		// #### End of Set up click listeners for all the buttons

		//graph = (GraphView) findViewById(R.id.graph);

		//graph.invalidate();
		
		// initialize our XYPlot reference:
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		ReadCSV r = new ReadCSV();
		int[][] vals1 = r.readCSV();
        // add a new series
        mySimpleXYPlot.addSeries(new SimpleXYSeries(vals1.length,vals1), LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(200, 0, 0), Color.rgb(0, 0, 200)));

        // reduce the number of range labels
        mySimpleXYPlot.getGraphWidget().setRangeTicksPerLabel(4);

        // reposition the domain label to look a little cleaner:
        Widget domainLabelWidget = mySimpleXYPlot.getDomainLabelWidget();

        mySimpleXYPlot.position(domainLabelWidget,                     // the widget to position
                                45,                                    // x position value, in this case 45 pixels
                                XLayoutStyle.ABSOLUTE_FROM_LEFT,       // how the x position value is applied, in this case from the left
                                0,                                     // y position value
                                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,     // how the y position is applied, in this case from the bottom
                                AnchorPosition.LEFT_BOTTOM);           // point to use as the origin of the widget being positioned

        // get rid of the visual aids for positioning:
        mySimpleXYPlot.disableAllMarkup();
				
		
		acquireDataFromMouse();
	}

	protected void acquireDataFromMouse() {

		File f;
		f = new File("/dev/input/mice");

		if (!f.exists() && f.length() < 0)
			System.out.println("The specified file is not exist");
		else {

			try {

				FileInputStream finp = new FileInputStream(f);

				int count = 0;
				char[] mouseV = new char[3];
				do {
					count++;
					int i = 0;
					while (i <= 2) {
						mouseV[i] = (char) finp.read();
						i = i + 1;
					}
					System.out.println("" + (int) mouseV[0] + ","
							+ (int) mouseV[1] + "," + (int) mouseV[2]);

					i = 0;
					
					graph.sendNewValueToDisplay((float) mouseV[2]);
					signalProcessing
							.sendNewValueToProcess(mouseV[1], mouseV[2]);
				} while ((mouseV[0] != -1) && (count < 50));
				finp.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
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

}