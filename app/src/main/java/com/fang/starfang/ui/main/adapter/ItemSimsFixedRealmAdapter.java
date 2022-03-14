package com.fang.starfang.ui.main.adapter;

import android.content.Context;
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

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ItemSimsFixedRealmAdapter extends RealmRecyclerViewAdapter<ItemSim, RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "FANG_ADPT_ITEM_FIX";

    private FragmentManager fragmentManager;
    private Context context;

    public ItemSimsFixedRealmAdapter(
            OrderedRealmCollection<ItemSim> itemCollection
            , FragmentManager fragmentManager
            , Context context) {
        super(itemCollection, false);
        this.fragmentManager = fragmentManager;
        this.context = context;
        Log.d(TAG, "constructed");
    }

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

    @Override
    public Filter getFilter() {
        return new ItemSimFilter(this, context);
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
