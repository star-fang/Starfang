package com.fang.starfang.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RadioGroup;


import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;

import com.fang.starfang.R;
import com.fang.starfang.ui.main.dialog.ManageRelicSuffixDialogFragment;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.YAxis;

public class DraggableRadarChart extends RadarChart {
    private static final String TAG = "FANG_DRAG_RADAR";

    private double distance_before;
    private int moving_responses;
    private AppCompatTextView text_suffix_info;
    private AppCompatButton button_suffix_info;
    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;

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

    public void setOutputComponents(AppCompatTextView textView, AppCompatButton button, RadioGroup radioGroup, FragmentManager fragmentManager) {
        this.text_suffix_info = textView;
        this.button_suffix_info = button;
        this.radioGroup = radioGroup;
        this.fragmentManager = fragmentManager;
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
                    yAxis.setAxisMaximum(diff<0? maximum + 0.1f : maximum - 0.1f);

                    distance_before = distance;
                    invalidate();
                    break;
                default:
            }
        } else if( pointerCount == 1 ){

            switch ( action ) {
                case MotionEvent.ACTION_DOWN:
                    moving_responses = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    moving_responses++;
                    break;
                case MotionEvent.ACTION_UP:
                    if( moving_responses > 14 ) {
                        // 각 기 미 심 방 저 항
                        // 0 7 6 5 4 3 2 1

                        // 0 -1 5 4 3 2 1
                        // 7 8 2 3 4 5 6
                        // 0 1 2 3 4 5 6
                        float angle = this.getRotationAngle();

                        Log.d(TAG,"angle:" + angle);

                        int suffixNo = (7 - (int)( ( angle -90f) / 360f * 7f) ) % 7;

                        //setRotation( ((float)suffixNo * 360f / 7f) + 90f);
                        //
                        // 90 40 346 300 248 197 144

                        //

                        // 0 -50 256 210 158 107 54

                        // 0 -1 5 4 3 2 1

                        String suffix ="suffixNo: " + suffixNo;
                        text_suffix_info.setText(suffix);

                        button_suffix_info.setOnClickListener( v-> {
                            int guardianType;
                            switch( radioGroup.getCheckedRadioButtonId() ) {
                                case R.id.radio_blue_dragon:
                                    guardianType = 1;
                                    break;
                                case R.id.radio_red_bird:
                                    guardianType = 2;
                                    break;
                                case R.id.radio_white_tiger:
                                    guardianType = 3;
                                    break;
                                case R.id.radio_black_tortoise:
                                    guardianType = 4;
                                    break;
                                    default:
                                        guardianType = 0;
                            }

                            ManageRelicSuffixDialogFragment manageRelicSuffixDialogFragment =
                                    ManageRelicSuffixDialogFragment.getInstance(guardianType, suffixNo );
                            manageRelicSuffixDialogFragment.show(fragmentManager,TAG);
                        });
                    } else {
                        super.performClick();
                        //Log.d(TAG,"click");
                    }

            }

            super.onTouchEvent(motionEvent);
        }
        return true;
    }


}
