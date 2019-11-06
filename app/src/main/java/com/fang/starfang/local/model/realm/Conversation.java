package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class Conversation extends RealmObject {

    public static final String FIELD_SENDCAT = "sendCat";
    public static final String FIELD_ROOM = "catRoom";
    public static final String FIELD_TIME = "timestamp";
    public static final String FIELD_TIME_VALUE = "timeValue";
    public static final String FIELD_PACKAGE  = "packageName";
    public static final String FIELD_CONVERSATION = "conversation";

    private String sendCat;
    private String catRoom;
    private String replyID;
    private String timestamp;
    private long timeValue;
    private String packageName;
    private String conversation;



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
