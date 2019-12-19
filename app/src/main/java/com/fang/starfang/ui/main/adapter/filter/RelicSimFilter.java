package com.fang.starfang.ui.main.adapter.filter;

import android.util.Log;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.AppConstant;
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;

import org.apache.commons.lang3.math.NumberUtils;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class RelicSimFilter extends Filter {

    private static final String TAG = "FANG_FILTER_ITEM_SIM";
    private RealmRecyclerViewAdapter<RelicSim, RecyclerView.ViewHolder> adapter;
    private Realm realm;

    public RelicSimFilter(RealmRecyclerViewAdapter<RelicSim, RecyclerView.ViewHolder> adapter, Realm realm) {
        super();
        this.adapter = adapter;
        this.realm = realm;
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
        // 사신, 접두사, 접미사, 등급

        cs = cs.trim();
        String[] csSplit = cs.split(AppConstant.CONSTRAINT_SEPARATOR);
        RealmQuery<RelicSim> query = realm.where(RelicSim.class).isNull(RelicSim.FIELD_HERO);
        String relicSimPrefixField = RelicSim.FIELD_PREFIX + ".";
        String relicSimSuffixField = RelicSim.FIELD_SUFFIX + ".";

        try {
            int guardianType = NumberUtils.toInt(csSplit[0],0);
            if (guardianType > 0) {
                query.and().equalTo( relicSimSuffixField + RelicSFX.FIELD_TYPE, guardianType);
            }

            if (!csSplit[1].isEmpty()) {
                query.and().equalTo(relicSimPrefixField + RelicPRFX.FIELD_NAME, csSplit[1]);
            }

            if( !csSplit[2].isEmpty() ) {
                query.and().equalTo(relicSimSuffixField + RelicSFX.FIELD_NAME, csSplit[2]);

            }

            int grade = NumberUtils.toInt(csSplit[3],0);
            if( grade > 0 ) {
                query.and().equalTo(relicSimSuffixField + RelicSFX.FIELD_GRD, grade);
            }

        } catch( ArrayIndexOutOfBoundsException | IllegalArgumentException e ) {
            Log.d(TAG, e.toString());
        }
        adapter.updateData(query.findAll().sort(RelicSim.FIELD_LEVEL, Sort.DESCENDING));
    }
}
