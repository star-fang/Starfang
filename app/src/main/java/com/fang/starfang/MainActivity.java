package com.fang.starfang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.fang.starfang.ui.main.Fragment.PlaceholderFragment;
import com.fang.starfang.ui.main.SectionsPagerAdapter;
import com.fang.starfang.ui.main.custom.MovableFloatingActionButton;
import com.fang.starfang.ui.main.dialog.AddItemDialogFragment;
import com.fang.starfang.ui.main.dialog.AddRelicDialogFragment;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFloatingRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFloatingRealmAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends AppCompatActivity implements PlaceholderFragment.OnUpdateEventListener {

    private static final String TAG = "FANG_ACTIVITY_MAIN";
    private View view;
    private Realm realm;
    private HeroesFloatingRealmAdapter heroFloatAdapter;
    private HeroesFixedRealmAdapter heroFixAdapter;
    private ItemSimsFloatingRealmAdapter itemFloatAdapter;
    private ItemSimsFixedRealmAdapter itemFixAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_check:
                RealmConfiguration config = Realm.getDefaultConfiguration();
                if (config != null) {
                    int count = Realm.getGlobalInstanceCount(config);
                    Snackbar.make(view, "Count of realm instances : " + count, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, "Error: realm configuration is null ", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.menu_item_chat:
                Intent start_chat = new Intent(this, ConversationActivity.class);
                startActivity(start_chat);
                break;
            case R.id.menu_item_setting:
                Intent start_setting = new Intent(this, SettingActivity.class);
                startActivity(start_setting);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        realm = Realm.getDefaultInstance();
        HeroesFloatingRealmAdapter.setInstance(realm, fragmentManager, this);
        HeroesFixedRealmAdapter.setInstance(realm, fragmentManager);
        ItemSimsFloatingRealmAdapter.setInstance(realm, fragmentManager);
        ItemSimsFixedRealmAdapter.setInstance(realm, fragmentManager);

        heroFloatAdapter = HeroesFloatingRealmAdapter.getInstance();
        heroFixAdapter = HeroesFixedRealmAdapter.getInstance();
        itemFloatAdapter = ItemSimsFloatingRealmAdapter.getInstance();
        itemFixAdapter = ItemSimsFixedRealmAdapter.getInstance();

        setContentView(R.layout.activity_main);
        view = this.findViewById(android.R.id.content).getRootView();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, fragmentManager);
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //final View parent_button_add = view.findViewById(R.id.parent_button_add);
        final View layout_toggle_button_add = view.findViewById(R.id.layout_toggle_button_add);
        final MovableFloatingActionButton button_add = view.findViewById(R.id.button_add);
        //final int color_primary = ContextCompat.getColor(this, R.color.colorPrimary);
        button_add.setOnClickListener(v -> {
            if (layout_toggle_button_add.getVisibility() == View.GONE) {
                layout_toggle_button_add.setVisibility(View.VISIBLE);
                //parent_button_add.setBackgroundColor(color_primary);
            } else {
                layout_toggle_button_add.setVisibility(View.GONE);
                //parent_button_add.setBackgroundColor(0);
            }
        });

        view.findViewById(R.id.button_add_item).setOnClickListener(v -> AddItemDialogFragment.newInstance().show(fragmentManager, TAG));
        view.findViewById(R.id.button_add_relic).setOnClickListener(v -> AddRelicDialogFragment.newInstance().show(fragmentManager, TAG));

    }

    @Override
    public void updateEvent(int code) {
        switch (code) {
            case AppConstant.RESULT_CODE_SUCCESS_ADD_ITEM:
                itemFloatAdapter.notifyDataSetChanged();
                itemFixAdapter.notifyDataSetChanged();
                Log.d(TAG, "보물 추가");
                break;
            case AppConstant.RESULT_CODE_SUCCESS_ADD_HERO:
                heroFloatAdapter.notifyDataSetChanged();
                heroFixAdapter.notifyDataSetChanged();
                Log.d(TAG, "장수 추가");
                break;
            case AppConstant.RESULT_CODE_SUCCESS_MODIFY_HERO:
                heroFloatAdapter.notifyDataSetChanged();
                heroFixAdapter.notifyDataSetChanged();
                Log.d(TAG, "장수 변경");
                break;
            case AppConstant.RESULT_CODE_SUCCESS_MODIFY_ITEM:
                itemFloatAdapter.notifyDataSetChanged();
                itemFixAdapter.notifyDataSetChanged();
                heroFloatAdapter.notifyDataSetChanged();
                heroFixAdapter.notifyDataSetChanged();
                Log.d(TAG, "보물 변경");
                break;
            case AppConstant.RESULT_CODE_SUCCESS_MODIFY_RELIC:
                heroFloatAdapter.notifyDataSetChanged();
                heroFixAdapter.notifyDataSetChanged();
                Log.d(TAG, "보패 변경");
                break;
            default:
        }

    }
}