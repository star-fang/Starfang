package com.fang.starfang.local.model.realm;

import com.fang.starfang.local.model.realm.primitive.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;

public class TermSynergy extends RealmObject {
    public static final String PREF_TABLE = "몽매 시너지";
    public static final String FIELD_TERM = "synTerm";
    public static final String FIELD_MEMBERS = "synMembers";
    public static final String FIELD_SPECS = "synSpecs";
    private String synTerm;
    private RealmList<RealmString> synMembers;
    private RealmList<RealmString> synEnemies;
    private RealmList<RealmString> synSpecs;
    private RealmList<RealmString> synSpecValues;

    public String getSynTerm() { return synTerm; }
    public RealmList<RealmString> getSynMembers() { return synMembers; }
    public RealmList<RealmString> getSynEnemies() { return synEnemies; }
    public RealmList<RealmString> getSynSpecs() { return synSpecs; }
    public RealmList<RealmString> getSynSpecValues() { return synSpecValues; }
}
