package com.fang.starfang.ui.main.recycler.filter;

import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ItemFilter extends Filter {

    private RealmRecyclerViewAdapter<Item, RecyclerView.ViewHolder> adapter;
    private Realm realm;

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
        String[] csSplit = cs.split(",");
        RealmQuery<Item> query = realm.where(Item.class).equalTo(Item.FIELD_GRD,csSplit[0].replace("등급",""));
        if(!csSplit[1].equals("전체")) {
            query.and().beginGroup().alwaysFalse();
            RealmResults<ItemCate> cates = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_MAIN_CATE,csSplit[1]).findAll();
            for(ItemCate cate : cates) {
                query.or().equalTo(Item.FIELD_SUB_CATE, cate.getItemSubCate());
            }
            query.endGroup();
        }
        adapter.updateData(query.findAll().sort(Item.FIELD_SUB_CATE));
    }
}
