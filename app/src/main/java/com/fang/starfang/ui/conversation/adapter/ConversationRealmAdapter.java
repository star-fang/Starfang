package com.fang.starfang.ui.conversation.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Conversation;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ConversationRealmAdapter
        extends RealmRecyclerViewAdapter<Conversation, RecyclerView.ViewHolder>
        implements Filterable {

    private static final String TAG = "FANG_ADAPTER_CONV";

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_conversation,viewGroup,false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ConversationViewHolder conversationViewHolder =
                (ConversationViewHolder) viewHolder;

        Conversation conversation = getItem(i);
        conversationViewHolder.bind(conversation);

    }

    public ConversationRealmAdapter(OrderedRealmCollection<Conversation> conversations) {
        super(conversations,false);
        Log.d(TAG,"constructed");
    }


    @Override
    public Filter getFilter() {
        return new ConversationFilter(this );
    }


    private class ConversationViewHolder extends RecyclerView.ViewHolder {
        private TextView sendCatTv;
        private TextView catRoomTv;
        private TextView timestampTv;
        private TextView conv_conversation;


        private ConversationViewHolder(View itemView) {
            super(itemView);

            sendCatTv = itemView.findViewById(R.id.conv_sendCat);
            catRoomTv = itemView.findViewById(R.id.conv_catRoom);
            timestampTv = itemView.findViewById(R.id.conv_timestamp);
            conv_conversation = itemView.findViewById(R.id.conv_conversation);
        }



        private void bind(final Conversation conversation ) {

            if( conversation == null) {
                return;
            }

            String sendCatStr = conversation.getSendCat();
            if( sendCatStr == null ) {
                sendCatTv.setVisibility(View.GONE);
            } else {
                sendCatTv.setVisibility(View.VISIBLE);
                sendCatTv.setText(sendCatStr);
            }

            String catRoomStr = conversation.getCatRoom();
            if( catRoomStr == null ) {
                catRoomTv.setVisibility(View.GONE);
            } else {
                catRoomTv.setVisibility(View.VISIBLE);
                catRoomTv.setText( catRoomStr );
            }

            timestampTv.setText(conversation.getTimestamp());
            conv_conversation.setText(conversation.getConversation());

            String packageStr = conversation.getPackageName();

            if(packageStr != null) {
                switch (packageStr) {
                    case FangConstant.PACKAGE_KAKAO:
                        conv_conversation.setBackgroundResource(R.drawable.kakao_border);
                        break;
                    case FangConstant.PACKAGE_DISCORD:
                        conv_conversation.setBackgroundResource(R.drawable.discord_border);
                        break;
                    default:
                        conv_conversation.setBackgroundResource(R.drawable.round_border);
                }
            }



        }


    }

}
