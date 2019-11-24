package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class RecyclerAdapterRef extends RecyclerView.Adapter<RecyclerAdapterRef.RecyclerViewAdapterRefViewHolder> {

    private static final String TAG = "FANG_ADAPTER_SPEC";

    @NonNull
    @Override
    public RecyclerViewAdapterRefViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(0,viewGroup,false);
        return new RecyclerAdapterRef.RecyclerViewAdapterRefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterRefViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public RecyclerAdapterRef() {

        Log.d(TAG, "constructed");
    }

    static class RecyclerViewAdapterRefViewHolder extends RecyclerView.ViewHolder {

        private RecyclerViewAdapterRefViewHolder(View itemView) {
            super(itemView);
        }


        private void bind() {
        }
    }

}
