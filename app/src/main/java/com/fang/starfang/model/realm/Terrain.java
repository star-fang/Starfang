package com.fang.starfang.model.realm;

import org.apache.commons.lang3.StringUtils;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Terrain extends RealmObject {

    public static final String PREF_TABLE = "지형 정보";
    public static final String FIELD_BRANCH_NAME = "branchName";
    public static final String FIELD_TERRAIN_SYNS = "terrainSyns";
    public static final String FIELD_MOVING_COST = "movingCost";
    private static final char padChar = '　';

    private String branchName;
    private RealmList<TVpair> terrainSyns;
    private RealmList<TVpair> movingCost;

    public String getBranchName() {return branchName;}
    public RealmList<TVpair> getTerrainSyns() {return terrainSyns;}
    public RealmList<TVpair> getMovingCost() {return movingCost;}
    public String getPaddBranchName(int size) {
        return StringUtils.leftPad(branchName,size,padChar);
    }

}
