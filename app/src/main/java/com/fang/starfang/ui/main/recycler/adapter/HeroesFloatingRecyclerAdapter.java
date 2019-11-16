package com.fang.starfang.ui.main.recycler.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.recycler.filter.HeroFilter;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class HeroesFloatingRecyclerAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = "FANG_FLOATING_ADAPTER";
    private String sort_field;
    private Sort sort;
    private WeakReference<Context> contextWeakReference;
    private final static int[] COST_PLUS_BY_UPGRADE = {0,3,5,8,10};

    public HeroesFloatingRecyclerAdapter(RealmResults<Heroes> realmResults, Context context) {
        super(realmResults,false);
        this.contextWeakReference = new WeakReference<>(context);
        sort_field = null;
        sort = null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_heroes_floating,viewGroup,false);

        return new HeroesFloatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        HeroesFloatingViewHolder heroesViewHolder = (HeroesFloatingViewHolder) viewHolder;

        Heroes hero = getItem(i);
        if( hero != null ) {
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
        return new HeroFilter(this );
    }


    private class HeroesFloatingViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView text_hero_cost_init;
        private AppCompatTextView text_hero_cost_cur;
        private AppCompatTextView text_hero_cost_fin;
        private AppCompatTextView text_hero_lineage;
        private RecyclerView recycler_view_hero_stats;
        private RecyclerView recycler_view_hero_specs_unique;
        private RecyclerView recycler_view_hero_specs_branch;

        private HeroesFloatingViewHolder(View itemView) {
            super(itemView);
            text_hero_cost_init = itemView.findViewById(R.id.text_hero_cost_init);
            text_hero_cost_cur = itemView.findViewById(R.id.text_hero_cost_cur);
            text_hero_cost_fin = itemView.findViewById(R.id.text_hero_cost_fin);
            text_hero_lineage = itemView.findViewById(R.id.text_hero_lineage);
            recycler_view_hero_stats = itemView.findViewById(R.id.recycler_view_hero_stats);
            recycler_view_hero_specs_unique = itemView.findViewById(R.id.recycler_view_hero_specs_unique);
            recycler_view_hero_specs_branch = itemView.findViewById(R.id.recycler_view_hero_specs_branch);
        }



        private void bind(final Heroes hero ) {

            Realm realm = Realm.getDefaultInstance();
            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, hero.getHeroNo()).findFirst();
            RealmList<RealmInteger> heroStats = hero.getHeroStats();
            RealmList<RealmInteger> heroPlusStats = null;
            int cost_init = hero.getHeroCost();
            if(heroSim != null) {
                heroPlusStats = heroSim.getHeroStatsUp();
                int plus_cost = COST_PLUS_BY_UPGRADE[heroSim.getHeroGrade()-1];
                switch ( plus_cost ) {
                    case 10:
                        text_hero_cost_cur.setTextColor(Color.parseColor("#000FFF"));
                        break;
                    case 8:
                        text_hero_cost_cur.setTextColor(Color.parseColor("#00498C"));
                        break;
                        default:
                            text_hero_cost_cur.setTextColor(Color.parseColor("#000000"));
                }
                text_hero_cost_cur.setText(String.valueOf( plus_cost + cost_init));
            } else {
                text_hero_cost_cur.setText(String.valueOf(cost_init));
            }
            text_hero_cost_init.setText(String.valueOf(cost_init));
            text_hero_cost_fin.setText(String.valueOf(cost_init+10));
            text_hero_lineage.setText(hero.getHeroLineage());

            HeroesStatsRecyclerAdapter heroesStatsRecyclerAdapter= new HeroesStatsRecyclerAdapter(contextWeakReference.get(), heroStats, heroPlusStats);
            recycler_view_hero_stats.setLayoutManager(new LinearLayoutManager(contextWeakReference.get()));
            recycler_view_hero_stats.setAdapter(heroesStatsRecyclerAdapter);

            RealmList<RealmString> heroSpecs = hero.getHeroSpecs();
            RealmList<RealmString> heroSepcVals = hero.getHeroSpecValues();
            HeroesUniqueSpecsRecyclerAdapter heroesUniqueSpecsRecyclerAdapter= new HeroesUniqueSpecsRecyclerAdapter(contextWeakReference.get(), heroSpecs, heroSepcVals);
            recycler_view_hero_specs_unique.setLayoutManager(new LinearLayoutManager(contextWeakReference.get()));
            recycler_view_hero_specs_unique.setAdapter(heroesUniqueSpecsRecyclerAdapter);

            Branch branch = realm.where(Branch.class).equalTo(Branch.FIELD_NAME, hero.getHeroBranch()).findFirst();
            if(branch != null) {
                RealmList<RealmString> branchSepcs = branch.getBranchSpecs();
                RealmList<RealmString> branchSepcVals = branch.getBranchSpecValues();
                HeroesBranchSpecsRecyclerAdapter heroesBranchSpecsRecyclerAdapter = new HeroesBranchSpecsRecyclerAdapter(contextWeakReference.get(), branchSepcs, branchSepcVals);
                recycler_view_hero_specs_branch.setLayoutManager(new LinearLayoutManager(contextWeakReference.get()));
                recycler_view_hero_specs_branch.setAdapter(heroesBranchSpecsRecyclerAdapter);
                recycler_view_hero_specs_branch.setVisibility(View.VISIBLE);
            } else {
                recycler_view_hero_specs_branch.setVisibility(View.GONE);
            }




        }
    }

}
