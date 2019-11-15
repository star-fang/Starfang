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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
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
        private TextView text_hero_level;
        private TextView text_hero_cost_cur;
        private TextView text_hero_cost;
        private TextView text_hero_lineage;
        private TextView[] text_hero_plus_stats = new TextView[Heroes.INIT_STATS.length];
        private TextView[] text_hero_stats = new TextView[Heroes.INIT_STATS.length];
        private TextView[] text_hero_specs  = new TextView[Heroes.INIT_SPECS.length];
        private TextView[] text_hero_spec_vals = new TextView[Heroes.INIT_SPECS.length - 2];


        private HeroesFloatingViewHolder(View itemView) {
            super(itemView);

            text_hero_level = itemView.findViewById(R.id.text_hero_level);
            text_hero_cost_cur = itemView.findViewById(R.id.text_hero_cost_cur);
            text_hero_cost = itemView.findViewById(R.id.text_hero_cost);
            text_hero_lineage = itemView.findViewById(R.id.text_hero_lineage);

            Context context = contextWeakReference.get();
            Resources resources = context.getResources();
            String packageName = context.getPackageName();
            for (int i = 0; i < Heroes.INIT_STATS.length; i++) {
                text_hero_stats[i] = itemView.findViewById(resources.getIdentifier("text_hero_stat" + (i + 1), "id", packageName));
                text_hero_plus_stats[i] = itemView.findViewById(resources.getIdentifier("text_hero_plus_stat" + (i + 1), "id", packageName));

            }

            for( int i = 0; i < Heroes.INIT_SPECS.length; i++) {
                text_hero_specs[i] = itemView.findViewById(resources.getIdentifier("text_hero_spec" + (i + 1), "id", packageName));
            }


            for( int i = 0; i < Heroes.INIT_SPECS.length - 2; i++ ) {
                text_hero_spec_vals[i] = itemView.findViewById(resources.getIdentifier("text_hero_spec_val" + (i + 1), "id", packageName));
            }

        }



        private void bind(final Heroes hero ) {

            Realm realm = Realm.getDefaultInstance();
            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, hero.getHeroNo()).findFirst();
            int cost_init = hero.getHeroCost();
            if(heroSim != null) {
                text_hero_level.setText(String.valueOf(heroSim.getHeroLevel()));
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
                text_hero_level.setText("1");
                text_hero_cost_cur.setText(String.valueOf(cost_init));
            }

            text_hero_cost.setText(String.valueOf(cost_init+10));
            text_hero_lineage.setText(hero.getHeroLineage());
            RealmList<RealmInteger> heroStats = hero.getHeroStats();
            if(heroStats != null ) {
                for(int  i = 0; i < Heroes.INIT_STATS.length; i++) {
                    RealmInteger stat = heroStats.get(i);
                    if(stat != null) {
                        text_hero_stats[i].setText(stat.toString());
                        if(heroSim != null) {
                            RealmList<RealmInteger> heroPlusStats = heroSim.getHeroStatsUp();
                            if( heroPlusStats != null ) {
                                RealmInteger plusStat = heroPlusStats.get(i);
                                if( plusStat != null ) {
                                    if( plusStat.toInt() > 0) {
                                        String plusStatStr = "(+" + plusStat.toString() + ")";
                                        text_hero_stats[i].setText(String.valueOf(stat.toInt() + plusStat.toInt()));
                                        text_hero_plus_stats[i].setText(plusStatStr);
                                        text_hero_plus_stats[i].setVisibility(View.VISIBLE);
                                    } else {
                                        text_hero_plus_stats[i].setVisibility(View.GONE);
                                    }
                                }
                            }
                        } else {
                            text_hero_plus_stats[i].setVisibility(View.GONE);
                        }
                    }
                }
            }



            RealmList<RealmString> heroSpecs = hero.getHeroSpecs();
            if(heroSpecs != null) {
                for (int i = 0; i < Heroes.INIT_SPECS.length; i++) {
                    RealmString spec = heroSpecs.get(i);
                    if (spec != null) {
                        text_hero_specs[i].setText(spec.toString());
                    }
                }
            }

            RealmList<RealmString> heroSepcVals = hero.getHeroSpecValues();
            if(heroSepcVals != null ) {
                for (int i = 0; i < Heroes.INIT_SPECS.length - 2; i++) {
                    RealmString specVal = heroSepcVals.get(i);
                    if (specVal != null) {
                        text_hero_spec_vals[i].setText(specVal.toString());
                    }
                }
            }



        }
    }

}
