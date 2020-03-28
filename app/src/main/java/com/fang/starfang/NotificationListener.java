package com.fang.starfang;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.task.PrefixHandler;
import com.fang.starfang.ui.main.MainActivity;
import com.fang.starfang.util.VersionUtils;

import io.realm.Realm;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "FANG_SERVICE_NOTIFY";
    public static final String PACKAGE_KAKAO = "com.kakao.talk";
    public static final String PACKAGE_DISCORD = "com.discord";
    public static final String PACKAGE_STARFANG = "com.fang.starfang";
    private static final String COMMAND_CAT = "냥";
    private static final String COMMAND_DOG = "멍";
    private static String record = AppConstant.RECORD_STATUS_STOP;
    private static String status = AppConstant.SERVICE_STATUS_STOP;
    private static String name = "";


    public static String getCommandCat() {
        return COMMAND_CAT;
    }

    public static String getCommandDog() {
        return COMMAND_DOG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        super.stopSelf();
        //Log.d(TAG, "Notification Listener created");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "Notification Listener destroyed");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if( bundle != null ) {
            String getStatus = (String)intent.getExtras().get(AppConstant.INTENT_KEY_SERVICE_STATUS);
            if( getStatus != null ) {
                status = getStatus;
                Log.d(TAG, "status changed:" + status);
                if(status.equals(AppConstant.SERVICE_STATUS_START)) {
                    super.onStartCommand(intent, flags, startId);
                    startForeground();
                } else {
                    stopForeground(true);
                    super.stopSelf();
                }
            } else {
                String getName = (String)intent.getExtras().get(AppConstant.INTENT_KEY_SERVICE_NAME);
                if( getName != null ) {
                    name = getName;
                    Log.d(TAG, "name changed:" + name);
                }
            }
        }
        return START_STICKY;
    }


    public void addNotification(StatusBarNotification sbn)  {
        //Log.d(TAG, "addNotification activated");

            Notification mNotification = sbn.getNotification();
            Bundle extras = mNotification.extras;

        String from = null;
        String text = null;
        String room = null;
        String packageName = sbn.getPackageName();

        boolean isAvailablePackage = false;

        //Log.d(TAG, "before package name check");
        if( packageName.equalsIgnoreCase(PACKAGE_KAKAO )) {


            from = extras.getString(Notification.EXTRA_TITLE);
            text = extras.getString(Notification.EXTRA_TEXT);
            room = extras.getString(Notification.EXTRA_SUB_TEXT);
            isAvailablePackage = true;


        } else if( packageName.equalsIgnoreCase(PACKAGE_DISCORD ) ) {

            text = extras.getCharSequence(Notification.EXTRA_TEXT)+"";
            from = extras.getCharSequence(Notification.EXTRA_TITLE)+"";
            room = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)+"";
            //isAvailablePackage = true;
        }

        if( isAvailablePackage ) {
            if( record.equals(AppConstant.RECORD_STATUS_START) ) {
                Conversation conversation = new Conversation(from, room, sbn.getTag(), packageName, text);
                Realm realm = Realm.getDefaultInstance();
                if (realm.isInTransaction()) {
                    realm.commitTransaction();
                }
                realm.beginTransaction();
                realm.copyToRealm(conversation);
                realm.commitTransaction();
                realm.close();
            }
            new PrefixHandler(this,from,room,sbn,false, name ).execute(text);
            //Log.d(TAG, sbn.getPackageName() + ">> from: " + from + ", text: " + text + ", room: " + room);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if(status.equals(AppConstant.SERVICE_STATUS_STOP))
            return;
        if (sbn!=null) {
            String sbnPackage = sbn.getPackageName();
            String sbnTag = sbn.getTag();
            //Log.d(TAG, sbnPackage + " Notification [" + sbn.getKey() + "] Posted:\n");
            if ( ( sbnTag != null &&  sbnPackage.equalsIgnoreCase(PACKAGE_KAKAO) ) || sbnPackage.equalsIgnoreCase(PACKAGE_DISCORD) ) {
                addNotification(sbn);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.d(TAG, "Notification [" + sbn.getKey() + "] Removed:\n");
    }

    public static String getStatus() {
        return status;
    }

    public static String getName() {
        return name;
    }


    private void startForeground() {
        String channelId = "";
        if( VersionUtils.isOreo() ) {
            channelId = createNotificationChannel(AppConstant.FORE_NOTIFY_CHANNEL_ID, AppConstant.FORE_NOTIFY_CHANNEL_NAME);
        }

        Intent notificationIntent = new Intent(NotificationListener.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationListener.this, 0,
                notificationIntent, 0);

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