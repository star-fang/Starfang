package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.ui.main.recycler.filter.ItemFilter;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ItemsRealmAdapter extends RealmRecyclerViewAdapter<Item, RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "FANG_ITEM_ADAPTER";

    @NonNull
    @Override
    public ItemsRealmAdapter.ItemsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_add_item_cell,viewGroup,false);
        return new ItemsRealmAdapter.ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = getItem( position );
        ItemsViewHolder itemsViewHolder = (ItemsViewHolder) holder;
        if( item != null ) {
            itemsViewHolder.bind(item);
        }
    }


    public ItemsRealmAdapter(Realm realm) {
        super(realm.where(Item.class).findAll().sort(Item.FIELD_SUB_CATE), false);
        Log.d(TAG, "constructed");
    }

    @Override
    public Filter getFilter() {
        return new ItemFilter(this);
    }

    static class ItemsViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_cell_title_item_grade;
        CheckedTextView button_cell_item_name;

        private ItemsViewHolder(View itemView) {
            super(itemView);
            text_cell_title_item_grade = itemView.findViewById(R.id.text_cell_title_item_grade);
            button_cell_item_name = itemView.findViewById(R.id.button_cell_item_name);
        }


        private void bind(Item item) {
            text_cell_title_item_grade.setText(item.getItemGrade());
            button_cell_item_name.setText(item.getItemName());
        }
    }

}
