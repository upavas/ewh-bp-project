package com.ewhoxford.android.mouseInputDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

public class MiceStreamActivityListener extends Observable {

	private static final long SLEEP_TIME = 300;
	private Map<Long, char[]> bpMeasureHistoryTimeMap;
	private LinkedList<Number> bpMeasureArray;

	{
		bpMeasureHistoryTimeMap = new HashMap<Long, char[]>();
		bpMeasureArray = new LinkedList<Number>();
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

		// miceReaderRun();
		miceReaderThread = new Thread(new Runnable() {
			public void run() {
				miceReaderRun();
			}
		}, "Mice Reader");
		miceReaderThread.start();
	}

	// public class Generator1 extends Thread {

	public void miceReaderRun() {
		File f;
		f = new File("/dev/input/mice");
		int yValue = 0;
		if (!f.exists() && f.length() < 0) {
			System.out.println("The specified file is not exist");
			notifyObservers(new FileNotFoundException(
					"/dev/input/mice not available"));
		} else {
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
					bpMeasureArray.add(yValue);
					setChanged();
					notifyObservers(mouseV);
					if (!active) {
						finp.close();
					}
					// Thread.currentThread().sleep(1000);
					// Thread.sleep(SLEEP_TIME);
				}
			} catch (IOException e) {
				e.printStackTrace(System.err);
				notifyObservers(e);
			}
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Map<Long, char[]> getBpMeasureHistoryTimeMap() {
		return bpMeasureHistoryTimeMap;
	}

	public void setBpMeasureHistoryTimeMap(
			Map<Long, char[]> bpMeasureHistoryTimeMap) {
		this.bpMeasureHistoryTimeMap = bpMeasureHistoryTimeMap;
	}

	public Thread getMiceReaderThread() {
		return miceReaderThread;
	}

	public void setMiceReaderThread(Thread miceReaderThread) {
		this.miceReaderThread = miceReaderThread;
	}

	public void setBpMeasureArray(LinkedList<Number> bpMeasureArray) {
		this.bpMeasureArray = bpMeasureArray;
	}

	public LinkedList<Number> getBpMeasureArray() {
		return bpMeasureArray;
	}

}
