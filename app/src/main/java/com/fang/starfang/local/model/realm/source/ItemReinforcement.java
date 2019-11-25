package com.fang.starfang.local.model.realm.source;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ItemReinforcement extends RealmObject {
    public static final String PREF_TABLE = "보물 강화";
    public static final String FIELD_GRD = "reinfGRD";
    public static final String FIELD_TYPE = "reinfType";


    private int reinfGRD; // 1 ~ 7
    private String reinfType;
    private RealmList<RealmInteger> reinfValues;

    public int getReinfGRD() { return reinfGRD; }
    public String getreinfType() { return reinfType; }
    public RealmList<RealmInteger> getReinfValues() { return reinfValues; }
}
