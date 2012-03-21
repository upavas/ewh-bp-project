package org.ewhoxford.swt.bloodpressure.pressureInputDevice;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.ewhoxford.swt.bloodpressure.signalProcessing.ConvertTommHg;

/**
 * 
 * @author mauro
 * 
 */
public class SerialDynamicXYDatasource implements Runnable {

	// encapsulates management of the observers watching this datasource for
	// update events:
	class MyObservable extends Observable {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}

	public class SimpleRead implements Runnable {

		private long previousTime = 0;
		public Thread readThread;

		public SimpleRead() {
			try {
				inputStream = serialPort.getInputStream();
				//				
				// serialPort.notifyOnDataAvailable(true);
				//
				// try {
				// serialPort.addEventListener(this);
				// } catch (TooManyListenersException e) {
				// }
				//				
				readThread = new Thread(this);
				readThread.start();
			} catch (IOException e) {
			}
		}

		public void run() {
			int chunkSize = 15;
			byte[] readBuffer = new byte[chunkSize];
			String answer = "";
			try {
				long elapsedTime = 0;

				if (previousTime == 0) {
					previousTime = System.currentTimeMillis();
				}

				while (inputStream.available() < chunkSize
						&& elapsedTime < 3000) {
					elapsedTime = Math.abs(System.currentTimeMillis()
							- previousTime);
				}

				int numBytes = inputStream.read(readBuffer, 0, chunkSize);

				answer = new String(readBuffer);

				long end = System.currentTimeMillis();

				System.out.print(answer);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// TODO validation

			if (answer.contains("san")) {
				deviceFound = true;
				new Thread() {

					public void run() {

						// new SerialReader(inputStream);
						run2(inputStream);
					};

				}.start();

			} else {
				serialPort.close();
			}

			System.out.println("Ice,ice baby!!!");
		}

		/**
		 * Method declaration
		 * 
		 * 
		 * @param event
		 * 
		 * @see
		 */
		public void serialEvent(SerialPortEvent event) {
			switch (event.getEventType()) {

			case SerialPortEvent.BI:

			case SerialPortEvent.OE:

			case SerialPortEvent.FE:

			case SerialPortEvent.PE:

			case SerialPortEvent.CD:

			case SerialPortEvent.CTS:

			case SerialPortEvent.DSR:

			case SerialPortEvent.RI:

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;

			case SerialPortEvent.DATA_AVAILABLE:
				// int chunkSize = 10;
				// byte[] readBuffer = new byte[chunkSize];
				// String answer = "";
				// try {
				// long elapsedTime = 0;
				//
				// // if (previousTime == 0) {
				// // previousTime = System.currentTimeMillis();
				// // }
				// //
				// // while (inputStream.available() < chunkSize
				// // && elapsedTime < 2000) {
				// // elapsedTime = Math.abs(System.currentTimeMillis()
				// // - previousTime);
				// // }
				//
				// int numBytes = inputStream.read(readBuffer, 0, chunkSize);
				//
				// answer = new String(readBuffer);
				//
				// long end = System.currentTimeMillis();
				//
				// System.out.print(answer);
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				//
				// // TODO validation
				//
				// if (answer.contains("ice")) {
				// deviceFound = true;
				// new Thread() {
				//
				// public void run() {
				//
				// new SerialReader(inputStream);
				//
				// };
				//
				// }.start();
				//
				// } else {
				// serialPort.close();
				// }

				break;
			}
		}
	}

	public static final int SIGNAL1 = 0;
	// private static final int SAMPLE_SIZE = 1;

	private static final int MAX_SIZE = 200;

	private double pressureValue = 0;
	private MyObservable notifier;
	private int count = 0;
	private boolean active = true;
	private int countMiceSamples = 0;
	private int linearFilterThreshold = 40;
	private int mouseDisconnectedCount = 0;
	private static String serial = "";
	private SerialPort serialPort;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	private boolean deviceFound;
	// final Handler mHandler = new Handler();

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
			detectPorts();

