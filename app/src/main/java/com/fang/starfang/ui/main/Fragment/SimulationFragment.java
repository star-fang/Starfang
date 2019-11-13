package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.view.recycler.DiagonalScrollRecyclerView;
import com.fang.starfang.view.recycler.HeroesFixedRecyclerAdapter;
import com.fang.starfang.view.recycler.HeroesFloatingRecyclerAdapter;

import io.realm.Realm;

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

        Realm realm = Realm.getDefaultInstance();
        final View child_setting = inflater.inflate(R.layout.fragment_simulation, container, false);
        final HorizontalScrollView scroll_hero_header_floating = child_setting.findViewById(R.id.scroll_hero_header_floating);
        final RecyclerView recycler_view_hero_fixed = child_setting.findViewById(R.id.recycler_view_hero_fixed);
        final LinearLayout hero_content_layout = child_setting.findViewById(R.id.hero_content_layout);
        //final RecyclerView recycler_view_hero_floating = child_setting.findViewById(R.id.recycler_view_hero_floating);
        final HeroesFloatingRecyclerAdapter heroesFloatingRecyclerAdapter = new HeroesFloatingRecyclerAdapter(realm);
        final HeroesFixedRecyclerAdapter heroesFixedRecyclerAdapter = new HeroesFixedRecyclerAdapter(realm);
        final LinearLayoutManager layoutManager_fixed = new LinearLayoutManager(mActivity);
        final GridLayoutManager layoutManager_floating = new GridLayoutManager(mActivity,10);


        DiagonalScrollRecyclerView diagonalScrollRecyclerView = new DiagonalScrollRecyclerView(mActivity);
        diagonalScrollRecyclerView.setRecyclerViewLayoutManager(layoutManager_floating);
        diagonalScrollRecyclerView.setRecyclerViewAdapter(heroesFloatingRecyclerAdapter);
        hero_content_layout.addView(diagonalScrollRecyclerView);

        recycler_view_hero_fixed.setLayoutManager(layoutManager_fixed);
        recycler_view_hero_fixed.setAdapter(heroesFixedRecyclerAdapter);


        //recycler_view_hero_floating.setLayoutManager(layoutManager_floating);
        //recycler_view_hero_floating.setAdapter(heroesFloatingRecyclerAdapter);

        recycler_view_hero_fixed.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        if(adapter == null) {
                            return;
                        }
                        int itemLastPosition = (adapter.getItemCount() - 1);
                        if(itemLastPosition < 0 ) {
                            return;
                        }
                        if(newState == RecyclerView.SCROLL_STATE_DRAGGING ) {
                            Log.d(TAG, "scrolling...");
                        }
                    }
                }
        );








        return child_setting;

    }

}
