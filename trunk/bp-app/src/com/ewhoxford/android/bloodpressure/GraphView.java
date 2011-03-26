// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// Class GraphView : a view controller for displaying the blood pressure / time graph
public class GraphView extends View {
	// Attributes
	private Paint PaintRect; // Paint object for background rectangle
	private Paint PaintCurve; // Paint object for the curve
	float[] values = new float[50000]; // Array of data to be plotted
	float valuesMax = 0; // Maximum value in the array
	float valuesMin = 0; // Minimum value in the array
	int valuesEnd = -1; // Maximum index of the array that has been completed

	// Constructor 1/3
	public GraphView(Context context) {
		// Parent's constructor
		super(context);

		// Initialization
		initGraphView();
	}

	// Constructor 2/3
	public GraphView(Context context, AttributeSet attrs) {
		// Parent's constructor
		super(context, attrs);

		// Initialization
		initGraphView();
	}

	// Constructor 3/3
	public GraphView(Context context, AttributeSet ats, int defaultStyle) {
		// Parent's constructor
		super(context, ats, defaultStyle);

		// Initialization
		initGraphView();
	}

	public boolean onTrackballEvent(MotionEvent me) {
		float x = me.getX();
		float y = me.getY();
		// sendNewValueToDisplay(y);
		return true;

	};

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		// TODO Auto-generated method stub

		float x = me.getX();
		float y = me.getY();
		// sendNewValueToDisplay(y);
		return true;
	}

	// Initialization method
	protected void initGraphView() {
		setFocusable(true);

		Resources r = this.getResources();

		// Paint object for background rectangle
		PaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
		PaintRect.setColor(r.getColor(R.color.graphview_background_color));
		PaintRect.setStrokeWidth(1);
		PaintRect.setStyle(Paint.Style.FILL_AND_STROKE);

		// Paint object for curve
		PaintCurve = new Paint(Paint.ANTI_ALIAS_FLAG);
		PaintCurve.setColor(r.getColor(R.color.graphview_foreground_color));
		PaintCurve.setStrokeWidth(1);
		PaintCurve.setStyle(Paint.Style.FILL_AND_STROKE);
		
	}

	// Draw method
	protected void onDraw(Canvas canvas) {
		// Measures
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();

		// Draw the background rectangle
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(),
				PaintRect);

		// Range ?
		float max_h = valuesEnd * 1.3f;
		float max_v = (valuesMax - valuesMin) * 1.3f;

		// Draw a line
		for (int i = 0; i < valuesEnd; i++) {
			canvas.drawLine(i * w / max_h, (values[i] - valuesMin) * h / max_v,
					(i + 1) * w / max_h, (values[i + 1] - valuesMin) * h
							/ max_v, PaintCurve);
		}

	}

	

}
