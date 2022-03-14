package com.fang.starfang.ui.main.adapter.filter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
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
    private String aid;

    public ItemSimFilter(
            RealmRecyclerViewAdapter<ItemSim,
                    RecyclerView.ViewHolder> adapter,
            Context context) {
        super();
        this.adapter = adapter;
        this.aid = context.getResources().getString(R.string.aid);
    }


    @Override
    protected FilterResults performFiltering(@NonNull CharSequence constraint) {
        return new FilterResults();
    }

    @Override
    protected void publishResults(@NonNull CharSequence constraint, FilterResults results) {
        if (!TextUtils.isEmpty(constraint)) {
            filterResults(constraint.toString());
        }
    }

    private void filterResults(String cs) {
        if (cs != null) {
            cs = cs.trim();
            try ( Realm realm = Realm.getDefaultInstance() ){
            String[] csSplit = cs.split(FangConstant.CONSTRAINT_SEPARATOR);
            RealmQuery<ItemSim> query = realm.where(ItemSim.class);
            String itemSimItemField = ItemSim.FIELD_ITEM + ".";


                if (csSplit.length > 0 && !csSplit[0].isEmpty()) {
                    String digits = csSplit[0].replaceAll("[^0-9]", "");
                    query.equalTo(itemSimItemField + Item.FIELD_GRD, digits.isEmpty() ? csSplit[0] : digits);
                }

                if (csSplit.length > 1 && !csSplit[1].isEmpty()) {
                    query.and().beginGroup().alwaysFalse();
                    RealmResults<ItemCate> categories = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_MAIN_CATE, csSplit[1]).findAll();
                    for (ItemCate cate : categories) {
                        query.or().equalTo(itemSimItemField + Item.FIELD_SUB_CATE, cate.getItemSubCate());
                    }
                    query.endGroup();
                }

                if (csSplit.length > 2 && !csSplit[2].isEmpty()) {
                    if (!csSplit[2].equals(aid) &&
                            csSplit[1].equals(aid)) {
                        query.and().beginGroup().isNull(itemSimItemField + Item.FIELD_RESTRICT_BRANCH).or().
                                isEmpty(itemSimItemField + Item.FIELD_RESTRICT_BRANCH).or().
                                contains(itemSimItemField + Item.FIELD_RESTRICT_BRANCH, csSplit[2]).endGroup();
                    } else {
                        query.and().equalTo(itemSimItemField + Item.FIELD_SUB_CATE, csSplit[2]);
                    }
                }
                adapter.updateData(query.findAll().sort(itemSimItemField + Item.FIELD_SUB_CATE));
            } catch ( RuntimeException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }// try..catch
        } // if cs != null
    }
}
