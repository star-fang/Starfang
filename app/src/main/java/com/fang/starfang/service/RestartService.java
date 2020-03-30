package com.fang.starfang.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.fang.starfang.R;
import com.fang.starfang.ui.main.MainActivity;
import com.fang.starfang.util.VersionUtils;

public class RestartService extends Service {

    private static final String TAG = "FANG_RESTART";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId ) {
        startForegroundService("rsChannel","RestartServiceChannel");

        Intent startServiceIntent = new Intent( RestartService.this, FangcatService.class );
        Resources resources = getResources();
        startServiceIntent.putExtra(resources.getString(R.string.bot_status_change),
                resources.getString(R.string.bot_status_start));
        startService(startServiceIntent);

        stopForeground(true);
        stopSelf();
        return START_STICKY;
    }

    private void startForegroundService(String id, String name) {

        Intent notificationIntent = new Intent(RestartService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(RestartService.this, 0,
                notificationIntent, 0);

        String channelId = "";
        if( VersionUtils.isOreo() ) {
            channelId = createNotificationChannel(id, name);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId );
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle("ANDROID")
                .setContentText(VersionUtils.currentVersion())
                .setContentIntent(pendingIntent)
                .build();
        startForeground(101, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor( Color.BLUE );
        channel.setLockscreenVisibility( Notification.VISIBILITY_PRIVATE );
        NotificationManager service = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(channel);
        return channelId;
    }
}
