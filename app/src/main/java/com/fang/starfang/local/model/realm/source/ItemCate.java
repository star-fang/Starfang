package com.fang.starfang.local.model.realm.source;

import com.fang.starfang.local.model.realm.primitive.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ItemCate extends RealmObject {
    public static final String PREF_TABLE = "보물 분류";
    public static final String FIELD_SUB_CATE = "itemSubCate";
    public static final String FIELD_MAIN_CATE = "itemMainCate";
    public static final String FIELD_RESTRICT_BRANCH = "itemRestrictionBranch";

    private String itemSubCate;
    private String itemMainCate;
    private String itemRestrictionBranch;
    private RealmList<RealmString> itemReinforcementTypes;

    public String getItemSubCate() {return itemSubCate;}
    public String getItemMainCate() {return itemMainCate;}
    public RealmList<RealmString> getItemReinforcementTypes() {
        return itemReinforcementTypes;
    }

    public String getItemRestrictionBranch() {
        return itemRestrictionBranch;
    }
}
