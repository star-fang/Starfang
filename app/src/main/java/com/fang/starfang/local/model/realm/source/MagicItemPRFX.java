package com.fang.starfang.local.model.realm.source;

import io.realm.RealmList;
import io.realm.RealmObject;

public class MagicItemPRFX extends RealmObject {
    public static final String PREF_TABLE = "보패 접두사";
    public static final String FIELD_NAME = "prefixName";
    public static final String FIELD_SPEC = "prefixSpec";
    public static final String FIELD_STAT = "prefixStat";

    private String prefixName;
    private String prefixSpec;
    private String prefixStat;
    private RealmList<Double> prefixValue;

    public String getPrefixName() {
        return prefixName;
    }

    public String getPrefixSpec() {
        return prefixSpec;
    }

    public String getPrefixStat() {
        return prefixStat;
    }

    public RealmList<Double> getPrefixValue() {
        return prefixValue;
    }
}
