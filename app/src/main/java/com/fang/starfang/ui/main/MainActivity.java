package com.fang.starfang.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.ui.creative.UpdateDialogFragment;
import com.fang.starfang.ui.setting.SettingActivity;
import com.fang.starfang.ui.creative.MovableFloatingActionButton;
import com.fang.starfang.ui.conversation.ConversationActivity;
import com.fang.starfang.ui.main.dialog.AddItemDialogFragment;
import com.fang.starfang.ui.main.dialog.AddRelicDialogFragment;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SYNC_DB = 0;

    private static final String TAG = "FANG_MAIN";

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
                startActivityForResult(start_setting, REQ_CODE_SYNC_DB);
                break;

            case R.id.menu_item_chat:
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
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        /*
        application context : stable resources
        base context(activity) : activity ui
         */

        setContentView(R.layout.activity_main);
        View view = this.findViewById(android.R.id.content).getRootView();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
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

        view.findViewById(R.id.button_add_item).setOnClickListener(v -> AddItemDialogFragment.newInstance().show(getSupportFragmentManager(), TAG));
        view.findViewById(R.id.button_add_relic).setOnClickListener(v -> AddRelicDialogFragment.newInstance().show(getSupportFragmentManager(), TAG));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //Log.d(TAG, "onActivityResult");
        if (requestCode == REQ_CODE_SYNC_DB && resultCode == SettingActivity.RESULT_CODE_SYNC_SUCCESS) {

            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof UpdateDialogFragment.OnUpdateEventListener && fragment.isVisible()) {
                    Log.d(TAG, "sync: " + fragment.getClass().getName());
                    ((UpdateDialogFragment.OnUpdateEventListener) fragment).updateEvent(
                            FangConstant.RESULT_CODE_SUCCESS
                            , null, null);
                }
            }

        }
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

}