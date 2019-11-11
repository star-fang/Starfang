package com.fang.starfang.ui.main.Fragment;

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
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fang.starfang.NotificationListener;
import com.fang.starfang.R;
import com.fang.starfang.local.task.RealmSyncTask;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

public class SettingFragment extends PlaceholderFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "FANG_SETTING_FRAG";
    private static WeakReference<SettingFragment> settingFragmentWeakReference = null;
    private View child_setting;

    static SettingFragment getInstance() {
        if( settingFragmentWeakReference == null ) {
            SettingFragment settingFragment = new SettingFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, 3);
            settingFragment.setArguments(bundle);
            settingFragmentWeakReference = new WeakReference<>(settingFragment);
        }
        return settingFragmentWeakReference.get();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"_ON CREATE");
        /*냥봇 설정 파트*/
        child_setting = View.inflate(mActivity, R.layout.view_setting, null );
        final Switch switch_bot = child_setting.findViewById(R.id.notifications_start);
        final EditText text_address = child_setting.findViewById(R.id.text_address);
        final Button button_sync_all = child_setting.findViewById(R.id.start_sync_key_all);
        final Button button_notifications_setting= child_setting.findViewById(R.id.notifications_setting);
        final Button button_insert_name = child_setting.findViewById(R.id.button_insert_name);
        final EditText text_name = child_setting.findViewById(R.id.text_name);
        text_address.setEnabled(false);
        text_address.setInputType(InputType.TYPE_NULL);
        button_sync_all.setOnClickListener(v -> new RealmSyncTask(text_address.getText().toString(), mActivity).execute(getResources().getStringArray(R.array.pref_list_table)));
        button_notifications_setting.setOnClickListener(v -> {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } );
        switch_bot.setChecked(NotificationListener.getStatus().equals("start"));
        switch_bot.setOnCheckedChangeListener((v,isChecked)->{

            Intent intent = new Intent(mActivity, NotificationListener.class);
            if(isChecked) {
                if(!isMyServiceRunning()) {
                    Snackbar.make(child_setting,"알림 읽기 권한 설정 하세요",Snackbar.LENGTH_SHORT).show();
                    switch_bot.setChecked(false);
                } else {
                    intent.putExtra("status", "start");
                    Snackbar.make(child_setting,"냥봇 시작",Snackbar.LENGTH_SHORT).show();
                    mActivity.startService(intent);
                }
            } else {
                intent.putExtra("status","stop");
                Snackbar.make(child_setting,"냥봇 중지",Snackbar.LENGTH_SHORT).show();
                mActivity.startService(intent);
            }
        });

        String curName = NotificationListener.getName();
        text_name.setText(curName);

        button_insert_name.setOnClickListener(v-> {
            String name = text_name.getText().toString();
            name = name.trim();
            if(name.length() > 1 ) {
                Intent intent = new Intent(mActivity, NotificationListener.class);
                intent.putExtra("name", name);
                Snackbar.make(child_setting,"냥봇 이름 설정됨 : " + name, Snackbar.LENGTH_SHORT).show();
                mActivity.startService(intent);
            } else {
                Snackbar.make(child_setting,"이름을 2글자 이상 입력 하세요.", Snackbar.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"_ON CREATE VIEW");

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        final ConstraintLayout constraintLayout = root.findViewById(R.id.constraintLayout);
        constraintLayout.removeAllViews();
        constraintLayout.addView(child_setting);

        return root;

    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager)mActivity.getSystemService(Context.ACTIVITY_SERVICE);
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
