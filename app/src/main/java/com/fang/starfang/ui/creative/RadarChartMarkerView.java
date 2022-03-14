package com.fang.starfang.ui.creative;

import android.content.Context;

import androidx.appcompat.widget.AppCompatButton;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import com.fang.starfang.R;

public class RadarChartMarkerView extends MarkerView{

    private final static String TAG = "FANG_MARKER";

    private final AppCompatButton button_radar_marker;

    public RadarChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        button_radar_marker = findViewById(R.id.button_radar_marker);
    }



    @Override
    public void refreshContent(Entry e, Highlight highlight) {



        //String msg = "dataIndex:"+ highlight.getStackIndex() +  " offX:" + getOffset().getX() +
       // " offY:" + getOffset().getY();
       // Log.d(TAG, msg);
      //  button_radar_marker.setText(msg);
        super.refreshContent(e, highlight);
    }
}
