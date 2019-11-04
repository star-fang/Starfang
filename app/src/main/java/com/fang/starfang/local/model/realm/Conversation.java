package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class Conversation extends RealmObject {

    private String sandCat;
    private String catRoom;
    private String replyID;
    private String timestamp;
    private String packageName;
    private String conversation;

    public String getSandCat() {
        return sandCat;
    }

    public void setSandCat(String sandCat) {
        this.sandCat = sandCat;
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
}
