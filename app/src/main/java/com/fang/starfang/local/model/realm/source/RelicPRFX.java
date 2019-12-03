package com.fang.starfang.local.model.realm.source;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RelicPRFX extends RealmObject {
    public static final String PREF_TABLE = "보패 접두사";
    public static final String FIELD_ID = "relicPrefixID";
    public static final String FIELD_NAME = "relicPrefixName";
    public static final String FIELD_SPEC = "relicPrefixSpec";
    public static final String FIELD_STAT = "relicPrefixStat";

    @PrimaryKey
    private int relicPrefixID;
    private String relicPrefixName;
    private String relicPrefixSpec;
    private String relicPrefixStat;
    private RealmList<Double> relicPrefixValue;


    public int getRelicPrefixID() {
        return relicPrefixID;
    }

    public String getRelicPrefixName() {
        return relicPrefixName;
    }

    public String getRelicPrefixSpec() {
        return relicPrefixSpec;
    }

    public String getRelicPrefixStat() {
        return relicPrefixStat;
    }

    public RealmList<Double> getRelicPrefixValue() {
        return relicPrefixValue;
    }
}
