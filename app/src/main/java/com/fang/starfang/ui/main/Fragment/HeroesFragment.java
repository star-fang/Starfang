package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.ui.common.DiagonalScrollRecyclerView;
import com.fang.starfang.ui.main.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.adapter.HeroesFloatingRealmAdapter;
import com.fang.starfang.ui.main.adapter.filter.HeroSimFilter;

public class HeroesFragment extends PlaceholderFragment {

    static HeroesFragment newInstance(int index) {
            HeroesFragment heroesFragment = new HeroesFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, index);
            heroesFragment.setArguments(bundle);
        return heroesFragment;
    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View child_sim = inflater.inflate(R.layout.fragment_heroes, container, false);
        final RecyclerView recycler_view_hero_fixed = child_sim.findViewById(R.id.recycler_view_hero_fixed);
        final RecyclerView recycler_view_hero_floating = child_sim.findViewById(R.id.recycler_view_hero_floating);

        final HeroesFloatingRealmAdapter heroesFloatingRecyclerAdapter = HeroesFloatingRealmAdapter.getInstance();
        final HeroesFixedRealmAdapter heroesFixedRecyclerAdapter = HeroesFixedRealmAdapter.getInstance();

        final DiagonalScrollRecyclerView recycler_view_hero_content = child_sim.findViewById(R.id.recycler_view_hero_content);

        recycler_view_hero_floating.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_hero_floating.setAdapter(heroesFloatingRecyclerAdapter);
        recycler_view_hero_content.setRecyclerView(recycler_view_hero_floating);

        recycler_view_hero_fixed.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_hero_fixed.setAdapter(heroesFixedRecyclerAdapter);

        final RecyclerView.OnScrollListener[] heroRecyclerViewListeners =
                new RecyclerView.OnScrollListener[2];
        heroRecyclerViewListeners[0] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recycler_view_hero_fixed.removeOnScrollListener(heroRecyclerViewListeners[1]);
                recycler_view_hero_fixed.scrollBy(0,dy);
                recycler_view_hero_fixed.addOnScrollListener(heroRecyclerViewListeners[1]);
            }
        };

        heroRecyclerViewListeners[1] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recycler_view_hero_floating.removeOnScrollListener(heroRecyclerViewListeners[0]);
                recycler_view_hero_floating.scrollBy(0,dy);
                recycler_view_hero_floating.addOnScrollListener(heroRecyclerViewListeners[0]);
            }
        };

        recycler_view_hero_floating.addOnScrollListener(heroRecyclerViewListeners[0]);
        recycler_view_hero_fixed.addOnScrollListener(heroRecyclerViewListeners[1]);

        final AppCompatButton button_sort_hero = child_sim.findViewById(R.id.button_sort_hero);
        final AppCompatEditText simulation_et_search = child_sim.findViewById(R.id.simulation_et_search);
        final AppCompatButton button_search_hero = child_sim.findViewById(R.id.button_search_hero);

        button_sort_hero.setOnClickListener( v-> {

        });

        button_search_hero.setOnClickListener( v-> {
            Editable cs_editable = simulation_et_search.getText();
            if( cs_editable != null ) {
            String cs = cs_editable.toString();
            HeroSimFilter floatingFilter = (HeroSimFilter)heroesFloatingRecyclerAdapter.getFilter();
            floatingFilter.filter(cs);

            HeroSimFilter fixedFilter = (HeroSimFilter)heroesFixedRecyclerAdapter.getFilter();
            fixedFilter.filter(cs);
            }
        });


        return child_sim;

    }

}
