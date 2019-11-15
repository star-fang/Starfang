package com.fang.starfang.local.model.realm.primitive;

import androidx.annotation.NonNull;

import io.realm.RealmObject;

public class RealmString extends RealmObject {

    public static final String VALUE = "stringValue";
    private String stringValue;

    public RealmString(){}

    public RealmString(String stringValue){
        this.stringValue =  stringValue;
    }

    @NonNull
    @Override
    public String toString() {
        return stringValue;
    }

}