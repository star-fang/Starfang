package com.fang.starfang.util;

import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.util.model.io.Action;

import java.lang.ref.WeakReference;
import java.util.StringTokenizer;

public class KakaoReplier extends AsyncTask<String, Integer, String> {

    private static final String TAG = "FANG_REPLY";

    private WeakReference<Context> context;
    private StatusBarNotification sbn;
    private String send_cat;

    public KakaoReplier(Context c, String sender, StatusBarNotification _sbn) {
        sbn = _sbn;
        send_cat = sender;
        context = new WeakReference<>(c);
    }

    @Override
    protected String doInBackground(String... answers) {
        if(sbn == null) {
            return null;
        }
        Action a = NotificationUtils.getQuickReplyAction(sbn.getNotification(),sbn.getPackageName());
        Log.d(TAG, "REPLY TO " + sbn.getPackageName());

        StringTokenizer st = new StringTokenizer(answers[0],"," );

        while( st.hasMoreTokens()) {
            String tmpRes = st.nextToken();


            if( tmpRes.substring(0,2).equals("\r\n")) {
                tmpRes = tmpRes.substring(2);
            }

            try {
                if( tmpRes.substring(tmpRes.length()-2).equals("\r\n")) {
                    tmpRes = tmpRes.substring(0,tmpRes.length()-2);
                }


                if (a != null) {
                    a.sendReply(context.get(), answers[1] + "to. " + send_cat + "\r\n" + tmpRes);
                }
            } catch (PendingIntent.CanceledException | NullPointerException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException ignored) {
            }

        }


        return null;
    }
}
