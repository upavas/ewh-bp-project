// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import com.ewhoxford.android.bloodpressure.R;

// Import Android stuff
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.util.AttributeSet;
import android.content.res.Resources;

// Class GraphView : a view controller for displaying the blood pressure / time graph
public class GraphView extends View
{
	// Attributes
	private Paint PaintRect;	// Paint object for background rectangle

	// Constructor 1/3
	public GraphView(Context context)
	{
		// Parent's constructor
		super(context);
		
		// Initialization
		initGraphView();
	}

	// Constructor 2/3
	public GraphView(Context context, AttributeSet attrs)
	{
		// Parent's constructor
		super(context, attrs);
		
		// Initialization
		initGraphView();
	}
	
	// Constructor 3/3
	public GraphView(Context context, AttributeSet ats,	int defaultStyle)
	{
		// Parent's constructor
		super(context, ats, defaultStyle);
		
		// Initialization
		initGraphView();
	}
	
	// Initialization method
	protected void initGraphView()
	{
		setFocusable(true);
		
		Resources r = this.getResources();
		
		// Paint object for background rectangle
		PaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
		PaintRect.setColor(r.getColor(R.color.graphview_background_color));
		PaintRect.setStrokeWidth(1);
		PaintRect.setStyle(Paint.Style.FILL_AND_STROKE);

	}

	// Draw method
	protected void onDraw(Canvas canvas)
	{
		// Draw the background rectangle
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), PaintRect);
		
	}

}
