package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fang.starfang.R;

import java.lang.ref.WeakReference;

public class SimulationFragment extends PlaceholderFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "FANG_SIMULATION_FRAG";
    private static WeakReference<SimulationFragment> simulationFragmentWeakReference = null;
    private View child_simulation;

    static SimulationFragment getInstance() {
        if( simulationFragmentWeakReference == null ) {
            SimulationFragment simulationFragment = new SimulationFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, 1);
            simulationFragment.setArguments(bundle);
            simulationFragmentWeakReference = new WeakReference<>(simulationFragment);
        }
        return simulationFragmentWeakReference.get();
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
