package com.fang.starfang.view.recycler;

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
import com.fang.starfang.view.dialog.HeroesDialogFragment;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;

public class HeroesFixedRecyclerAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> implements Filterable {

    private final String TAG  = "FANG_HERO_FIX";
    private Realm realm;


    public HeroesFixedRecyclerAdapter(Realm realm) {
        super(realm.where(Heroes.class).findAll(),false);
        this.realm = realm;
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



    private void filterResults( String cs ) {
        cs = (cs == null) ? null : cs.toLowerCase().trim();
        RealmQuery<Heroes> query = realm.where(Heroes.class);
        if( !(cs == null || "".equals(cs))) {
            query.contains(Heroes.FIELD_NAME,cs, Case.INSENSITIVE);
        }
        updateData(query.findAll());
    }

    @Override
    public Filter getFilter() {
        return new HeroesFilter(this);
    }







    private class HeroesFilter extends Filter {
        private final HeroesFixedRecyclerAdapter adapter;
        private HeroesFilter(HeroesFixedRecyclerAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());

        }
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
