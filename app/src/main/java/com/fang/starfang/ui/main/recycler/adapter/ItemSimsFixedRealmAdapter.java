package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.ui.main.dialog.ReinforceDialogFragment;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ItemSimsFixedRealmAdapter extends RealmRecyclerViewAdapter<ItemSim, RecyclerView.ViewHolder> {

    private static final String TAG = "FANG_ADAPTER_ITEM_FIXED";

    private static ItemSimsFixedRealmAdapter instance = null;

    public static ItemSimsFixedRealmAdapter getInstance() {
        return instance;
    }

    private FragmentManager fragmentManager;

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


    public ItemSimsFixedRealmAdapter(Realm realm, FragmentManager fragmentManager) {
        super(realm.where(ItemSim.class).findAll().sort(ItemSim.FIELD_REINF), false);
        instance = this;
        this.fragmentManager = fragmentManager;
        Log.d(TAG, "constructed");
    }


    public class ItemsSimViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_item_reinforcement;
        AppCompatTextView text_item_name;

        private ItemsSimViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "view holder constructed");
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

            itemView.setOnClickListener( v -> ReinforceDialogFragment.newInstance( itemSim.getItemID() ).show(fragmentManager,TAG));
        }
    }

}
