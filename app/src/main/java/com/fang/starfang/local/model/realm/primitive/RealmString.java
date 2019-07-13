package com.fang.starfang.local.model.realm.primitive;

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

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public boolean isEmpty() { return stringValue.isEmpty(); }

}