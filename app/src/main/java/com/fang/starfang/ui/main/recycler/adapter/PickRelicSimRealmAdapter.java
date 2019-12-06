package com.fang.starfang.ui.main.recycler.adapter;

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
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.ui.main.recycler.filter.RelicSimFilter;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class PickRelicSimRealmAdapter extends RealmRecyclerViewAdapter<RelicSim, RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "FANG_ADAPTER_RELIC_SIM";
    private AppCompatTextView text_info;
    private AppCompatTextView text_desc;
    private RelicSim relic_selected;
    private Realm realm;

    @NonNull
    @Override
    public PickRelicSimRealmAdapter.RelicSimViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_pick_relic_sim_cell,viewGroup,false);
        return new PickRelicSimRealmAdapter.RelicSimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RelicSim relicSim = getItem( position );
        RelicSimViewHolder itemsViewHolder = (RelicSimViewHolder) holder;
        if( relicSim != null ) {
            itemsViewHolder.bind(relicSim);
        }
    }


    public PickRelicSimRealmAdapter(Realm realm, AppCompatTextView text_info, AppCompatTextView text_desc) {
        super(realm.where(RelicSim.class).isNull(RelicSim.FIELD_HERO).findAll(), false);
        this.text_info = text_info;
        this.text_desc = text_desc;
        this.relic_selected = null;
        this.realm = realm;
        Log.d(TAG, "constructed");
    }

    public RelicSim getSelectedRelic() {
        return relic_selected;
    }

    @Override
    public Filter getFilter() {
        return new RelicSimFilter(this, realm);
    }

    public class RelicSimViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_cell_relic_sim_prefix;
        AppCompatTextView text_cell_relic_sim_suffix;
        AppCompatTextView text_cell_relic_sim_grade;
        AppCompatTextView text_cell_relic_sim_level;

        private RelicSimViewHolder(View itemView) {
            super(itemView);
            text_cell_relic_sim_prefix = itemView.findViewById(R.id.text_cell_relic_sim_prefix);
            text_cell_relic_sim_suffix = itemView.findViewById(R.id.text_cell_relic_sim_suffix);
            text_cell_relic_sim_grade = itemView.findViewById(R.id.text_cell_relic_sim_grade);
            text_cell_relic_sim_level = itemView.findViewById(R.id.text_cell_relic_sim_level);
        }


        private void bind(RelicSim relicSim) {
            RelicSFX relicSFX= relicSim.getSuffix();
            RelicPRFX relicPRFX = relicSim.getPrefix();
            text_cell_relic_sim_prefix.setText( relicPRFX == null ? null : relicPRFX.getRelicPrefixName());
            text_cell_relic_sim_suffix.setText( relicSFX == null ? null : relicSFX.getRelicSuffixName());
            text_cell_relic_sim_grade.setText( relicSFX == null ? null : String.valueOf(relicSFX.getRelicSuffixGrade()));
            int level = relicSim.getRelicLevel();
            text_cell_relic_sim_level.setText( String.valueOf( level ) );

            itemView.setOnFocusChangeListener((view, b) -> {
                if(b) {
                    relic_selected = relicSim;
                    text_info.setText(relicSim.toString());
                }
            });
        }
    }

}
