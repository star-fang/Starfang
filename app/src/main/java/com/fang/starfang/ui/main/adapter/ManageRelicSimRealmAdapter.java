package com.fang.starfang.ui.main.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.ui.main.adapter.filter.RelicSimFilter;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ManageRelicSimRealmAdapter extends RealmRecyclerViewAdapter<RelicSim, RecyclerView.ViewHolder> {

    private static final String TAG = "FANG_ADAPT_M_RELIC";
    private AppCompatTextView text_selected_relic_count;
    private LinearLayout layout_edit_relic_group;
    private ArrayList<RelicSim> relics_selected;

    @NonNull
    @Override
    public ManageRelicSimRealmAdapter.RelicSimViewHolderM onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_relic,viewGroup,false);
        return new ManageRelicSimRealmAdapter.RelicSimViewHolderM(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RelicSim relicSim = getItem( position );
        RelicSimViewHolderM itemsViewHolder = (RelicSimViewHolderM) holder;
        if( relicSim != null ) {
            itemsViewHolder.bind(relicSim);
        }
    }


    public ManageRelicSimRealmAdapter(RealmResults<RelicSim> relicSims,
            LinearLayout layout_edit_relic_group, AppCompatTextView text_selected_relic_count ) {
        super(relicSims, false);
        this.text_selected_relic_count = text_selected_relic_count;
        this.layout_edit_relic_group = layout_edit_relic_group;
        this.relics_selected = new ArrayList<>();
        Log.d(TAG, "constructed");
    }

    public ArrayList<RelicSim> getSelectedRelics() {
        return relics_selected;
    }


    public class RelicSimViewHolderM extends RecyclerView.ViewHolder {

        AppCompatCheckBox checkbox_relic;
        AppCompatTextView text_relic_id;
        AppCompatTextView text_relic_prefix;
        AppCompatTextView text_relic_hero;
        AppCompatImageButton button_confirm_relic;
        AppCompatImageButton button_remove_relic;

        private RelicSimViewHolderM(View itemView) {
            super(itemView);
            checkbox_relic = itemView.findViewById(R.id.checkbox_relic);
            text_relic_id = itemView.findViewById(R.id.text_relic_id);
            text_relic_prefix = itemView.findViewById(R.id.text_relic_prefix);
            text_relic_hero = itemView.findViewById(R.id.text_relic_hero);
            button_confirm_relic = itemView.findViewById(R.id.button_confirm_relic);
            button_remove_relic = itemView.findViewById(R.id.button_remove_relic);
        }


        private void bind(RelicSim relicSim) {
            HeroSim heroSim = relicSim.getHeroWhoHasThis();
            RelicPRFX relicPRFX = relicSim.getPrefix();
            text_relic_id.setText(relicSim.getRelicID());
            text_relic_prefix.setText( relicPRFX == null ? null : relicPRFX.getRelicPrefixName());
            text_relic_hero.setText( heroSim == null ? null : heroSim.getHero().getHeroName());
            checkbox_relic.setOnCheckedChangeListener((compoundButton, isChecked)->{
                if(isChecked) {
                    relics_selected.add(relicSim);
                } else {
                    relics_selected.remove(relicSim);
                }

                int count_selected = relics_selected.size();
                text_selected_relic_count.setText(count_selected);
                if(count_selected > 0 ) {
                    layout_edit_relic_group.setVisibility(View.VISIBLE);
                } else {
                    layout_edit_relic_group.setVisibility(View.GONE);
                }
            });

        }
    }

}
