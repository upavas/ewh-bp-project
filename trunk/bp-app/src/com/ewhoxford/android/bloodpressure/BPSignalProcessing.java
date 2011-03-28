// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources

// Class GraphView : a view controller for displaying the blood pressure / time graph
public class BPSignalProcessing {
	// Attributes

	float[] valuesX = new float[50000]; // Array of data to be plotted
	float[] valuesY = new float[50000]; // Array of data to be plotted
	int valuesEnd = -1; // Maximum index of the array that has been completed

	// Plot a set of values in the form of a curve
	public void sendNewValueToProcess(float newX, float newY) {

		valuesEnd++;
		valuesX[valuesEnd] = newX;
		valuesY[valuesEnd] = newX;

	}

	public void process() {

	}

}
