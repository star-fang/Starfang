package com.fang.starfang;

import android.app.ActionBar;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.local.task.PrefixHandler;
import com.fang.starfang.ui.main.dialog.ConstraintDocBuilder;
import com.fang.starfang.ui.main.dialog.RoomFilterDialogFragment;
import com.fang.starfang.ui.main.dialog.SendCatFilterDialogFragment;
import com.fang.starfang.ui.main.dialog.TimeFilterDatePickerDialog;
import com.fang.starfang.ui.main.recycler.adapter.ConversationRealmAdapter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilterObject;
import com.fang.starfang.util.ScreenUtils;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "FANG_ACTIVITY_CONV";

    private RealmChangeListener<Realm> realmChangeListener;
    private Realm realm;
    private FragmentManager fragmentManager;
    private ConversationFilter conversationFilter;
    private ConversationFilterObject filterObject;
    private ConstraintDocBuilder docBuilder;


    @Override
    protected void onRestart() {
        super.onRestart();
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmChangeListener);
        Log.d(TAG,"_on Restart : add change listener to new realm instance");
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.removeChangeListener(realmChangeListener);
        realm.close();
        Log.d(TAG,"_on Stop : remove realm change listener / realm closed");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch( item.getItemId() ) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_filter_send_cat:
                SendCatFilterDialogFragment.newInstance().show(fragmentManager,TAG);
                break;
            case R.id.menu_item_filter_room:
                RoomFilterDialogFragment.newInstance().show(fragmentManager,TAG);
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
        Log.d(TAG,"_ON CREATE");
        setContentView(R.layout.activity_conversation);
        this.realm = Realm.getDefaultInstance();
        this.fragmentManager = getSupportFragmentManager();


        ActionBar actionBar = getActionBar();
        if( actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final RecyclerView conversation_recycler_view = findViewById(R.id.conversation_recycler_view);
        final ConversationRealmAdapter conversationRecyclerAdapter = new ConversationRealmAdapter( realm );
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversation_recycler_view.setLayoutManager(layoutManager);
        conversation_recycler_view.setAdapter(conversationRecyclerAdapter);
        conversation_recycler_view.scrollToPosition(conversationRecyclerAdapter.getItemCount() - 1);
        this.conversationFilter = ((ConversationFilter)conversationRecyclerAdapter.getFilter());
        this.filterObject = ConversationFilterObject.getInstance();
        conversationFilter.filter("on");

        final AppCompatEditText text_conversation = findViewById(R.id.text_conversation);
        final AppCompatButton button_conversation = findViewById(R.id.button_conversation);
        final AppCompatButton button_clear_conversation = findViewById(R.id.button_clear_conversation);
        final AppCompatTextView text_filter_summary = findViewById(R.id.text_filter_summary);
        final AppCompatButton button_scroll_bottom = findViewById(R.id.button_scroll_bottom);
        this.docBuilder = new ConstraintDocBuilder(realm,text_filter_summary,filterObject);
        docBuilder.build();


        conversation_recycler_view.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        if(adapter == null) {
                            return;
                        }
                        int itemLastPosition = (adapter.getItemCount() - 1);
                        if(itemLastPosition < 0 ) {
                            return;
                        }
                        if(newState == RecyclerView.SCROLL_STATE_IDLE ) {
                            int itemPosition = layoutManager.findLastVisibleItemPosition();
                            if( itemPosition == itemLastPosition ) {
                                button_scroll_bottom.setVisibility( View.GONE );
                            } else if( itemLastPosition > 3 ) {
                                if( itemPosition <  itemLastPosition - 3 ) {
                                    button_scroll_bottom.setVisibility( View.VISIBLE );
                                }
                            }
                        }
                    }
                } );
        button_scroll_bottom.setOnClickListener( v -> {
            RecyclerView.Adapter adapter = conversation_recycler_view.getAdapter();
            if(adapter == null ) {
                return;
            }

            int itemCount = adapter.getItemCount();
            Log.d(TAG,"count: " + itemCount);
            conversation_recycler_view.scrollToPosition(itemCount - 1);
            button_scroll_bottom.setVisibility( View.GONE );
        });

        final View.OnClickListener searchButton_default_listener = v -> {

            Editable editable = text_conversation.getText();
            if( editable == null ) {
                return;
            }
            String text = editable.toString();

            if(text.length() > 1 ) {
                new PrefixHandler(this,null,null, null,true, NotificationListener.getName() ).execute(text);
            } else {
                int itemCount = conversationRecyclerAdapter.getItemCount();
                Log.d(TAG,"count: " + itemCount);
                conversation_recycler_view.scrollToPosition(itemCount - 1);
            }
            text_conversation.setText("");
            ScreenUtils.hideSoftKeyboard(this);
        };
        button_conversation.setOnClickListener(searchButton_default_listener);
        button_clear_conversation.setOnClickListener( v -> text_conversation.setText(""));


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
                } catch (NullPointerException ignored) {
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
            Log.d(TAG,"realm changed!");
            int itemPosition = layoutManager.findLastVisibleItemPosition();
            int itemLastPosition = (conversationRecyclerAdapter.getItemCount() - 1);
            if( itemLastPosition < 0 ) {
                return;
            }

            if( itemPosition > itemLastPosition - 4 ) {
                conversation_recycler_view.scrollToPosition(conversationRecyclerAdapter.getItemCount()-1);
            }
        };
        realm.addChangeListener(realmChangeListener);

    } // end onCreate


}
