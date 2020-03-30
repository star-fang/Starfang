package com.fang.starfang.ui.setting;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.fang.starfang.R;
import com.fang.starfang.local.task.RealmSyncTask;
import com.fang.starfang.service.FangcatService;
import com.fang.starfang.util.ScreenUtils;
import com.fang.starfang.util.VersionUtils;
import com.google.android.material.snackbar.Snackbar;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "FANG_ACT_SET";

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

        final Resources resources = getResources();
        SharedPreferences sharedPref = getSharedPreferences(
                resources.getString(R.string.shared_preference_store_name),
                Context.MODE_PRIVATE);

        ActionBar actionBar = getActionBar();
        if( actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


        /*냥봇 설정 파트*/
        final Switch switch_record = findViewById(R.id.record_start);
        final Switch switch_bot = findViewById(R.id.notifications_start);
        final EditText text_address = findViewById(R.id.text_address);
        final Button button_sync_all = findViewById(R.id.start_sync_key_all);
        final Button button_notifications_setting= findViewById(R.id.notifications_setting);
        final Button button_insert_name = findViewById(R.id.button_insert_name);
        final EditText text_name = findViewById(R.id.text_name);
        text_address.setEnabled(false);
        text_address.setInputType(InputType.TYPE_NULL);
        button_sync_all.setOnClickListener(v -> new RealmSyncTask(text_address.getText().toString(), this).execute(
                resources.getStringArray(R.array.pref_list_table)));
        button_notifications_setting.setOnClickListener(v -> {

            String actionString;
            if(VersionUtils.isMarshmallow() ) {
                actionString = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
            } else {
                actionString =  "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
            }
            Intent intent = new Intent(actionString);
            startActivity(intent);
        } );


        String recordStatus = sharedPref.getString(
                resources.getString(R.string.shared_preference_key_botRecord),
                resources.getString(R.string.bot_status_stop) );
        if( recordStatus != null ) {
            Log.d(TAG, "record status : " + recordStatus );
            switch_record.setChecked(recordStatus.equals(resources.getString(R.string.bot_status_start)));
        }

        switch_record.setOnCheckedChangeListener((view, isChecked)-> {
            if(isChecked) {
                sharedPref.edit().putString(
                        resources.getString(R.string.shared_preference_key_botRecord),
                        resources.getString(R.string.bot_status_start)
                ).apply();
                Snackbar.make(view,"대화 녹화 시작",Snackbar.LENGTH_SHORT).show();
            } else {
                sharedPref.edit().putString(
                        resources.getString(R.string.shared_preference_key_botRecord),
                        resources.getString(R.string.bot_status_stop)
                ).apply();
                Snackbar.make(view,"대화 녹화 정지",Snackbar.LENGTH_SHORT).show();
            }
        });

        String botStatus = sharedPref.getString(
                resources.getString(R.string.shared_preference_key_botStatus),
                resources.getString(R.string.bot_status_stop) );
        if( botStatus != null ) {
            Log.d(TAG, "bot status : " + botStatus );
            switch_bot.setChecked(botStatus.equals(resources.getString(R.string.bot_status_start)));
        }

        switch_bot.setOnCheckedChangeListener((view,isChecked)->{

            Intent intent = new Intent(this, FangcatService.class);
            if(isChecked) {
                if(!isServiceRunning(this, FangcatService.class,false)) {
                    Snackbar.make(view,"알림 읽기 권한 설정 하세요",Snackbar.LENGTH_SHORT).show();
                    switch_bot.setChecked(false);
                } else {
                    Snackbar.make(view,"냥봇 시작",Snackbar.LENGTH_SHORT).show();
                    intent.putExtra(resources.getString(R.string.bot_status_change),
                            resources.getString(R.string.bot_status_start));
                    startService( intent );
                    sharedPref.edit().putString(
                            resources.getString(R.string.shared_preference_key_botStatus),
                            resources.getString(R.string.bot_status_start)
                    ).apply();
                }
            } else {
                Snackbar.make(view,"냥봇 정지",Snackbar.LENGTH_SHORT).show();
                intent.putExtra(resources.getString(R.string.bot_status_change),
                        resources.getString(R.string.bot_status_stop));
                startService( intent );
                sharedPref.edit().putString(
                        resources.getString(R.string.shared_preference_key_botStatus),
                        resources.getString(R.string.bot_status_stop)
                ).apply();
            }
        });


        text_name.setText( sharedPref.getString(
                resources.getString(R.string.shared_preference_key_botName),
                resources.getString(R.string.bot_name_default)) );

        button_insert_name.setOnClickListener(view-> {
                String name = text_name.getText().toString();
                name = name.trim();
                if (name.length() > 1) {
                    sharedPref.edit().putString("botName", name).apply();
                    Snackbar.make(view, "냥봇 이름 설정됨 : " + name, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, "이름을 2글자 이상 입력 하세요.", Snackbar.LENGTH_SHORT).show();
                }
                ScreenUtils.hideSoftKeyboard(this);
        });


    }


    public static boolean isServiceRunning(Context context, Class<?> serviceClass, boolean checkForeground) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if( checkForeground ) {
                    if( service.foreground ) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

}