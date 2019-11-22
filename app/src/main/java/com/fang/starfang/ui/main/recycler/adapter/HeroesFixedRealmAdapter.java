package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.dialog.HeroesDialogFragment;
import com.fang.starfang.ui.main.recycler.filter.HeroSimFilter;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class HeroesFixedRealmAdapter extends RealmRecyclerViewAdapter<HeroSim, RecyclerView.ViewHolder> implements Filterable {

    private final String TAG  = "FANG_HERO_FIX";
    private FragmentManager fragmentManager;
    private Realm realm;
    private static HeroesFixedRealmAdapter instance = null;

    public static HeroesFixedRealmAdapter getInstance() {
        return instance;
    }

    public HeroesFixedRealmAdapter(Realm realm, FragmentManager fragmentManager) {
        super(realm.where(HeroSim.class).findAll().sort(HeroSim.FIELD_HERO+"."+Heroes.FIELD_NAME).
                sort(HeroSim.FIELD_GRADE,Sort.DESCENDING).sort(HeroSim.FIELD_LEVEL, Sort.DESCENDING),false);
        this.realm = realm;
        this.fragmentManager = fragmentManager;
        instance = this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_heroes_fixed,viewGroup,false);

        return new HeroesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        HeroesViewHolder heroesViewHolder = (HeroesViewHolder) viewHolder;

        HeroSim heroSim = getItem(i);
        if(heroSim != null) {
            heroesViewHolder.bind(heroSim);
        }
    }

    public void sort(ArrayList<Pair<String, Sort>> sortPairs) {
        OrderedRealmCollection<HeroSim> realmCollection = this.getData();
        for( Pair<String, Sort> pair : sortPairs) {
            String cs = pair.first;
            Sort sort = pair.second;
            if(realmCollection != null && cs != null && sort != null ) {
                realmCollection = realmCollection.sort(cs, sort);
            }

        }
        updateData(realmCollection);

    }

    @Override
    public Filter getFilter() {
        return new HeroSimFilter(this);
    }

    private class HeroesViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView text_hero_branch;
        private AppCompatTextView text_hero_name;
        private AppCompatTextView text_hero_level;

        private HeroesViewHolder(View itemView) {
            super(itemView);
            text_hero_branch = itemView.findViewById(R.id.text_hero_branch);
            text_hero_name = itemView.findViewById(R.id.text_hero_name);
            text_hero_level = itemView.findViewById(R.id.text_hero_level);
        }

        private void bind(final HeroSim heroSim ) {
            Heroes hero = heroSim.getHero();
            String branch = hero.getHeroBranch();
            text_hero_branch.setText(hero.getHeroBranch());
            text_hero_name.setText(hero.getHeroName());
            text_hero_branch.setOnClickListener(v -> Log.d(TAG, branch + " clicked"));
            text_hero_name.setOnClickListener(v-> HeroesDialogFragment.newInstance(hero.getHeroNo()).show(fragmentManager,TAG));
            text_hero_level.setText(String.valueOf(heroSim.getHeroLevel()));

        }
    }

}
