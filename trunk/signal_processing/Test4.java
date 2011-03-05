import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Test4 {
	public static void main(String[] args) throws IOException {
		File f;
		f = new File("/dev/input/mice");

		if (!f.exists() && f.length() < 0)
			System.out.println("The specified file is not exist");

		else {
			FileInputStream finp = new FileInputStream(f);
			int b;
			char[] mouseV = new char[3];
			do {
				int i = 0;
				while (i <= 2) {
					mouseV[i] = (char) finp.read();
					i = i + 1;
				}
				System.out.println("" + (int) mouseV[0]+"," + (int) mouseV[1]
						+ ","+(int) mouseV[2]);
				i = 0;
			} while (mouseV[0] != -1);
			finp.close();
		}
	}
}