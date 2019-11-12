package com.fang.starfang.local.model.realm.source;

import io.realm.RealmObject;

public class Spec extends RealmObject {

    public static final String PREF_TABLE = "설명";
    public static final String FIELD_ID = "specID";
    public static final String FIELD_NAME = "specName";
    public static final String FIELD_CATE = "specCategory";
    public static final String FIELD_NAME_NO_BLANK = "specNameNoBlank";
    public static final String FIELD_NAME2 = "specName2";

    private int specID;
    private String specName;
    private String specCategory;
    private String specNameNoBlank;
    private String specDescription;
    private String specName2;

    public int getSpecID() { return specID; }
    public String getSpecName() { return specName; }
    public void setSpecName( String name ) { specName = name; }
    public String getSpecCategory() { return specCategory; }
    public void setSpecCategory( String cate ) { specCategory = cate; }
    public String getSpecDescription() {return specDescription; }
    public void setSpecDescription( String desc ) { specDescription = desc; }
    public void setSpecNameNoBlank(String noBlank) { specNameNoBlank = noBlank; }
    public String getSpecName2() { return specName2; }
    public void setSpecName2( String name2 ) { specName2 = name2; }

}
