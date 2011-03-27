package com.ewhoxford.android.bloodpressure.signalProcessing;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.androidplot.ui.layout.AnchorPosition;
import com.androidplot.ui.layout.XLayoutStyle;
import com.androidplot.ui.layout.YLayoutStyle;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.XYStepType;


public class MyActivity extends Activity
{

    private XYPlot mySimpleXYPlot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

    	super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);

        // initialize our XYPlot reference:
        //mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		ReadCSVfile r = new ReadCSVfile();
		int[][] vals1 = r.readCSV();
	
        // add a new series
        mySimpleXYPlot.addSeries(new SimpleXYSeries(vals1,100), LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(255, 0, 0), Color.rgb(0, 0, 200)));

        // reduce the number of range labels
        mySimpleXYPlot.getGraphWidget().setRangeTicksPerLabel(2);

        // reposition the domain label to look a little cleaner:
        Widget domainLabelWidget = mySimpleXYPlot.getDomainLabelWidget();

        mySimpleXYPlot.position(domainLabelWidget,                     // the widget to position
                                45,                                    // x position value, in this case 45 pixels
                                XLayoutStyle.ABSOLUTE_FROM_LEFT,       // how the x position value is applied, in this case from the left
                                0,                                     // y position value
                                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,     // how the y position is applied, in this case from the bottom
                                AnchorPosition.LEFT_BOTTOM);           // point to use as the origin of the widget being positioned
        
        mySimpleXYPlot.setDomainStep(XYStepType.IncrementalValue, 3);
        
        mySimpleXYPlot.setDomainLabel("Time (s)");
        
        mySimpleXYPlot.setRangeLabel("Pressure (mmHg)");
        
        // get rid of the visual aids for positioning:
        mySimpleXYPlot.disableAllMarkup();
    }
}
