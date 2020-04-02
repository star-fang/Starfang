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
import android.text.TextUtils;
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
        isWorking = false;
        Resources resources = getResources();
        SharedPreferences sharedPref = getSharedPreferences(
                resources.getString(R.string.shared_preference_store),
                Context.MODE_PRIVATE);
        String start_count_key = resources.getString(R.string.start_count);
        int startCount = sharedPref.getInt(start_count_key, 0) + 1;
        Log.d(TAG, "NotificationListenerService: " + startCount + "th(st|nd|rd) created");
        sharedPref.edit().putInt(start_count_key, startCount).apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Notification Listener receive start command");
        Bundle bundle = intent.getExtras();
        Resources resources = getResources();
        if (bundle != null) {
            String command = bundle.getString(
                    resources.getString(R.string.bot_status),
                    resources.getString(R.string.bot_status_start)
            );
            if (command.equals(resources.getString(R.string.bot_status_stop))) {
                Log.d(TAG, "service stop");
                isWorking = false;
                stopSelf();
            } else {
                isWorking = true;
                if (command.equals(resources.getString(R.string.bot_status_restart))) {
                    SharedPreferences sharedPref = getSharedPreferences(
                            resources.getString(R.string.shared_preference_store),
                            Context.MODE_PRIVATE);
                    String restart_count_key = resources.getString(R.string.restart_count);
                    int restartCount = sharedPref.getInt(
                            restart_count_key, 0) + 1;
                    Log.d(TAG, "service restart: " + restartCount + "th(st|nd|rd) restart command");
                    sharedPref.edit().putInt(restart_count_key, restartCount).apply();
                } else {
                    Log.d(TAG, "service start");
                }
                return super.onStartCommand(intent, flags, startId);
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isWorking) {
            Log.d(TAG, "Notification Listener destroyed abnormally");
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.SECOND, 3);
            Intent intent = new Intent(FangcatService.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(FangcatService.this, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            }
        } else {
            Resources resources = getResources();
            SharedPreferences sharedPref = getSharedPreferences(
                    resources.getString(R.string.shared_preference_store)
                    , Context.MODE_PRIVATE
            );
            sharedPref.edit().putString(
                    resources.getString(R.string.bot_status),
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

    public void addNotification(StatusBarNotification sbn
            , boolean isLocalRequest) {

        Notification mNotification = sbn.getNotification();
        Bundle extras = mNotification.extras;

        String text;
        CharSequence textChars = extras.getCharSequence(Notification.EXTRA_TEXT);
        if (!TextUtils.isEmpty(textChars)) {
            text = textChars.toString();
        } else if (!TextUtils.isEmpty((textChars = extras.getString(Notification.EXTRA_SUMMARY_TEXT)))) {
            text = textChars.toString();
        } else {
            text = null;
        }

        String from;
        CharSequence fromChars = extras.getCharSequence(Notification.EXTRA_TITLE);
        if (!TextUtils.isEmpty(fromChars)) {
            from = fromChars.toString();
        } else {
            from = null;
        }

        String room;
        CharSequence roomChars = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
        if (!TextUtils.isEmpty(roomChars)) {
            room = roomChars.toString();
        } else {
            room = null;
        }

        Resources resources = getResources();
        SharedPreferences sharedPref = getSharedPreferences(
                resources.getString(R.string.shared_preference_store),
                Context.MODE_PRIVATE);
        String botRecord = sharedPref.getString(
                resources.getString(R.string.bot_record),
                resources.getString(R.string.bot_status_stop)); // default : stop record

        boolean record = botRecord.equals(resources.getString(R.string.bot_status_start));

        String botName = sharedPref.getString(
                resources.getString(R.string.bot_name),
                resources.getString(R.string.bot_name_default)
        );
        new PrefixHandler(this, from, room, sbn, isLocalRequest, botName, record).execute(text);
        //Log.d(TAG, sbn.getPackageName() + ">> from: " + from + ", text: " + text + ", room: " + room);

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (sbn != null && isWorking) {
            //Log.d(TAG, sbnPackage + " posted!");
            boolean isAvailablePackage = false;
            boolean isLocalRequest = false;
            String packageName = sbn.getPackageName();

            switch (packageName.toLowerCase()) {
                case FangConstant.PACKAGE_STARFANG:
                    isLocalRequest = true;
                    isAvailablePackage = true;
                    break;
                case FangConstant.PACKAGE_KAKAO:
                    isAvailablePackage = sbn.getTag() != null;
                    break;
                case FangConstant.PACKAGE_DISCORD:
                    //isAvailablePackage = true;
                    break;
                default:
            }

            if (isAvailablePackage) {
                addNotification(sbn, isLocalRequest);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.d(TAG, "Notification [" + sbn.getKey() + "] Removed:\n");
    }

}