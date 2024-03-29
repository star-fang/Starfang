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
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.ui.main.adapter.filter.ItemSimFilter;

import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;

public class PickItemSimRealmAdapter extends RealmRecyclerViewAdapter<ItemSim, RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "FANG_ADAPTER_ITEM_SIM";
    private AppCompatTextView text_info;
    private ItemSim item_selected;
    private Context context;

    public PickItemSimRealmAdapter(
            OrderedRealmCollection<ItemSim> itemCollection
            , AppCompatTextView text_info
            , Context context) {
        super(itemCollection, false);
        this.text_info = text_info;
        this.item_selected = null;
        this.context = context;
        Log.d(TAG, "constructed");
    }

    @NonNull
    @Override
    public PickItemSimRealmAdapter.ItemSimsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_pick_item_sim_cell,viewGroup,false);
        return new PickItemSimRealmAdapter.ItemSimsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemSim itemSim = getItem( position );
        ItemSimsViewHolder itemsViewHolder = (ItemSimsViewHolder) holder;
        if( itemSim != null ) {
            itemsViewHolder.bind(itemSim);
        }
    }

    public ItemSim getSelectedItem() {
        return item_selected;
    }

    @Override
    public Filter getFilter() {
        return new ItemSimFilter(this, context);
    }

    public class ItemSimsViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_cell_title_item_grade;
        AppCompatTextView text_cell_title_item_reinforce;
        AppCompatTextView text_cell_title_item_name;
        AppCompatTextView text_cell_title_item_who_has;

        private ItemSimsViewHolder(View itemView) {
            super(itemView);
            //Log.d(TAG, "view holder constructed");
            text_cell_title_item_grade = itemView.findViewById(R.id.text_cell_title_item_grade);
            text_cell_title_item_reinforce = itemView.findViewById(R.id.text_cell_title_item_reinforce);
            text_cell_title_item_name = itemView.findViewById(R.id.text_cell_title_item_name);
            text_cell_title_item_who_has = itemView.findViewById(R.id.text_cell_title_item_who_has);
        }


        private void bind(ItemSim itemSim) {
            Item item = itemSim.getItem();
            text_cell_title_item_grade.setText(item.getItemGrade());
            text_cell_title_item_reinforce.setText(String.valueOf(itemSim.getItemReinforcement()));
            text_cell_title_item_name.setText(item.getItemName());
            HeroSim heroSim = itemSim.getHeroWhoHasThis();
            text_cell_title_item_who_has.setText( heroSim == null ? null : heroSim.getHero().getHeroName() );

            itemView.setOnFocusChangeListener((view, b) -> {
                if(b) {
                    item_selected = itemSim;
                    StringBuilder infoBuilder = new StringBuilder();
                    infoBuilder.append(item.getItemName());
                    for( int i = 0; i < Item.INIT_STATS.length; i++ ) {
                        Integer power = itemSim.getItemPowersList().get(i);
                        Integer plusPower =  itemSim.getItemPlusPowersList().get(i);
                        String powerStr = power == null ? "" : power == 0 ? "" :
                                "\r\n" + Item.INIT_STATS[i] + ": "+ power.toString();
                        String plusPowerStr = powerStr.equals("")? "" : plusPower == null ? "" :
                                plusPower == 0 ? "" : " (+" + plusPower + ")";
                        infoBuilder.append(powerStr).append(plusPowerStr);
                    }

                    RealmList<RealmString> itemSpecs = item.getItemSpecs();
                    int specIndex = 0;
                    for( RealmString itemSpec : itemSpecs) {
                        if( itemSpec != null) {
                            RealmString specVal = item.getItemSpecValues().get(specIndex++);
                            String specStr = "\r\n"+itemSpec.toString() +
                                    (specVal == null ? "" : " " + specVal.toString());
                            infoBuilder.append(specStr);
                        }
                    } // end for

                    text_info.setText(infoBuilder.toString());

                }

                Log.d(TAG, "focus changed : " + b );
            });
        }
    }

}
