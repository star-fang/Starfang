package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilterObject;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class SendCatFilterRealmAdapter extends RealmRecyclerViewAdapter<Conversation, RecyclerView.ViewHolder> {
     //   implements Filterable {
    private static final String TAG = "FANG_ADAPTER_SEND_CAT";
    private AppCompatTextView countText;
    private AppCompatTextView listText;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_filter_sendcat,viewGroup,false);
        return new SendCatFilterRealmAdapter.SendCatFilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        SendCatFilterRealmAdapter.SendCatFilterViewHolder sendCatFilterViewHolder =
                (SendCatFilterRealmAdapter.SendCatFilterViewHolder) viewHolder;

        Conversation conversation = getItem(i);
        sendCatFilterViewHolder.bind(conversation);

    }

    public SendCatFilterRealmAdapter(Realm realm, AppCompatTextView countText, AppCompatTextView listText) {
        super(realm.where(Conversation.class).isNotNull(Conversation.FIELD_SENDCAT).distinct(Conversation.FIELD_SENDCAT).findAll(),true);
        this.countText = countText;
        this.listText = listText;

        ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        countText.setText(String.valueOf(filterObject.getSendCatCount()));
        listText.setText(filterObject.getSendCatJoinString(10));

        Log.d(TAG, "constructed");
    }






/*
    @Override
    public Filter getFilter() {
        return new SendCatFilterFilter(this, realm);
    }
*/

    private class SendCatFilterViewHolder extends RecyclerView.ViewHolder {
        private CheckedTextView sendCatTv;

        private SendCatFilterViewHolder(View itemView) {
            super(itemView);

            sendCatTv = itemView.findViewById(R.id.text_filter_sendCat);
        }



        private void bind(final Conversation conversation ) {

            if( conversation == null ) {
                return;
            }
            try {

                final String sendCatStr = conversation.getSendCat();
                if( sendCatStr == null) {
                    return;
                }
                final ConversationFilterObject filterObject = ConversationFilterObject.getInstance();

                if( filterObject.catIsAdded(sendCatStr)) {
                    sendCatTv.setCheckMarkDrawable(R.drawable.ic_check_pink_24dp);
                    sendCatTv.setChecked(true);
                } else {
                    sendCatTv.setCheckMarkDrawable(null);
                    sendCatTv.setChecked(false);
                }


                sendCatTv.setText(sendCatStr);

                sendCatTv.setOnClickListener( v -> {
                    if(sendCatTv.isChecked()) {
                        sendCatTv.setCheckMarkDrawable(null);
                        filterObject.removeSendCat(sendCatStr);
                        sendCatTv.setChecked(false);
                    } else {
                        sendCatTv.setCheckMarkDrawable(R.drawable.ic_check_pink_24dp);
                        filterObject.addSendCat(sendCatStr);
                        sendCatTv.setChecked(true);
                    }

                    countText.setText(String.valueOf(filterObject.getSendCatCount()));
                    listText.setText(filterObject.getSendCatJoinString(10));
                });


            } catch( NullPointerException e) {
                e.printStackTrace();
            }
        }


    }



}

