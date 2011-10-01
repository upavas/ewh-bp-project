package org.ewhoxford.swt.bloodpressure.pressureInputDevice;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

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

	public static final int SIGNAL1 = 0;
	// private static final int SAMPLE_SIZE = 1;

	private static final int MAX_SIZE = 200;

	private double pressureValue = 0;
	private MyObservable notifier;
	private int count = 0;
	private boolean active = true;
	int countMiceSamples = 0;
	int linearFilterThreshold = 40;
	int mouseDisconnectedCount = 0;
	private static String serial = "/dev/ttyACM0";
	SerialPort serialPort;
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

			new Thread() {
				public void run() {
					try {
						connect(serial);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();

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
				if (true) {
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

	void connect(String portName) throws Exception {
		System.setProperty("gnu.io.rxtx.SerialPorts", serial);
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					2000);

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				(new Thread(new SerialReader(in))).start();
				// (new Thread(new SerialWriter(out))).start();

			} else {
				System.out
						.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	/** */
	public class SerialReader implements Runnable {
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void run() {
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

				while ((len = this.in.read(buffer)) > -1) {
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
	}

}
