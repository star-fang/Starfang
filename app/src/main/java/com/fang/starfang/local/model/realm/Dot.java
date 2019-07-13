package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class Dot extends RealmObject {
    public static final String PREF_TABLE = "도트";
    public static final String FIELD_NAME = "dotName";
    public static final String FIELD_POINTS = "dotPoints";

    private String dotName;
    private String dotPoints;

    public String getDotName() {
        return dotName;
    }

    public String getDotPoints() {
        return dotPoints;
    }
}
