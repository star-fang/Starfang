package com.fang.starfang;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.task.LocalDataHandlerCat;
import com.fang.starfang.local.task.LocalDataHandlerDog;

import java.util.Objects;

import io.realm.Realm;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "FANG_LISTENER";
    public static final String PACKAGE_KAKAO = "com.kakao.talk";
    public static final String PACKAGE_DISCORD = "com.discord";
    private static String COMMAND_CAT = "냥";
    private static String COMMAND_DOG = "멍";
    private static String status = "stop";

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
        status = (String) Objects.requireNonNull(intent.getExtras()).get("status");
        Log.d(TAG, "status changed:" + status);
        if(status.equals("start")) {
            super.onStartCommand(intent, flags, startId);
        } else {
            super.stopSelf();
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
            try {
                if (text != null) {
                    if (text.substring(text.length() - 1).equals(COMMAND_CAT) && text.length() > 2) {
                        new LocalDataHandlerCat(this, from, room, sbn, false).execute(text);
                    } else if (text.substring(text.length() - 1).equals(COMMAND_DOG) && text.length() > 2) {
                        new LocalDataHandlerDog(this, from, sbn, false).execute(text);
                    }
                }
            } catch (NullPointerException ignore) {
            }

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





}