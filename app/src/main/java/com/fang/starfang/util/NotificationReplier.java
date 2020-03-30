package com.fang.starfang.util;

import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.FangConstant;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.util.model.io.Action;

import java.lang.ref.WeakReference;
import java.util.StringTokenizer;

import io.realm.Realm;

public class NotificationReplier extends AsyncTask<String, Integer, String> {

    private static final String TAG = "FANG_REPLY";

    private WeakReference<Context> context;
    private StatusBarNotification sbn;
    private String sendCat;
    private String catRoom;
    private boolean record;
    private boolean isLocalRequest;

    public NotificationReplier(Context context, String sendCat, String catRoom,
                               StatusBarNotification sbn, boolean isLocalRequest, boolean record ) {
        this.sbn = sbn;
        this.sendCat = sendCat;
        this.catRoom = catRoom;
        this.context = new WeakReference<>(context);
        this.isLocalRequest= isLocalRequest;
        this.record = record || isLocalRequest;
    }

    @Override
    protected String doInBackground(String... answers) {
        if( sbn != null ) {
            try (Realm realm = Realm.getDefaultInstance()) {
                Log.d(TAG, "REPLY TO " + sbn.getPackageName());

                Action quickReplyAction = isLocalRequest? null :
                        NotificationUtils.getQuickReplyAction(sbn.getNotification(), sbn.getPackageName());

                StringTokenizer st = new StringTokenizer(answers[0], FangConstant.CONSTRAINT_SEPARATOR);

                while (st.hasMoreTokens()) {
                    String message = st.nextToken();


                    if (message.substring(0, 2).equals("\r\n")) {
                        message = message.substring(2);
                    }

                    try {
                        if (message.substring(message.length() - 2).equals("\r\n")) {
                            message = message.substring(0, message.length() - 2);
                        }

                        if (quickReplyAction != null) {
                            quickReplyAction.sendReply(context.get(), "[" + answers[1] + "] to. " + sendCat + "\r\n" + message);
                        }

                        final String finalMessage = message;
                        if( record ) {
                            realm.executeTransaction(bgRealm -> {
                                Conversation conversation = bgRealm.createObject(Conversation.class);
                                conversation.setReplyID(sbn.getTag());
                                conversation.setConversation(finalMessage);
                                conversation.setPackageName(sbn.getPackageName());
                                conversation.setCatRoom(catRoom);
                                conversation.setSendCat(sendCat);
                            });
                        }
                    } catch (PendingIntent.CanceledException | NullPointerException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException error) {
                        error.printStackTrace();
                    }

                }
            } catch (RuntimeException ignored) {

            }
        }


        return null;

    }
}
