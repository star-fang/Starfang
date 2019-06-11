package com.fang.starfang.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

// class Communicate : set of networking method
public class Communicate {

    public final static String[] list_line = { "검사", "경기병", "군악대", "군주", "궁기병", "궁병", "노병", "노전차", "도독", "도사", "마왕",
            "무인", "무희", "보병", "산악기병", "수군", "웅술사", "적병", "전차", "중기병", "창병", "책사", "천자", "포차", "풍수사", "현자", "호술사",
            "효기병" };

    public final static String NULL_POINTER_DESC = "(짜증)";
    public final static String OUT_OF_BOUNDS_DESC = "(빠직)";
    public final static String NUN_FORMAT_DESC = "(깜짝)";
    public final static String PARSE_DESC = "(흑흑)";

    public static final String SERVER_URL = "http://clavis.dothome.co.kr/fangcat/";
    private final static String TAG = "COMMUNICATE";

    // toServer() : send message to server and receive echo message
    public static String toServer(String phpURL, String... command) {
        String tmp = "전송 :";
        //System.out.print("전송 : ");
        for (String cmd : command) {
            tmp += (" " + cmd);
            //System.out.print(" " + temp);
        }
        tmp += "("+ phpURL + ")";

        Log.d(TAG,tmp);

        //System.out.println("\n\n통신중... (" + phpURL + ")\n");

        String mResult = "";
        try {
            URL url = new URL(phpURL);
            HttpURLConnection conn;
            StringBuilder html = new StringBuilder();
            conn = (HttpURLConnection) url.openConnection();
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
            StringBuilder buffer = new StringBuilder();
            buffer.append("json").append("=").append(commandJasonString(command));
            pw.write(buffer.toString());
            pw.flush();
            InputStreamReader input = new InputStreamReader(conn.getInputStream(), "utf-8");
            try (BufferedReader br = new BufferedReader(input, 8192)) {
                String line;
                while (true) {
                    line = br.readLine();
                    // System.out.println("read line : " + line);
                    if (line == null) {
                        break;
                    }
                    html.append(line);
                }
            }
            mResult = html.toString();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        // mResult = mResult.substring(1);
        System.out.println("응답 : " + mResult);
        return mResult.trim();
    } // end toServer()

    // commandJasonString() : make JSON string
    @SuppressWarnings("unchecked")
    private static String commandJasonString(String... key) {
        JSONObject obj = new JSONObject();

        for (int i = 0; i < key.length; i++) {
            obj.put("key" + i, key[i]);
        }
        return obj.toString();
    } // end commandJasonString()

    // parseJSONObjects() : parse JSON string to JSON array
    public static ArrayList<JSONObject> parseJSONObjects(String jsonstr) {

        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        Object jsonobj = null;
        try {
            jsonobj = JSONValue.parseWithException(jsonstr);

        } catch (ParseException pe) {
            //System.out.println(pe);
        }

        JSONArray jsonary = (JSONArray) jsonobj;

        try {
            for (int i = 0; i < jsonary.size(); i++) {
                list.add((JSONObject) jsonary.get(i));
            }
        } catch (NullPointerException npe) {

        }

        return list;
    } // end parseJSONObjects()

} // end class
