package com.fang.starfang.ui.main.Fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.ui.common.DraggableRadarChart;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;

import io.realm.RealmResults;

public class RelicFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_FRAG_RELIC";

    //private static final String[] mActivities = new String[]{"각","항","저","방","심","미","기"};

    static RelicFragment newInstance(int index) {
        RelicFragment magicItemsFragment = new RelicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        magicItemsFragment.setArguments(bundle);
        return magicItemsFragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_relic, container, false);
        final DraggableRadarChart radarChart =  view.findViewById(R.id.chart_relic);

        radarChart.setBackgroundColor(Color.rgb(60,65,82));
        radarChart.getDescription().setEnabled(false);
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebAlpha(100);

        MarkerView markerView = new MarkerView(mActivity, R.layout.radar_markerview);
        markerView.setChartView(radarChart);
        radarChart.setMarker(markerView);
        markerView.setOnClickListener(v-> {
            //markerView.ddd
            //todo: create custom markerView which show y-axis value & edit button
        });

        draw(1, 4, radarChart);

        Legend legend = radarChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTypeface(Typeface.DEFAULT);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(5f);
        legend.setTextColor(Color.WHITE);



        final RadioGroup radioGroup = view.findViewById(R.id.radio_guardians);
        radioGroup.setOnCheckedChangeListener( (r,id) -> {
            switch( id ) {
                case R.id.radio_blue_dragon:
                    draw( 1, 4, radarChart );
                    break;
                case R.id.radio_red_bird:
                    draw( 2, 4, radarChart );
                    break;
                case R.id.radio_white_tiger:
                    draw( 3, 4, radarChart );
                    break;
                case R.id.radio_black_tortoise:
                    draw( 4, 4, radarChart );
                    break;
                    default:
            }
            //Toast.makeText(mActivity, id, Toast.LENGTH_SHORT).show();

        });


        return view;

    }



    private void draw( final int guardianType, int grade, RadarChart radarChart ) {


        ArrayList<RadarEntry> entriesAll = new ArrayList<>();
        ArrayList<RadarEntry> entriesUsed = new ArrayList<>();

        RealmResults<RelicSFX> sfxes = realm.where(RelicSFX.class).equalTo(RelicSFX.FIELD_TYPE,guardianType).and().equalTo(RelicSFX.FIELD_GRD, grade).findAll();
        for( RelicSFX sfx : sfxes ) {
            RealmResults<RelicSim> relicSimsAll = realm.where(RelicSim.class).equalTo(RelicSim.FIELD_SUFFIX+"."+RelicSFX.FIELD_ID, sfx.getRelicSuffixID()).findAll();
            entriesAll.add(new RadarEntry((float)relicSimsAll.size()));

            RealmResults<RelicSim> relicSimsUsed = realm.where(RelicSim.class).equalTo(RelicSim.FIELD_SUFFIX+"."+RelicSFX.FIELD_ID, sfx.getRelicSuffixID())
                    .and().isNotNull(RelicSim.FIELD_HERO).findAll();

            entriesUsed.add(new RadarEntry((float)relicSimsUsed.size()));

        }

        RadarDataSet radarDataSetAll = new RadarDataSet(entriesAll,"습득");
        radarDataSetAll.setColor(Color.rgb(103, 110, 129));
        radarDataSetAll.setFillColor(Color.rgb(103, 110, 129));
        radarDataSetAll.setDrawFilled(true);
        radarDataSetAll.setFillAlpha(180);
        radarDataSetAll.setLineWidth(2f);
        radarDataSetAll.setDrawHighlightCircleEnabled(true);
        radarDataSetAll.setDrawHighlightIndicators(false);

        RadarDataSet radarDataSetUsed = new RadarDataSet(entriesUsed,"착용");
        radarDataSetUsed.setColor(Color.rgb(121, 162, 175));
        radarDataSetUsed.setFillColor(Color.rgb(121, 162, 175));
        radarDataSetUsed.setDrawFilled(true);
        radarDataSetUsed.setFillAlpha(180);
        radarDataSetUsed.setLineWidth(2f);
        radarDataSetUsed.setDrawHighlightCircleEnabled(true);
        radarDataSetUsed.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> radarDataSets = new ArrayList<>();
        radarDataSets.add(radarDataSetAll);
        radarDataSets.add(radarDataSetUsed);

        RadarData radarData = new RadarData(radarDataSets);
        radarData.setValueTypeface(Typeface.DEFAULT);
        radarData.setValueTextSize(8f);
        radarData.setDrawValues(true);
        radarData.setValueTextColor(Color.WHITE);

        radarChart.setData( radarData );
        radarChart.invalidate();

        radarChart.animateXY(1400,1400, Easing.EaseInOutQuad);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new ValueFormatter() {

            RealmResults<RelicSFX> sfxes = realm.where(RelicSFX.class).equalTo(RelicSFX.FIELD_TYPE,guardianType).distinct(RelicSFX.FIELD_NAME).findAll();

            @Override
            public String getFormattedValue(float value) {
                int size = sfxes.size();
                if( size == 0) {
                    return null;
                }

                RelicSFX sfx = sfxes.get((int) value % sfxes.size());
                return sfx == null ? null : sfx.getRelicSuffixName();
            }
        });

        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setTypeface(Typeface.DEFAULT);
        yAxis.setLabelCount(7,false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(15f);
        yAxis.setDrawLabels(false);


    }

}