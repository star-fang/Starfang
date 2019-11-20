package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.dialog.HeroesDialogFragment;
import com.fang.starfang.ui.main.recycler.filter.HeroFilter;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class HeroesFixedRealmAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> implements Filterable {

    private final String TAG  = "FANG_HERO_FIX";
    private String sort_field;
    private Sort sort;
    private FragmentManager fragmentManager;
    private Realm realm;
    private static HeroesFixedRealmAdapter instance = null;

    public static HeroesFixedRealmAdapter getInstance() {
        return instance;
    }

    public HeroesFixedRealmAdapter(Realm realm, FragmentManager fragmentManager) {
        super(realm.where(Heroes.class).findAll().sort(Heroes.FIELD_NAME).sort(Heroes.FIELD_COST).sort(Heroes.FIELD_BRANCH),false);
        this.realm = realm;
        sort = null;
        sort_field = null;
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
        private AppCompatTextView text_hero_branch;
        private AppCompatTextView text_hero_name;
        private AppCompatTextView text_hero_level;

        private HeroesViewHolder(View itemView) {
            super(itemView);
            text_hero_branch = itemView.findViewById(R.id.text_hero_branch);
            text_hero_name = itemView.findViewById(R.id.text_hero_name);
            text_hero_level = itemView.findViewById(R.id.text_hero_level);
        }

        private void bind(final Heroes hero ) {
            String branch = hero.getHeroBranch();
            text_hero_branch.setText(hero.getHeroBranch());
            text_hero_name.setText(hero.getHeroName());
            text_hero_branch.setOnClickListener(v -> Log.d(TAG, branch + " clicked"));
            text_hero_name.setOnClickListener(v-> HeroesDialogFragment.newInstance(hero.getHeroNo()).show(fragmentManager,TAG));
            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID,hero.getHeroNo()).findFirst();
            if(heroSim != null) {
                text_hero_level.setText(String.valueOf(heroSim.getHeroLevel()));
            } else {
                text_hero_level.setText("1");
            }

        }
    }

}
