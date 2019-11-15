package com.fang.starfang.local.model.realm.primitive;


import androidx.annotation.NonNull;

import io.realm.RealmObject;

public class RealmInteger extends RealmObject {
    private int intValue;

    public static final String VALUE = "intValue";
    public RealmInteger(){}

    public RealmInteger(int intValue){
        this.intValue =  intValue;
    }

    public int toInt() { return intValue; }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    @NonNull
    @Override
    public String toString() {
        return intValue + "";
    }
}
