package com.fang.starfang.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.fang.starfang.R;
import com.fang.starfang.util.VersionUtils;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "FANG_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"restart receiver activated");
        Resources resources = context.getResources();
        if(VersionUtils.isOreo() ) {
            Intent startForeIntent = new Intent(context, RestartService.class);
            context.startForegroundService(startForeIntent);
        } else {
            Intent startBackIntent = new Intent(context, FangcatService.class);
            startBackIntent.putExtra(resources.getString(R.string.bot_status),
                    resources.getString(R.string.bot_status_restart));
            context.startService(startBackIntent);
        }
    }
}
