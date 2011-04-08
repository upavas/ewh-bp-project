package com.ewhoxford.android.mouseInputDevice;


import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import com.ewhoxford.android.bloodpressure.signalProcessing.ConvertTommHg;
import com.ewhoxford.android.bloodpressure.signalProcessing.RmZeros;
import com.ewhoxford.android.bloodpressure.signalProcessing.TimeSeriesMod;

public class TestDataSource implements Runnable {

	// encapsulates management of the observers watching this datasource for
	// update events:
	class MyObservable extends Observable {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}

	public static final int SIGNAL1 = 0;
	// private static final int SAMPLE_SIZE = 1;

	private double pressureValue = 0;
	private MyObservable notifier;
	private int count = 0;
	private boolean active = true;

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

			ReadCSV r = new ReadCSV();
			int[][] values = r.readCSV();
			int bpSignalLenght = values.length;
			RmZeros r1 = new RmZeros();
		    int vals1[][]= r1.rmZeros(values); 
			
			TimeSeriesMod aux5 = ConvertTommHg.convertArrayTommHg(vals1, 100);
			double[] arrayPressure=aux5.getPressure();
			//float[] converted = convert2Pressure(values);
			int i = 0;
			int j = 0;
			while (active) {

				Thread.sleep(5); // decrease or remove to speed up the refresh
				j = 0;
				while (j <= 1000) {
					if (i < bpSignalLenght) {
						pressureValue = arrayPressure[i];
						bpMeasure.add(pressureValue);
					} else {
						pressureValue = 0;
						bpMeasure.add(pressureValue);
					}
					j++;
					i++;
				}
				// i = i + 100;
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

	public float[] convert2Pressure(int[][] vals1) {
		int valsx = 0;
		int valsy = 0;
		float aux1 = 0;
		float aux2 = 0;
		int l = vals1.length;
		float[] vals = new float[l];

		int i = 0;

		while (i < l) {
			valsy = Math.abs(vals1[i][1] - 255);
			if (vals1[i][0] == 1) {
				valsx = 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2;
			} else if (vals1[i][0] == 2) {
				valsx = 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2;
			} else if (vals1[i][0] == 3) {
				valsx = (2 * 2 * 2 * 2 * 2 * 2 * 2 * 2)
						+ (2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2);
			} else {
				valsx = 0;
			}
			aux1 = (float) (valsx + valsy) / 1024;
			aux2 = (float) (aux1 - 0.04);
			vals[i] = (float) (aux2 * 7.50061683 / 0.018);
			bpMeasureHistory.add(vals[i]);
			i++;
		}
		return vals;

	}

	public double getPressureValue() {
		return pressureValue;
	}

	public void setPressureValue(float pressureValue) {
		this.pressureValue = pressureValue;
	}
}

