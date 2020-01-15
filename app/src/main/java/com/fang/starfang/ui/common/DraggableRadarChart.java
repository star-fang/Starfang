package com.fang.starfang.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.YAxis;

public class DraggableRadarChart extends RadarChart {
    private static final String TAG = "FANG_DRAG_RADAR";

    private int pos_x;
    private int pos_x_current;

    public DraggableRadarChart(Context context) {
        super(context);

        pos_x = 0;
        pos_x_current = 0;

        Log.d(TAG, "1 arg constructor");
    }

    public DraggableRadarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        pos_x = 0;
        pos_x_current = 0;

        Log.d(TAG, "2 args constructor");
    }

    public DraggableRadarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        pos_x = 0;
        pos_x_current = 0;

        Log.d(TAG, "3 args constructor");
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        if (pointerCount == 2) {
            int action = motionEvent.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                    pos_x = (int) motionEvent.getX(1);
                    break;
                case MotionEvent.ACTION_UP:
                    pos_x_current = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    pos_x_current = (int) motionEvent.getX(1);
                    int diff = pos_x - pos_x_current;
                    Log.d(TAG, "diff:" + diff);

                    YAxis yAxis = getYAxis();
                    int maximum = (int)yAxis.getAxisMaximum();
                    yAxis.setAxisMaximum(diff<0? maximum - 1 : maximum + 1);
                    invalidate();
                    break;
                default:
            }
           // pointerCount = 0;
        } else {
            pos_x = 0;
            pos_x_current = 0;
        }
        return true;
    }


}
