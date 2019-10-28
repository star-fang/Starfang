package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class Magic  extends RealmObject {
    public static final String PREF_TABLE = "책략";
    public static final String FIELD_NAME = "magicName";
    public static final String FIELD_TYPE = "magicSkillType";

    private int magicID;
    private int magicMP;
    private int magicEP;
    private String magicSkillType;
    private int magicSkillPower;
    private int magicAccu;
    private String magicEffectArea;
    private String magicTargetArea;
    private String magicDamageType;
    private String magicHealType;
    private String magicAccuType;
    private int magicCanStreakCast;
    private int magicObstructiveSkill;
    private String magicIcon;
    private String magicName;
    private String magicDesc;


    public int getMagicID() {
        return magicID;
    }

    public int getMagicMP() {
        return magicMP;
    }

    public int getMagicEP() {
        return magicEP;
    }

    public String getMagicSkillType() {
        return magicSkillType;
    }

    public int getMagicSkillPower() {
        return magicSkillPower;
    }

    public int getMagicAccu() {
        return magicAccu;
    }

    public String getMagicEffectArea() {
        return magicEffectArea;
    }

    public String getMagicTargetArea() {
        return magicTargetArea;
    }

    public String getMagicDamageType() {
        return magicDamageType;
    }

    public String getMagicHealType() {
        return magicHealType;
    }

    public String getMagicAccuType() {
        return magicAccuType;
    }

    public int getMagicCanStreakCast() {
        return magicCanStreakCast;
    }

    public int getMagicObstructiveSkill() {
        return magicObstructiveSkill;
    }

    public String getMagicIcon() {
        return magicIcon;
    }

    public String getMagicName() {
        return magicName;
    }

    public String getMagicDesc() {
        return magicDesc;
    }
}
