package com.fang.starfang.ui.main.adapter.filter;

import android.util.Log;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Heroes;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;

public class HeroSimFilter extends Filter {

    private static final String TAG = "FANG_HERO_FILTER";
    private RealmRecyclerViewAdapter<HeroSim, RecyclerView.ViewHolder> adapter;

    public HeroSimFilter(RealmRecyclerViewAdapter<HeroSim, RecyclerView.ViewHolder> adapter) {
        super();
        this.adapter = adapter;
    }


    @Override
    protected FilterResults performFiltering(@NonNull CharSequence constraint) {
        return new FilterResults();
    }

    @Override
    protected void publishResults(@NonNull CharSequence constraint, FilterResults results) {
        filterResults(constraint.toString());
    }

    private void filterResults( String cs ) {
        if(cs != null ) {
            cs = cs.trim();
            try (Realm realm = Realm.getDefaultInstance() ) {

                RealmQuery<HeroSim> query = realm.where(HeroSim.class).contains(HeroSim.FIELD_HERO + "." + Heroes.FIELD_NAME, cs).or()
                        .contains(HeroSim.FIELD_HERO + "." + Heroes.FIELD_NAME2, cs);

                adapter.updateData(query.findAll());
            } catch ( RuntimeException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }
        }
    }
}
