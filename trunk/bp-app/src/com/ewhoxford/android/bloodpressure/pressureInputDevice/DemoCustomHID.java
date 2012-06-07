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
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import com.ewhoxford.android.bloodpressure.signalProcessing.ConvertTommHg;

/**
 * 
 * DemoCustomHID class is a demo class that interfaces to the HID custom HID
 * demos located in the Microchip Application Library USB framework.
 * www.microchip.com/usb
 * 
 * This example shows how to connect an Android device with USB host capability
 * to a device using a custom class driver.
 */
public class DemoCustomHID extends Demo implements Runnable, DemoInterface {
	private UsbDevice device = null;
	private UsbManager manager = null;
	private Boolean closeRequested = new Boolean(false);
	private UsbDeviceConnection connection;
	private UsbInterface intf;
	private boolean connected = false;
	Thread thread;
	private double pressureValue = 0;
	private double pressureValueFiltered = 0;

	public static final int SIGNAL1 = 0;
	// private static final int SAMPLE_SIZE = 1;

	// private static final int MAX_SIZE =
	// MeasureActivity.BOUNDARY_NUMBER_OF_POINTS;

	private int count = 0;
	private boolean active = true;
	int countMiceSamples = 0;
	int linearFilterThreshold = 40;
	int mouseDisconnectedCount = 0;

	private LinkedList<Number> bpMeasure = new LinkedList<Number>();

	private LinkedList<Number> bpMeasureHistory = new LinkedList<Number>();
	private LinkedList<Number> bpMeasureFilteredHistory = new LinkedList<Number>();
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
	public DemoCustomHID(Context context, UsbDevice device, Handler handler) {
		/* Save the device and handler information for later use. */
		this.device = device;
		/* Get the USB manager from the requesting context */
		this.manager = (UsbManager) context
				.getSystemService(Context.USB_SERVICE);

		/*
		 * Get the required interface from the USB device. In this case we are
		 * hard coding the interface number to 0. In a dynamic example the code
		 * could scan through the interfaces to find the right interface. In
		 * this case since we know the exact device we are connecting to, we can
		 * hard code it.
		 */
		intf = device.getInterface(0);

		/* Open a connection to the USB device */
		connection = manager.openDevice(device);

		if (connection == null) {
			return;
		}

		/* Claim the required interface to gain access to it */
		if (connection.claimInterface(intf, true) == true) {
			thread = new Thread(this);
			thread.start();
			connected = true;
		} else {
			/*
			 * if the interface claim failed, we should close the connection and
			 * exit.
			 */
			connection.close();
		}
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
		if (device != null) {
			return "Custom HID Device Demo (VID = 0x"
					+ Integer.toHexString(device.getVendorId()) + " PID = 0x"
					+ Integer.toHexString(device.getProductId()) + ")";
		}

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

		/* Get the IN endpoint. It is the first endpoint in the interface */
		UsbEndpoint endpointIN = intf.getEndpoint(0);
		/*
		 * Create the packets that we are going to send to the attached USB
		 * device.
		 */
		// byte[] getPotentiometerRequest = new byte[]{(byte)0x37};
		//
		byte[] getPotentiometerResults = new byte[64];
		// int potentiometerLastResults = Integer.MAX_VALUE;
		int result = 0;
		int yValue = 0;
		int xValue = 0;
		int yValueFiltered = 0;
		int xValueFiltered = 0;

		int mod = 1;
		// int currentPosition = 0;
		// boolean update = false;
		try {
			while (true) {
				/*
				 * If the connection was closed, destroy the connections and
				 * variables and exit this thread.
				 */
				if (wasCloseRequested() == true) {
					destroy();
					return;
				}

				/* Sleep the thread for a while */
				try {
					Thread.sleep(13);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				/* Read the results of that request */
				do {
					result = connection.bulkTransfer(endpointIN,
							getPotentiometerResults,
							getPotentiometerResults.length, 1000);
				} while ((result < 0) && (wasCloseRequested() == false));

				/* Convert the resulting data to an int */
				byte[] potentiometerBuffer = new byte[] { 0, 0, 0, 0 };// MAURO
																		// CHANGED
																		// THIS
																		// TO 2
																		// BUFFERS
				potentiometerBuffer[0] = getPotentiometerResults[0];
				potentiometerBuffer[1] = getPotentiometerResults[1];
				potentiometerBuffer[2] = getPotentiometerResults[2];
				potentiometerBuffer[3] = getPotentiometerResults[3];

				xValue = (int) (potentiometerBuffer[0]);
				yValue = (int) (potentiometerBuffer[1]);
				xValueFiltered = (int) (potentiometerBuffer[2]);
				yValueFiltered = (int) (potentiometerBuffer[3]);
				// setX(xValue);
				// setY(yValue);

				double aux = ConvertTommHg.convertTommHg(xValueFiltered, yValueFiltered);
				double aux2 = ConvertTommHg.convertToVolt(xValue,
						yValue);
				bpMeasureHistory.add(aux);
				bpMeasureFilteredHistory.add(aux2);
				// TODO : check this code.

				if (bpMeasureHistory.size() != 0) {

					pressureValue = bpMeasureHistory.getLast().doubleValue();
					pressureValueFiltered = bpMeasureFilteredHistory.getLast()
							.doubleValue();
				//	System.out.printf("e do DEMOCUSTOMHID:"+count);
					count++;
					mod = count % 75;

					if (mod == 0) {
						 Log.v("DemoCustom", "e do MeasureActiviry: Im here 2"
						 + bpMeasureHistory.size());
						// handler.obtainMessage(0,
						// new MessageSampledPressure(pressureValue))
						// .sendToTarget();
						this.setPressureValue(bpMeasureHistory.getLast()
								.doubleValue());
						this.setPressureValueFiltered(pressureValueFiltered);
						this.setBpMeasureHistory(bpMeasureHistory);
						this.setBpMeasureFilteredHistory(bpMeasureFilteredHistory);
						notifier.notifyObservers();
					}
				}
				//
				// ByteBuffer buf = ByteBuffer.wrap(potentiometerBuffer);
				// buf.order(ByteOrder.LITTLE_ENDIAN);
				// int potentiometerResults = buf.getInt();
				//
				/*
				 * If the new results are different from the previous results,
				 * then send a message to the specified handler containing the
				 * new data.
				 */

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("DemoCustomHID", "e do DEMOCUSTOMHID");
		}
	}

	private void setY(int yValue) {
		this.y = yValue;

	}

	private void setX(int xValue) {
		this.x = xValue;

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
		device = null;
		manager = null;
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
