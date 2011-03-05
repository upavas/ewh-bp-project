// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

//Class Measure : activity that pops when the user wants to start taking blood pressure
public class Measure extends Activity implements OnClickListener {
	int[] values;
	int valuesEnd;
	Random rand;
	int d;
	GraphView graph;

	// To be performed on the creation
	public void onCreate(Bundle savedInstanceState) {
		// Parent's method
		super.onCreate(savedInstanceState);

		// Layout
		setContentView(R.layout.measure);

		// #### Set up click listeners for all the buttons

		// Help button
		View HelpButton = findViewById(R.id.button_help);
		HelpButton.setOnClickListener(this);

		// #### End of Set up click listeners for all the buttons

		// Simulate fake BP acquisition
		values = new int[60];
		rand = new Random();
		d = 4;
		graph = (GraphView) findViewById(R.id.graph);

		//		
		// values[0] = 40;
		// valuesEnd = 0;
		//
		addValue();
	}

	protected void addValue() {

		File f;
		f = new File("/dev/input/mice");

		if (!f.exists() && f.length() < 0)
			System.out.println("The specified file is not exist");
		else {

			try {

				FileInputStream finp = new FileInputStream(f);
				int b;
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
					valuesEnd = valuesEnd + 1;
					int j = valuesEnd;
					values[j] = mouseV[2];
					i = 0;
				} while ((mouseV[0] != -1) && (count < 100));
				finp.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			graph.plotValues(values, valuesEnd);

			// if (i < 59) {
			//
			// final Handler handler = new Handler();
			// Timer t = new Timer();
			// t.schedule(new TimerTask() {
			// public void run() {
			// handler.post(new Runnable() {
			// public void run() {
			// addValue();
			// }
			// });
			// }
			// }, 80);
			//
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

	public OnClickListener mCorkyListener = new OnClickListener() {
		public void onClick(View v) {
			// do something when the button is clicked
		}
	};

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