package com.fang.starfang.network.task;

import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;

import com.fang.starfang.util.model.io.Action;
import com.fang.starfang.network.Communicate;
import com.fang.starfang.util.NotificationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SuggestTask extends AsyncTask<String, Integer, String> {

    private final static String SUGGEST_PHP = "suggest.php";

    private static final String TAG = "SUGGEST";
    private Context context;
    private StatusBarNotification sbn;
    private String send_cat;



    public SuggestTask(Context c, String sender, StatusBarNotification _sbn) {
        sbn = _sbn;
        send_cat = sender;
        context = c;
    }
/*
    private String privacyCheck( String name, String delim ) {

        if( name.contains(delim)) {
            String tmpName = "";
            StringTokenizer st = new StringTokenizer(name,delim);
            while( st.hasMoreTokens() ) {
                String token = st.nextToken();
                tmpName += ( token.replaceAll("[0-9]","").equals("") )? "**" : token;
                tmpName += delim;
            }

            return tmpName.substring(0,tmpName.length()-1);
        } else {
            return name;
        }

    }
    */

    @Override
    protected String doInBackground(String... strings) {
        String cmd = strings[0];
        String [] msg = new String[5];
        msg[0] = ( cmd.contains("조회") || cmd.contains("오늘")  )?"조회" : "건의";
        msg[1] = ( cmd.contains("오늘") ) ? "" : send_cat;
        msg[1] = msg[1].replaceAll("[0-9]","*");


        msg[2] = cmd;

        Date curr = new Date();
        SimpleDateFormat format_date = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm:ss");

        msg[3] = format_date.format(curr);
        msg[4] = format_time.format(curr);

        String jsonstr = Communicate.toServer(Communicate.SERVER_URL + SUGGEST_PHP, msg);

        jsonstr = jsonstr.replace("crlf","\r\n");

        Action a = NotificationUtils.getQuickReplyAction(sbn.getNotification(),"com.kakao.talk");
        try {
            a.sendReply(context, "to." + send_cat + "\r\n" + jsonstr);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        return "EXECUTE SUGGEST_TASK";
    }






}
