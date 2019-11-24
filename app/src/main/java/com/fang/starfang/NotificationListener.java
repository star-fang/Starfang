package com.fang.starfang;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.task.PrefixHandler;

import io.realm.Realm;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "FANG_SERVICE_NOTIFY";
    public static final String PACKAGE_KAKAO = "com.kakao.talk";
    public static final String PACKAGE_DISCORD = "com.discord";
    public static final String PACKAGE_STARFANG = "com.fang.starfang";
    private static final String COMMAND_CAT = "냥";
    private static final String COMMAND_DOG = "멍";
    private static String status = "stop";
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
            String getStatus = (String)intent.getExtras().get("status");
            if( getStatus != null ) {
                status = getStatus;
                Log.d(TAG, "status changed:" + status);
                if(status.equals("start")) {
                    super.onStartCommand(intent, flags, startId);
                } else {
                    super.stopSelf();
                }
            } else {
                String getName = (String)intent.getExtras().get("name");
                if( getName != null ) {
                    name = getName;
                    Log.d(TAG, "name changed:" + name);
                }
            }
        }
        return START_NOT_STICKY;
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
            isAvailablePackage = true;

        }

        if( isAvailablePackage ) {
            Conversation conversation = new Conversation(from,room,sbn.getTag(),packageName,text);
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(conversation);
            realm.commitTransaction();
            realm.close();
        }

            //Log.d(TAG, sbn.getPackageName() + ">> from: " + from + ", text: " + text + ", room: " + room);
        new PrefixHandler(this,from,room,sbn,false, name ).execute(text);

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if(status.equals("stop"))
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
}