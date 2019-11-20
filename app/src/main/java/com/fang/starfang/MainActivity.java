package com.fang.starfang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.fang.starfang.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        AppCompatButton button_setting = findViewById(R.id.button_setting);
        AppCompatButton button_check_realm = findViewById(R.id.button_check_realm);

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        Intent intent = new Intent(this, SettingAcitivity.class);
        button_setting.setOnClickListener( view -> startActivity(intent));

        button_check_realm.setOnClickListener(view -> {
            RealmConfiguration config = Realm.getDefaultConfiguration();
            if( config != null ) {
                int count = Realm.getGlobalInstanceCount(config);
                Snackbar.make(view, "Count of realm instances : " + count, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(view, "Error: realm configuration is null ", Snackbar.LENGTH_LONG).show();
            }


        });



    }
}