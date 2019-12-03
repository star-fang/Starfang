package com.fang.starfang.ui.main.recycler.filter;

import android.util.Log;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.AppConstant;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ItemSimFilter extends Filter {

    private static final String TAG = "FANG_FILTER_ITEM_SIM";
    private RealmRecyclerViewAdapter<ItemSim, RecyclerView.ViewHolder> adapter;
    private Realm realm;

    public ItemSimFilter(RealmRecyclerViewAdapter<ItemSim, RecyclerView.ViewHolder> adapter, Realm realm) {
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

        cs = cs.trim();
        String[] csSplit = cs.split(AppConstant.CONSTRAINT_SEPARATOR);
        RealmQuery<ItemSim> query = realm.where(ItemSim.class);
        String itemSimItemField = ItemSim.FIELD_ITEM + ".";

        try {
            if (!csSplit[0].equals(AppConstant.ALL_PICK_KOR)) {
                query.equalTo( itemSimItemField + Item.FIELD_GRD, csSplit[0].replace(AppConstant.GRADE_KOR, ""));
            }

            if (!csSplit[1].equals(AppConstant.ALL_PICK_KOR)) {
                query.and().beginGroup().alwaysFalse();
                RealmResults<ItemCate> categories = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_MAIN_CATE, csSplit[1]).findAll();
                for (ItemCate cate : categories) {
                    query.or().equalTo(itemSimItemField + Item.FIELD_SUB_CATE, cate.getItemSubCate());
                }
                query.endGroup();
            }

            if( !csSplit[2].equals(AppConstant.ALL_PICK_KOR) ) {
                if ( !csSplit[2].equals(AppConstant.AID_KOR) &&
                        csSplit[1].equals(AppConstant.AID_KOR)) {
                    query.and().beginGroup().isNull(itemSimItemField + Item.FIELD_RESTRICT_BRANCH).or().
                            isEmpty(itemSimItemField + Item.FIELD_RESTRICT_BRANCH).or().
                            contains(itemSimItemField + Item.FIELD_RESTRICT_BRANCH, csSplit[2]).endGroup();
                } else  {
                    query.and().equalTo(itemSimItemField + Item.FIELD_SUB_CATE, csSplit[2]);
                }
            }
        } catch( ArrayIndexOutOfBoundsException | IllegalArgumentException e ) {
            Log.d(TAG, e.toString());
        }
        adapter.updateData(query.findAll().sort(itemSimItemField+Item.FIELD_SUB_CATE));
    }
}
