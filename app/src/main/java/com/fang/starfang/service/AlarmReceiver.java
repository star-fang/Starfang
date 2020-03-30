package com.fang.starfang.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.fang.starfang.R;
import com.fang.starfang.util.VersionUtils;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "FANG_ALARM";
    @Override
    public void onReceive(Context context, Intent intent) {

        Resources resources = context.getResources();
        SharedPreferences sharedPref = context.getSharedPreferences(
                resources.getString(R.string.shared_preference_store_name),
                Context.MODE_PRIVATE);
        String count_key = resources.getString(R.string.shared_preference_key_restart_count);
        int restartCount = sharedPref.getInt(
                count_key, 0) + 1;
        Log.d(TAG,"restart service: " + restartCount );
        sharedPref.edit().putInt(count_key,restartCount).apply();

        if(VersionUtils.isOreo() ) {
            Intent startForeIntent = new Intent(context, RestartService.class);
            context.startForegroundService(startForeIntent);
        } else {
            Intent startBackIntent = new Intent(context, FangcatService.class);
            startBackIntent.putExtra(resources.getString(R.string.bot_status_change),
                    resources.getString(R.string.bot_status_start));
            context.startService(startBackIntent);
        }
    }
}
