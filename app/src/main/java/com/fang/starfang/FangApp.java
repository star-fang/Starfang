package com.fang.starfang;

import android.app.Application;

import io.realm.Realm;

public class FangApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }


}
