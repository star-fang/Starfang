package com.fang.starfang.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Destiny;
import com.fang.starfang.local.model.realm.Spec;
import com.fang.starfang.view.dialog.HeroesDialogFragment;
import com.fang.starfang.view.list.DestinyListAdapter;
import com.fang.starfang.view.list.HeroesListAdapter;
import com.fang.starfang.local.model.realm.Heroes;
import com.fang.starfang.view.list.SpecListAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

public class ShowRealmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_realm);


        Realm realm = Realm.getDefaultInstance();

        String ref_table = getIntent().getStringExtra("ref_table");

        ListView listView = findViewById(R.id.realm_view_list);
        EditText et_search = findViewById(R.id.et_search);


        switch(ref_table) {
            case Heroes.PREF_TABLE:




                RealmResults<Heroes> heroResult = realm.where(Heroes.class).findAll();
                HeroesListAdapter heroAdapter = new HeroesListAdapter(heroResult, realm);
                listView.setAdapter(heroAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Log.d("hero!!",((Heroes)parent.getItemAtPosition(position)).getHeroName());

                        HeroesDialogFragment fragment = HeroesDialogFragment.newInstance((Heroes)parent.getItemAtPosition(position));
                        fragment.show(getSupportFragmentManager(),"dialog");




                    }
                });


                et_search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        heroAdapter.getFilter().filter(s);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


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
