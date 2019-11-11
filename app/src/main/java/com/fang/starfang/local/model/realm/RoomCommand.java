package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class RoomCommand extends RealmObject {
    public static final String PREF_TABLE = "병종 상성";
    public static final String FIELD_ROOM= "roomName";
    public static final String FIELD_STATUS = "status";

    private String roomName;
    private String status;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}