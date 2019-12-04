package com.fang.starfang.ui.main.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.ui.main.recycler.adapter.RoomFilterRealmAdapter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilterObject;

public class RoomFilterDialogFragment extends UpdateDialogFragment {

    public static RoomFilterDialogFragment newInstance() {
        return new RoomFilterDialogFragment();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_filter_room, null);

        final ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        final RoomFilterRealmAdapter roomFilterRealmAdapter =
                new RoomFilterRealmAdapter(realm,
                        view.findViewById(R.id.text_filter_room_count),
                        view.findViewById(R.id.text_filter_room_count_desc));

        final RecyclerView recycler_view_filter_room = view.findViewById(R.id.recycler_view_filter_room);
        recycler_view_filter_room.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_filter_room.setAdapter(roomFilterRealmAdapter);

        builder.setView(view).setPositiveButton(R.string.setting_kor, (dialog, which) -> {
            filterObject.setCheckRoom(true);
            ConversationFilter conversationFilter = ConversationFilter.getInstance();
            if( conversationFilter != null ) {
                conversationFilter.filter("on");
            }
            boolean check = filterObject.getRoomCount()>0;
            filterObject.setCheckRoom(check);
            ConstraintDocBuilder docBuilder = ConstraintDocBuilder.getInstance();
            if(docBuilder != null) {
                docBuilder.build();
            }
        });

        return builder.create();
    }


}

    /*
     final RoomFilterRealmAdapter roomFilterRealmAdapter =
                new RoomFilterRealmAdapter(realm,
                        findViewById(R.id.text_filter_room_count),
                        findViewById(R.id.text_filter_room_count_desc));
        final AppCompatButton button_filter_room =  findViewById(R.id.button_filter_room);
        final View inner_column_filter_room = findViewById(R.id.inner_column_filter_room);
        final AppCompatButton button_filter_room_commit = findViewById(R.id.button_filter_room_commit);

        if( filterObject.isRoomChecked()) {
            button_filter_room.setBackgroundResource(R.drawable.round_button_checked);
        }


        button_filter_room.setOnLongClickListener( view -> {
            filter_summary_layout.setVisibility(View.INVISIBLE);
            conversation_recycler_view.setAdapter(roomFilterRealmAdapter);
            changeLayouWeight(inner_column_filter,0.0f);
            changeLayouWeight(inner_column_filter_room,1.0f);
            title_conversation.setText(R.string.filter_room);
            return true;
        });
        button_filter_room.setOnClickListener( view -> {
            boolean checked =  filterObject.isRoomChecked();
            if( checked ) {
                filterObject.setCheckRoom(false);
                conversationFilter.filter("on");
                button_filter_room.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"단톡방 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckRoom(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getRoomCount()>0;
                if( check ) {
                    button_filter_room.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(view,"단톡방 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckRoom(false);
                    Snackbar.make(view,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
                }
            }
            buildConstraintDoc( realm, text_filter_summary);
        });
        button_filter_room_commit.setOnClickListener( view -> {
            filter_summary_layout.setVisibility(View.VISIBLE);
            filterObject.setCheckRoom(true);
            conversationFilter.filter("on");
            boolean check = filterObject.getRoomCount()>0;
            if( check ) {
                button_filter_room.setBackgroundResource(R.drawable.round_button_checked);
                Snackbar.make(view,"단톡방 필터 ON",Snackbar.LENGTH_SHORT).show();
            } else {
                button_filter_room.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"단톡방 필터 OFF",Snackbar.LENGTH_SHORT).show();
            }
            filterObject.setCheckRoom(check);
            conversation_recycler_view.setAdapter(conversationRecyclerAdapter);

            changeLayouWeight(inner_column_filter,1.0f);
            changeLayouWeight(inner_column_filter_room,0.0f);
            buildConstraintDoc( realm, text_filter_summary);
            title_conversation.setText(R.string.tab_text_3);

        });
     */
