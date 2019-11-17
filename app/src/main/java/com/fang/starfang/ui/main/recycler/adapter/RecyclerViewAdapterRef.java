package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;


public class RecyclerViewAdapterRef extends RecyclerView.Adapter<RecyclerViewAdapterRef.RecyclerViewAdapterRefViewHolder> {

    private static final String TAG = "FANG_SPEC_ADAPTER";

    @NonNull
    @Override
    public RecyclerViewAdapterRefViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(0,viewGroup,false);
        return new RecyclerViewAdapterRef.RecyclerViewAdapterRefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterRefViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public RecyclerViewAdapterRef() {

        Log.d(TAG, "SpecsRecyclerViewAdapter constructed");
    }

    static class RecyclerViewAdapterRefViewHolder extends RecyclerView.ViewHolder {

        private RecyclerViewAdapterRefViewHolder(View itemView) {
            super(itemView);
        }


        private void bind() {
        }
    }

}
