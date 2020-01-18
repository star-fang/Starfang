package com.fang.starfang.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.YAxis;

public class DraggableRadarChart extends RadarChart {
    private static final String TAG = "FANG_DRAG_RADAR";

    private double distance_before;

    public DraggableRadarChart(Context context) {
        super(context);

        Log.d(TAG, "1 arg constructor");
    }

    public DraggableRadarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.d(TAG, "2 args constructor");
    }

    public DraggableRadarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Log.d(TAG, "3 args constructor");
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        int action = motionEvent.getActionMasked();
        if (pointerCount == 2) {
            switch (action) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                    double pos_x_origin = motionEvent.getX(0);
                    double pos_y_origin = motionEvent.getY(0);
                    double pos_x = motionEvent.getX(1);
                    double pos_y = motionEvent.getY(1);
                    double diffXSQ = Math.pow(pos_x - pos_x_origin, 2.0);
                    double diffYSQ = Math.pow(pos_y - pos_y_origin, 2.0);
                    distance_before = Math.sqrt(diffXSQ + diffYSQ);
                    Log.d(TAG,"double pointer detected: ( " + pos_x_origin + " , " + pos_y_origin + " ), ( " + pos_x + " , " + pos_y + " ) => " + distance_before );
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "double pointing stop");
                    break;
                case MotionEvent.ACTION_MOVE:
                    pos_x_origin = motionEvent.getX(0);
                    pos_y_origin = motionEvent.getY(0);
                    pos_x = motionEvent.getX(1);
                    pos_y = motionEvent.getY(1);
                    diffXSQ = Math.pow( pos_x - pos_x_origin, 2.0 );
                    diffYSQ = Math.pow( pos_y - pos_y_origin, 2.0 );
                    double distance = Math.sqrt(diffXSQ + diffYSQ);
                    double diff = distance - distance_before;
                    Log.d(TAG, "diff:" + diff);

                    YAxis yAxis = getYAxis();
                    float maximum = yAxis.getAxisMaximum();
                    yAxis.setAxisMaximum(diff<0? maximum + 0.5f : maximum - 0.5f);

                    distance_before = distance;
                    invalidate();
                    break;
                default:
            }
        } else if( pointerCount == 1 ){

            if( action == MotionEvent.ACTION_UP) {
                super.performClick();
                Log.d(TAG,"click");
            }
            super.onTouchEvent(motionEvent);
        }
        return true;
    }


}
