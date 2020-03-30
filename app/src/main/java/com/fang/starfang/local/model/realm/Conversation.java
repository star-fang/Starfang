package com.fang.starfang.local.model.realm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;

public class Conversation extends RealmObject {
    public static final String FIELD_SENDCAT = "sendCat";
    public static final String FIELD_ROOM = "catRoom";
    public static final String FIELD_TIME = "timestamp";
    public static final String FIELD_TIME_VALUE = "timeValue";
    public static final String FIELD_PACKAGE  = "packageName";
    public static final String FIELD_CONVERSATION = "conversation";

    private static final String CONVERSATION_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String sendCat;
    private String catRoom;
    private String replyID;
    private String timestamp;
    private long timeValue;
    private String packageName;
    private String conversation;

    public Conversation() {
        Date curDate = new Date();
        String timestamp =  new SimpleDateFormat(CONVERSATION_TIME_FORMAT, Locale.KOREA).format(curDate);
        long timeValue = curDate.getTime();
        this.timestamp = timestamp;
        this.timeValue = timeValue;
    }

    public Conversation(String sendCat, String catRoom, String replyID, String packageName, String conversation) {
        this.sendCat = sendCat;
        this.catRoom = catRoom;
        this.replyID = replyID;
        this.packageName = packageName;
        this.conversation = conversation;
        Date curDate = new Date();
        String timestamp =  new SimpleDateFormat(CONVERSATION_TIME_FORMAT, Locale.KOREA).format(curDate);
        long timeValue = curDate.getTime();
        this.timestamp = timestamp;
        this.timeValue = timeValue;
    }

    public Conversation(String sendCat, String catRoom, String replyID, String packageName, String conversation, String timestamp) {
        this.sendCat = sendCat;
        this.catRoom = catRoom;
        this.replyID = replyID;
        this.packageName = packageName;
        this.conversation = conversation;
        this.timestamp = timestamp;
        try {
            this.timeValue = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).parse(timestamp)).getTime();
        } catch( ParseException e ) {
            this.timeValue = -1;
        }
    }







    public String getCatRoom() {
        return catRoom;
    }

    public void setCatRoom(String catRoom) {
        this.catRoom = catRoom;
    }

    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public String getSendCat() {
        return sendCat;
    }

    public void setSendCat(String sendCat) {
        this.sendCat = sendCat;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(long timeValue) {
        this.timeValue = timeValue;
    }
}
