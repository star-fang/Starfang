package com.fang.starfang.util;

import android.app.Notification;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.fang.starfang.FangConstant;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.util.model.io.Action;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import io.realm.Realm;

public class NotificationReplier extends AsyncTask<String, Integer, String> {

    private static final String TAG = "FANG_REPLY";
    private static final String[] REPLY_KEYWORDS = {"reply", "android.intent.extra.text"};
    private static final CharSequence REPLY_KEYWORD = "reply";
    private static final CharSequence INPUT_KEYWORD = "input";

    private WeakReference<Context> context;
    private StatusBarNotification sbn;
    private String sendCat;
    private String catRoom;
    private boolean record;
    private boolean isLocalRequest;

    public NotificationReplier(Context context, String sendCat, String catRoom,
                               StatusBarNotification sbn, boolean isLocalRequest, boolean record) {
        this.sbn = sbn;
        this.sendCat = sendCat;
        this.catRoom = catRoom;
        this.context = new WeakReference<>(context);
        this.isLocalRequest = isLocalRequest;
        this.record = record || isLocalRequest;
    }

    @Override
    protected String doInBackground(String... answers) {
        if (sbn != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                Log.d(TAG, "REPLY TO " + sbn.getPackageName());

                Action quickReplyAction = isLocalRequest ? null :
                        getQuickReplyAction(sbn.getNotification(), sbn.getPackageName());

                StringTokenizer st = new StringTokenizer(answers[0], FangConstant.CONSTRAINT_SEPARATOR);

                while (st.hasMoreTokens()) {
                    String message = st.nextToken();

                    int crflIndex = message.length() - 2;
                    if (crflIndex >= 0) {
                        if (message.substring(0, 2).equals("\r\n")) {
                            message = message.substring(2);
                        }

                        if (message.substring(crflIndex).equals("\r\n")) {
                            message = message.substring(0, crflIndex);
                        }
                    }

                    final String finalBotName = answers[1];
                    final String finalMessage = message.trim();

                    if( finalMessage.length() > 0) {
                        if (quickReplyAction != null) {
                            quickReplyAction.sendReply(context.get(), "[" + finalBotName + "] to. " + sendCat + "\r\n" + finalMessage);
                        }
                        if (record) {
                            realm.executeTransaction(bgRealm -> {
                                Conversation conversation = bgRealm.createObject(Conversation.class);
                                conversation.setReplyID(sbn.getTag());
                                conversation.setConversation(finalMessage);
                                conversation.setPackageName(sbn.getPackageName());
                                conversation.setCatRoom(catRoom);
                                conversation.setSendCat(finalBotName);
                                Date date = new Date();
                                conversation.setTimeValue(date.getTime());
                            });
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return null;

    }

    private Action getQuickReplyAction(Notification n, String packageName) {
        NotificationCompat.Action action = null;
        if (Build.VERSION.SDK_INT >= 24)
            action = getQuickReplyAction(n);
        if (action == null)
            action = getWearReplyAction(n);
        if (action == null)
            return null;
        return new Action(action, packageName, true);
    }

    private NotificationCompat.Action getQuickReplyAction(Notification n) {
        for (int i = 0; i < NotificationCompat.getActionCount(n); i++) {
            NotificationCompat.Action action = NotificationCompat.getAction(n, i);
            if (action.getRemoteInputs() != null) {
                for (int x = 0; x < action.getRemoteInputs().length; x++) {
                    RemoteInput remoteInput = action.getRemoteInputs()[x];
                    if (isKnownReplyKey(remoteInput.getResultKey()))
                        return action;
                }
            }
        }
        return null;
    }

    private NotificationCompat.Action getWearReplyAction(Notification n) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(n);
        for (NotificationCompat.Action action : wearableExtender.getActions()) {
            if (action.getRemoteInputs() != null) {
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


    private boolean isKnownReplyKey(String resultKey) {
        if (TextUtils.isEmpty(resultKey))
            return false;

        resultKey = resultKey.toLowerCase();
        for (String keyword : REPLY_KEYWORDS)
            if (resultKey.contains(keyword))
                return true;

        return false;
    }

    public static ArrayList<Action> getActions(Notification n, String packageName, ArrayList<Action> actions) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(n);
        if (wearableExtender.getActions().size() > 0) {
            for (NotificationCompat.Action action : wearableExtender.getActions())
                actions.add(new Action(action, packageName, action.title.toString().toLowerCase().contains(REPLY_KEYWORD)));
        }
        return actions;
    }
}
