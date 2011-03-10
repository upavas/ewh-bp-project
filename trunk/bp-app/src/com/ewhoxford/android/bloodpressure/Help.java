// Name of the package
package com.ewhoxford.android.bloodpressure;

// Import resources
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

// Class Home : activity that pops at the beginning of the application
public class Help extends Activity {
	// To be performed on the creation
	public void onCreate(Bundle savedInstanceState) {
		// Parent's method
		super.onCreate(savedInstanceState);

		// Layout
		setContentView(R.layout.home);

		// #### Set up click listeners for all the buttons

		// Start button
		// View StartButton = findViewById(R.id.button_start);
		// StartButton.setOnClickListener(this);

		// #### End of Set up click listeners for all the buttons

		// get root access

	}

	// event : click on something
	// public void onClick(View V) {
	// // let's find what has been clicked
	// switch (V.getId()) {
	// // Start button
	// case R.id.button_start:
	// Intent I = new Intent(this, Measure.class);
	// startActivity(I);
	// break;
	//
	// }
	// }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// This is our one standard application action -- inserting a
		// new note into the list.
		menu.add(0, MeasureList.MENU_ITEM_INSERT, 0, R.string.add_measure).setShortcut('3',
				'a').setIcon(android.R.drawable.ic_menu_add);

		// Generate any additional actions that can be performed on the
		// overall list. In a normal install, there are no additional
		// actions found here, but this allows other applications to extend
		// our menu with their own actions.
		Intent intent = new Intent(null, Measure.class);
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
				new ComponentName(this, Measure.class), null, intent, 0,
				null);

		return true;
	}

}