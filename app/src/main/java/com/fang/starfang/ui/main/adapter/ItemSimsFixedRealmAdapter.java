package com.fang.starfang.ui.main.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.ui.main.dialog.ReinforceItemDialogFragment;
import com.fang.starfang.ui.main.adapter.filter.ItemSimFilter;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ItemSimsFixedRealmAdapter extends RealmRecyclerViewAdapter<ItemSim, RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "FANG_ADAPTER_ITEM_FIXED";

    private static ItemSimsFixedRealmAdapter instance;

    public static ItemSimsFixedRealmAdapter getInstance() {
        return instance;
    }
    public static void setInstance(Realm realm, FragmentManager fragmentManager) {
        instance = new ItemSimsFixedRealmAdapter(realm, fragmentManager);
    }

    private FragmentManager fragmentManager;
    private Realm realm;

    @NonNull
    @Override
    public ItemSimsFixedRealmAdapter.ItemsSimViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_items_fixed,viewGroup,false);
        return new ItemSimsFixedRealmAdapter.ItemsSimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemSim itemSim = getItem( position );
        ItemsSimViewHolder itemsViewHolder = (ItemsSimViewHolder) holder;
        if( itemSim != null ) {
            itemsViewHolder.bind(itemSim);
        }
    }


    private ItemSimsFixedRealmAdapter(Realm realm, FragmentManager fragmentManager) {
        super(realm.where(ItemSim.class).findAll().sort(ItemSim.FIELD_REINF), false);
        this.realm = realm;
        this.fragmentManager = fragmentManager;
        Log.d(TAG, "constructed");
    }

    @Override
    public Filter getFilter() {
        return new ItemSimFilter(this,realm);
    }


    public class ItemsSimViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_item_reinforcement;
        AppCompatTextView text_item_name;

        private ItemsSimViewHolder(View itemView) {
            super(itemView);
            //Log.d(TAG, "view holder constructed");
            text_item_reinforcement = itemView.findViewById(R.id.text_item_reinforcement);
            text_item_name = itemView.findViewById(R.id.text_item_name);
        }


        private void bind(ItemSim itemSim) {
            Item item = itemSim.getItem();
            String itemName;
            if(item != null) {
                itemName = item.getItemName();
            } else {
                itemName = "??";
            }
            text_item_name.setText(itemName);
            text_item_reinforcement.setText(String.valueOf(itemSim.getItemReinforcement()));

            itemView.setOnClickListener( v -> ReinforceItemDialogFragment.newInstance( itemSim.getItemID(), -1 ).show(fragmentManager,TAG));
        }
    }

}
