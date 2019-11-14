package com.fang.starfang.ui.main.recycler.filter;

import android.view.View;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFixedRecyclerAdapter;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFloatingRecyclerAdapter;

import org.apache.commons.lang3.math.NumberUtils;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class HeroFilter extends Filter {

    private RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> adapter;
    private Realm realm;
    private int cs_field_position;

    public HeroFilter(RealmRecyclerViewAdapter adapter, Realm realm) {
        super();
        this.adapter = adapter;
        this.realm = realm;
        cs_field_position = 0;
    }

    public void setCsFieldPosition( int position) {
        this.cs_field_position = position;
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

        RealmQuery<Heroes> query = realm.where(Heroes.class);

        switch(cs_field_position ) {
            case 0:
                query.contains(Heroes.FIELD_NAME,cs).or().contains(Heroes.FIELD_NAME2,cs);
                break;
            case 1:
                query.contains(Heroes.FIELD_BRANCH,cs);
                break;
            case 2:
                cs = cs.replaceAll("[^0-9]","");
                query.equalTo(Heroes.FIELD_COST, NumberUtils.toInt(cs,0));
                break;
            case 3:
                query.contains(Heroes.FIELD_LINEAGE,cs);
                break;
            case 4:
                cs = cs.replaceAll("[^0-9]","");
                query.equalTo(Heroes.FIELD_STATS + "." + RealmInteger.VALUE, NumberUtils.toInt(cs,0));
                break;
            case 5:
                query.contains(Heroes.FIELD_SPECS + "." + RealmString.VALUE,cs);
                break;
                default:
        }

        /*
        <item>이름</item>
        <item>병종</item>
        <item>Cost</item>
        <item>계보</item>
        <item>스탯</item>
        <item>특성</item>
         */




        Sort sort = null;
        String field = null;
        if( adapter instanceof HeroesFloatingRecyclerAdapter) {
            sort = ((HeroesFloatingRecyclerAdapter) adapter).getCurSort();
            field = ((HeroesFloatingRecyclerAdapter) adapter).getCurSortField();
        } else if( adapter instanceof HeroesFixedRecyclerAdapter) {
            sort = ((HeroesFixedRecyclerAdapter) adapter).getCurSort();
            field = ((HeroesFixedRecyclerAdapter) adapter).getCurSortField();
        }

        if(sort != null && field != null) {
            query.sort(field,sort);
        }
        adapter.updateData(query.findAll());
    }
}
