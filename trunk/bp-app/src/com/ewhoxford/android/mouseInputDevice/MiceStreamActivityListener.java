package com.ewhoxford.android.mouseInputDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Observable;

import com.ewhoxford.android.bloodpressure.Measure;

public class MiceStreamActivityListener extends Observable {

	private char[] mouse = new char[3];

	public MiceStreamActivityListener() {
		
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
				do {
					count++;
					int i = 0;
					while (i <= 2) {
						mouseV[i] = (char) finp.read();
						i = i + 1;
					}
					System.out.println("" + (int) mouseV[0] + ","
							+ (int) mouseV[1] + "," + (int) mouseV[2]);

					i = 0;

					// signal processing here

					// yValue = (int) (mouseV[2]);
					setMouse(mouseV);

				} while ((mouseV[0] != -1) && (count < 50));
				finp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void setMouse(char[] mouse) {
		this.mouse = mouse;
		setChanged();
		notifyObservers(mouse);
	}

	public char[] getMouse() {
		return mouse;
	}

}
