package com.fang.starfang;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.task.PrefixHandler;
import com.fang.starfang.ui.main.recycler.adapter.ConversationRealmAdapter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilterObject;
import com.fang.starfang.ui.main.recycler.adapter.RoomFilterRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.SendCatFilterRealmAdapter;
import com.fang.starfang.util.ScreenUtils;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "FANG_ACTIVITY_CONV";
    private static final String SUMMARY_TIME_FORMAT = "yyyy년 MM월 dd일";
    private RealmChangeListener<Realm> realmChangeListener;
    private Realm realm;


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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"_ON CREATE");
        setContentView(R.layout.activity_conversation);
        realm = Realm.getDefaultInstance();


        ActionBar actionBar = getActionBar();
        if( actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final RecyclerView conversation_recycler_view = findViewById(R.id.conversation_recycler_view);
        final ConversationRealmAdapter conversationRecyclerAdapter = new ConversationRealmAdapter( realm );
        final View filter_summary_layout = findViewById(R.id.filter_summary_layout);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversation_recycler_view.setLayoutManager(layoutManager);
        conversation_recycler_view.setAdapter(conversationRecyclerAdapter);
        conversation_recycler_view.scrollToPosition(conversationRecyclerAdapter.getItemCount() - 1);
        final ConversationFilter conversationFilter = ((ConversationFilter)conversationRecyclerAdapter.getFilter());
        final ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        conversationFilter.filter("on");

        final AppCompatTextView title_conversation = findViewById(R.id.title_conversation);
        final AppCompatEditText text_conversation = findViewById(R.id.text_conversation);
        final AppCompatButton button_conversation = findViewById(R.id.button_conversation);
        final AppCompatButton button_clear_conversation = findViewById(R.id.button_clear_conversation);
        final View inner_column_filter = findViewById((R.id.inner_column_filter));
        final AppCompatTextView text_filter_summary = findViewById(R.id.text_filter_summary);
        final AppCompatButton button_summary_filter = findViewById(R.id.button_summary_filter);
        final View scroll_summary_filter = findViewById(R.id.scroll_summary_filter);
        final AppCompatButton button_hide_filters = findViewById(R.id.button_hide_filters);
        final AppCompatButton button_show_filters = findViewById(R.id.button_show_filters);
        final AppCompatButton button_scroll_bottom = findViewById(R.id.button_scroll_bottom);
        final View row_filter = findViewById(R.id.row_filter);
        buildConstraintDoc( realm, text_filter_summary);

        button_summary_filter.setOnClickListener(v -> {
            if( scroll_summary_filter.getLayoutParams().height == 0 ) {
                changeLayoutSize(scroll_summary_filter, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            } else {
                changeLayoutSize(scroll_summary_filter, ScreenUtils.dip2pix(this,120), 0);
            }
        });

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
                                         }

        );
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
            String botMode = getString(R.string.tab_text_3);

            if( botMode.equals(title_conversation.getText().toString() )) {
                if(text.length() > 1 ) {
                    new PrefixHandler(this,null,null, null,true, NotificationListener.getName() ).execute(text);
                } else {
                    int itemCount = conversationRecyclerAdapter.getItemCount();
                    Log.d(TAG,"count: " + itemCount);
                    conversation_recycler_view.scrollToPosition(itemCount - 1);
                }
                text_conversation.setText("");

            } else {
                ScreenUtils.hideSoftKeyboard(this);
            }


        };
        button_conversation.setOnClickListener(searchButton_default_listener);
        button_clear_conversation.setOnClickListener( v -> text_conversation.setText(""));

        final View.OnClickListener showButton_default_listener = v -> {
            button_show_filters.setVisibility(View.GONE);
            button_hide_filters.setVisibility(View.VISIBLE);
            changeLayoutSize(row_filter,-5,ScreenUtils.dip2pix(this,100));
        };

        button_show_filters.setOnClickListener(showButton_default_listener);

        final View.OnClickListener hideButton_default_listener = v -> {
            button_hide_filters.setVisibility(View.GONE);
            button_show_filters.setVisibility(View.VISIBLE);
            changeLayoutSize(row_filter,-5,0);
        };
        button_hide_filters.setOnClickListener(hideButton_default_listener);
        final SendCatFilterRealmAdapter sendCatFilterRealmAdapter =
                new SendCatFilterRealmAdapter(realm,
                        findViewById(R.id.text_filter_sendCat_count),
                        findViewById(R.id.text_filter_sendCat_count_desc));
        final AppCompatButton button_filter_sendCat =  findViewById(R.id.button_filter_sendCat);
        final View inner_column_filter_sendCat = findViewById(R.id.inner_column_filter_sendCat);
        final AppCompatButton button_filter_sendCat_commit = findViewById(R.id.button_filter_sendCat_commit);

        if( filterObject.isCatChecked()) {
            button_filter_sendCat.setBackgroundResource(R.drawable.round_button_checked);
        }

        button_filter_sendCat.setOnLongClickListener( view -> {
            filter_summary_layout.setVisibility(View.INVISIBLE);
            conversation_recycler_view.setAdapter(sendCatFilterRealmAdapter);
            changeLayouWeight(inner_column_filter,0.0f);
            changeLayouWeight(inner_column_filter_sendCat,1.0f);
            title_conversation.setText(R.string.filter_send_cat);
            return true;
        });
        button_filter_sendCat.setOnClickListener( view -> {
            boolean checked =  filterObject.isCatChecked();
            if( checked ) {
                filterObject.setCheckCat(false);
                conversationFilter.filter("on");
                button_filter_sendCat.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"작성자 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckCat(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getSendCatCount()>0;
                if( check ) {
                    button_filter_sendCat.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(view,"작성자 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckCat(false);
                    Snackbar.make(view,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
                }
            }
            buildConstraintDoc( realm, text_filter_summary);
        });
        button_filter_sendCat_commit.setOnClickListener( view -> {
            filter_summary_layout.setVisibility(View.VISIBLE);
            filterObject.setCheckCat(true);
            conversationFilter.filter("on");
            boolean check = filterObject.getSendCatCount()>0;
            if( check ) {
                button_filter_sendCat.setBackgroundResource(R.drawable.round_button_checked);
                Snackbar.make(view,"작성자 필터 ON",Snackbar.LENGTH_SHORT).show();
            } else {
                button_filter_sendCat.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"작성자 필터 OFF",Snackbar.LENGTH_SHORT).show();
            }
            filterObject.setCheckCat(check);
            conversation_recycler_view.setAdapter(conversationRecyclerAdapter);
            changeLayouWeight(inner_column_filter,1.0f);
            changeLayouWeight(inner_column_filter_sendCat,0.0f);
            buildConstraintDoc( realm, text_filter_summary);
            title_conversation.setText(R.string.tab_text_3);

        });
        final RoomFilterRealmAdapter roomFilterRealmAdapter =
                new RoomFilterRealmAdapter(realm,
                        findViewById(R.id.text_filter_room_count),
                        findViewById(R.id.text_filter_room_count_desc));
        final AppCompatButton button_filter_room =  findViewById(R.id.button_filter_room);
        final View inner_column_filter_room = findViewById(R.id.inner_column_filter_room);
        final AppCompatButton button_filter_room_commit = findViewById(R.id.button_filter_room_commit);

        if( filterObject.isRoomChecked()) {
            button_filter_room.setBackgroundResource(R.drawable.round_button_checked);
        }


        button_filter_room.setOnLongClickListener( view -> {
            filter_summary_layout.setVisibility(View.INVISIBLE);
            conversation_recycler_view.setAdapter(roomFilterRealmAdapter);
            changeLayouWeight(inner_column_filter,0.0f);
            changeLayouWeight(inner_column_filter_room,1.0f);
            title_conversation.setText(R.string.filter_room);
            return true;
        });
        button_filter_room.setOnClickListener( view -> {
            boolean checked =  filterObject.isRoomChecked();
            if( checked ) {
                filterObject.setCheckRoom(false);
                conversationFilter.filter("on");
                button_filter_room.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"단톡방 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckRoom(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getRoomCount()>0;
                if( check ) {
                    button_filter_room.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(view,"단톡방 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckRoom(false);
                    Snackbar.make(view,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
                }
            }
            buildConstraintDoc( realm, text_filter_summary);
        });
        button_filter_room_commit.setOnClickListener( view -> {
            filter_summary_layout.setVisibility(View.VISIBLE);
            filterObject.setCheckRoom(true);
            conversationFilter.filter("on");
            boolean check = filterObject.getRoomCount()>0;
            if( check ) {
                button_filter_room.setBackgroundResource(R.drawable.round_button_checked);
                Snackbar.make(view,"단톡방 필터 ON",Snackbar.LENGTH_SHORT).show();
            } else {
                button_filter_room.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"단톡방 필터 OFF",Snackbar.LENGTH_SHORT).show();
            }
            filterObject.setCheckRoom(check);
            conversation_recycler_view.setAdapter(conversationRecyclerAdapter);

            changeLayouWeight(inner_column_filter,1.0f);
            changeLayouWeight(inner_column_filter_room,0.0f);
            buildConstraintDoc( realm, text_filter_summary);
            title_conversation.setText(R.string.tab_text_3);

        });

        final AppCompatButton button_filter_time = findViewById(R.id.button_filter_time);

        if( filterObject.isTimeChecked()) {
            button_filter_time.setBackgroundResource(R.drawable.round_button_checked);
        }
        button_filter_time.setOnLongClickListener( v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    R.style.DatePickerDialogTheme, null,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)) {
                private boolean allowClose = false;
                @Override
                public void show() {
                    setTitle("시작 날짜를 선택 하세요!");
                    setButton(DialogInterface.BUTTON_POSITIVE, "완료",
                            (dialog, which) -> {
                                long dateValue = getDateValueFromDatePicker(getDatePicker());
                                filterObject.setTime_before(dateValue);
                                conversationFilter.filter("on");
                                buildConstraintDoc( realm, text_filter_summary);
                                button_filter_time.setBackgroundResource(R.drawable.round_button_checked);
                                //Snackbar.make(child_conversation,"기간 필터 ON",Snackbar.LENGTH_SHORT).show();
                                allowClose = true;
                            });

                    setButton(DialogInterface.BUTTON_NEGATIVE, "취소",
                            (dialog, which) -> allowClose = true);

                    setButton(DialogInterface.BUTTON_NEUTRAL, "선택",
                            (dialog, which) -> {
                                getButton(which).setVisibility(View.INVISIBLE);
                                getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                                long dateValue = getDateValueFromDatePicker(getDatePicker());
                                filterObject.setTime_after(getDateValueFromDatePicker(getDatePicker()));
                                filterObject.setCheckTime(true);
                                conversationFilter.filter("on");
                                button_filter_time.setBackgroundResource(R.drawable.round_button_checked);
                                buildConstraintDoc(  realm, text_filter_summary);
                                getDatePicker().setMinDate(dateValue);
                                setTitle("종료 날짜를 선택 하세요.");
                            } );
                    super.show();
                    getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
                }

                @Override
                public void dismiss() {
                    if (allowClose) {
                        Log.d(TAG,"dismiss : closing allowed");
                        super.dismiss();
                    } else {
                        Log.d(TAG,"dismiss : closing not allowed");
                    }
                }

            };

            datePickerDialog.setCancelable(false);
            datePickerDialog.show();

            return true;
        } );
        button_filter_time.setOnClickListener( view -> {
            boolean checked = filterObject.isTimeChecked();
            if(checked) {
                filterObject.setCheckTime(false);
                conversationFilter.filter("on");
                button_filter_time.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(view,"기간 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckTime(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getTime_before() >= 0;
                if( check ) {
                    button_filter_time.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(view,"기간 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckTime(false);
                    Snackbar.make(view,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
                }
            }
            buildConstraintDoc( realm, text_filter_summary);
        });

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

        realmChangeListener = o -> {
            conversationRecyclerAdapter.notifyDataSetChanged();
            sendCatFilterRealmAdapter.notifyDataSetChanged();
            roomFilterRealmAdapter.notifyDataSetChanged();
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

    private long getDateValueFromDatePicker(DatePicker picker) {
        Calendar calendar_gregorian = new GregorianCalendar(
                picker.getYear(),
                picker.getMonth(),
                picker.getDayOfMonth());
        Date date = calendar_gregorian.getTime();
        return date.getTime();
    }

    private void changeLayouWeight(View view, float weight) {

        try {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (weight >= 0) {
                params.weight = weight;
            }

            view.setLayoutParams(params);
        } catch (ClassCastException ignored) {
        }
    }

    private void changeLayoutSize(View view, int width, int height) {

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(width > -5 ) {
            params.width = width;
        }

        if(height >= -5 ) {
            params.height = height;
        }

        view.setLayoutParams(params);
    }






    private void buildConstraintDoc(Realm realm, AppCompatTextView summaryView) {
        ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SUMMARY_TIME_FORMAT, Locale.KOREA);
        String[] cs_cats = filterObject.getSendCats();
        String[] cs_rooms = filterObject.getRooms();
        long time_after = filterObject.getTime_after();
        long time_before = filterObject.getTime_before();
        String[] cs_convs = filterObject.getConversations();
        String[] cs_packs = filterObject.getPackages();

        StringBuilder stringBuilder = new StringBuilder();
        if(cs_cats != null) {
            if (cs_cats.length > 0 && filterObject.isCatChecked()) {
                RealmQuery<Conversation> query_cats = realm.where(Conversation.class).alwaysFalse();
                for(String cs_cat : cs_cats) {
                    query_cats.or().contains(Conversation.FIELD_SENDCAT, cs_cat);
                }
                RealmResults<Conversation> realmResults_cats = query_cats.distinct(Conversation.FIELD_SENDCAT).findAll();
                stringBuilder.append(" ★작성자:  ").append(realmResults_cats.size()).append("명").append("\n");
                for(Conversation conversation : realmResults_cats) {
                    stringBuilder.append("  - ").append(conversation.getSendCat()).append("\n");
                }
                stringBuilder.append("\n");
            }
        }

        if(cs_rooms != null) {
            if (cs_rooms.length > 0 && filterObject.isRoomChecked()) {
                RealmQuery<Conversation> query_rooms = realm.where(Conversation.class).alwaysFalse();
                for(String cs_room : cs_rooms) {
                    query_rooms.or().contains(Conversation.FIELD_ROOM, cs_room);
                }
                RealmResults<Conversation> realmResults_rooms = query_rooms.distinct(Conversation.FIELD_ROOM).findAll();
                stringBuilder.append(" ★단톡방:  ").append(realmResults_rooms.size()).append("개").append("\n");
                for(Conversation conversation : realmResults_rooms) {
                    stringBuilder.append("  - ").append(conversation.getCatRoom()).append("\n");
                }
                stringBuilder.append("\n");
            }
        }

        if(( time_after > 0  || time_before > 0) && filterObject.isTimeChecked()) {
            stringBuilder.append("★기간:\n");
            if (time_after > 0) {
                stringBuilder.append("    ").append(simpleDateFormat.format(new Date(time_after))).append("\n");
            } else {
                stringBuilder.append("    2016년 10월 6일\n");
            }

            if (time_before > 0) {
                stringBuilder.append("    ~ ").append(simpleDateFormat.format(new Date(time_before))).append("\n");
            } else {
                stringBuilder.append("    ~ 2026년 10월 6일\n");
            }
            stringBuilder.append("\n");
        }

        if(cs_convs != null) {
            if(cs_convs.length>0 && filterObject.isConvChecked()) {
                stringBuilder.append("★단어 포함:").append("\n");
                for(String conv : cs_convs) {
                    stringBuilder.append("  - ").append(conv).append("\n");
                }
                stringBuilder.append("\n");
            }

        }

        if(cs_packs != null) {
            if(cs_packs.length>0) {
                stringBuilder.append("\n ★메신저:").append("\n");
                for(String pack : cs_packs) {
                    stringBuilder.append("  ").append(pack).append("\n");
                }
                stringBuilder.append("\n");
            }

        }

        String resultStr = stringBuilder.toString();
        resultStr = resultStr.equals("")? " 필터 설정 안됨..." : resultStr.substring(0,resultStr.length()-2);
        summaryView.setText(resultStr);

    }



}
