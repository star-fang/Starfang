package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class UnionSpec extends RealmObject {

    public static final String PREF_TABLE = "연합전 능력";
    public static final String FIELD_ID = "uSpecID";
    public static final String FIELD_NAME = "uSpecName";

    private String uSpecID;
    private String uSpecName;
    private String uSpecDesc;

    public String getuSpecID() {
        return uSpecID;
    }

    public String getuSpecName() {
        return uSpecName;
    }

    public String getuSpecDesc() {
        return uSpecDesc;
    }
}
