package com.fang.starfang.ui.conversation;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.service.FangcatReceiver;
import com.fang.starfang.ui.creative.UpdateDialogFragment;
import com.fang.starfang.ui.conversation.adapter.ConversationFilter;
import com.fang.starfang.ui.conversation.adapter.ConversationFilterObject;
import com.fang.starfang.ui.conversation.adapter.ConversationRealmAdapter;
import com.fang.starfang.ui.conversation.dialog.RoomFilterDialogFragment;
import com.fang.starfang.ui.conversation.dialog.SendCatFilterDialogFragment;
import com.fang.starfang.ui.conversation.dialog.TimeFilterDatePickerDialog;
import com.fang.starfang.util.ScreenUtils;
import com.fang.starfang.util.VersionUtils;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.Sort;

public class ConversationActivity extends AppCompatActivity implements UpdateDialogFragment.OnUpdateEventListener {

    private static final String TAG = "FANG_ACTIVITY_CONV";

    private RealmChangeListener<Realm> realmChangeListener;
    private Realm realm;
    private FragmentManager fragmentManager;
    private ConversationFilter conversationFilter;
    private ConversationFilterObject filterObject;
    private ConstraintDocBuilder docBuilder;
    private ConversationRealmAdapter conversationRecyclerAdapter;

    private NotificationManager mNM;
    private static final int NOTIFICATION_ID = 202;
    private static final String CHANNEL_ID = "channel-fangcat";
    private static final String CHANNEL_NAME = "fangcat";

