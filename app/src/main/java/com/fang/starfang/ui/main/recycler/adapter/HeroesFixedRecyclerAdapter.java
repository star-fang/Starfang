package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.dialog.HeroesDialogFragment;
import com.fang.starfang.ui.main.recycler.filter.HeroFilter;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class HeroesFixedRecyclerAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> implements Filterable {

    private final String TAG  = "FANG_HERO_FIX";
    private String sort_field;
    private Sort sort;
    private FragmentManager fragmentManager;

    public HeroesFixedRecyclerAdapter(RealmResults<Heroes> realmResults, FragmentManager fragmentManager) {
        super(realmResults,false);
        sort = null;
        sort_field = null;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_heroes_fixed,viewGroup,false);

        return new HeroesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        HeroesViewHolder heroesViewHolder = (HeroesViewHolder) viewHolder;

        Heroes hero = getItem(i);
        if(hero != null) {
            heroesViewHolder.bind(hero);
        }
    }

    public void setSort(String field, Sort sort) {
        this.sort_field = field;
        this.sort = sort;
    }

    public String getCurSortField() {
        return  sort_field;
    }

    public Sort getCurSort() {
        return  sort;
    }

    @Override
    public Filter getFilter() {
        return new HeroFilter(this);
    }

    private class HeroesViewHolder extends RecyclerView.ViewHolder {
        private TextView text_hero_branch;
        private TextView text_hero_name;

        private HeroesViewHolder(View itemView) {
            super(itemView);

            text_hero_branch = itemView.findViewById(R.id.text_hero_branch);
            text_hero_name = itemView.findViewById(R.id.text_hero_name);

        }



        private void bind(final Heroes hero ) {
            String branch = hero.getHeroBranch();
            text_hero_branch.setText(hero.getHeroBranch());
            text_hero_name.setText(hero.getHeroName());
            text_hero_branch.setOnClickListener(v -> Log.d(TAG, branch + " clicked"));
            text_hero_name.setOnClickListener(v-> HeroesDialogFragment.newInstance(hero.getHeroNo()).show(fragmentManager,TAG));

        }
    }

}
