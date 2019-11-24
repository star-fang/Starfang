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

    private static final String TAG = "FANG_ADAPTER_ITEM";
    private AppCompatTextView text_info;
    private AppCompatTextView text_desc;
    private Item item_selected;
    private Realm realm;

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


    public ItemsRealmAdapter(Realm realm, AppCompatTextView text_info, AppCompatTextView text_desc) {
        super(null, false);
        this.text_desc = text_desc;
        this.text_info = text_info;
        this.item_selected = null;
        this.realm = realm;
        Log.d(TAG, "constructed");
    }

    public Item getSelectedItem() {
        return item_selected;
    }

    @Override
    public Filter getFilter() {
        return new ItemFilter(this, realm);
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_cell_title_item_grade;
        CheckedTextView button_cell_item_name;

        private ItemsViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "view holder constructed");
            text_cell_title_item_grade = itemView.findViewById(R.id.text_cell_title_item_grade);
            button_cell_item_name = itemView.findViewById(R.id.button_cell_item_name);
        }


        private void bind(Item item) {
            text_cell_title_item_grade.setText(item.getItemGrade());
            button_cell_item_name.setText(item.getItemName());

            itemView.setOnFocusChangeListener((view, b) -> {
                if(b) {
                    item_selected = item;
                    text_info.setText(item.toString());
                    text_desc.setText(item.getitemDescription());

                }

                Log.d(TAG, "focus changed : " + b );
            });
        }
    }

}
