package com.fang.starfang.view.recycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Conversation;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ConversationRecyclerAdapter
        extends RealmRecyclerViewAdapter<Conversation, RecyclerView.ViewHolder>
        implements Filterable {

    private static final String TAG = "FANG_CONVERSATION";
    private WeakReference<RecyclerView> recyclerViewWeakReference;
    private Realm realm;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Log.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_conversation,viewGroup,false);
        return new ConversationRecyclerAdapter.ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ConversationRecyclerAdapter.ConversationViewHolder conversationViewHolder =
                (ConversationRecyclerAdapter.ConversationViewHolder) viewHolder;

        Conversation conversation = getItem(i);
        conversationViewHolder.bind(conversation);

    }

    public ConversationRecyclerAdapter(Realm realm, RecyclerView recyclerView) {
        super(realm.where(Conversation.class).findAll(),true);
        //Log.d(TAG,"ConversationRecyclerAdapter constructed");
        realm.addChangeListener(o -> {
            notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(getItemCount());
        });
        recyclerView.scrollToPosition(getItemCount());

        this.realm = realm;
        this.recyclerViewWeakReference = new WeakReference<>(recyclerView);
    }


    @Override
    public Filter getFilter() {
        return new ConversationFilter(this, realm);
    }


    private class ConversationViewHolder extends RecyclerView.ViewHolder {
        private TextView sandCatTv;
        private TextView catRoomTv;
        private TextView timestampTv;
        private TextView conversationTv;

        private ConversationViewHolder(View itemView) {
            super(itemView);

            sandCatTv = itemView.findViewById(R.id.conv_sendCat);
            catRoomTv = itemView.findViewById(R.id.conv_catRoom);
            timestampTv = itemView.findViewById(R.id.conv_timestamp);
            conversationTv = itemView.findViewById(R.id.conv_conversation);
        }



        private void bind(final Conversation conversation ) {
            try {
                //Log.d(TAG,conversation.getSandCat());
                sandCatTv.setText(conversation.getSendCat());
                timestampTv.setText(conversation.getTimestamp());
                catRoomTv.setText(conversation.getCatRoom());
                conversationTv.setText(conversation.getConversation());

            } catch( NullPointerException ignored) {
            }
            //itemView.setOnClickListener(v -> {
            //});
        }


    }

}
