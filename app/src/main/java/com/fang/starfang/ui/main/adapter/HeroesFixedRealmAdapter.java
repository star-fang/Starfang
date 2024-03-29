package com.fang.starfang.ui.main.adapter;

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
import com.fang.starfang.ui.main.adapter.filter.HeroSimFilter;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class HeroesFixedRealmAdapter extends RealmRecyclerViewAdapter<HeroSim, RecyclerView.ViewHolder> implements Filterable {

    private final String TAG  = "FANG_ADPT_HERO_FIX";
    private FragmentManager fragmentManager;


    public HeroesFixedRealmAdapter(
            OrderedRealmCollection<HeroSim> heroCollection
            , FragmentManager fragmentManager) {
        super(heroCollection,false);
        this.fragmentManager = fragmentManager;

        Log.d(TAG,"constructed");
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
        return new HeroSimFilter(this );
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
            String branchStr = hero.getHeroBranch();
            text_hero_branch.setText(branchStr);
            text_hero_name.setText(hero.getHeroName());
            text_hero_level.setText(String.valueOf(heroSim.getHeroLevel()));
            this.itemView.setOnClickListener(v-> HeroesDialogFragment.newInstance(hero.getHeroNo()).show(fragmentManager,TAG));
        }
    }

}
