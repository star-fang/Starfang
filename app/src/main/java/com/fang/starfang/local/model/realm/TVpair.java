package com.fang.starfang.local.model.realm;

import org.apache.commons.lang3.StringUtils;

import io.realm.RealmObject;

public class TVpair extends RealmObject {
    public static final String FIELD_TV_KEY = "tvTerrainName";
    public static final String FIELD_TV_VALUE = "tvValue";
    private static final char padChar = '　';
    private static final char padNum = '0';
    private String tvTerrainName;
    private int tvValue;

    public String getTvTerrainName() { return tvTerrainName; }

    public String getPaddTvTerrainName(int size) {
        return StringUtils.leftPad(tvTerrainName,size,padChar);
    }

    public int getTvValue() { return tvValue; }

    public String getPaddTvValue(int size) {
        return StringUtils.leftPad(String.valueOf(tvValue),size,padNum);
    }
}
