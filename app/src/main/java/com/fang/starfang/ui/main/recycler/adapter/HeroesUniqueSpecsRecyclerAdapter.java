package com.fang.starfang.ui.main.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.source.Heroes;

import io.realm.RealmList;

public class HeroesUniqueSpecsRecyclerAdapter extends RecyclerView.Adapter<HeroesUniqueSpecsRecyclerAdapter.HeroesUniqueSpecsRecyclerAdapterViewHolder> {
    private RealmList<RealmString> specs;
    private RealmList<RealmString> specValues;
    private static final String[] HOLDER = {"Lv30", "Lv50", "Lv70", "Lv90", "태수", "군주 "};

    HeroesUniqueSpecsRecyclerAdapter(Context context, RealmList<RealmString> specs, RealmList<RealmString> specValues) {
        this.specs = specs;
        this.specValues = specValues;
    }

    @NonNull
    @Override
    public HeroesUniqueSpecsRecyclerAdapter.HeroesUniqueSpecsRecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_heroes_specs_unique,parent,false);
        return new HeroesUniqueSpecsRecyclerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  HeroesUniqueSpecsRecyclerAdapterViewHolder holder, int position) {
        if( specs != null ) {
            RealmString spec = specs.get(position);
            if( spec != null ) {
                if( specValues != null && position < Heroes.INIT_SPECS.length - 2  ) {
                    holder.bind(spec,specValues.get(position),position);
                } else {
                    holder.bind(spec,null,position);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return specs.size();
    }

    static class HeroesUniqueSpecsRecyclerAdapterViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView text_hero_spec_holder;
        private AppCompatTextView text_hero_spec;
        private AppCompatTextView text_hero_spec_val;

        private HeroesUniqueSpecsRecyclerAdapterViewHolder(View itemView) {
            super(itemView);
            text_hero_spec_holder = itemView.findViewById(R.id.text_hero_spec_holder_unique);
            text_hero_spec = itemView.findViewById(R.id.text_hero_spec_unique);
            text_hero_spec_val = itemView.findViewById(R.id.text_hero_spec_val_unique);
        }

        private void bind(final RealmString spec, final RealmString specValue, int position) {
            text_hero_spec_holder.setText(HOLDER[position]);
            text_hero_spec.setText(spec.toString());
            if( specValue != null) {
                text_hero_spec_val.setText(specValue.toString());
                text_hero_spec_val.setVisibility(View.VISIBLE);
            } else {
                text_hero_spec_val.setVisibility(View.GONE);
            }
        }

    }

}
