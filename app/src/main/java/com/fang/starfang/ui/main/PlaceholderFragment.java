package com.fang.starfang.ui.main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.NotificationListener;
import com.fang.starfang.R;
import com.fang.starfang.local.task.RealmSyncTask;
import com.fang.starfang.view.recycler.ConversationFilter;
import com.fang.starfang.view.recycler.ConversationRecyclerAdapter;

import java.util.Objects;

import io.realm.Realm;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "FANG_SETTING";

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        /*냥봇 설정 파트*/
        final ConstraintLayout constraintLayout = root.findViewById(R.id.constraintLayout);
        final View child_setting = inflater.inflate(R.layout.view_setting,constraintLayout,false);

        final Switch switch_bot = child_setting.findViewById(R.id.notifications_start);
        final EditText text_address = child_setting.findViewById(R.id.text_address);
        final Button button_sync_all = child_setting.findViewById(R.id.start_sync_key_all);
        final Button button_notifications_setting= child_setting.findViewById(R.id.notifications_setting);
        text_address.setEnabled(false);
        text_address.setInputType(InputType.TYPE_NULL);

        button_sync_all.setOnClickListener(v -> new RealmSyncTask(text_address.getText().toString(), getActivity()).execute(getResources().getStringArray(R.array.pref_list_table)));

        button_notifications_setting.setOnClickListener(v -> {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } );


        switch_bot.setChecked(NotificationListener.getStatus().equals("start"));

        switch_bot.setOnCheckedChangeListener((v,isChecked)->{

            Intent intent = new Intent(getActivity(), NotificationListener.class);
            if(isChecked) {
                if(!isMyServiceRunning()) {
                    Toast.makeText(getActivity(), "알림 읽기 권한 설정 하세요", Toast.LENGTH_SHORT).show();
                    switch_bot.setChecked(false);
                } else {
                    intent.putExtra("status", "start");
                    Toast.makeText(getActivity(), "냥봇 시작", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getActivity()).startService(intent);
                }
            } else {
                intent.putExtra("status","stop");
                Toast.makeText(getActivity(), "냥봇 중지", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getActivity()).startService(intent);
            }
        });

        /*대화 파트*/
        Realm realm = Realm.getDefaultInstance();
        final View child_conversation = inflater.inflate(R.layout.view_conversation,constraintLayout,false);
        RecyclerView recyclerView = child_conversation.findViewById(R.id.conversation_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        final ConversationRecyclerAdapter conversationRecyclerAdapter = new ConversationRecyclerAdapter( realm, recyclerView );
        recyclerView.setAdapter(conversationRecyclerAdapter);

        final AppCompatButton button_filter_sendCat =  child_conversation.findViewById(R.id.button_filter_sendCat);
        final LinearLayout inner_row_filter_sendCat = child_conversation.findViewById(R.id.inner_row_filter_sendCat);
        final AppCompatButton button_input_filter_sendCat = child_conversation.findViewById(R.id.button_input_filter_sendCat);
        final AppCompatEditText text_input_filter_sendCat = child_conversation.findViewById(R.id.text_input_filter_sendCat);
        final AppCompatCheckBox checkbox_filter_sendCat = child_conversation.findViewById(R.id.checkbox_filter_sendCat);

        button_filter_sendCat.setOnClickListener( v -> {
            ViewGroup.LayoutParams layoutParams = inner_row_filter_sendCat.getLayoutParams();
            layoutParams.width = 270;
            inner_row_filter_sendCat.setLayoutParams(layoutParams);
        });

        button_input_filter_sendCat.setOnClickListener( v -> {
            ViewGroup.LayoutParams layoutParams_check = checkbox_filter_sendCat.getLayoutParams();
            layoutParams_check.width = 30;
            checkbox_filter_sendCat.setLayoutParams(layoutParams_check);
            checkbox_filter_sendCat.setChecked(true);

            ViewGroup.LayoutParams layoutParams_row = inner_row_filter_sendCat.getLayoutParams();
            layoutParams_row.width = 0;
            inner_row_filter_sendCat.setLayoutParams(layoutParams_row);

            try {
                String filterStr_sendCat = text_input_filter_sendCat.getText().toString();
                ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setSendCats(new String[]{filterStr_sendCat});
                conversationRecyclerAdapter.getFilter().filter("on");
                //ConversationFilter.ConversationFilterObject = new ConversationFilter.ConversationFilterObject();
            } catch (NullPointerException ignored) {

            }




        });


        pageViewModel.getText().observe(this, s -> {
            if( s== null)
                return;

            constraintLayout.removeAllViews();
            switch(s) {
                case "search":

                    break;
                case "conversation":
                    constraintLayout.addView(child_conversation);
                    break;
                case "reply":
                    constraintLayout.addView(child_setting);
                    break;
                    default:

            }

           //textView.setText(s);
        });
        return root;
    }




    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationListener.class.getName().equals(service.service.getClassName())) {
                Log.d(TAG,"알림 설정 됨");
                return true;

            }
        }
        Log.d(TAG,"알림 설정 안됨");
        return false;
    }
}