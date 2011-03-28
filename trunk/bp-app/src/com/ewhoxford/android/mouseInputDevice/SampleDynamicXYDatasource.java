package com.ewhoxford.android.mouseInputDevice;

import java.util.Observable;
import java.util.Observer;

public class SampleDynamicXYDatasource implements Runnable {

	// encapsulates management of the observers watching this datasource for
	// update events:
	class MyObservable extends Observable {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}

	private static final int MAX_AMP_SEED = 100;
	private static final int MIN_AMP_SEED = 10;
	private static final int AMP_STEP = 5;
	public static final int SIGNAL1 = 0;
	public static final int SIGNAL2 = 1;
	private static final int SAMPLE_SIZE = 1;

	private float pressureValue = 0;
	private MyObservable notifier;
	private int count = 0;;

	{
		notifier = new MyObservable();
	}

	// @Override
	public void run() {
		try {

			ReadCSV r = new ReadCSV();
			int[][] values = r.readCSV();
			int bpSignalLenght = values.length;
			float[] converted = new float[bpSignalLenght];

			int i = 0;
			int x = 0;
			while (i <= bpSignalLenght) {
				x = 0;
				if (values[i][1] == 1)
					x = 2 ^ 8;
			}
			if (values[i][1] == 1) {
				x = 2 ^ 9;
			}
			if (values[i][1] == 1) {
				x = 2 ^ 8 + 2 ^ 9;
			}

			converted[i] = ((((float) values[i][2] + (float) x / 1024F) - 0.04F) / 0.018F * 7.5F);

			boolean isRising = true;
			i = 0;
			while (true) {

				Thread.sleep(50); // decrease or remove to speed up the refresh

				if (i > bpSignalLenght) {

				} else
					pressureValue = converted[i];

				if (isRising) {
					pressureValue = 0;
				} else {
					pressureValue = converted[i];
				}
				i++;
				count++;
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
		if (index >= SAMPLE_SIZE) {
			throw new IllegalArgumentException();
		}
		return index;
	}

	public Number getY(int series, int index) {
		if (index >= SAMPLE_SIZE) {
			throw new IllegalArgumentException();
		}

		switch (series) {
		case SIGNAL1:
			return pressureValue;
		case SIGNAL2:
			return 0;
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

}
