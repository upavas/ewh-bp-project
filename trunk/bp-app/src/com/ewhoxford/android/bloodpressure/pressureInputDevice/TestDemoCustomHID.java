/********************************************************************
 Software License Agreement:

 The software supplied herewith by Microchip Technology Incorporated
 (the �Company�) for its PIC� Microcontroller is intended and
 supplied to you, the Company�s customer, for use solely and
 exclusively on Microchip PIC Microcontroller products. The
 software is owned by the Company and/or its supplier, and is
 protected under applicable copyright laws. All rights are reserved.
 Any use in violation of the foregoing restrictions may subject the
 user to criminal sanctions under applicable laws, as well as to
 civil liability for the breach of the terms and conditions of this
 license.

 THIS SOFTWARE IS PROVIDED IN AN �AS IS� CONDITION. NO WARRANTIES,
 WHETHER EXPRESS, IMPLIED OR STATUTORY, INCLUDING, BUT NOT LIMITED
 TO, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 PARTICULAR PURPOSE APPLY TO THIS SOFTWARE. THE COMPANY SHALL NOT,
 IN ANY CIRCUMSTANCES, BE LIABLE FOR SPECIAL, INCIDENTAL OR
 CONSEQUENTIAL DAMAGES, FOR ANY REASON WHATSOEVER.
 ********************************************************************/

package com.ewhoxford.android.bloodpressure.pressureInputDevice;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.ewhoxford.android.bloodpressure.R;
import com.ewhoxford.android.bloodpressure.utils.ReadCSV;

/**
 * 
 * DemoCustomHID class is a demo class that interfaces to the HID custom HID
 * demos located in the Microchip Application Library USB framework.
 * www.microchip.com/usb
 * 
 * This example shows how to connect an Android device with USB host capability
 * to a device using a custom class driver.
 */
public class TestDemoCustomHID extends Demo implements Runnable, DemoInterface {
	// private UsbDevice device = null;
	private UsbManager manager = null;
	private Handler handler = null;
	private Boolean closeRequested = new Boolean(false);
	private UsbDeviceConnection connection;
	private UsbInterface intf;
	private boolean connected = false;
	private Context context = null;
	Thread thread;
	private double pressureValue = 0;
	private double pressureValueFiltered = 0;

	public static final int SIGNAL1 = 0;
	// private static final int SAMPLE_SIZE = 1;
	private static final int MAX_SIZE = 2000;

	// private static final int MAX_SIZE =
	// MeasureActivity.BOUNDARY_NUMBER_OF_POINTS;

	private int count = 0;
	private boolean active = true;
	int countMiceSamples = 0;
	int linearFilterThreshold = 40;
	int mouseDisconnectedCount = 0;

	private LinkedList<Number> bpMeasureFilteredHistory = new LinkedList<Number>();

	private LinkedList<Number> bpMeasureHistory = new LinkedList<Number>();

	private int x = 0;
	private int y = 0;
	private MyObservable notifier;

	class MyObservable extends Observable {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}

	{
		notifier = new MyObservable();
	}

	/**
	 * Constructor - creates connection to device and launches the thread that
	 * runs the actual demo.
	 * 
	 * @param context
	 *            Context requesting to run the demo.
	 * @param device
	 *            The USB device to attach to.
	 * @param handler
	 *            The Handler where demo Messages should be sent.
	 */
	public TestDemoCustomHID(Context context, UsbDevice device, Handler handler) {
		/* Save the device and handler information for later use. */
		// this.device = device;
		this.handler = handler;

		/* Get the USB manager from the requesting context */
		this.manager = (UsbManager) context
				.getSystemService(Context.USB_SERVICE);
		this.context = context;

		/*
		 * Get the required interface from the USB device. In this case we are
		 * hard coding the interface number to 0. In a dynamic example the code
		 * could scan through the interfaces to find the right interface. In
		 * this case since we know the exact device we are connecting to, we can
		 * hard code it.
		 */
		// intf = device.getInterface(0);

		/* Open a connection to the USB device */
		// connection = manager.openDevice(device);
		//
		// if (connection == null) {
		// return;
		// }
		//
		// /* Claim the required interface to gain access to it */
		// if (connection.claimInterface(intf, true) == true) {
		thread = new Thread(this);
		thread.start();
		// connected = true;
		// } else {
		// /*
		// * if the interface claim failed, we should close the connection and
		// * exit.
		// */
		// connection.close();
		// }
	}

