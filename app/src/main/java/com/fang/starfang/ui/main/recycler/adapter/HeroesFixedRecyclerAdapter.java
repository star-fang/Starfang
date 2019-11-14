package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.recycler.filter.HeroFilter;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class HeroesFixedRecyclerAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> implements Filterable {

    private final String TAG  = "FANG_HERO_FIX";
    private Realm realm;
    private String sort_field;
    private Sort sort;

    public HeroesFixedRecyclerAdapter(Realm realm) {
        super(realm.where(Heroes.class).findAll(),false);
        this.realm = realm;
        sort = null;
        sort_field = null;
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
        return new HeroFilter(this, realm);
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
        }
    }

}
