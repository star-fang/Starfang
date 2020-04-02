package com.fang.starfang.local.model.realm;

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

    public Conversation() {}

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
        Date date = new Date( timeValue );
        this.timestamp = new SimpleDateFormat(CONVERSATION_TIME_FORMAT, Locale.KOREA).format(date);
    }
}
