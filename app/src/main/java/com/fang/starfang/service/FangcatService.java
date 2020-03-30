package com.fang.starfang.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.task.PrefixHandler;

import java.util.Calendar;

public class FangcatService extends NotificationListenerService {

    private static final String TAG = "FANG_SERVICE_NOTIFY";

    private boolean isWorking;

    @Override
    public void onCreate() {
        super.onCreate();
        super.stopSelf();
        isWorking = false;
        Log.d(TAG, "Notification Listener created");
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId) {
        Log.d(TAG, "Notification Listener receive start command");
        Bundle bundle = intent.getExtras();
        Resources resources = getResources();
        isWorking = true;
        if( bundle != null ) {
            String command = bundle.getString(
                    resources.getString(R.string.bot_status_change),
                    resources.getString(R.string.bot_status_start)
            );
            if( command.equals(resources.getString(R.string.bot_status_stop))) {
                isWorking = false;
                stopSelf();
                Log.d(TAG, "Notification Listener stop itself");
            } else {
                Log.d(TAG, "Notification Listener started");
               return super.onStartCommand(intent, flags, startId);
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if( isWorking ) {
            Log.d(TAG, "Notification Listener destroyed abnormally");
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.SECOND, 1);
            Intent intent = new Intent(FangcatService.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(FangcatService.this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        } else {
            Resources resources = getResources();
            SharedPreferences sharedPref = getSharedPreferences(
                    resources.getString(R.string.shared_preference_store_name)
                    , Context.MODE_PRIVATE
            );
            sharedPref.edit().putString(
                    resources.getString(R.string.shared_preference_key_botStatus),
                    resources.getString(R.string.bot_status_stop)
            ).apply();
            Log.d(TAG, "Notification Listener destroyed ");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Notification Listener bind");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Notification Listener un-bind");
        return true; // return true : make possible to rebind
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "Notification Listener re-bind");
        super.onRebind(intent);
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
        boolean isLocalRequest = false;

        switch ( packageName.toLowerCase() ) {
            case FangConstant.PACKAGE_STARFANG:
                isLocalRequest = true;
            case FangConstant.PACKAGE_KAKAO:
                from = extras.getString(Notification.EXTRA_TITLE);
                text = extras.getString(Notification.EXTRA_TEXT);
                room = extras.getString(Notification.EXTRA_SUB_TEXT);
                isAvailablePackage = true;
                break;
            case FangConstant.PACKAGE_DISCORD:
                text = extras.getCharSequence(Notification.EXTRA_TEXT)+"";
                from = extras.getCharSequence(Notification.EXTRA_TITLE)+"";
                room = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)+"";
                //isAvailablePackage = true;
                break;
                default:
        }

        if( isAvailablePackage ) {

            Resources resources = getResources();
            SharedPreferences sharedPref = getSharedPreferences(
                    resources.getString(R.string.shared_preference_store_name),
                    Context.MODE_PRIVATE);
            String botRecord = sharedPref.getString(
                    resources.getString(R.string.shared_preference_key_botRecord), //
                    resources.getString(R.string.bot_status_stop) ); // default : stop record

            boolean record = ( botRecord != null && botRecord.equals(resources.getString(R.string.bot_status_start)) );

            String botName = sharedPref.getString(
                    resources.getString(R.string.shared_preference_key_botName),
                    resources.getString(R.string.bot_name_default)
            );
            new PrefixHandler(this, packageName,from,room,sbn,isLocalRequest, botName, record  ).execute(text);
            //Log.d(TAG, sbn.getPackageName() + ">> from: " + from + ", text: " + text + ", room: " + room);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if ( sbn!=null && isWorking ) {
            String sbnPackage = sbn.getPackageName();
            String sbnTag = sbn.getTag();
            //Log.d(TAG, sbnPackage + " posted!");

            if ( ( sbnTag != null &&  sbnPackage.equalsIgnoreCase(FangConstant.PACKAGE_KAKAO) ) ||
                    sbnPackage.equalsIgnoreCase(FangConstant.PACKAGE_DISCORD) ||
                    sbnPackage.equalsIgnoreCase(FangConstant.PACKAGE_STARFANG)
            ) {
                addNotification(sbn);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.d(TAG, "Notification [" + sbn.getKey() + "] Removed:\n");
    }

}