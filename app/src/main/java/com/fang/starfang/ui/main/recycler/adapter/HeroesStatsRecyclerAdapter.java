package com.fang.starfang.ui.main.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;

import io.realm.RealmList;

public class HeroesStatsRecyclerAdapter extends RecyclerView.Adapter<HeroesStatsRecyclerAdapter.HeroesStatsRecyclerAdapterViewHolder> {
    private RealmList<RealmInteger> stats;
    private RealmList<RealmInteger> plusStats;
    private static final String[] HOLDER = {"무력", "지력", "통솔", "민첩", "행운"};

    HeroesStatsRecyclerAdapter(Context context, RealmList<RealmInteger> stats, RealmList<RealmInteger> plusStats) {
        this.stats = stats;
        this.plusStats = plusStats;
    }

    @NonNull
    @Override
    public HeroesStatsRecyclerAdapter.HeroesStatsRecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_heroes_stats,parent,false);
        return new HeroesStatsRecyclerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  HeroesStatsRecyclerAdapterViewHolder holder, int position) {
        if( stats != null ) {
            RealmInteger stat = stats.get(position);
            if( stat != null ) {
                if( plusStats != null ) {
                    holder.bind(stat,plusStats.get(position),position);
                } else {
                    holder.bind(stat,null,position);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    static class HeroesStatsRecyclerAdapterViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView text_hero_stat_holder;
        private AppCompatTextView text_hero_stat;
        private AppCompatTextView text_hero_plus_stat;

        private HeroesStatsRecyclerAdapterViewHolder(View itemView) {
            super(itemView);
            text_hero_stat_holder = itemView.findViewById(R.id.text_hero_stat_holder);
            text_hero_stat = itemView.findViewById(R.id.text_hero_stat);
            text_hero_plus_stat = itemView.findViewById(R.id.text_hero_plus_stat);
        }

        private void bind(final RealmInteger stat, final RealmInteger plusStat, int position) {
            text_hero_stat_holder.setText(HOLDER[position]);
            if( plusStat != null) {
                int sumStat = stat.toInt() + plusStat.toInt();
                text_hero_stat.setText(String.valueOf(sumStat));
                if( plusStat.toInt() > 0) {
                    String plusStatStr = "+" + plusStat.toString();
                    text_hero_plus_stat.setText(plusStatStr);
                    text_hero_plus_stat.setVisibility(View.VISIBLE);
                }
            } else {
                text_hero_stat.setText(stat.toString());
                text_hero_plus_stat.setVisibility(View.GONE);
            }
        }

    }

}
