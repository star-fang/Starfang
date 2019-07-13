package com.fang.starfang.view.list;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Heroes;


import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmQuery;

public class HeroesListAdapter extends RealmBaseAdapter<Heroes> implements Filterable {

    private Realm realm;

    private static class ViewHolder {
        TextView hero_no;
        TextView hero_line;
        TextView hero_name;
        TextView hero_cost;
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
        HeroesFilter filter = new HeroesFilter(this);
        return filter;
    }




    public HeroesListAdapter(OrderedRealmCollection<Heroes> data, Realm realm) {
        super(data);
        this.realm = realm;




    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_heroes,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.hero_no = convertView.findViewById(R.id.hero_no);
            viewHolder.hero_line = convertView.findViewById(R.id.hero_line);
            viewHolder.hero_name = convertView.findViewById(R.id.hero_name);
            viewHolder.hero_cost = convertView.findViewById(R.id.hero_cost);
            convertView.setTag(viewHolder);

        } else {
                   viewHolder = (ViewHolder) convertView.getTag();
        }

        //if(adapterData != null) {
            Heroes hero = this.getItem( position);
            if( viewHolder != null ) {
                viewHolder.hero_no.setText(String.valueOf(hero.getHeroNo()));
                viewHolder.hero_line.setText(hero.getHeroBranch());
                viewHolder.hero_name.setText(hero.getHeroName());
                viewHolder.hero_cost.setText(String.valueOf(hero.getHeroCost() + 10));
       //     }




        }



        return convertView;
    }


    private class HeroesFilter extends Filter {
        private final HeroesListAdapter adapter;
        private HeroesFilter(HeroesListAdapter adapter) {
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

}
