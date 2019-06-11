package com.fang.starfang.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class MagicItemSFX extends RealmObject {
    public static final String PREF_TABLE = "보패 접미사";
    public static final String FIELD_NAME = "magicSuffixName";
    public static final String FIELD_GRD = "magicSuffixGRD";

    private String magicSuffixName;
    private String magicSuffixGRD;
    private RealmList<Integer> magicSuffixStats;

    public String getMagicSuffixName() {
        return magicSuffixName;
    }

    public String getMagicSuffixGRD() {
        return magicSuffixGRD;
    }

    public RealmList<Integer> getMagicSuffixStats() {
        return magicSuffixStats;
    }
}
