package com.fang.starfang.view.recycler;

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
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.view.dialog.HeroesDialogFragment;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class HeroesFloatingRecyclerAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> {

    Realm realm;

    public HeroesFloatingRecyclerAdapter(Realm realm) {
        super(realm.where(Heroes.class).findAll(),false);
        this.realm = realm;
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

    public void sort(String field, Sort sort) {
        updateData(realm.where(Heroes.class).sort(field, sort).findAll());
    }



    private class HeroesFloatingViewHolder extends RecyclerView.ViewHolder {
        private TextView text_hero_cost;
        private TextView text_hero_lineage;
        private TextView[] text_hero_stats = new TextView[Heroes.INIT_STATS.length];
        private TextView[] text_hero_specs  = new TextView[Heroes.INIT_SPECS.length];
        private TextView[] text_hero_spec_vals = new TextView[Heroes.INIT_SPECS.length - 2];


        private HeroesFloatingViewHolder(View itemView) {
            super(itemView);

            text_hero_cost = itemView.findViewById(R.id.text_hero_cost);
            text_hero_lineage = itemView.findViewById(R.id.text_hero_lineage);
            text_hero_stats[0] = itemView.findViewById(R.id.text_hero_stat1);
            text_hero_stats[1] = itemView.findViewById(R.id.text_hero_stat2);
            text_hero_stats[2] = itemView.findViewById(R.id.text_hero_stat3);
            text_hero_stats[3] = itemView.findViewById(R.id.text_hero_stat4);
            text_hero_stats[4] = itemView.findViewById(R.id.text_hero_stat5);
            text_hero_specs[0] = itemView.findViewById(R.id.text_hero_spec1);
            text_hero_spec_vals[0] = itemView.findViewById(R.id.text_hero_spec_val1);
            text_hero_specs[1] = itemView.findViewById(R.id.text_hero_spec2);
            text_hero_spec_vals[1] = itemView.findViewById(R.id.text_hero_spec_val2);
            text_hero_specs[2] = itemView.findViewById(R.id.text_hero_spec3);
            text_hero_spec_vals[2] = itemView.findViewById(R.id.text_hero_spec_val3);
            text_hero_specs[3] = itemView.findViewById(R.id.text_hero_spec4);
            text_hero_spec_vals[3] = itemView.findViewById(R.id.text_hero_spec_val4);
            text_hero_specs[4]= itemView.findViewById(R.id.text_hero_spec_vice);
            text_hero_specs[5] = itemView.findViewById(R.id.text_hero_spec_lord);

        }



        private void bind(final Heroes hero ) {
            text_hero_cost.setText(String.valueOf(hero.getHeroCost()+10));
            text_hero_lineage.setText(hero.getHeroLineage());
            RealmList<RealmInteger> heroStats = hero.getHeroStats();
            if(heroStats != null ) {
                for(int  i = 0; i < 5; i++) {
                    RealmInteger stat = heroStats.get(i);
                    if(stat != null) {
                        text_hero_stats[i].setText(stat.toString());
                    }
                }
            }

            RealmList<RealmString> heroSpecs = hero.getHeroSpecs();
            RealmList<RealmString> heroSepcVals = hero.getHeroSpecValues();

            if(heroSpecs != null) {
                for (int i = 0; i < 6; i++) {
                    RealmString spec = heroSpecs.get(i);
                    if (spec != null) {
                        text_hero_specs[i].setText(spec.toString());
                    }
                }
            }

            if(heroSepcVals != null ) {
                for (int i = 0; i < 4; i++) {
                    RealmString specVal = heroSepcVals.get(i);
                    if (specVal != null) {
                        text_hero_spec_vals[i].setText(specVal.toString());
                    }
                }
            }



        }
    }

}
