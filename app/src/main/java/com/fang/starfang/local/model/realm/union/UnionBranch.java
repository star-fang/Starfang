package com.fang.starfang.local.model.realm.union;

import io.realm.RealmList;
import io.realm.RealmObject;

public class UnionBranch extends RealmObject {

    public static final String PREF_TABLE = "연합전 병종";
    public static final String FIELD_NAME = "uBranch";
    public static final String FIELD_CLASS = "uBranchClass";

    private String uBranch;
    private String uBranchClass;
    private String uBranckGrade;
    private String uBranchHP;
    private String uBranchEP;
    private RealmList<String> uBranchSpec; // passive specs
    private RealmList<String> uBranchSpecValue; // passive spec values
    private String uBranchAttackPower;
    private String uBranchMentalPower;
    private String uBranchDefensePower;
    private String uBranchAgilityPower;
    private String uBranchMoralePower;
    private String uBranchMove;
    private String uBranchNext;
    private String uBranchDesc;
    private String uBranchSkill;


    public String getuBranch() {
        return uBranch;
    }

    public String getuBranchHP() {
        return uBranchHP;
    }

    public String getuBranchEP() {
        return uBranchEP;
    }

    public RealmList<String> getuBranchSpec() {
        return uBranchSpec;
    }

    public RealmList<String> getuBranchSpecValue() {
        return uBranchSpecValue;
    }

    public String getuBranchAttackPower() {
        return uBranchAttackPower;
    }

    public String getuBranchMentalPower() {
        return uBranchMentalPower;
    }

    public String getuBranchDefensePower() {
        return uBranchDefensePower;
    }

    public String getuBranchAgilityPower() {
        return uBranchAgilityPower;
    }

    public String getuBranchMoralePower() {
        return uBranchMoralePower;
    }

    public String getuBranchMove() {
        return uBranchMove;
    }

    public String getuBranchNext() {
        return uBranchNext;
    }

    public String getuBranchDesc() {
        return uBranchDesc;
    }

    public String getuBranchSkill() {
        return uBranchSkill;
    }

    public String getuBranchClass() {
        return uBranchClass;
    }

    public String getuBranckGrade() {
        return uBranckGrade;
    }
}
