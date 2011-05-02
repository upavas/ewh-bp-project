package com.ewhoxford.android.bloodpressure;

//Import resources
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/**
 *  Class GraphView : a view controller for displaying the blood pressure / time graph
 * @author corentin
 *
 */
public class ValuesView extends View {
	// Attributes
	private Paint paintRectLeft; // Paint object for background rectangle of the
	// left
	private Paint paintRectRight; // Paint object for background rectangle of
	// the right
	private Paint paintFontLeft; // Paint object for fonts of the left rectangle
	private Paint paintFontRight; // Paint object for fonts of the right
	private int sPressure = 0;
	private int dPressure = 0;
	private int pulseRate = 0;

	// rectangle

	// Constructor 1/3
	public ValuesView(Context context) {
		// Parent's constructor
		super(context);

		// Initialization
		initValuesView();
	}

	// Constructor 2/3
	public ValuesView(Context context, AttributeSet attrs) {
		// Parent's constructor
		super(context, attrs);

		// Initialization
		initValuesView();
	}

	// Constructor 3/3
	public ValuesView(Context context, AttributeSet ats, int defaultStyle) {
		// Parent's constructor
		super(context, ats, defaultStyle);

		// Initialization
		initValuesView();
	}

	// Initialization method
	protected void initValuesView() {
		setFocusable(true);

		Resources r = this.getResources();

		// Paint object for background rectangle of the left
		paintRectLeft = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintRectLeft.setColor(r
				.getColor(R.color.valuesview_background_color_left));
		paintRectLeft.setStrokeWidth(1);
		paintRectLeft.setStyle(Paint.Style.FILL_AND_STROKE);

		// Paint object for background rectangle of the left
		paintRectRight = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintRectRight.setColor(r
				.getColor(R.color.valuesview_background_color_right));
		paintRectRight.setStrokeWidth(1);
		paintRectRight.setStyle(Paint.Style.FILL_AND_STROKE);

		// Paint object for fonts of the left rectangle
		paintFontLeft = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFontLeft.setColor(r
				.getColor(R.color.valuesview_foreground_color_left));
		paintFontLeft.setStyle(Style.FILL);
		paintFontLeft.setTextAlign(Paint.Align.LEFT);

		// Paint object for fonts of the left rectangle
		paintFontRight = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFontRight.setColor(r
				.getColor(R.color.valuesview_foreground_color_right));
		paintFontRight.setStyle(Style.FILL);
		paintFontRight.setTextAlign(Paint.Align.RIGHT);

	}

	// Draw method
	protected void onDraw(Canvas canvas) {
		// Temporary variables
		FontMetrics fm;
		float y;

		// Measures
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();

		// Draw the background rectangle on the left
		canvas.drawRect(0, 0, 0.33f * w, h, paintRectLeft);

		// Draw the background rectangle on the right
		canvas.drawRect(0.33f * w, 0, w, h, paintRectRight);

		// Draw the text in the left rectangle
		paintFontLeft.setTextSize(h / 3 * 0.75f);
		// PaintFontLeft.setTextScaleX(w / h);

		// Centering in Y: measure ascent/descent first
		fm = paintFontLeft.getFontMetrics();
		y = h / 6 - (fm.ascent + fm.descent) / 2;

		canvas.drawText(getResources().getText(R.string.dia).toString(), 5, y, paintFontLeft);
		canvas.drawText(getResources().getText(R.string.sys).toString(), 5, h / 3 + y, paintFontLeft);
		canvas.drawText(getResources().getText(R.string.pulse).toString(), 5, 2 * h / 3 + y, paintFontLeft);

		// Draw the text in the right rectangle
		paintFontRight.setTextSize(h / 3 * 0.75f);
		// PaintFontLeft.setTextScaleX(w / h);

		// Centering in Y: measure ascent/descent first
		fm = paintFontRight.getFontMetrics();
		y = h / 6 - (fm.ascent + fm.descent) / 2;

		canvas.drawText(sPressure + " mmHg", w - 5, y, paintFontRight);
		canvas.drawText(dPressure + " mmHg", w - 5, h / 3 + y, paintFontRight);
		canvas.drawText(pulseRate + " bpm    ", w - 5, 2 * h / 3 + y,
				paintFontRight);

	}

	public int getSPressure() {
		return sPressure;
	}

	public void setSPressure(int sPressure) {
		this.sPressure = sPressure;
	}

	public int getDPressure() {
		return dPressure;
	}

	public void setDPressure(int dPressure) {
		this.dPressure = dPressure;
	}

	public int getPulseRate() {
		return pulseRate;
	}

	public void setPulseRate(int pulseRate) {
		this.pulseRate = pulseRate;
	}

}
