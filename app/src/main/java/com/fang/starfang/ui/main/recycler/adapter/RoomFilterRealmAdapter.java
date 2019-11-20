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

public class RoomFilterRealmAdapter extends RealmRecyclerViewAdapter<Conversation, RecyclerView.ViewHolder> {
    //   implements Filterable {

    private static final String TAG = "FANG_FILTER_ROOM";
    private AppCompatTextView countText;
    private AppCompatTextView listText;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_filter_room,viewGroup,false);
        return new RoomFilterRealmAdapter.RoomFilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        RoomFilterRealmAdapter.RoomFilterViewHolder roomFilterViewHolder =
                (RoomFilterRealmAdapter.RoomFilterViewHolder) viewHolder;

        Conversation conversation = getItem(i);
        roomFilterViewHolder.bind(conversation);

    }

    public RoomFilterRealmAdapter(Realm realm, View view) {
        super(realm.where(Conversation.class).isNotNull(Conversation.FIELD_ROOM).distinct(Conversation.FIELD_ROOM).findAll(),true);
        countText = view.findViewById(R.id.text_filter_room_count);
        listText = view.findViewById(R.id.text_filter_room_count_desc);

        ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        countText.setText(String.valueOf(filterObject.getRoomCount()));
        listText.setText(filterObject.getRoomJoinString(10));

        Log.d(TAG, "constructed");
    }


/*
    @Override
    public Filter getFilter() {
        return new RoomFilterFilter(this, realm);
    }
*/

    private class RoomFilterViewHolder extends RecyclerView.ViewHolder {
        private CheckedTextView roomTv;

        private RoomFilterViewHolder(View itemView) {
            super(itemView);

            roomTv = itemView.findViewById(R.id.text_filter_room);
        }



        private void bind(final Conversation conversation ) {

            if(conversation == null) {
                return;
            }
            try {
                final String roomStr = conversation.getCatRoom();
                if( roomStr == null) {
                    return;
                }
                final ConversationFilterObject filterObject = ConversationFilterObject.getInstance();

                if( filterObject.roomIsAdded(roomStr)) {
                    roomTv.setCheckMarkDrawable(R.drawable.ic_check_pink_24dp);
                    roomTv.setChecked(true);
                } else {
                    roomTv.setCheckMarkDrawable(null);
                    roomTv.setChecked(false);
                }


                roomTv.setText(roomStr);

                roomTv.setOnClickListener( v -> {
                    if(roomTv.isChecked()) {
                        roomTv.setCheckMarkDrawable(null);
                        filterObject.removeRoom(roomStr);
                        roomTv.setChecked(false);
                    } else {
                        roomTv.setCheckMarkDrawable(R.drawable.ic_check_pink_24dp);
                        filterObject.addRoom(roomStr);
                        roomTv.setChecked(true);
                    }

                    countText.setText(String.valueOf(filterObject.getRoomCount()));
                    listText.setText(filterObject.getRoomJoinString(10));
                });


            } catch( NullPointerException ignored) {
            }
        }


    }



}