    @Override
    protected void onRestart() {
        super.onRestart();
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmChangeListener);
        Log.d(TAG, "_on Restart : add change listener to new realm instance");
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.removeChangeListener(realmChangeListener);
        realm.close();
        Log.d(TAG, "_on Stop : remove realm change listener / realm closed");
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION_ID);
        Log.d(TAG, "_on Destroy : remove notification");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_filter_send_cat:
                SendCatFilterDialogFragment.newInstance().show(fragmentManager, TAG);
                break;
            case R.id.menu_item_filter_room:
                RoomFilterDialogFragment.newInstance().show(fragmentManager, TAG);
                break;
            case R.id.menu_item_filter_time:
                Calendar calendar = Calendar.getInstance();
                TimeFilterDatePickerDialog datePickerDialog = new TimeFilterDatePickerDialog(this,
                        R.style.DatePickerDialogTheme, null,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        filterObject,
                        conversationFilter,
                        docBuilder);
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
                break;
            case R.id.menu_item_filter_word:
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "_ON CREATE");
        setContentView(R.layout.activity_conversation);
        this.realm = Realm.getDefaultInstance();
        this.fragmentManager = getSupportFragmentManager();
        this.mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (VersionUtils.isOreo()) {
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            mNM.createNotificationChannel(mChannel);
            Log.d(TAG, "Notification channel created");
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final RecyclerView conversation_recycler_view = findViewById(R.id.conversation_recycler_view);
        conversationRecyclerAdapter = new ConversationRealmAdapter(
                realm.where(Conversation.class).findAll().sort(Conversation.FIELD_TIME_VALUE, Sort.ASCENDING)
        );
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversation_recycler_view.setLayoutManager(layoutManager);
        conversation_recycler_view.setAdapter(conversationRecyclerAdapter);
        conversation_recycler_view.scrollToPosition(conversationRecyclerAdapter.getItemCount() - 1);
        this.conversationFilter = ((ConversationFilter) conversationRecyclerAdapter.getFilter());
        this.filterObject = ConversationFilterObject.getInstance();
        conversationFilter.filter("on");

        final AppCompatEditText text_conversation = findViewById(R.id.text_conversation);
        final AppCompatButton button_conversation = findViewById(R.id.button_conversation);
        final AppCompatButton button_clear_conversation = findViewById(R.id.button_clear_conversation);
        final AppCompatTextView text_filter_summary = findViewById(R.id.text_filter_summary);
        final AppCompatButton button_scroll_bottom = findViewById(R.id.button_scroll_bottom);
        this.docBuilder = new ConstraintDocBuilder(realm, text_filter_summary, filterObject);
        docBuilder.build();


        conversation_recycler_view.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        if (adapter == null) {
                            return;
                        }
                        int itemLastPosition = (adapter.getItemCount() - 1);
                        if (itemLastPosition < 0) {
                            return;
                        }
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            int itemPosition = layoutManager.findLastVisibleItemPosition();
                            if (itemPosition == itemLastPosition) {
                                button_scroll_bottom.setVisibility(View.GONE);
                            } else if (itemLastPosition > 3) {
                                if (itemPosition < itemLastPosition - 3) {
                                    button_scroll_bottom.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
        button_scroll_bottom.setOnClickListener(v -> {
            RecyclerView.Adapter adapter = conversation_recycler_view.getAdapter();
            if (adapter == null) {
                return;
            }

            int itemCount = adapter.getItemCount();
            Log.d(TAG, "count: " + itemCount);
            conversation_recycler_view.scrollToPosition(itemCount - 1);
            button_scroll_bottom.setVisibility(View.GONE);
        });

        final View.OnClickListener searchButton_default_listener = v -> {

            Editable editable = text_conversation.getText();
            if (editable == null) {
                return;
            }
            String text = editable.toString();

            if (text.length() > 0) {

                Resources resources = getResources();
                SharedPreferences sharedPref = getSharedPreferences(
                        FangConstant.SHARED_PREF_STORE,
                        Context.MODE_PRIVATE
                );
                String botName = sharedPref.getString(
                        FangConstant.BOT_NAME_KEY,
                        resources.getString(R.string.bot_name_default));
                Intent fangcatIntent = new Intent(this, FangcatReceiver.class);
                Bundle information = new Bundle();
                information.putString(Notification.EXTRA_TITLE, botName);
                information.putString(Notification.EXTRA_SUB_TEXT, resources.getString(R.string.notify_debug_room));
                fangcatIntent.putExtra(FangConstant.EXTRA_INFORMATION,information);

                String replyLabel = resources.getString(R.string.label_reply);

                RemoteInput remoteInput = new RemoteInput.Builder(FangConstant.REPLY_KEY_LOCAL)
                        .setLabel(replyLabel)
                        .build();

                PendingIntent replyPendingIntent =
                        PendingIntent.getBroadcast(getApplicationContext(),
                                0, fangcatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action replyAction =
                        new NotificationCompat.Action.Builder(R.drawable.ic_sentiment_very_satisfied_black_24dp,
                                replyLabel, replyPendingIntent)
                                .addRemoteInput(remoteInput)
                                .setAllowGeneratedReplies(true)
                                .build();

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(resources.getString(R.string.notify_debug_who))
                        .setContentText(text)
                        .setSubText(resources.getString(R.string.notify_debug_room))
                        .addAction(replyAction)
                        .build();
                mNM.notify(NOTIFICATION_ID, notification);

            } else {
                int itemCount = conversationRecyclerAdapter.getItemCount();
                Log.d(TAG, "count: " + itemCount);
                conversation_recycler_view.scrollToPosition(itemCount - 1);
            }
            text_conversation.setText("");
            ScreenUtils.hideSoftKeyboard(this);
        };
        button_conversation.setOnClickListener(searchButton_default_listener);
        button_clear_conversation.setOnClickListener(v -> text_conversation.setText(""));


        /*



        final AppCompatButton button_filter_conversation =  findViewById(R.id.button_filter_conversation);
        final RelativeLayout inner_column_filter_conversation = findViewById(R.id.inner_column_filter_conversation);
        final AppCompatTextView text_input_filter_conversation = findViewById(R.id.text_input_filter_conversation);
        final AppCompatButton button_filter_conversation_commit =  findViewById(R.id.button_filter_conversation_commit);
        final TextWatcher textWatcher_conversation = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String filterStr_conv = s.toString();
                    text_input_filter_conversation.setText(filterStr_conv);
                    filterObject.setConversations(filterStr_conv.split(","));
                    conversationFilter.filter("on");
                } catch (NullPointerException e) {
                Log.e(TAG,Log.getStackTraceString(e));
                }
            }
        };

        if( filterObject.isConvChecked()) {
            button_filter_conversation.setBackgroundResource(R.drawable.round_button_checked);
        }


         */


        /*
        button_filter_conversation.setOnLongClickListener( v -> {
            filterObject.setCheckConv(true);
            text_conversation.addTextChangedListener(textWatcher_conversation);
            ScreenUtils.showSoftKeyboard(this, text_conversation);
            changeLayouWeight(inner_column_filter,0.0f);
            changeLayouWeight(inner_column_filter_conversation,1.0f);
            title_conversation.setText(R.string.filter_conversation);
            return true;
        } );

        button_filter_conversation.setOnClickListener( view -> {
            boolean checked = filterObject.isConvChecked();
            if(checked) {
                filterObject.setCheckConv(false);
                conversationFilter.filter("on");
                button_filter_conversation.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"단어 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckConv(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getConvCount() > 0;
                if( check ) {
                    button_filter_conversation.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(view,"단어 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckConv(false);
                    Snackbar.make(view,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
                }
            }
            buildConstraintDoc( realm, text_filter_summary);
        });


        button_filter_conversation_commit.setOnClickListener( view -> {

            ScreenUtils.hideSoftKeyboard(this);
            String text = text_input_filter_conversation.getText().toString();
            text= text.trim();
            text_conversation.removeTextChangedListener(textWatcher_conversation);
            text_conversation.setText("");

            if(text.equals("") ) {
                filterObject.setCheckConv(false);
                filterObject.setConversations(new String[]{});
                button_filter_conversation.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"단어 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                //filterObject.setCheckConv(true);
                filterObject.setConversations(text.split(","));
                conversationFilter.filter("on");
                if( filterObject.getConvCount() > 0) {
                    button_filter_conversation.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(view,"단어 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckConv(false);
                    button_filter_conversation.setBackgroundResource(R.drawable.round_button);
                    Snackbar.make(view,"단어 필터 OFF",Snackbar.LENGTH_SHORT).show();

                }
            }

            buildConstraintDoc(  realm, text_filter_summary);
            changeLayouWeight(inner_column_filter,1.0f);
            changeLayouWeight(inner_column_filter_conversation,0.0f);


            title_conversation.setText(R.string.tab_text_3);

        });


         */

        realmChangeListener = o -> {
            conversationRecyclerAdapter.notifyDataSetChanged();
            //sendCatFilterRealmAdapter.notifyDataSetChanged();
            //roomFilterRealmAdapter.notifyDataSetChanged();
            Log.d(TAG, "realm changed!");
            int itemPosition = layoutManager.findLastVisibleItemPosition();
            int itemLastPosition = (conversationRecyclerAdapter.getItemCount() - 1);
            if (itemLastPosition < 0) {
                return;
            }

            if (itemPosition > itemLastPosition - 4) {
                conversation_recycler_view.scrollToPosition(conversationRecyclerAdapter.getItemCount() - 1);
            }
        };
        realm.addChangeListener(realmChangeListener);

    } // end onCreate


    @Override
    public void updateEvent(int resultCode, String message, int[] pos) {
        if (resultCode == FangConstant.RESULT_CODE_SUCCESS) {
            conversationRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
