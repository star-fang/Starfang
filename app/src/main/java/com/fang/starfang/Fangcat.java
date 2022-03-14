package com.fang.starfang;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class Fangcat extends Application {

    private static final String TAG = "Fang_APP";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        Realm.init(this);
        Realm realm;
        try {
            realm = Realm.getDefaultInstance();
            realm.close();
        } catch ( RealmMigrationNeededException e ) {
            Log.e(TAG,Log.getStackTraceString(e));
            // delete and re-configuration when new table added
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            realm.close();
        }

        Log.d(TAG,"Realm initialized");

        /*
        if( getSharedPreferences(
                FangConstant.SHARED_PREF_STORE,
                Context.MODE_PRIVATE).edit()
                .clear().commit() ) {
            Log.d(TAG,"SharedPreferences initialized");
        } else {
            Log.d(TAG,"fail to clear SharedPreferences");
        }

         */


    }
}
