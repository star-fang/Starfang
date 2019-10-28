package com.fang.starfang.util;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.util.model.io.Action;

import java.util.StringTokenizer;

public class KakaoReplier extends AsyncTask<String, Integer, String> {

    private static final String TAG = "FANG";

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private StatusBarNotification sbn;
    private String send_cat;

    public KakaoReplier(Context c, String sender, StatusBarNotification _sbn) {
        sbn = _sbn;
        send_cat = sender;
        context = c;
    }

    @Override
    protected String doInBackground(String... answers) {
        Action a = NotificationUtils.getQuickReplyAction(sbn.getNotification(),sbn.getPackageName());
        Log.d(TAG, "REP!!!" + sbn.getPackageName());

        StringTokenizer st = new StringTokenizer(answers[0],"," );

        while( st.hasMoreTokens()) {
            String tmpRes = st.nextToken();


            if( tmpRes.substring(0,2).equals("\r\n")) {
                tmpRes = tmpRes.substring(2);
            }

            try {
                if( tmpRes.substring(tmpRes.length()-2, tmpRes.length()).equals("\r\n")) {
                    tmpRes = tmpRes.substring(0,tmpRes.length()-2);
                }



                a.sendReply(context, answers[1] + "to. " + send_cat + "\r\n" + tmpRes);
            } catch (PendingIntent.CanceledException pce) {
                //e.printStackTrace();
            } catch( NullPointerException ne) {

            } catch( StringIndexOutOfBoundsException sioobe) {

            } catch( ArrayIndexOutOfBoundsException aoobe ) {

            }

        }


        return null;
    }
}