	/**
	 * @return boolean Indicates if the connection to the USB device was
	 *         successfully made.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @return String Returns the title/description of the device
	 */
	public String getDeviceTitle() {
		// if (device != null) {
		// return "Custom HID Device Demo (VID = 0x"
		// + Integer.toHexString(device.getVendorId()) + " PID = 0x"
		// + Integer.toHexString(device.getProductId()) + ")";
		// }

		return null;
	}

	/**
	 * Request that the demo close itself.
	 */
	public void close() {
		connected = false;

		/*
		 * We should synchronize to the closeRequested object here to insure
		 * that the running thread isn't in the middle of checking this object
		 * when we change it.
		 */
		synchronized (closeRequested) {
			closeRequested = true;
		}
	}

	/**
	 * The man thread for the demo
	 */
	@Override
	public void run() {

		try {

			// new Thread() {
			// public void run() {
			// miceReaderRun();
			// }
			// }.start();
			ReadCSV r = new ReadCSV();

			float[][] pressureValues = r.readCSV2(context.getResources()
					.openRawResource(R.raw.mstest2));

			int l = pressureValues.length;

			int k = 1;
			while (k < l) {
				bpMeasureHistory.add(pressureValues[k][0]);
				bpMeasureFilteredHistory.add(pressureValues[k][1]);
				k = k + 1;
			}

			while (active) {

				Thread.sleep(10);

				int j = 1;
				while (j < 101) {

					// signal processing problem correction

					if (j == 100) {

						pressureValue = pressureValues[count][0];
						pressureValueFiltered = pressureValues[count][1];

					}
					j++;
					count = count + 1;
					;
					if (count == l) {
						active = false;
						// this.close();
					}
				}

				notifier.notifyObservers();

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/***********************************************************************
	 * Private methods
	 ***********************************************************************/

	/**
	 * @return boolean Indicates if someone has requested to close the demo
	 */
	private boolean wasCloseRequested() {
		synchronized (closeRequested) {
			return closeRequested;
		}
	}

	/**
	 * Closes connections, releases resources, cleans up variables
	 */
	private void destroy() {
		/*
		 * Release the interface that was previously claimed and close the
		 * connection.
		 */
		connection.releaseInterface(intf);
		connection.close();

		/* Clear up all of the locals */
		// device = null;
		manager = null;
		handler = null;
		closeRequested = false;
		connection = null;
		intf = null;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LinkedList<Number> getBpMeasureHistory() {
		return bpMeasureHistory;
	}

	public void setBpMeasureHistory(LinkedList<Number> bpMeasureHistory) {
		this.bpMeasureHistory = bpMeasureHistory;
	}

	public double getPressureValue() {
		return pressureValue;
	}

	public void setPressureValue(double d) {
		this.pressureValue = d;
	}

	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}

	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

	public void addObserver(Observer observer) {
		notifier.addObserver(observer);
	}

	public void removeObserver(Observer observer) {
		notifier.deleteObserver(observer);
	}

	public LinkedList<Number> getBpMeasureFilteredHistory() {
		return bpMeasureFilteredHistory;
	}

	public void setBpMeasureFilteredHistory(
			LinkedList<Number> bpMeasureFilteredHistory) {
		this.bpMeasureFilteredHistory = bpMeasureFilteredHistory;
	}

	public double getPressureValueFiltered() {
		return pressureValueFiltered;
	}

	public void setPressureValueFiltered(double pressureValueFiltered) {
		this.pressureValueFiltered = pressureValueFiltered;
	}

}
