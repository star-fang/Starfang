package com.fang.starfang;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class FangApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Realm realm;
        try {
            realm = Realm.getDefaultInstance();
            realm.close();
        } catch ( RealmMigrationNeededException e ) {
            Log.d("FANG_APP", e.toString());
            // delete and re-configuration when new table added
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            realm.close();
        }

    }
}
