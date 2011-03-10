// Name of the package
package com.ewhoxford.android.bloodpressure;

// Import resources
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

// Class Home : activity that pops at the beginning of the application
public class Home extends Activity implements OnClickListener {
	// To be performed on the creation
	public void onCreate(Bundle savedInstanceState) {
		// Parent's method
		super.onCreate(savedInstanceState);

		// Layout
		setContentView(R.layout.home);

		

		// #### Set up click listeners for all the buttons

		// Start button
		View StartButton = findViewById(R.id.button_start);
		StartButton.setOnClickListener(this);

		// #### End of Set up click listeners for all the buttons
		
		// get root access
		Process p;
		try {
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");

//			// Attempt to write a file to a root-only
//			// DataOutputStream os = new DataOutputStream(p.getOutputStream());
//			// os
//			// .writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");
//			//
//			// // Close the terminal
//			// os.writeBytes("exit\n");
//			// os.flush();
//			try {
//				p.waitFor();
//				if (p.exitValue() != 255) {
//					// TODO Code to run on success
//					System.out.println("root");
//				} else {
//					// TODO Code to run on unsuccessful
//					System.out.println("not root");
//				}
//			} catch (InterruptedException e) {
//				// TODO Code to run in interrupted exception
//				System.out.println("not root");
//			}
		} catch (IOException e) {
			// TODO Code to run in input/output exception
			System.out.println("not root");
		}

	}

	// event : click on something
	public void onClick(View V) {
		// let's find what has been clicked
		switch (V.getId()) {
		// Start button
		case R.id.button_start:
			Intent I = new Intent(this, Measure.class);
			startActivity(I);
			break;

		}
	}

}