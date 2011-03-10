// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources
import com.ewhoxford.android.bloodpressure.R;

// Import Android stuff
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.*;
import android.view.*;
import android.util.AttributeSet;
import android.content.res.Resources;

// Class GraphView : a view controller for displaying the blood pressure / time graph
public class MeasureView extends View
{
	// Attributes
	private Paint PaintRectLeft;	// Paint object for background rectangle of the left
	private Paint PaintRectRight;	// Paint object for background rectangle of the right
	private Paint PaintFontLeft;	// Paint object for fonts of the left rectangle
	private Paint PaintFontRight;	// Paint object for fonts of the right rectangle

	// Constructor 1/3
	public MeasureView(Context context)
	{
		// Parent's constructor
		super(context);
		
		// Initialization
		initValuesView();
	}

	// Constructor 2/3
	public MeasureView(Context context, AttributeSet attrs)
	{
		// Parent's constructor
		super(context, attrs);
		
		// Initialization
		initValuesView();
	}
	
	// Constructor 3/3
	public MeasureView(Context context, AttributeSet ats, int defaultStyle)
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
		
		// Paint object for fonts of the left rectangle
		PaintFontLeft = new Paint(Paint.ANTI_ALIAS_FLAG);
		PaintFontLeft.setColor(r.getColor(R.color.valuesview_foreground_color_left));
		PaintFontLeft.setStyle(Style.FILL);
		PaintFontLeft.setTextAlign(Paint.Align.LEFT);

		// Paint object for fonts of the left rectangle
		PaintFontRight = new Paint(Paint.ANTI_ALIAS_FLAG);
		PaintFontRight.setColor(r.getColor(R.color.valuesview_foreground_color_right));
		PaintFontRight.setStyle(Style.FILL);
		PaintFontRight.setTextAlign(Paint.Align.RIGHT);

	}

	// Draw method
	protected void onDraw(Canvas canvas)
	{
		// Temporary variables
		FontMetrics fm;
		float y;
		
		// Measures
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		
		// Draw the background rectangle on the left
		canvas.drawRect(0, 0, 0.33f * w, h, PaintRectLeft);

		// Draw the background rectangle on the right
		canvas.drawRect(0.33f * w, 0, w, h, PaintRectRight);
		
		// Draw the text in the left rectangle
		PaintFontLeft.setTextSize(h/3 * 0.75f);
		//PaintFontLeft.setTextScaleX(w / h);
		
		// Centering in Y: measure ascent/descent first
		fm = PaintFontLeft.getFontMetrics();
		y = h / 6 - (fm.ascent + fm.descent) / 2;

		canvas.drawText("DIA", 5, y, PaintFontLeft);
		canvas.drawText("SYS", 5, h/3 + y, PaintFontLeft);
		canvas.drawText("PUL", 5, 2*h/3 + y, PaintFontLeft);

		// Draw the text in the right rectangle
		PaintFontRight.setTextSize(h/3 * 0.75f);
		//PaintFontLeft.setTextScaleX(w / h);
		
		// Centering in Y: measure ascent/descent first
		fm = PaintFontRight.getFontMetrics();
		y = h / 6 - (fm.ascent + fm.descent) / 2;

		canvas.drawText("0 mmHg", w-5, y, PaintFontRight);
		canvas.drawText("0 mmHg", w-5, h/3 + y, PaintFontRight);
		canvas.drawText("0 bpm    ", w-5, 2*h/3 + y, PaintFontRight);
		
	}


}
