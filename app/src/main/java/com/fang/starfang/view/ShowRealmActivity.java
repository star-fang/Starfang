package com.fang.starfang.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.fang.starfang.R;
import com.fang.starfang.model.realm.Destiny;
import com.fang.starfang.model.realm.Spec;
import com.fang.starfang.view.list.DestinyListAdapter;
import com.fang.starfang.view.list.HeroesListAdapter;
import com.fang.starfang.model.realm.Heroes;
import com.fang.starfang.view.list.SpecListAdapter;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class ShowRealmActivity extends AppCompatActivity {
     private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_realm);
        realm = Realm.getDefaultInstance();

        String ref_table = getIntent().getStringExtra("ref_table");

        ListView listView = findViewById(R.id.realm_view_list);
        switch(ref_table) {
            case Heroes.PREF_TABLE:
                RealmResults<Heroes> heroResult = realm.where(Heroes.class).findAll();
                HeroesListAdapter heroAdapter = new HeroesListAdapter(heroResult);
                listView.setAdapter(heroAdapter);
                break;
            case Destiny.PREF_TABLE:
                RealmResults<Destiny> desResult = realm.where(Destiny.class).findAll();
                DestinyListAdapter destinyAdapter = new DestinyListAdapter(desResult);
                listView.setAdapter(destinyAdapter);
                break;
            case Spec.PREF_TABLE:
                RealmResults<Spec> specResult = realm.where(Spec.class).findAll();
                SpecListAdapter specAdapter = new SpecListAdapter(specResult);
                listView.setAdapter(specAdapter);
                break;
                default:
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
