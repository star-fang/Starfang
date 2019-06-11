package com.fang.starfang.model.realm;

import io.realm.RealmObject;

public class Relation extends RealmObject {
    public static final String PREF_TABLE = "병종 상성";
    public static final String FIELD_ATTACKER= "branchAttacker";
    public static final String FIELD_DEFENDER = "branchDefender";
    public static final String FIELD_RELATION = "relationValue";

    private String branchAttacker;
    private String branchDefender;
    private int relationValue;

    public String getBranchAttacker() {return branchAttacker;}
    public String getBranchDefender() {return branchDefender;}
    public int getRelationValue() {return relationValue;}
}
