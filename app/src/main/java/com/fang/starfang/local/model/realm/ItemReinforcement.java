package com.fang.starfang.local.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ItemReinforcement extends RealmObject {
    public static final String PREF_TABLE = "보물 강화";
    public static final String FIELD_GRD = "reinfGRD";
    public static final String FIELD_STAT = "reinfStat";


    private String reinfGRD;
    private String reinfStat;
    private RealmList<Integer> reinfValues;

    public String getReinfGRD() { return reinfGRD; }
    public String getReinfStat() { return reinfStat; }
    public RealmList<Integer> getReinfValues() { return reinfValues; }
}
