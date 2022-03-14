package com.fang.starfang.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.task.FangcatHandler;

import java.util.Calendar;

public class FangcatService extends NotificationListenerService {

    private static final String TAG = "FANG_SERVICE_BG";

    private boolean isWorking;
    private boolean isBound;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = getSharedPreferences(
                FangConstant.SHARED_PREF_STORE,
                Context.MODE_PRIVATE);
        String start_count_key = FangConstant.BOT_START_COUNT_KEY;
        int startCount = sharedPref.getInt(start_count_key, 0) + 1;
        Log.d(TAG, "NotificationListenerService: " + startCount + "th(st|nd|rd) created");
        sharedPref.edit().putInt(start_count_key, startCount).apply();

        isWorking = false;
        isBound = false;
        super.stopSelf();
    }

    /*
    isWorking
    onCreate false
    onBind
    onStartCommand>start true
    onStartCommand>stop false
    onUnbind false
    onRebind true


    onDestroy >> false: normal // true: abnormal

    create>bind>start>stop>unbind>destroy
    create>bind>unbind>destroy
    create>bind>start>unbind>...>destroy
    create>bind>start>unbind>start>...>destroy>...
    create>bind>start>unbind>rebind>
    create>start>...>destroy>create>....

     */
    @Override
    public void onListenerConnected(){
        super.onListenerConnected();
        Log.d(TAG,"Listener connected");
    }

    @Override
    public void onListenerDisconnected(){
        super.onListenerDisconnected();
        Log.d(TAG,"Listener disconnected");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "receive startCommand : [flags,startId] = ["
                + flags + "," + startId+"]");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int command = bundle.getInt(
                    FangConstant.BOT_STATUS_KEY,
                    FangConstant.BOT_STATUS_START
            );
            if ( command == FangConstant.BOT_STATUS_STOP ) {
                /*
                *state and works
                 : activated + bound >> notification listener service works
                 : deactivated + bound >> it still works
                 : activated + unbound >> non-working service will be destroyed soon
                 : deactivated + unbound >> service destroyed immediately

                 *[activated] becomes
                          : true by onCreate(default)
                          : true by onStartCommand by startService
                          : false by stopSelf by onStartCommand or stopService
                 *startService, stopService
                          : activity or  foregroundService call
                 *startService by backgroundService
                          : forbidden since Oreo
                 *stopSelf << service call
                 */
                if (isWorking = !this.stopSelfResult(startId)) {
                    Log.d(TAG, "fail to stop service");
                    // todo: make callback to gui switch on
                } else {
                    Log.d(TAG, "switch off service");
                }

            } else { // start or restart
                if (command == FangConstant.BOT_STATUS_RESTART ) { // restart
                    String restart_count_key = FangConstant.BOT_RESTART_COUNT_KEY;
                    int restartCount = sharedPref.getInt(
                            restart_count_key, 0) + 1;
                    Log.d(TAG, "service restart: " + restartCount + "th(st|nd|rd) restart command");
                    sharedPref.edit().putInt(restart_count_key, restartCount).apply();
                    isWorking = true;
                } else if( isBound && !isWorking ) {
                    isWorking = true;
                    Log.d(TAG, "service activated");
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
            sharedPref.edit().putInt(
                    FangConstant.BOT_STATUS_KEY,
                    FangConstant.BOT_STATUS_STOP
            ).apply();

            Log.d(TAG, "Notification Listener destroyed ");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Notification Listener bind");
        isBound = true;
        if( sharedPref.getInt(FangConstant.BOT_STATUS_KEY, FangConstant.BOT_STATUS_STOP) == FangConstant.BOT_STATUS_START ) {
            isWorking = true;
            Log.d(TAG, "service already activated");
        }
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Notification Listener un-bind");
        isWorking = false;
        isBound = false;
        return true; // return true : make possible to rebind
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "Notification Listener re-bind");
        isWorking = true;
        isBound = true;
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

        int botRecord = sharedPref.getInt(
                FangConstant.BOT_RECORD_KEY,
                FangConstant.BOT_STATUS_STOP ); // default : stop record

        boolean record = botRecord == FangConstant.BOT_STATUS_START;

        String botName = sharedPref.getString(
                FangConstant.BOT_NAME_KEY,
                getResources().getString(R.string.bot_name_default)
        );
        new FangcatHandler(this, from, room, sbn, isLocalRequest, botName, record).execute(text);
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