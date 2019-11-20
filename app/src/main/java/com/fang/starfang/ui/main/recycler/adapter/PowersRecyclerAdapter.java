package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;

import java.util.ArrayList;
import io.realm.RealmList;

public class PowersRecyclerAdapter extends RecyclerView.Adapter<PowersRecyclerAdapter.PowersRecyclerViewAdapterViewHolder> {

    private static final String TAG = "FANG_POWER_ADAPTER";
    private RealmList<RealmString> branchStatGGs;
    private RealmList<RealmInteger> heroBaseStats;
    private ArrayList<Integer> heroStatsUpList;
    private int level;

    @NonNull
    @Override
    public PowersRecyclerAdapter.PowersRecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_heroes_cell_power,viewGroup,false);
        return new PowersRecyclerAdapter.PowersRecyclerViewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PowersRecyclerAdapter.PowersRecyclerViewAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return heroStatsUpList.size();
    }

    //branchSpecGGs,heroBaseStats,heroStatsUpList,curLevel
    public PowersRecyclerAdapter(RealmList<RealmString> branchStatGGs, RealmList<RealmInteger> heroBaseStats,
                                 ArrayList<Integer> heroStatsUpList, int level) {
        this.branchStatGGs = branchStatGGs;
        this.heroBaseStats = heroBaseStats;
        this.heroStatsUpList = heroStatsUpList;
        this.level = level;
        Log.d(TAG, "constructed");
    }

    public void setLevel( int level ) {
        this.level = level;
    }

    class PowersRecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView text_dialog_heroes_cell_power_name;
        private AppCompatTextView text_dialog_heroes_cell_power_grade;
        private AppCompatTextView text_dialog_heroes_cell_power_rate;
        private AppCompatTextView text_dialog_heroes_cell_power;

        private PowersRecyclerViewAdapterViewHolder(View itemView) {
            super(itemView);
            text_dialog_heroes_cell_power_name= itemView.findViewById(R.id.text_dialog_heroes_cell_power_name);
            text_dialog_heroes_cell_power_grade= itemView.findViewById(R.id.text_dialog_heroes_cell_power_grade);
            text_dialog_heroes_cell_power_rate= itemView.findViewById(R.id.text_dialog_heroes_cell_power_rate);
            text_dialog_heroes_cell_power= itemView.findViewById(R.id.text_dialog_heroes_cell_power);
        }

        private void bind(int position) {
            text_dialog_heroes_cell_power_name.setText(HeroSim.POWERS_KOR[position]);

            String branchStatGGsStr = "S";
            if( branchStatGGs != null ) {
                RealmString branchStatGG = branchStatGGs.get(position);
                if( branchStatGG != null ) {
                    branchStatGGsStr =  branchStatGG.toString();
                }
            }

            text_dialog_heroes_cell_power_grade.setText(branchStatGGsStr);
            RealmInteger heroBaseStat = heroBaseStats.get(position);
            int heroBaseStatInt = heroBaseStat == null? 0 : heroBaseStat.toInt();
            int heroStatUp = heroStatsUpList.get(position);
            int statSum = heroBaseStatInt + heroStatUp;
            double growthRate = HeroSim.calcGrowthRateByStatAndGrade(statSum, branchStatGGsStr);
            text_dialog_heroes_cell_power_rate.setText(String.valueOf(growthRate));
            double power = (double)Math.max(0,heroStatUp - 100) +  statSum / 2.0  + level * growthRate;
            text_dialog_heroes_cell_power.setText(String.valueOf(power));
        }
    }

}
