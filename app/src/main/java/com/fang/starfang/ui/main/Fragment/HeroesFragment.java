package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.ui.main.recycler.custom.DiagonalScrollRecyclerView;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFloatingRealmAdapter;
import com.fang.starfang.ui.main.recycler.filter.HeroSimFilter;

import io.realm.Realm;

public class HeroesFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_FRAG_HEROES";
    private Realm realm;

    static HeroesFragment newInstance(int index) {
            HeroesFragment heroesFragment = new HeroesFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, index);
            heroesFragment.setArguments(bundle);
        return heroesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG,"_ON CREATE");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
        Log.d(TAG, "_ON DESTROY VIEW : realm instance closed");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"_ON CREATE VIEW");
        realm = Realm.getDefaultInstance();
        final View child_sim = inflater.inflate(R.layout.fragment_heroes, container, false);
        final RecyclerView recycler_view_hero_fixed = child_sim.findViewById(R.id.recycler_view_hero_fixed);
        final RecyclerView recycler_view_hero_floating = child_sim.findViewById(R.id.recycler_view_hero_floating);
        final HeroesFloatingRealmAdapter heroesFloatingRecyclerAdapter = new HeroesFloatingRealmAdapter(realm,mActivity);
        final HeroesFixedRealmAdapter heroesFixedRecyclerAdapter = new HeroesFixedRealmAdapter(realm, getFragmentManager());
        final DiagonalScrollRecyclerView recycler_view_hero_content = child_sim.findViewById(R.id.recycler_view_hero_content);

        recycler_view_hero_floating.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_hero_floating.setAdapter(heroesFloatingRecyclerAdapter);
        recycler_view_hero_content.setRecyclerView(recycler_view_hero_floating);

        recycler_view_hero_fixed.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_hero_fixed.setAdapter(heroesFixedRecyclerAdapter);

        final RecyclerView.OnScrollListener[] heroRecyclerViewLiteners =
                new RecyclerView.OnScrollListener[2];
        heroRecyclerViewLiteners[0] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recycler_view_hero_fixed.removeOnScrollListener(heroRecyclerViewLiteners[1]);
                recycler_view_hero_fixed.scrollBy(0,dy);
                recycler_view_hero_fixed.addOnScrollListener(heroRecyclerViewLiteners[1]);
            }
        };

        heroRecyclerViewLiteners[1] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recycler_view_hero_floating.removeOnScrollListener(heroRecyclerViewLiteners[0]);
                recycler_view_hero_floating.scrollBy(0,dy);
                recycler_view_hero_floating.addOnScrollListener(heroRecyclerViewLiteners[0]);
            }
        };

        recycler_view_hero_floating.addOnScrollListener(heroRecyclerViewLiteners[0]);
        recycler_view_hero_fixed.addOnScrollListener(heroRecyclerViewLiteners[1]);

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
