package com.fang.starfang;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.local.task.LocalDataHandlerCat;
import com.fang.starfang.local.task.LocalDataHandlerDog;

// 카톡 알림을 읽어오는 리스너

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "FANG";
    private static final String PACKAGE_KAKAO = "com.kakao.talk";
    private static final String PACKAGE_DISCORD = "com.discord";
    private static final String COMMAND_CAT = "냥";
    private static final String COMMAND_DOG = "멍";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Notification Listener created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Notification Listener destroyed!");
    }

    //@TargetApi(Build.VERSION_CODES.P)
    public void addNotification(StatusBarNotification sbn, boolean updateDash)  {
       // Log.i(TAG, sbn.getPackageName() + ">> " + sbn.getNotification().extras.toString() );


            Notification mNotification = sbn.getNotification();
            Bundle extras = mNotification.extras;

        String from = null;
        String text = null;
        String room = null;

        if( sbn.getPackageName().equalsIgnoreCase(PACKAGE_KAKAO )) {


            from = extras.getString(Notification.EXTRA_TITLE);
            text = extras.getString(Notification.EXTRA_TEXT);
            room = extras.getString(Notification.EXTRA_SUB_TEXT);

        } else if( sbn.getPackageName().equalsIgnoreCase(PACKAGE_DISCORD ) ) {

            text = extras.getCharSequence(Notification.EXTRA_TEXT)+"";

            from = extras.getCharSequence(Notification.EXTRA_TITLE)+"";

            room = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)+"";

        }

            Log.i(TAG, sbn.getPackageName() + ">> from: " + from + ", text: " + text + ", room: " + room);

            try {

                if (text.substring(text.length() - 1).equals(COMMAND_CAT) && text.length() > 2) {
                    new LocalDataHandlerCat(this, from, room, sbn).execute(text);
                } else if (text.substring(text.length() - 1).equals(COMMAND_DOG) && text.length() > 2) {
                    new LocalDataHandlerDog(this, from, room, sbn).execute(text);
                }
            } catch (NullPointerException ignore) {
            }

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn!=null) {
            String sbnPackage = sbn.getPackageName();
            String sbnTag = sbn.getTag();
            //Log.d(TAG, sbnPackage + " Notification [" + sbn.getKey() + "] Posted:\n");
            if ( ( sbnTag != null &&  sbnPackage.equalsIgnoreCase(PACKAGE_KAKAO) ) || sbnPackage.equalsIgnoreCase(PACKAGE_DISCORD) )
                addNotification(sbn, true);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.d(TAG, "Notification [" + sbn.getKey() + "] Removed:\n");
    }






}