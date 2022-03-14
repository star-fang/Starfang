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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.task.RealmSyncTask;
import com.fang.starfang.service.FangcatService;
import com.fang.starfang.util.ScreenUtils;
import com.fang.starfang.util.VersionUtils;
import com.google.android.material.snackbar.Snackbar;

public class SettingActivity extends AppCompatActivity implements RealmSyncTask.OnTaskCompleted {

    private static final String TAG = "FANG_ACT_SET";

    public static final int RESULT_CODE_SYNC_ZERO = 0;
    public static final int RESULT_CODE_SYNC_SUCCESS = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "_ON CREATE");
        setContentView(R.layout.activity_setting);

        final Resources resources = getResources();
        SharedPreferences sharedPref = getSharedPreferences(
                FangConstant.SHARED_PREF_STORE,
                Context.MODE_PRIVATE);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


        /*냥봇 설정 파트*/
        final Switch switch_record = findViewById(R.id.record_start);
        final Switch switch_bot = findViewById(R.id.notifications_start);
        final EditText text_address = findViewById(R.id.text_address);
        final Button button_sync_all = findViewById(R.id.start_sync_key_all);
        final Button button_notifications_setting = findViewById(R.id.notifications_setting);
        final Button button_insert_name = findViewById(R.id.button_insert_name);
        final EditText text_name = findViewById(R.id.text_name);
        text_address.setEnabled(false);
        text_address.setInputType(InputType.TYPE_NULL);
        button_sync_all.setOnClickListener(v ->
                new RealmSyncTask(text_address.getText().toString(),
                        SettingActivity.this, // context : application context vs activity context
                        this).execute(
                        resources.getStringArray(R.array.pref_list_table)));
        button_notifications_setting.setOnClickListener(v -> {

            String actionString;
            if (VersionUtils.isMarshmallow()) {
                actionString = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
            } else {
                actionString = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
            }
            Intent intent = new Intent(actionString);
            startActivity(intent);
        });


        int recordStatus = sharedPref.getInt(
                FangConstant.BOT_RECORD_KEY,
                FangConstant.BOT_STATUS_STOP);
        Log.d(TAG, "record status : " + recordStatus);
        switch_record.setChecked(recordStatus == FangConstant.BOT_STATUS_START);

        switch_record.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                sharedPref.edit().putInt(
                        FangConstant.BOT_RECORD_KEY,
                        FangConstant.BOT_STATUS_START
                ).apply();
                Snackbar.make(view, "대화 녹화 시작", Snackbar.LENGTH_SHORT).show();
            } else {
                sharedPref.edit().putInt(
                        FangConstant.BOT_RECORD_KEY,
                        FangConstant.BOT_STATUS_STOP
                ).apply();
                Snackbar.make(view, "대화 녹화 정지", Snackbar.LENGTH_SHORT).show();
            }
        });

        switch_bot.setChecked(false);
        switch_bot.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                if (!isServiceExist(this, FangcatService.class, false)) {
                    Snackbar.make(view, "알림 읽기 권한 설정 하세요", Snackbar.LENGTH_SHORT).show();
                    switch_bot.setChecked(false);
                } else {
                    Intent intent = new Intent(this, FangcatService.class);
                    Snackbar.make(view, "냥봇 시작", Snackbar.LENGTH_SHORT).show();
                    intent.putExtra(
                            FangConstant.BOT_STATUS_KEY,
                            FangConstant.BOT_STATUS_START);
                    startService(intent);
                    sharedPref.edit().putInt(
                            FangConstant.BOT_STATUS_KEY,
                            FangConstant.BOT_STATUS_START
                    ).apply();

                }
            } else {
                Snackbar.make(view, "냥봇 정지", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FangcatService.class);
                intent.putExtra(
                        FangConstant.BOT_STATUS_KEY,
                        FangConstant.BOT_STATUS_STOP);
                startService(intent);
                sharedPref.edit().putInt(
                        FangConstant.BOT_STATUS_KEY,
                        FangConstant.BOT_STATUS_STOP
                ).apply();

            }
        });
        int botStatus = sharedPref.getInt(
                FangConstant.BOT_STATUS_KEY,
                FangConstant.BOT_STATUS_STOP);
        Log.d(TAG, "sharedPref botStatus:" + botStatus);

        if (botStatus == FangConstant.BOT_STATUS_START) {
            if (isServiceExist(this, FangcatService.class, false)) {
                Log.d(TAG, "service already bound: start service");
                switch_bot.setChecked(true);
            } else {
                Log.d(TAG, "service not exist: rewrite sharedPref: status stop");
                sharedPref.edit().putInt(
                        FangConstant.BOT_STATUS_KEY,
                        FangConstant.BOT_STATUS_STOP
                ).apply();
            }
        }


        text_name.setText(sharedPref.getString(
                FangConstant.BOT_NAME_KEY,
                resources.getString(R.string.bot_name_default)));

        button_insert_name.setOnClickListener(view -> {
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


    public static boolean isServiceExist(Context context, Class<?> serviceClass, boolean checkForeground) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    if (checkForeground) {
                        if (service.foreground) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onTaskCompleted(int updateCount) {
        if (updateCount == 0) {
            setResult(RESULT_CODE_SYNC_ZERO);
        } else {
            setResult(RESULT_CODE_SYNC_SUCCESS);
        }
    }
}