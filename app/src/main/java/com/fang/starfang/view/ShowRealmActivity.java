package com.fang.starfang.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.fang.starfang.view.recycler.HeroesRecyclerAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

public class ShowRealmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_realm_recycler_view);


       try {
           Realm realm = Realm.getDefaultInstance();

           String ref_table = getIntent().getStringExtra("ref_table");

           RecyclerView recyclerView = findViewById(R.id.realm_recycler_view);
           EditText et_search = findViewById(R.id.et_search);

           RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

           recyclerView.setHasFixedSize(true);
           recyclerView.setItemAnimator(new DefaultItemAnimator());
           recyclerView.setLayoutManager(layoutManager);

           switch (ref_table) {
               case Heroes.PREF_TABLE:

                   HeroesRecyclerAdapter heroAdapter = new HeroesRecyclerAdapter(realm, getSupportFragmentManager());
                   recyclerView.setAdapter(heroAdapter);
                /*
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Log.d("hero!!",((Heroes)parent.getItemAtPosition(position)).getHeroName());

                        HeroesDialogFragment fragment = HeroesDialogFragment.newInstance((Heroes)parent.getItemAtPosition(position));
                        fragment.show(getSupportFragmentManager(),"dialog");

                    }
                });
                */


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
                   break;
               case Spec.PREF_TABLE:
                   break;
               default:
           }

       }catch (RuntimeException ignore) {

       }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
