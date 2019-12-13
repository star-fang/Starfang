package com.fang.starfang.ui.main.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.Heroes;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class PickHeroSimRealmAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "FANG_ADAPTER_PICK_HERO";
    private Heroes hero_selected;
    @NonNull
    @Override
    public PickHeroSimRealmAdapter.PickHeroViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_pick_hero_cell,viewGroup,false);
        return new PickHeroSimRealmAdapter.PickHeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Heroes hero = getItem( position );
        PickHeroViewHolder itemsViewHolder = (PickHeroViewHolder) holder;
        if( hero != null ) {
            itemsViewHolder.bind(hero);
        }
    }


    public PickHeroSimRealmAdapter(RealmResults<Heroes> realmResults) {
        super(realmResults, false);
        this.hero_selected = null;
        Log.d(TAG, "constructed");
    }

    public Heroes getSelectedHero() {
        return hero_selected;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class PickHeroViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_pick_cell_hero_name;
        AppCompatTextView text_pick_cell_hero_branch;

        private PickHeroViewHolder(View itemView) {
            super(itemView);
            //Log.d(TAG, "view holder constructed");
            text_pick_cell_hero_name = itemView.findViewById(R.id.text_pick_cell_hero_name);
            text_pick_cell_hero_branch = itemView.findViewById(R.id.text_pick_cell_hero_branch);
        }


        private void bind(Heroes hero) {
            text_pick_cell_hero_name.setText(hero.getHeroName());
            text_pick_cell_hero_branch.setText(hero.getHeroBranch());

            itemView.setOnFocusChangeListener((view, b) -> {
                if(b) {
                    hero_selected = hero;
                }
                Log.d(TAG, "focus changed : " + b );
            });
        }
    }

}
