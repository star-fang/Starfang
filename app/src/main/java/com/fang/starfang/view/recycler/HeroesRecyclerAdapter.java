package com.fang.starfang.view.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Heroes;
import com.fang.starfang.view.dialog.HeroesDialogFragment;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;

public class HeroesRecyclerAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> implements Filterable {

    private Realm realm;
    private FragmentManager fragmentManager;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_heroes,viewGroup,false);

        return new HeroesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        HeroesViewHolder heroesViewHolder = (HeroesViewHolder) viewHolder;

        heroesViewHolder.bind(getItem(i));


    }



    public void filterResults( String cs ) {
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




    public HeroesRecyclerAdapter(Realm realm, FragmentManager fragmentManager) {
        super(realm.where(Heroes.class).findAll(),false);
        this.realm = realm;
        this.fragmentManager = fragmentManager;

    }



    private class HeroesFilter extends Filter {
        private final HeroesRecyclerAdapter adapter;
        private HeroesFilter(HeroesRecyclerAdapter adapter) {
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



    public class HeroesViewHolder extends RecyclerView.ViewHolder {
        public TextView hero_no;
        public TextView hero_line;
        public TextView hero_name;
        public TextView hero_cost;

        public HeroesViewHolder(@NonNull View itemView) {
            super(itemView);

            hero_no = itemView.findViewById(R.id.hero_no);
            hero_line = itemView.findViewById(R.id.hero_line);
            hero_name = itemView.findViewById(R.id.hero_name);
            hero_cost = itemView.findViewById(R.id.hero_cost);

        }



        public void bind(final Heroes hero ) {
            hero_no.setText(String.valueOf(hero.getHeroNo()));
            hero_line.setText(hero.getHeroBranch());
            hero_name.setText(hero.getHeroName());
            hero_cost.setText(String.valueOf(hero.getHeroCost() + 10));
            itemView.setOnClickListener(v -> {
                HeroesDialogFragment fragment = HeroesDialogFragment.newInstance(hero);
                fragment.show(fragmentManager,"dialog");

            });
        }
    }

}
