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
public class ValuesView extends View
{
	// Attributes
	private Paint PaintRectLeft;	// Paint object for background rectangle of the left
	private Paint PaintRectRight;	// Paint object for background rectangle of the right

	// Constructor 1/3
	public ValuesView(Context context)
	{
		// Parent's constructor
		super(context);
		
		// Initialization
		initValuesView();
	}

	// Constructor 2/3
	public ValuesView(Context context, AttributeSet attrs)
	{
		// Parent's constructor
		super(context, attrs);
		
		// Initialization
		initValuesView();
	}
	
	// Constructor 3/3
	public ValuesView(Context context, AttributeSet ats, int defaultStyle)
	{
		// Parent's constructor
		super(context, ats, defaultStyle);
		
		// Initialization
		initValuesView();
	}
	
	// Initialization method
	protected void initValuesView()
	{
		setFocusable(true);
		
		Resources r = this.getResources();
		
		// Paint object for background rectangle of the left
		PaintRectLeft = new Paint(Paint.ANTI_ALIAS_FLAG);
		PaintRectLeft.setColor(r.getColor(R.color.valuesview_background_color_left));
		PaintRectLeft.setStrokeWidth(1);
		PaintRectLeft.setStyle(Paint.Style.FILL_AND_STROKE);

		// Paint object for background rectangle of the left
		PaintRectRight = new Paint(Paint.ANTI_ALIAS_FLAG);
		PaintRectRight.setColor(r.getColor(R.color.valuesview_background_color_right));
		PaintRectRight.setStrokeWidth(1);
		PaintRectRight.setStyle(Paint.Style.FILL_AND_STROKE);

	}

	// Draw method
	protected void onDraw(Canvas canvas)
	{
		// Measures
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		
		// Draw the background rectangle on the left
		canvas.drawRect(0, 0, (float) 0.33 * w, h, PaintRectLeft);

		// Draw the background rectangle on the right
		canvas.drawRect((float) 0.33 * w, 0, w, h, PaintRectRight);
		
	}

}
