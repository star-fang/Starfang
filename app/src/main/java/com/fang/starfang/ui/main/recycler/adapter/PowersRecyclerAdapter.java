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
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.NormalItem;

import java.util.ArrayList;

import io.realm.RealmList;

public class PowersRecyclerAdapter extends RecyclerView.Adapter<PowersRecyclerAdapter.PowersRecyclerViewAdapterViewHolder> {

    private static final String TAG = "FANG_ADAPTER_POWER";
    private RealmList<RealmString> branchStatGGs;
    private RealmList<RealmInteger> heroBaseStats;
    private ArrayList<Integer> heroStatsUpList;
    private int level;
    private int reinforce;
    private RealmList<NormalItem> normalItems; // weapon, armor, aid
    private RealmList<ItemSim> itemSims; // weapon, armor, aid

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
                                 ArrayList<Integer> heroStatsUpList, int level, int reinforce, RealmList<NormalItem> normalItems,RealmList<ItemSim> itemSims ) {
        this.branchStatGGs = branchStatGGs;
        this.heroBaseStats = heroBaseStats;
        this.heroStatsUpList = heroStatsUpList;
        this.level = level;
        this.reinforce = reinforce;
        this.normalItems = normalItems;
        this.itemSims = itemSims;
        Log.d(TAG, "constructed");


    }

    public void setLevel( int level ) {
        this.level = level;
    }
    public void setReinforce( int reinforce ) {
        this.reinforce = reinforce;
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
            int power = (int)Math.floor(Math.max(0,heroStatUp - 100) +  statSum / 2.0  + level * growthRate);

            int normalItemPowerSum = 0;
            for(NormalItem normalItem : normalItems ) {
                RealmList<RealmInteger> normalItemPowerInitList = normalItem == null ? null : normalItem.getNormalItemPowers();
                RealmInteger normalItemPowerInit = normalItemPowerInitList == null? null : normalItem.getNormalItemPowers().get(position);
                int normalItemPowerInt = normalItemPowerInit == null? 0 : normalItemPowerInit.toInt();

                if(reinforce > 1 && normalItemPowerInt != 0) {
                    RealmList<RealmInteger> normalItemLevelUpPowers = normalItem.getNormalItemLevelUpPowers();
                    RealmInteger normalItemLevelUpPower = normalItemLevelUpPowers == null ? null : normalItemLevelUpPowers.get(reinforce - 2);
                    normalItemPowerInt = normalItemLevelUpPower == null ? 0 : normalItemLevelUpPower.toInt();
                } // end if reinforce > 1

                normalItemPowerSum += normalItemPowerInt;
            } // end for

            int itemPowerSum = 0;
            for(ItemSim itemSim : itemSims ) {
                RealmList<RealmInteger> itemPowers = itemSim == null? null : itemSim.getItemPowers();
                RealmInteger itemPower = itemPowers == null ? null : itemPowers.get(position);
                int itemPowerInt = itemPower == null ? 0 : itemPower.toInt();
                itemPowerSum += itemPowerInt;
            } // end for

            //if( normalItemPowerSum > 0 ) {
             //   Log.d(TAG, "power position : " + position + "// normal power : " + normalItemPowerSum);
            //}

            power += normalItemPowerSum;
            power += itemPowerSum;
            text_dialog_heroes_cell_power.setText(String.valueOf(power));



        }
    }

}
