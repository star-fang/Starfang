package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ItemSimsRealmAdapter extends RealmRecyclerViewAdapter<ItemSim, RecyclerView.ViewHolder> {

    private static final String TAG = "FANG_ADAPTER_ITEM_SIM";
    private AppCompatTextView text_info;
    private AppCompatTextView text_desc;
    private ItemSim item_sim_selected;

    private static ItemSimsRealmAdapter instance = null;

    public static ItemSimsRealmAdapter getInstance() {
        return instance;
    }

    @NonNull
    @Override
    public ItemSimsRealmAdapter.ItemsSimViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_add_item_cell,viewGroup,false);
        return new ItemSimsRealmAdapter.ItemsSimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemSim itemSim = getItem( position );
        ItemsSimViewHolder itemsViewHolder = (ItemsSimViewHolder) holder;
        if( itemSim != null ) {
            itemsViewHolder.bind(itemSim);
        }
    }


    public ItemSimsRealmAdapter(Realm realm, AppCompatTextView text_info, AppCompatTextView text_desc) {
        super(realm.where(ItemSim.class).findAll().sort(ItemSim.FIELD_REINF), false);
        this.text_desc = text_desc;
        this.text_info = text_info;
        this.item_sim_selected = null;
        instance = this;

        Log.d(TAG, "constructed");
    }

    public ItemSim getSelectedItemSim() {
        return item_sim_selected;
    }


    public class ItemsSimViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_cell_title_item_grade;
        CheckedTextView button_cell_item_name;

        private ItemsSimViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "view holder constructed");
            text_cell_title_item_grade = itemView.findViewById(R.id.text_cell_title_item_grade);
            button_cell_item_name = itemView.findViewById(R.id.button_cell_item_name);
        }


        private void bind(ItemSim itemSim) {
            Item item = itemSim.getItem();
            if(item != null) {
                text_cell_title_item_grade.setText(item.getItemGrade());
                button_cell_item_name.setText(item.getItemName());

                itemView.setOnFocusChangeListener((view, b) -> {
                    if (b) {
                        item_sim_selected = itemSim;
                        text_info.setText(item.toString());
                        text_desc.setText(item.getitemDescription());
                        Log.d(TAG, "focused : " + itemSim.getItemID());
                    }


                });
            }
        }
    }

}
