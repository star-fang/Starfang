package com.fang.starfang.local.model.realm;

import com.fang.starfang.local.model.realm.primitive.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;

public class MagicItemCombination extends RealmObject {
    public static final String PREF_TABLE = "보패 조합";
    public static final String FIELD_COMB = "magicCombination";
    public static final String FIELD_SFXES = "magicCombSFXes";

    private String magicCombination;
    private int magicCombGRD;
    private RealmList<RealmString> magicCombSFXes;

    public String getMagicCombination() { return magicCombination; }
    public int getMagicCombGRD() { return magicCombGRD; }
    public RealmList<RealmString> getMagicCombSFXes() { return magicCombSFXes; }
}
