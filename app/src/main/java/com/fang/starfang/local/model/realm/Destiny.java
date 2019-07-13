package com.fang.starfang.local.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Destiny extends RealmObject {

    public static final String PREF_TABLE = "인연";
    public static final String FIELD_NAME = "desName";
    public static final String FIELD_NAME_NO_BLANK = "desNameNoBlank";
    public static final String INIT_CORD = "인연의 끈: ";
    public static final String[] INIT_CONDITIONS = {"조건1: ","조건2: ","조건3: "};
    public static final String INIT_LASTING_EFFECT = "지속 효과: ";

    private String desName;
    private String desCord;
    private String desLastingEffect;
    private RealmList<String> desCondition;
    private RealmList<String> desJoinEffect;
    private String desNameNoBlank;


    public String getDesName() { return desName; }
    public String getDesCord() { return desCord; }
    public String getDesLastingEffect() { return desLastingEffect; }
    public RealmList<String> getdesCondition() {
        return desCondition;
    }
    public RealmList<String> getdesJoinEffect() {
        return desJoinEffect;
    }
    public void setDesNameNoBlank(String noBlank) {desNameNoBlank=noBlank;}

}
