package com.fang.starfang;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.fang.starfang.local.task.RealmSyncTask;
import com.fang.starfang.util.ScreenUtils;
import com.google.android.material.snackbar.Snackbar;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "FANG_ACTIVITY_SETTING";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"_ON CREATE");

        setContentView(R.layout.activity_setting);

        ActionBar actionBar = getActionBar();
        if( actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


        /*냥봇 설정 파트*/
        final Switch switch_bot = findViewById(R.id.notifications_start);
        final EditText text_address = findViewById(R.id.text_address);
        final Button button_sync_all = findViewById(R.id.start_sync_key_all);
        final Button button_notifications_setting= findViewById(R.id.notifications_setting);
        final Button button_insert_name = findViewById(R.id.button_insert_name);
        final EditText text_name = findViewById(R.id.text_name);
        text_address.setEnabled(false);
        text_address.setInputType(InputType.TYPE_NULL);
        button_sync_all.setOnClickListener(v -> new RealmSyncTask(text_address.getText().toString(), this).execute(getResources().getStringArray(R.array.pref_list_table)));
        button_notifications_setting.setOnClickListener(v -> {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } );
        switch_bot.setChecked(NotificationListener.getStatus().equals(AppConstant.SERVICE_STATUS_START));
        switch_bot.setOnCheckedChangeListener((view,isChecked)->{

            Intent intent = new Intent(this, NotificationListener.class);
            if(isChecked) {
                if(!isMyServiceRunning()) {
                    Snackbar.make(view,"알림 읽기 권한 설정 하세요",Snackbar.LENGTH_SHORT).show();
                    switch_bot.setChecked(false);
                } else {
                    intent.putExtra(AppConstant.INTENT_KEY_SERVICE_STATUS, AppConstant.SERVICE_STATUS_START);
                    Snackbar.make(view,"냥봇 시작",Snackbar.LENGTH_SHORT).show();
                    startService(intent);
                }
            } else {
                intent.putExtra(AppConstant.INTENT_KEY_SERVICE_STATUS,AppConstant.SERVICE_STATUS_STOP);
                Snackbar.make(view,"냥봇 중지",Snackbar.LENGTH_SHORT).show();
                startService(intent);
            }
        });

        String curName = NotificationListener.getName();
        text_name.setText(curName);

        button_insert_name.setOnClickListener(view-> {
            String name = text_name.getText().toString();
            name = name.trim();
            if(name.length() > 1 ) {
                Intent intent = new Intent(this, NotificationListener.class);
                intent.putExtra(AppConstant.INTENT_KEY_SERVICE_NAME, name);
                Snackbar.make(view,"냥봇 이름 설정됨 : " + name, Snackbar.LENGTH_SHORT).show();
                startService(intent);
            } else {
                Snackbar.make(view,"이름을 2글자 이상 입력 하세요.", Snackbar.LENGTH_SHORT).show();
            }
            ScreenUtils.hideSoftKeyboard(this);
        });


    }



    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
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