package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class Spec extends RealmObject {

    public static final String PREF_TABLE = "설명";
    public static final String FIELD_NAME = "specName";
    public static final String FIELD_NAME_NO_BLANK = "specNameNoBlank";
    public static final String FIELD_NAME2 = "specName2";

    private String specName;
    private String specNameNoBlank;
    private String specDescription;
    private String specName2;

    public String getSpecName() {return specName;}
    public String getSpecDescription() {return specDescription;}
    public void setSpecNameNoBlank(String noBlank) {specNameNoBlank=noBlank;}
    public String getSpecName2() {return specName2;}
}
