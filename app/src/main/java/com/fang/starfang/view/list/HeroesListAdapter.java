package com.fang.starfang.view.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fang.starfang.R;
import com.fang.starfang.model.realm.Heroes;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class HeroesListAdapter extends RealmBaseAdapter<Heroes> {

    private static class ViewHolder {
        TextView hero_no;
        TextView hero_line;
        TextView hero_name;
        TextView hero_cost;
    }

    public HeroesListAdapter(OrderedRealmCollection<Heroes> data) {
        super(data);
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

    //public void setData(OrderedRealmCollection<Heroes> details) {
    //    this.adapterData = details;
     //   notifyDataSetChanged();
   // }


}
