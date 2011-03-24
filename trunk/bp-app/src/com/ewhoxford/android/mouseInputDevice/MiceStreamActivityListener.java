package com.ewhoxford.android.mouseInputDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.ewhoxford.android.bloodpressure.model.PressureDataPoint;

public class MiceStreamActivityListener extends Observable {

	private static final long SLEEP_TIME = 300;
	private Map<Long, char[]> bpMeasureHistoryTimeMap;

	{
		bpMeasureHistoryTimeMap = new HashMap<Long, char[]>();
	}

	private boolean active = true;

	// public MiceStreamActivityListener() {
	//
	// File f;
	// f = new File("/dev/input/mice");
	// // int yValue = 0;
	// if (!f.exists() && f.length() < 0)
	// System.out.println("The specified file is not exist");
	// else {
	//
	// try {
	//
	//				
	// FileInputStream finp = new FileInputStream(f);
	//
	// int count = 0;
	// final char[] mouseV = { 0, 0, 0 };
	// do {
	// count++;
	// int i = 0;
	// while (i <= 2) {
	// mouseV[i] = (char) finp.read();
	// i = i + 1;
	// }
	// mouseV[3] = (char) finp.read();
	// System.out.println("" + (int) mouseV[0] + ","
	// + (int) mouseV[1] + "," + (int) mouseV[2]);
	//
	// i = 0;
	//
	// // signal processing here
	//
	// // yValue = (int) (mouseV[2]);
	// while (active) {
	// final Handler handler = new Handler();
	// Timer t = new Timer();
	// t.schedule(new TimerTask() {
	// public void run() {
	// handler.post(new Runnable() {
	// public void run() {
	// setMouse(mouseV);
	// }
	// });
	// }
	// }, 80);
	// }
	//
	// } while ((mouseV[0] != -1) && (active));
	// finp.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// }

	// The thread, if any, which is currently reading. Null if not running.
	private Thread miceReaderThread = null;

	public MiceStreamActivityListener() {
		// new Generator1().start();

		miceReaderThread = new Thread(new Runnable() {
			public void run() {
				miceReaderRun();
			}
		}, "Mice Reader");
		miceReaderThread.start();

		// get rid the oldest sample in history:
		long time = 0;

		while (active) {
			try {

				Thread.sleep(1000);
				time = System.currentTimeMillis();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// add the latest history sample:

			int yValue;
			char[] bpValue = { 0, 0, 0 };
			if (bpMeasureHistoryTimeMap.get(time) == null) {
				yValue = 0;
				System.out.println("value1:" + yValue);
			} else {
				bpValue = bpMeasureHistoryTimeMap.get(time);
				yValue = (int) bpValue[2];
				System.out.println("value2:" + yValue);
			}

			PressureDataPoint p = new PressureDataPoint();
			p.setMouseData(bpValue);
			p.setTime(time);
			setChanged();
			notifyObservers(p);

		}

	}

	// public class Generator1 extends Thread {

	public void miceReaderRun() {
		File f;
		f = new File("/dev/input/mice");
		int yValue = 0;
		if (!f.exists() && f.length() < 0)
			System.out.println("The specified file is not exist");
		else {
			try {

				FileInputStream finp = new FileInputStream(f);
				int count = 0;
				char[] mouseV = { 0, 0, 0 };

				while (true) {

					count++;
					int i = 0;

					while (i <= 2) {
						mouseV[i] = (char) finp.read();
						i = i + 1;
					}
					System.out.println("" + (int) mouseV[0] + ","
							+ (int) mouseV[1] + "," + (int) mouseV[2]);
					i = 0;
					yValue = (int) (mouseV[2]);

					bpMeasureHistoryTimeMap.put(System.currentTimeMillis(),
							mouseV);

					if (!active) {
						finp.close();
					}
					// Thread.currentThread().sleep(1000);
					// Thread.sleep(SLEEP_TIME);
				}
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
