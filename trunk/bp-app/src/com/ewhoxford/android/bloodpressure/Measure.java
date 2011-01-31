// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import com.ewhoxford.android.bloodpressure.R;

//Import Android stuff
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

//Class Measure : activity that pops when the user wants to start taking blood pressure
public class Measure extends Activity implements OnClickListener
{
	// To be performed on the creation
    public void onCreate(Bundle savedInstanceState)
    {
    	// Parent's method
        super.onCreate(savedInstanceState);
        
        // Layout
        setContentView(R.layout.measure);

		// #### Set up click listeners for all the buttons
		
		// Help button
		View HelpButton = findViewById(R.id.button_help);
		HelpButton.setOnClickListener(this);

		// #### End of Set up click listeners for all the buttons

	}
    
	// event : click on something
	public void onClick(View V)
	{
		// let's find what has been clicked
		switch (V.getId())
		{
			// Start button
			case R.id.button_help:
				finish(); // kill this activity, so going back to the previous one
				break;

		}
	}
}