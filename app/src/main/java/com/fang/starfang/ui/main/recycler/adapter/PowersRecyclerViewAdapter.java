package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PowersRecyclerViewAdapter extends RecyclerView.Adapter<PowersRecyclerViewAdapter.PowersRecyclerViewAdapterViewHolder> {

    private static final String TAG = "FANG_POWER_ADAPTER";

    @NonNull
    @Override
    public PowersRecyclerViewAdapter.PowersRecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(0,viewGroup,false);
        return new PowersRecyclerViewAdapter.PowersRecyclerViewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PowersRecyclerViewAdapter.PowersRecyclerViewAdapterViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public PowersRecyclerViewAdapter() {

        Log.d(TAG, "constructed");
    }

    static class PowersRecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder {

        private PowersRecyclerViewAdapterViewHolder(View itemView) {
            super(itemView);
        }


        private void bind() {
        }
    }

}
