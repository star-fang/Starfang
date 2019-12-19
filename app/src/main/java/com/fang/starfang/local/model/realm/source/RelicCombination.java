package com.fang.starfang.local.model.realm.source;

import com.fang.starfang.local.model.realm.primitive.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RelicCombination extends RealmObject {
    public static final String FIELD_ID = "relicCombinationID";
    public static final String PREF_TABLE = "보패 조합";
    public static final String FIELD_SPEC = "relicCombinationSpec";
    public static final String FIELD_SFX = "relicSFXes";
    public static final String FIELD_GRADE = "relicCombinationGrade";

    @PrimaryKey
    private int relicCombinationID;
    private String relicCombinationSpec;
    private String relicCombinationSpecVal;
    private int relicCombinationGrade;
    private RealmList<RealmString> relicSFXes;
    private int guardianType;
    private boolean relicCombinationDuplicated;

    public int getRelicCombinationID() {
        return relicCombinationID;
    }

    public String getRelicCombinationSpec() {
        return relicCombinationSpec;
    }

    public String getRelicCombinationSpecVal() {
        return relicCombinationSpecVal;
    }

    public int getRelicCombinationGrade() {
        return relicCombinationGrade;
    }

    public RealmList<RealmString> getRelicSFXes() {
        return relicSFXes;
    }

    public int getGuardianType() {
        return guardianType;
    }

    public boolean isRelicCombinationDuplicated() {
        return relicCombinationDuplicated;
    }
}
