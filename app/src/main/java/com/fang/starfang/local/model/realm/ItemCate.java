package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class ItemCate extends RealmObject {
    public static final String PREF_TABLE = "보물 분류";
    public static final String FIELD_SUB_CATE = "itemSubCate";
    public static final String FIELD_MAIN_CATE = "itemMainCate";

    private String itemSubCate;
    private String itemMainCate;
    private String itemRestriction;

    public String getItemSubCate() {return itemSubCate;}
    public String getItemMainCate() {return itemMainCate;}
    public String getItemRestriction() {return itemRestriction;}
}
