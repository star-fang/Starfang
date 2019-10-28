package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class Memo extends RealmObject {
    public static final String PREF_TABLE = "메모";
    public static final String FIELD_ID = "MemoID";
    public static final String FIELD_NAME = "MemoName";
    public static final String FIELD_ROOM = "MemoRoom";

    private int MemoID;
    private String MemoRoom;
    private String MemoName;
    private String MemoText;
    private String MemoTimestamp;

    public String getMemoRoom() {
        return MemoRoom;
    }

    public void setMemoRoom(String memoRoom) {
        MemoRoom = memoRoom;
    }

    public String getMemoName() {
        return MemoName;
    }

    public void setMemoName(String memoName) {
        MemoName = memoName;
    }

    public String getMemoText() {
        return MemoText;
    }

    public void setMemoText(String memoText) {
        MemoText = memoText;
    }

    public String getMemoTimestamp() {
        return MemoTimestamp;
    }

    public void setMemoTimestamp(String memoTimestamp) {
        MemoTimestamp = memoTimestamp;
    }

    public int getMemoID() {
        return MemoID;
    }

    public void setMemoID(int memoID) {
        MemoID = memoID;
    }
}
