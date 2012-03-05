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

package com.microchip.android.BasicUSBDeviceDemo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.LinkedList;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;

/**
 * 
 * DemoCustomHID class is a demo class that interfaces to the HID custom HID
 * demos located in the Microchip Application Library USB framework.
 * www.microchip.com/usb
 * 
 * This example shows how to connect an Android device with USB host capability
 * to a device using a custom class driver.
 */
public class DemoCustomHID1 extends Demo implements Runnable, DemoInterface1 {
	private UsbDevice device = null;
	private UsbManager manager = null;
	private Handler handler = null;
	private Integer toggleLEDCount = 0;
	private boolean lastButtonState = false;
	private boolean buttonStatusInitialized = false;
	private Boolean closeRequested = new Boolean(false);
	private UsbDeviceConnection connection;
	private UsbInterface intf;
	private boolean connected = false;
	Thread thread;
	LinkedList<Number> history1= new LinkedList<Number>();
	LinkedList<Number> history2= new LinkedList<Number>();
	int a=1;
	private final static int CUSTOM_HID_DEMO_BUTTON_PRESSED = (int) 0x00;

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
	DemoCustomHID1(Context context, UsbDevice device, Handler handler) {
		/* Save the device and handler information for later use. */
		this.device = device;
		this.handler = handler;

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
		/* Get the OUT endpoint. It is the second endpoint in the interface */
		UsbEndpoint endpointOUT = intf.getEndpoint(1);

		/* Get the IN endpoint. It is the first endpoint in the interface */
		UsbEndpoint endpointIN = intf.getEndpoint(0);

		byte[] getPotentiometerResults = new byte[64];
		
		int potentiometerLastResults = Integer.MAX_VALUE;
		int result = 0;

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
//			try {
//				Thread.sleep(13);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}

			/* Read the results of that request */
			do {
				result = connection.bulkTransfer(endpointIN,
						getPotentiometerResults,
						getPotentiometerResults.length, 1000);
			} while ((result < 0) && (wasCloseRequested() == false));

			/* Convert the resulting data to an int */
			short[] potentiometerBuffer = new short[] { 0, 0};
			potentiometerBuffer[0] = getPotentiometerResults[0];
			potentiometerBuffer[1] = getPotentiometerResults[1];
		//	potentiometerBuffer[2] = getPotentiometerResults[2];
		//	potentiometerBuffer[3] = getPotentiometerResults[3];

//			ShortBufferBuffer buf = ByteBuffer.wrap(potentiometerBuffer);
//			buf.order(ByteOrder.LITTLE_ENDIAN);
//			int potentiometerResults = buf.getInt();
			//--------------------------------------------------------------
			long potentiometerResultsString1= (long)potentiometerBuffer[0];
			long potentiometerResultsString2= (long)potentiometerBuffer[1];
			//long potentiometerResultsString3= (long)potentiometerBuffer[2];
			//long potentiometerResultsString4= (long)potentiometerBuffer[3];
			//------------------------------------------------------
			history1.add(potentiometerResultsString1);
			history2.add(potentiometerResultsString2);
			
			/*
			 * If the new results are different from the previous results, then
			 * send a message to the specified handler containing the new data.
			 */
			a= history1.size() % 75 ;
			if(a==0){
				String ps="";
				ps+='(';
				ps+=history1.getLast();
				ps+=')';
				ps+='(';
				ps+=history2.getLast();
				ps+=')';
				ps+='(';
			
			handler.obtainMessage(
					0,
					new MessagePotentiometer(
							0,ps))
					.sendToTarget();
			}

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
		device = null;
		manager = null;
		handler = null;
		toggleLEDCount = 0;
		lastButtonState = false;
		buttonStatusInitialized = false;
		closeRequested = false;
		connection = null;
		intf = null;
	}

}
