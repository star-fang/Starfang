package com.fang.starfang.ui.main.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fang.starfang.R;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarDataSet;

public class RelicFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_FRAG_RELIC";

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
        final RadarChart radarChart =  view.findViewById(R.id.chart_relic);
        radarChart.setBackgroundColor(Color.rgb(60,65,82));

        radarChart.getDescription().setEnabled(false);

        return view;

    }

}