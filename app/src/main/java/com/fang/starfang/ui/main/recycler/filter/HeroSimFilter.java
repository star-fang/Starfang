package com.fang.starfang.ui.main.recycler.filter;

import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFloatingRealmAdapter;

import org.apache.commons.lang3.math.NumberUtils;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class HeroSimFilter extends Filter {

    private RealmRecyclerViewAdapter<HeroSim, RecyclerView.ViewHolder> adapter;

    public HeroSimFilter(RealmRecyclerViewAdapter<HeroSim, RecyclerView.ViewHolder> adapter) {
        super();
        this.adapter = adapter;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        return new FilterResults();
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        filterResults(constraint.toString());
    }

    private void filterResults( String cs ) {
        if(cs == null ) {
            return;
        }

        cs = cs.trim();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<HeroSim> query = realm.where(HeroSim.class).contains(HeroSim.FIELD_HERO+"."+Heroes.FIELD_NAME, cs).or()
                .contains(HeroSim.FIELD_HERO+"."+Heroes.FIELD_NAME2,cs);


        adapter.updateData(query.findAll());
    }
}
