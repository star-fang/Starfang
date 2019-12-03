package com.fang.starfang.local.model.realm.source;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RelicSFX extends RealmObject {
    public static final String PREF_TABLE = "보패 접미사";
    public static final String FIELD_ID = "relicSuffixID";
    public static final String FIELD_NAME = "relicSuffixName";
    public static final String FIELD_GRD = "relicSuffixGRD";

    @PrimaryKey
    private int relicSuffixID;
    private String relicSuffixName;
    private String relicSuffixGrade;
    private RealmList<Integer> relicSuffixPowers; // 공 정 방 순 사 HP MP
    private int guardianType;

    public int getRelicSuffixID() {
        return relicSuffixID;
    }


    public String getRelicSuffixName() {
        return relicSuffixName;
    }

    public String getRelicSuffixGrade() {
        return relicSuffixGrade;
    }

    public RealmList<Integer> getRelicSuffixPowers() {
        return relicSuffixPowers;
    }

    public int getGuardianType() {
        return guardianType;
    }
}
