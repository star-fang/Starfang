package com.fang.starfang.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fang.starfang.FangConstant;
import com.fang.starfang.util.VersionUtils;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "FANG_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"restart receiver activated");
        if(VersionUtils.isOreo() ) {
            Intent startForeIntent = new Intent(context, RestartService.class);
            context.startForegroundService(startForeIntent);
        } else {
            Intent startBackIntent = new Intent(context, FangcatService.class);
            startBackIntent.putExtra(
                    FangConstant.BOT_STATUS_KEY,
                    FangConstant.BOT_STATUS_RESTART);
            context.startService(startBackIntent);
        }
    }
}
