package com.fang.starfang.local.model.realm;

import com.fang.starfang.local.model.realm.primitive.RealmString;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class CustomAgenda extends RealmObject {
    public static final String FIELD_ID = "customID";
    public static final String FIELD_START = "agendaStart";
    public static final String FIELD_ROOM = "room";
    public static final String FIELD_CLAN = "clan";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_REMARKS = "remarks";
    public static final String FIELD_INTERVAL = "interval";

    @PrimaryKey
    private int customID;
    private int agendaStart;
    private String title;
    private String remarks;
    private String room;
    private RealmList<RealmString> guardians;
    private int interval;

    public CustomAgenda() {}


    public CustomAgenda( int agendaStart, String title, String remarks, String room, RealmList guardians, int interval)
            throws RealmPrimaryKeyConstraintException
    {
        this.customID = (int) UUID.randomUUID().getMostSignificantBits();
        this.agendaStart = agendaStart;
        this.title = title;
        this.remarks = remarks;
        this.room = room;
        this.interval = interval;
        this.guardians = guardians;
    }


    public int getCustomID() {
        return customID;
    }

    public int getAgendaStart() {
        return agendaStart;
    }

    public void setAgendaStart(int agendaStart) {
        this.agendaStart = agendaStart;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public RealmList<RealmString> getGuardians() {
        return guardians;
    }

    public void setGuardians(RealmList<RealmString> guardians) {
        this.guardians = guardians;
    }
}