			int j = 0;
			int currentPosition = 0;
			boolean update = false;
			while (active) {

				Thread.sleep(100); // decrease or remove to speed up the
				// refresh
				currentPosition = countMiceSamples;

				// if (currentPosition==lastPosition) {
				// Thread.sleep(20);
				// currentPosition = countMiceSamples;
				// }
				// update = false;
				// while (j < currentPosition) {
				// if (bpMeasureHistory.size() != 0) {
				// pressureValue = bpMeasureHistory.get(j).doubleValue();
				// // maintain array with max size=12000 points
				// if (bpMeasure.size() > MAX_SIZE) {
				// bpMeasure.removeFirst();
				// }
				// bpMeasure.add(bpMeasureHistory.get(j));
				// update = true;
				// }
				//				
				// j = j + 100;
				//				
				// }
				//	
				// count++;
				if (active) {
					mouseDisconnectedCount = 0;
					notifier.notifyObservers();
				} else {
					mouseDisconnectedCount = mouseDisconnectedCount + 1;
					if (mouseDisconnectedCount == 3) {
						notifier.notifyObservers();
					}
				}
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

	// public void miceReaderRun() {
	//		
	// Process p;
	// try {
	// // Preform su to get root privledges
	// p = Runtime.getRuntime().exec("su");
	// } catch (IOException e) {
	// // TODO Code to run in input/output exception
	// System.out.println("not root");
	// }
	// try {
	// // Preform su to get root privledges
	// p = Runtime.getRuntime().exec("chmod 755 /dev/input/mice");
	// } catch (IOException e) {
	// // TODO Code to run in input/output exception
	// System.out.println("could not change mouse values");
	// }
	//		
	// File f;
	// f = new File("/dev/input/mice");
	// int yValue = 0;
	// int xValue = 0;
	// if (!f.exists() && f.length() < 0) {
	// System.out.println("The specified file is not exist");
	//
	// } else {
	// try {
	// FileInputStream finp = new FileInputStream(f);
	//
	// char[] mouseV = { 0, 0, 0 };
	//
	// while (active) {
	//
	// int i = 0;
	// while (i <= 2) {
	// mouseV[i] = (char) finp.read();
	// i = i + 1;
	// }
	// // System.out.println("" + (int) mouseV[0] + ","
	// // + (int) mouseV[1] + "," + (int) mouseV[2]);
	// i = 0;
	// xValue = (int) (mouseV[1]);
	// yValue = (int) (mouseV[2]);
	//
	// // int bpSignalLenght = values.length;
	// // RmZeros r1 = new RmZeros();
	// // int vals1[][]= r1.rmZeros(values);
	//
	// double aux = ConvertTommHg.convertTommHg(xValue, yValue);
	//
	// if (bpMeasureHistory.size() != 0)
	// if (Math.abs(bpMeasureHistory.getLast().doubleValue()
	// - aux) > linearFilterThreshold) {
	// aux = bpMeasureHistory.getLast().doubleValue();
	// }
	//
	// bpMeasureHistory.add(aux);
	// countMiceSamples++;
	//
	// }
	//
	// if (!active) {
	// finp.close();
	// }
	// } catch (IOException e) {
	// e.printStackTrace(System.err);
	//
	// }
	// }
	// }

	public static void waiting(int n) {

		long t0, t1;

		t0 = System.currentTimeMillis();

		do {
			t1 = System.currentTimeMillis();
		} while (t1 - t0 < n);
	}

	public static boolean isWindows() {

		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	static String getPortTypeName(int portType) {
		switch (portType) {
		case CommPortIdentifier.PORT_I2C:
			return "I2C";
		case CommPortIdentifier.PORT_PARALLEL:
			return "Parallel";
		case CommPortIdentifier.PORT_RAW:
			return "Raw";
		case CommPortIdentifier.PORT_RS485:
			return "RS485";
		case CommPortIdentifier.PORT_SERIAL:
			return "Serial";
		default:
			return "unknown type";
		}
	}

	public void detectPorts() {
		String aux = "";
		// Boolean confirm = false;
		if (isWindows()) {

			java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
					.getPortIdentifiers();
			while (portEnum.hasMoreElements()) {
				CommPortIdentifier portIdentifier = portEnum.nextElement();
				System.out.println(portIdentifier.getName() + " - "
						+ getPortTypeName(portIdentifier.getPortType()));
				serial = portIdentifier.getName();
				try {
					confirmSerialPort(serial);
				} catch (NoSuchPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (isUnix()) {
			java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
					.getPortIdentifiers();
			while (portEnum.hasMoreElements()) {
				CommPortIdentifier portIdentifier = portEnum.nextElement();
				System.out.println(portIdentifier.getName() + " - "
						+ getPortTypeName(portIdentifier.getPortType()));
				aux = portIdentifier.getName();
				if (aux.contains("/dev/ttyACM")) {
					serial = portIdentifier.getName();
					try {
						confirmSerialPort(serial);// TODO this wrong if we have
						// other ACM ports active
					} catch (NoSuchPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// if (confirm)
					// return true;
				}
			}
		}
		// return false;
	}

	public void confirmSerialPort(String portName) throws NoSuchPortException {

		String messageString = "who";
		CommPortIdentifier portId = CommPortIdentifier
				.getPortIdentifier(portName);

		try {
			serialPort = (SerialPort) portId.open("SimpleWrite", 2000);
		} catch (PortInUseException e) {
			System.out.println("Port in use.");
		}

		SimpleRead sr = new SimpleRead();

		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
		}

		try {
			serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
		}

		try {
			serialPort.notifyOnOutputEmpty(true);
		} catch (Exception e) {
			System.out.println("Error setting event notification");
			System.out.println(e.toString());
			System.exit(-1);
		}

		System.out.println("Writing \"" + messageString + "\" to "
				+ serialPort.getName());

		try {
			outputStream.write(messageString.getBytes());
		} catch (IOException e) {
		}

		// waiting(500);

		// return active;

	}

	/*
	 * void connect(String portName) throws Exception {
	 * 
	 * CommPortIdentifier portIdentifier =
	 * CommPortIdentifier.getPortIdentifier(portName);
	 * 
	 * if (portIdentifier.isCurrentlyOwned()) {
	 * System.out.println("Error: Port is currently in use"); } else { CommPort
	 * commPort = portIdentifier.open(this.getClass().getName(), 2000);
	 * 
	 * if (commPort instanceof SerialPort) { serialPort = (SerialPort) commPort;
	 * serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
	 * SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	 * 
	 * InputStream in = serialPort.getInputStream(); OutputStream out =
	 * serialPort.getOutputStream();
	 * 
	 * (new Thread(new SerialReader(in))).start(); // (new Thread(new
	 * SerialWriter(out))).start();
	 * 
	 * } else { System.out
	 * .println("Error: Only serial ports are handled by this example."); } } }
	 * 
	 * public static class SimpleSerialWriter implements Runnable { static
	 * String messageString = "who?"; OutputStream out;
	 * 
	 * public SimpleSerialWriter ( OutputStream out ) { this.out = out; }
	 * 
	 * public void run () { try { this.out.write(messageString.getBytes()); }
	 * catch (IOException e) { e.printStackTrace(); } try { Thread.sleep(100);
	 * // Be sure data is txferred before closing } catch (Exception e) {}
	 * Thread.currentThread().interrupt(); } }
	 * 
	 * public static class SimpleSerialReader implements Runnable { InputStream
	 * in;
	 * 
	 * public SimpleSerialReader ( InputStream in ) { this.in = in; }
	 * 
	 * public void run () { byte[] buffer = new byte[1024]; int len = -1; try {
	 * while ( ( len = this.in.read(buffer)) > -1 ) { System.out.print(new
	 * String(buffer,0,len)); } } catch ( IOException e ) { e.printStackTrace();
	 * } } }
	 * 
	 * /**
	 */
	// public class SerialReader implements Runnable {
	//
	// private InputStream in;
	//
	// public SerialReader(InputStream in) {
	// this.in = in;
	// }

	public void run2(InputStream in) {
		int sizeBuffer = 1024;
		byte[] buffer = new byte[sizeBuffer];
		int len = -1;
		try {

			// int availableBytes = this.in.available();
			// while (availableBytes < sizeBuffer) {
			// availableBytes = this.in.available();
			// }

			int state = 1;
			int lb = 0;
			byte[] b = null;
			int i = 0;
			boolean FLAG = false;
			int aux = 999, x = 999;
			int aux1 = 999, y = 999;
			double aux2 = 0;
			int l = 0;

			ArrayList<int[]> bufferArray = new ArrayList<int[]>();

			while ((len = in.read(buffer)) > -1) {
				i = 0;

				if (!active) {
					serialPort.close();
				}

				while (i < len) {

					switch (state) {
					case 1:
						if (buffer[i] == 0) {
							state++;
							i++;
						} else {
							FLAG = false;
							aux = 999;
							aux1 = aux;
							i++;
						}
						break;

					case 2:
						if (buffer[i] == 0 && FLAG == true) {
							x = aux;
							y = aux1;
							aux2 = ConvertTommHg.convertTommHg(x, y);

							if (aux2 > 1000) {
								System.out
										.println("everything is gonna be alright!");
							}

							bpMeasureHistory.add(aux2);
							pressureValue = aux2;
							System.out.println(aux2);
							FLAG = false;
							aux = 999;
							aux1 = aux;
							state++;
							i++;
						} else if (buffer[i] == 0) {
							state++;
							i++;
						} else {
							if (aux1 == 0)
								state = 3;
							else {
								state = 1;
								i++;
							}
						}
						break;
					case 3:
						if (buffer[i] < 4) {
							state++;
							aux = buffer[i];
							i++;

						} else {
							state = 1;
							i++;
						}
						break;
					case 4:
						if (!(aux == 0 && buffer[i] == 0)) {
							state = 1;
							FLAG = true;
							aux1 = buffer[i];
							i++;
						} else {
							state = 3;
							i++;
						}
						break;

					default:
						break;
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// }
}
