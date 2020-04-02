package com.fang.starfang.ui.main.adapter.filter;

import android.util.Log;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ItemFilter extends Filter {

    private RealmRecyclerViewAdapter<Item, RecyclerView.ViewHolder> adapter;
    private Realm realm;
    private static final String TAG = "FANG_FILTER_ITEM";

    public ItemFilter(RealmRecyclerViewAdapter<Item, RecyclerView.ViewHolder> adapter, Realm realm) {
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
        String[] csSplit = cs.split(FangConstant.CONSTRAINT_SEPARATOR);
        RealmQuery<Item> query = realm.where(Item.class);

        try {
            if ( csSplit.length > 0 && !csSplit[0].isEmpty()) {
                String digits = csSplit[0].replaceAll("[^0-9]","");
                query.equalTo(Item.FIELD_GRD, digits.isEmpty() ? csSplit[0] : digits);
            }

            if ( csSplit.length > 1 && !csSplit[1].isEmpty()) {
                query.and().beginGroup().alwaysFalse();
                RealmResults<ItemCate> categories = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_MAIN_CATE, csSplit[1]).findAll();
                for (ItemCate category : categories) {
                    query.or().equalTo(Item.FIELD_SUB_CATE, category.getItemSubCate());
                }
                query.endGroup();
            }

            if ( csSplit.length > 2 && !csSplit[2].isEmpty() ) {
                query.and().equalTo(Item.FIELD_SUB_CATE, csSplit[2]);
            }
        } catch( ArrayIndexOutOfBoundsException | IllegalArgumentException e ) {
            Log.d(TAG, e.toString());
        }
        adapter.updateData(query.findAll().sort(Item.FIELD_SUB_CATE));
    }
}
