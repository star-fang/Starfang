package com.fang.starfang.local.model.realm.source;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NormalItem extends RealmObject {

    public static final String PREF_TABLE = "일반 보물";
    public static final String FIELD_NO = "normalItemNo";
    public static final String FIELD_CATE_SUB = "normalItemSubCate";
    public static final String FIELD_TYPE = "normalItemRestrictionType";

    @PrimaryKey
    private int normalItemNo;
    private String normalItemSubCate; // 검, 곤, ..., 보조구
    private RealmList<RealmInteger> normalItemPowers; //  공 정 방 순 사 이동
    private String  normalItemRestrictionType;  // A ~ G
    private RealmList<RealmInteger> normalItemLevelUpPowers; // 7 14 21 28 35 42 49 56 63 70 77

    public int getNormalItemNo() {
        return normalItemNo;
    }

    public String getNormalItemSubCate() {
        return normalItemSubCate;
    }

    public RealmList<RealmInteger> getNormalItemPowers() {
        return normalItemPowers;
    }

    public String getNormalItemRestrictionType() {
        return normalItemRestrictionType;
    }

    public RealmList<RealmInteger> getNormalItemLevelUpPowers() {
        return normalItemLevelUpPowers;
    }
}
