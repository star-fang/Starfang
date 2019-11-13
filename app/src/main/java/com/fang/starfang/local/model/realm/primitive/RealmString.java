package com.fang.starfang.local.model.realm.primitive;

import androidx.annotation.NonNull;

import io.realm.RealmObject;

/**
 * Created by catalin prata on 29/05/15.
 *
 * Wrapper over String to support setting a list of Strings in a RealmObject
 * To use it with GSON, please see RealmStringDeserializer
 *
 */
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