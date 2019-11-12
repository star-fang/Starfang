package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fang.starfang.R;

public class SimulationFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_SIMULATION_FRAG";

    static SimulationFragment newInstance(int index) {
            SimulationFragment simulationFragment = new SimulationFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, index);
            simulationFragment.setArguments(bundle);
        return simulationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"_ON CREATE");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"_ON CREATE VIEW");

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        return root;

    }

}
