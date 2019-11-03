package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class Agenda extends RealmObject {
    public static final String PREF_TABLE = "일정";
    public static final String FIELD_START = "agendaStart";
    public static final String FIELD_MAP = "agendaMap";
    public static final String FIELD_DIV = "agendaDivision";

    private int agendaStart;
    private String agendaDivision;
    private String agendaMap;

    public int getAgendaStart() {
        return agendaStart;
    }

    public String getAgendaDivision() {
        return agendaDivision;
    }

    public String getAgendaMap() {
        return agendaMap;
    }


}