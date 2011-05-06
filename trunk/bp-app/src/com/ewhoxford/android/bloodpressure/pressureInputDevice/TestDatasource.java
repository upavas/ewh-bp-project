package com.ewhoxford.android.bloodpressure.pressureInputDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Handler;

import com.ewhoxford.android.bloodpressure.signalProcessing.ConvertTommHg;
import com.ewhoxford.android.bloodpressure.signalProcessing.TimeSeriesMod;
import com.ewhoxford.android.bloodpressure.utils.ReadCSV;

/**
 * 
 * @author mauro
 * 
 */
public class TestDatasource implements Runnable {

	// encapsulates management of the observers watching this datasource for
	// update events:
	class MyObservable extends Observable {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}

	Activity activity;

	public TestDatasource(Activity activity) {
		this.activity = activity;
	}

	public static final int SIGNAL1 = 0;
	// private static final int SAMPLE_SIZE = 1;

	private double pressureValue = 0;
	private MyObservable notifier;
	private int count = 0;
	private boolean active = true;
	int countMiceSamples = 0;
	int linearFilterThreshold = 20;
	final Handler mHandler = new Handler();
	// Create runnable for posting
	final Runnable runSignalAcquisition = new Runnable() {
		public void run() {
			miceReaderRun();
		}
	};
	final Runnable updataBPResultView = new Runnable() {
		public void run() {

		}
	};

	private LinkedList<Number> bpMeasure = new LinkedList<Number>();

	private LinkedList<Number> bpMeasureHistory = new LinkedList<Number>();

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LinkedList<Number> getBpMeasure() {
		return bpMeasure;
	}

	public void setBpMeasure(LinkedList<Number> bpMeasure) {
		this.bpMeasure = bpMeasure;
	}

	public LinkedList<Number> getBpMeasureHistory() {
		return bpMeasureHistory;
	}

	public void setBpMeasureHistory(LinkedList<Number> bpMeasureHistory) {
		this.bpMeasureHistory = bpMeasureHistory;
	}

	{
		notifier = new MyObservable();
	}

	// @Override
	public void run() {
		try {

			// new Thread() {
			// public void run() {
			// miceReaderRun();
			// }
			// }.start();
			ReadCSV r = new ReadCSV();
			int[][] pressureValues = r.readCSV(activity);
			int l = pressureValues.length;
			TimeSeriesMod pressureValuesMod = ConvertTommHg.convertArrayTommHg(
					pressureValues, 100);
			double[] pressureValuesFloat = pressureValuesMod.getPressure();

			while (active) {

				Thread.sleep(5);

				int j = 1;
				while (j < 1000) {

					pressureValue = pressureValuesFloat[count];
					// signal processing problem correction
					if (bpMeasure.size() != 0)
						if (Math.abs(bpMeasure.getLast().doubleValue()
								- pressureValue) > linearFilterThreshold) {
							pressureValue = bpMeasure.getLast().doubleValue();
						}
					bpMeasure.add(pressureValue);

					j++;
					count++;
				}

				notifier.notifyObservers();

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int getItemCount(int series) {
		return count;
	}

	public Number getX(int series, int index) {
		// if (index >= SAMPLE_SIZE) {
		// throw new IllegalArgumentException();
		// }
		return index;
	}

	public Number getY(int series, int index) {
		// if (index >= SAMPLE_SIZE) {
		// throw new IllegalArgumentException();
		// }
		switch (series) {
		case SIGNAL1:
			return pressureValue;

		default:
			throw new IllegalArgumentException();
		}
	}

	public void addObserver(Observer observer) {
		notifier.addObserver(observer);
	}

	public void removeObserver(Observer observer) {
		notifier.deleteObserver(observer);
	}

	public double getPressureValue() {
		return pressureValue;
	}

	public void setPressureValue(float pressureValue) {
		this.pressureValue = pressureValue;
	}

	public void miceReaderRun() {
		File f;
		f = new File("/dev/input/mice");
		int yValue = 0;
		int xValue = 0;
		if (!f.exists() && f.length() < 0) {
			System.out.println("The specified file is not exist");

		} else {
			try {
				FileInputStream finp = new FileInputStream(f);

				char[] mouseV = { 0, 0, 0 };

				while (active) {

					int i = 0;
					while (i <= 2) {
						mouseV[i] = (char) finp.read();
						i = i + 1;
					}
					// System.out.println("" + (int) mouseV[0] + ","
					// + (int) mouseV[1] + "," + (int) mouseV[2]);
					i = 0;
					xValue = (int) (mouseV[1]);
					yValue = (int) (mouseV[2]);

					// int bpSignalLenght = values.length;
					// RmZeros r1 = new RmZeros();
					// int vals1[][]= r1.rmZeros(values);

					double aux = ConvertTommHg.convertTommHg(xValue, yValue);

					if (bpMeasureHistory.size() != 0)
						if (Math.abs(bpMeasureHistory.getLast().doubleValue()
								- aux) > 3) {
							aux = bpMeasureHistory.getLast().doubleValue();
						}

					bpMeasureHistory.add(aux);
					countMiceSamples++;

				}

				if (!active) {
					finp.close();
				}
			} catch (IOException e) {
				e.printStackTrace(System.err);

			}
		}
	}

}
