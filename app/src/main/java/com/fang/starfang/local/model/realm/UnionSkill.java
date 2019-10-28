package com.fang.starfang.local.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class UnionSkill extends RealmObject {

    public static final String PREF_TABLE = "연합전 스킬";
    public static final String FIELD_NAME = "uSkillName";

    private String uSkillName;
    private String uSkillType;
    private String uSkillEP;
    private String uSkillPower;
    private String uSkillCooldown;
    private String uSkillTargetArea;
    private String uSkillEffectArea;
    private String uSkillDesc;


    public String getuSkillName() {
        return uSkillName;
    }

    public String getuSkillType() {
        return uSkillType;
    }

    public String getuSkillEP() {
        return uSkillEP;
    }

    public String getuSkillPower() {
        return uSkillPower;
    }

    public String getuSkillCooldown() {
        return uSkillCooldown;
    }

    public String getuSkillTargetArea() {
        return uSkillTargetArea;
    }

    public String getuSkillEffectArea() {
        return uSkillEffectArea;
    }

    public String getuSkillDesc() {
        return uSkillDesc;
    }
}
