// Name of the package
package com.ewhoxford.android.bloodpressure;

// Import resources
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

// Class Home : activity that pops at the beginning of the application
public class Help extends Activity implements OnClickListener {
	// To be performed on the creation
	public void onCreate(Bundle savedInstanceState) {
		// Parent's method
		super.onCreate(savedInstanceState);

		// Layout
		setContentView(R.layout.help);

		// #### Set up click listeners for all the buttons

		// Start button
		View StartButton = findViewById(R.id.button_start);
		StartButton.setOnClickListener(this);

		// #### End of Set up click listeners for all the buttons


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