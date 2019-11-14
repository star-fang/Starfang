package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.recycler.DiagonalScrollRecyclerView;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFixedRecyclerAdapter;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFloatingRecyclerAdapter;
import com.fang.starfang.ui.main.recycler.filter.HeroFilter;

import io.realm.Realm;
import io.realm.Sort;

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
        final View child_sim = inflater.inflate(R.layout.fragment_simulation, container, false);
        final RecyclerView recycler_view_hero_fixed = child_sim.findViewById(R.id.recycler_view_hero_fixed);
        final RecyclerView recycler_view_hero_floating = child_sim.findViewById(R.id.recycler_view_hero_floating);
        final HeroesFloatingRecyclerAdapter heroesFloatingRecyclerAdapter = new HeroesFloatingRecyclerAdapter(realm);
        final HeroesFixedRecyclerAdapter heroesFixedRecyclerAdapter = new HeroesFixedRecyclerAdapter(realm);
        final LinearLayoutManager layoutManager_fixed = new LinearLayoutManager(mActivity);
        final LinearLayoutManager layoutManager_floating = new LinearLayoutManager(mActivity);
        final DiagonalScrollRecyclerView recycler_view_hero_content = child_sim.findViewById(R.id.recycler_view_hero_content);

        recycler_view_hero_floating.setLayoutManager(layoutManager_floating);
        recycler_view_hero_floating.setAdapter(heroesFloatingRecyclerAdapter);
        recycler_view_hero_content.setRecyclerView(recycler_view_hero_floating);

        recycler_view_hero_fixed.setLayoutManager(layoutManager_fixed);
        recycler_view_hero_fixed.setAdapter(heroesFixedRecyclerAdapter);

        boolean touchOnStartHeader = false;
        boolean touchOnTopHeader = false;

        recycler_view_hero_floating.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        recycler_view_hero_fixed.scrollBy(0,dy);
                        Log.d(TAG,"right scroll by y :" +dy);
                    }
                }
        );

        recycler_view_hero_fixed.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                       // diagonalScrollRecyclerView.getRecyclerView().scrollBy(0,dy);
                        Log.d(TAG,"left scroll by y :"+dy);
                    }
                }
        );

        final View table_header_branch = child_sim.findViewById(R.id.table_header_branch);
        table_header_branch.setOnClickListener( v -> {
                    heroesFloatingRecyclerAdapter.setSort(Heroes.FIELD_BRANCH, Sort.ASCENDING);
                    heroesFixedRecyclerAdapter.setSort(Heroes.FIELD_BRANCH, Sort.ASCENDING);
                }
        );

        final  View table_header_name = child_sim.findViewById(R.id.table_header_name);
        table_header_name.setOnClickListener( v -> {
            heroesFloatingRecyclerAdapter.setSort(Heroes.FIELD_NAME, Sort.ASCENDING);
            heroesFixedRecyclerAdapter.setSort(Heroes.FIELD_NAME, Sort.ASCENDING);
        });

        final Spinner simulataion_hero_spinner = child_sim.findViewById(R.id.simulataion_hero_spinner);
        ArrayAdapter searchOptionAdapter = ArrayAdapter.createFromResource(mActivity,
                R.array.option_search_heroes, android.R.layout.simple_spinner_dropdown_item);
        simulataion_hero_spinner.setAdapter(searchOptionAdapter);

        final EditText simulation_et_search = child_sim.findViewById(R.id.simulation_et_search);

        final Button button_search_hero = child_sim.findViewById(R.id.button_search_hero);
        button_search_hero.setOnClickListener( v-> {
            String cs = simulation_et_search.getText().toString();
            HeroFilter floatingFilter = (HeroFilter)heroesFloatingRecyclerAdapter.getFilter();
            floatingFilter.setCsFieldPosition(simulataion_hero_spinner.getSelectedItemPosition());
            floatingFilter.filter(cs);

            HeroFilter fixedFilter = (HeroFilter)heroesFixedRecyclerAdapter.getFilter();
            fixedFilter.setCsFieldPosition(simulataion_hero_spinner.getSelectedItemPosition());
            fixedFilter.filter(cs);
        });












        return child_sim;

    }

}
