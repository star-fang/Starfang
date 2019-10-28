package com.fang.starfang.util;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.fang.starfang.util.model.io.Action;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class NotificationUtils {

    private static final String[] REPLY_KEYWORDS = {"reply", "android.intent.extra.text"};
    private static final CharSequence REPLY_KEYWORD = "reply";
    private static final CharSequence INPUT_KEYWORD = "input";
    private static final String TAG = "NOTIFICATION";


    public static boolean isRecent(StatusBarNotification sbn, long recentTimeframeInSecs) {
        return sbn.getNotification().when > 0 &&  //Checks against real time to make sure its new
                System.currentTimeMillis() - sbn.getNotification().when <= TimeUnit.SECONDS.toMillis(recentTimeframeInSecs);
    }

    /**
     * http://stackoverflow.com/questions/9292032/extract-notification-text-from-parcelable-contentview-or-contentintent *
     */

    public static boolean notificationMatchesFilter(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
        if (rankingMap.getRanking(sbn.getKey(), ranking))
            if (ranking.matchesInterruptionFilter())
                return true;
        return false;
    }

    public static String getMessage(Bundle extras) {
        Log.d(TAG, "Getting message from extras..");
        Log.d(TAG, "" + extras.getCharSequence(Notification.EXTRA_TEXT));
        Log.d(TAG, "" + extras.getCharSequence(Notification.EXTRA_SUB_TEXT));
        CharSequence chars = extras.getCharSequence(Notification.EXTRA_TEXT);
        if(!TextUtils.isEmpty(chars))
            return chars.toString();
        else if(!TextUtils.isEmpty((chars = extras.getString(Notification.EXTRA_SUMMARY_TEXT))))
            return chars.toString();
        else
            return null;
    }


    public static ViewGroup getMessageView(Context context, Notification n) {
        Log.d(TAG, "Getting message view..");
        RemoteViews views = null;
        if (Build.VERSION.SDK_INT >= 16)
            views = n.bigContentView;
        if (views == null)
            views = n.contentView;
        if (views == null)
            return null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup localView = (ViewGroup) inflater.inflate(views.getLayoutId(), null);
        views.reapply(context.getApplicationContext(), localView);
        return localView;

    }

    public static String getTitle(Bundle extras) {
        Log.d(TAG, "Getting title from extras..");
        String msg = extras.getString(Notification.EXTRA_TITLE);
        Log.d(TAG, "" + extras.getString(Notification.EXTRA_TITLE_BIG));
        return msg;
    }

    /** OLD/CURRENT METHODS **/

    public static ViewGroup getView(Context context, RemoteViews view)
    {
        ViewGroup localView = null;
        try
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            localView = (ViewGroup) inflater.inflate(view.getLayoutId(), null);
            view.reapply(context, localView);
        }
        catch (Exception exp)
        {
        }
        return localView;
    }

    public static ViewGroup getLocalView(Context context, Notification n)
    {
        RemoteViews view = null;
        if(Build.VERSION.SDK_INT >= 16) { view = n.bigContentView; }

        if (view == null)
        {
            view = n.contentView;
        }
        ViewGroup localView = null;
        try
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            localView = (ViewGroup) inflater.inflate(view.getLayoutId(), null);
            view.reapply(context, localView);
        } catch (Exception exp) { }
        return localView;
    }

    public static ArrayList<Action> getActions(Notification n, String packageName, ArrayList<Action> actions) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(n);
        if (wearableExtender.getActions().size() > 0) {
            for (NotificationCompat.Action action : wearableExtender.getActions())
                actions.add(new Action(action, packageName, action.title.toString().toLowerCase().contains(REPLY_KEYWORD)));
        }
        return actions;
    }

    public static Action getQuickReplyAction(Notification n, String packageName) {
        NotificationCompat.Action action = null;
        if(Build.VERSION.SDK_INT >= 24)
            action = getQuickReplyAction(n);
        if(action == null)
            action = getWearReplyAction(n);
        if(action == null)
            return null;
        return new Action(action, packageName, true);
    }

    private static NotificationCompat.Action getQuickReplyAction(Notification n) {
        for(int i = 0; i < NotificationCompat.getActionCount(n); i++) {
            NotificationCompat.Action action = NotificationCompat.getAction(n, i);
            if(action.getRemoteInputs() != null) {
                for (int x = 0; x < action.getRemoteInputs().length; x++) {
                    RemoteInput remoteInput = action.getRemoteInputs()[x];
                    if (isKnownReplyKey(remoteInput.getResultKey()))
                        return action;
                }
            }
        }
        return null;
    }

    private static NotificationCompat.Action getWearReplyAction(Notification n) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(n);
        for (NotificationCompat.Action action : wearableExtender.getActions()) {
            if(action.getRemoteInputs() != null) {
                for (int x = 0; x < action.getRemoteInputs().length; x++) {
                    RemoteInput remoteInput = action.getRemoteInputs()[x];
                    if (isKnownReplyKey(remoteInput.getResultKey()))
                        return action;
                    else if (remoteInput.getResultKey().toLowerCase().contains(INPUT_KEYWORD))
                        return action;
                }
            }
        }
        return null;
    }

    private static boolean isKnownReplyKey(String resultKey) {
        if(TextUtils.isEmpty(resultKey))
            return false;

        resultKey = resultKey.toLowerCase();
        for(String keyword : REPLY_KEYWORDS)
            if(resultKey.contains(keyword))
                return true;

        return false;
    }



    public static boolean isAPriorityMode(int interruptionFilter) {
        if(interruptionFilter == NotificationListenerService.INTERRUPTION_FILTER_NONE ||
                interruptionFilter == NotificationListenerService.INTERRUPTION_FILTER_UNKNOWN)
            return false;
        return true;
    }

}
