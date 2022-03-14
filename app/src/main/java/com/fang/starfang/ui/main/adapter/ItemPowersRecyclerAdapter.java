package com.fang.starfang.ui.main.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.task.Reinforcement;

import java.util.ArrayList;
import io.realm.RealmList;

public class ItemPowersRecyclerAdapter extends RecyclerView.Adapter<ItemPowersRecyclerAdapter.ItemPowersViewHolder> {

    private static final String TAG = "FANG_ADPT_ITEM_POW";
    private RealmList<RealmInteger> itemBasePowers;
    private ArrayList<Integer> itemPlusPowers;
    private ArrayList<Integer> itemPowers;
    private Reinforcement reinforcement;
    private int reinforceValue;

    public ItemPowersRecyclerAdapter(
            RealmList<RealmInteger> itemBasePowers
            , ArrayList<Integer> itemPlusPowers
            , ArrayList<Integer> itemPowers
            , Reinforcement reinforcement) {
        this.itemBasePowers = itemBasePowers;
        this.itemPlusPowers = itemPlusPowers;
        this.itemPowers = itemPowers;
        this.reinforcement = reinforcement;
        Log.d(TAG, "constructed");
    }

    @NonNull
    @Override
    public ItemPowersRecyclerAdapter.ItemPowersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_reinforce_cell_power,viewGroup,false);
        return new ItemPowersRecyclerAdapter.ItemPowersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPowersRecyclerAdapter.ItemPowersViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return itemPlusPowers.size();
    }

    public void setReinforceValue( int reinforceValue ) {
        this.reinforceValue = reinforceValue;
    }

    class ItemPowersViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView text_dialog_reinforce_cell_power_name;
        private AppCompatTextView text_dialog_reinforce_cell_power_init;
        private AppCompatTextView text_dialog_reinforce_cell_power_plus;
        private AppCompatTextView text_dialog_reinforce_cell_power_fin;

        private ItemPowersViewHolder(View itemView) {
            super(itemView);
            text_dialog_reinforce_cell_power_name = itemView.findViewById(R.id.text_dialog_reinforce_cell_power_name);
            text_dialog_reinforce_cell_power_init = itemView.findViewById(R.id.text_dialog_reinforce_cell_power_init);
            text_dialog_reinforce_cell_power_plus = itemView.findViewById(R.id.text_dialog_reinforce_cell_power_plus);
            text_dialog_reinforce_cell_power_fin = itemView.findViewById(R.id.text_dialog_reinforce_cell_power_fin);
        }

        private void bind(int position) {
            text_dialog_reinforce_cell_power_name.setText(HeroSim.POWERS_KOR[position]);
            int plusPower = reinforcement.reinforce(position,reinforceValue);

            RealmInteger basePower = itemBasePowers.get(position);
            int basePowerInt = basePower == null ? 0 : basePower.toInt();
            String basePowerStr = basePower == null ? "" : basePower.toString();

            text_dialog_reinforce_cell_power_init.setText(basePowerStr);
            itemPlusPowers.set(position,plusPower);
            text_dialog_reinforce_cell_power_plus.setText(String.valueOf(plusPower));

            int powerSum = basePowerInt + plusPower;
            itemPowers.set(position, powerSum);
            text_dialog_reinforce_cell_power_fin.setText(String.valueOf( powerSum ) );



        }
    }

}
