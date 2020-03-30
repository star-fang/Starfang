package com.fang.starfang.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.ui.setting.SettingActivity;
import com.fang.starfang.ui.common.MovableFloatingActionButton;
import com.fang.starfang.ui.conversation.ConversationActivity;
import com.fang.starfang.ui.main.dialog.AddItemDialogFragment;
import com.fang.starfang.ui.main.dialog.AddRelicDialogFragment;
import com.fang.starfang.ui.common.UpdateDialogFragment;
import com.fang.starfang.ui.main.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.adapter.HeroesFloatingRealmAdapter;
import com.fang.starfang.ui.main.adapter.ItemSimsFixedRealmAdapter;
import com.fang.starfang.ui.main.adapter.ItemSimsFloatingRealmAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity implements UpdateDialogFragment.OnUpdateEventListener {

    private static final String TAG = "FANG_ACTIVITY_MAIN";
    private View view;
    private Realm realm;
    private HeroesFloatingRealmAdapter heroFloatAdapter;
    private HeroesFixedRealmAdapter heroFixAdapter;
    private ItemSimsFloatingRealmAdapter itemFloatAdapter;
    private ItemSimsFixedRealmAdapter itemFixAdapter;
    private boolean dialogIsBeingShown;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_setting:
            Intent start_setting = new Intent(this, SettingActivity.class);
            startActivity(start_setting);
            break;

            case R.id.menu_item_chat:
                if(realm.isInTransaction()) {
                    realm.commitTransaction();
                }
                Intent start_chat = new Intent(this, ConversationActivity.class);
                startActivity(start_chat);
                break;
                default:

                /*
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
                if(realm.isInTransaction()) {
                    realm.commitTransaction();
                }
                Intent start_chat = new Intent(this, ConversationActivity.class);
                startActivity(start_chat);
                break;

            case R.id.menu_item_sheet:
                Intent start_sheet = new Intent(this, SheetsActivity.class);
                startActivity(start_sheet);
                break;
                 */
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

        dialogIsBeingShown = false;
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
    public void updateEvent(int code, String message) {
        int notifyType;
        switch (code) {
            case FangConstant.RESULT_CODE_SUCCESS_ADD_ITEM:
                notifyType = FangConstant.NOTIFY_TYPE_ITEM;
                notifyToAdapter(notifyType);
                Log.d(TAG, "item added:" + message);
                break;
            case FangConstant.RESULT_CODE_SUCCESS_ADD_HERO:
                notifyType = FangConstant.NOTIFY_TYPE_HERO;
                notifyToAdapter(notifyType);
                Log.d(TAG, "hero added:" + message);
                break;
            case FangConstant.RESULT_CODE_SUCCESS_MODIFY_HERO:
                notifyType = FangConstant.NOTIFY_TYPE_HERO;
                notifyToAdapter(notifyType);
                Log.d(TAG, "hero modified:" + message);
                break;
            case FangConstant.RESULT_CODE_SUCCESS_MODIFY_ITEM:
                notifyType = FangConstant.NOTIFY_TYPE_ITEM_HERO;
                notifyToAdapter(notifyType);
                Log.d(TAG, "item modified:" + message);
                break;
            case FangConstant.RESULT_CODE_SUCCESS_MODIFY_RELIC:
                notifyType = FangConstant.NOTIFY_TYPE_HERO;
                notifyToAdapter(notifyType);
                Log.d(TAG, "relic modified:" + message);
                break;
            default:
        } // end switch

        if(message != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
            //showCancelableSnackBar(view, message,notifyType);
        }

    }

    @Override
    public boolean dialogAttached() {
        if(dialogIsBeingShown) {
            return true;
        } else {
            dialogIsBeingShown = true;
            return false;
        }
    }

    @Override
    public void dialogDetached() {
        dialogIsBeingShown = false;
    }

    /*
    private void showCancelableSnackBar(View view, String message, int notifyType) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.cancel_transaction_kor, v -> {
           if( realm.isInTransaction() ) {
               realm.cancelTransaction();
               notifyToAdapter(notifyType);
               Toast.makeText(this,R.string.transaction_canceled_kor,Toast.LENGTH_SHORT).show();
           } else {
               Log.d(TAG, "cancelTransaction failure");
               Toast.makeText(this,R.string.transaction_cancel_failure_kor,Toast.LENGTH_SHORT).show();
           }
        });

        snackbar.addCallback( new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if(realm.isInTransaction()) {
                    realm.commitTransaction();
                    Log.d(TAG, "commitTransaction");
                } else {
                    Log.d(TAG, "commitTransaction failure");
                }
            }
        });

        snackbar.show();
    }

     */

    private void notifyToAdapter(int notifyType) {
        switch( notifyType ) {
            case FangConstant.NOTIFY_TYPE_ITEM:
                itemFloatAdapter.notifyDataSetChanged();
                itemFixAdapter.notifyDataSetChanged();
                break;
            case FangConstant.NOTIFY_TYPE_HERO:
                heroFloatAdapter.notifyDataSetChanged();
                heroFixAdapter.notifyDataSetChanged();
                break;
            case FangConstant.NOTIFY_TYPE_RELIC:
                break;
            case FangConstant.NOTIFY_TYPE_ITEM_HERO:
                itemFloatAdapter.notifyDataSetChanged();
                itemFixAdapter.notifyDataSetChanged();
                heroFloatAdapter.notifyDataSetChanged();
                heroFixAdapter.notifyDataSetChanged();
            default:
        }
    }
}