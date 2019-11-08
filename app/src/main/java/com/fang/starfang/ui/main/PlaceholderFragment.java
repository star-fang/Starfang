package com.fang.starfang.ui.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.NotificationListener;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.task.RealmSyncTask;
import com.fang.starfang.view.recycler.ConversationFilter;
import com.fang.starfang.view.recycler.ConversationRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "FANG_MAIN";

    private PageViewModel pageViewModel;
    private Activity mActivity;

    @SuppressWarnings("WeakerAccess")
    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            mActivity=(Activity) context;
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        /*냥봇 설정 파트*/
        final ConstraintLayout constraintLayout = root.findViewById(R.id.constraintLayout);
        final View child_setting = inflater.inflate(R.layout.view_setting,constraintLayout,false);

        final Switch switch_bot = child_setting.findViewById(R.id.notifications_start);
        final EditText text_address = child_setting.findViewById(R.id.text_address);
        final Button button_sync_all = child_setting.findViewById(R.id.start_sync_key_all);
        final Button button_notifications_setting= child_setting.findViewById(R.id.notifications_setting);
        text_address.setEnabled(false);
        text_address.setInputType(InputType.TYPE_NULL);

        button_sync_all.setOnClickListener(v -> new RealmSyncTask(text_address.getText().toString(), mActivity).execute(getResources().getStringArray(R.array.pref_list_table)));

        button_notifications_setting.setOnClickListener(v -> {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } );


        switch_bot.setChecked(NotificationListener.getStatus().equals("start"));

        switch_bot.setOnCheckedChangeListener((v,isChecked)->{

            Intent intent = new Intent(mActivity, NotificationListener.class);
            if(isChecked) {
                if(!isMyServiceRunning()) {
                    Toast.makeText(mActivity, "알림 읽기 권한 설정 하세요", Toast.LENGTH_SHORT).show();
                    switch_bot.setChecked(false);
                } else {
                    intent.putExtra("status", "start");
                    Toast.makeText(mActivity, "냥봇 시작", Toast.LENGTH_SHORT).show();
                    mActivity.startService(intent);
                }
            } else {
                intent.putExtra("status","stop");
                Toast.makeText(mActivity, "냥봇 중지", Toast.LENGTH_SHORT).show();
                mActivity.startService(intent);
            }
        });

        /*대화 파트*/
        Realm realm = Realm.getDefaultInstance();
        final View child_conversation = inflater.inflate(R.layout.view_conversation,constraintLayout,false);
        RecyclerView recyclerView = child_conversation.findViewById(R.id.conversation_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        final ConversationRecyclerAdapter conversationRecyclerAdapter = new ConversationRecyclerAdapter( realm, recyclerView );
        recyclerView.setAdapter(conversationRecyclerAdapter);
        final ConversationFilter conversationFilter = ((ConversationFilter)conversationRecyclerAdapter.getFilter());
        final ConversationFilter.ConversationFilterObject filterObject = conversationFilter.getConversationFilterObject();

        final AppCompatTextView title_conversation = child_conversation.findViewById(R.id.title_conversation);
        final AppCompatEditText text_conversation = child_conversation.findViewById(R.id.text_conversation);
        final AppCompatButton button_conversation = child_conversation.findViewById(R.id.button_conversation);
        final AppCompatButton button_clear_conversation = child_conversation.findViewById(R.id.button_clear_conversation);
        final View row_filter = child_conversation.findViewById((R.id.row_filter));
        final View scroll_filter = child_conversation.findViewById(R.id.scroll_filter);
        final AppCompatButton button_filter_summary = child_conversation.findViewById(R.id.button_filter_summary);
        final AppCompatTextView text_filter_summary = child_conversation.findViewById(R.id.text_filter_summary);
        final SwitchCompat switch_filter = child_conversation.findViewById(R.id.switch_filter);
        final AppCompatButton button_hide_filters = child_conversation.findViewById(R.id.button_hide_filters);

        button_clear_conversation.setOnClickListener( v -> text_conversation.setText(""));

        //button_filter_summary.setOnClickListener( v -> Log.d(TAG, buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary)));

        switch_filter.setOnCheckedChangeListener( (v, b)-> {

            if(b) {
                if(button_hide_filters.getLayoutParams().width == 0 ) {
                    toggleLayoutWidth(scroll_filter,ViewGroup.LayoutParams.WRAP_CONTENT,null,0);
                    toggleLayoutWidth(row_filter, ViewGroup.LayoutParams.MATCH_PARENT, button_hide_filters, dip2pix(mActivity, 40));
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ABOVE, R.id.row_filter);
                    recyclerView.setLayoutParams(params);
                    //Log.d(TAG,"SHOW ROW_FILTER");
                }
            }
        });

        button_hide_filters.setOnClickListener( v -> {
            toggleLayoutWidth(scroll_filter,0,null,0);
            toggleLayoutWidth(row_filter,dip2pix(mActivity,65),button_hide_filters,0);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ABOVE,R.id.conversationEtLayout);
            recyclerView.setLayoutParams(params);

        });


        final AppCompatButton button_filter_sendCat =  child_conversation.findViewById(R.id.button_filter_sendCat);
        final RelativeLayout inner_column_filter_sendCat = child_conversation.findViewById(R.id.inner_column_filter_sendCat);
        final AppCompatTextView text_input_filter_sendCat = child_conversation.findViewById(R.id.text_input_filter_sendCat);
        final AppCompatCheckBox checkbox_filter_sendCat = child_conversation.findViewById(R.id.checkbox_filter_sendCat);
        final TextWatcher textWatcher_sendCat = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String filterStr_sendCat = s.toString();
                    text_input_filter_sendCat.setText(filterStr_sendCat);
                    filterObject.setSendCats(filterStr_sendCat.split(","));
                    conversationFilter.filter("on");
                } catch (NullPointerException ignored) {
                }
            }
        };
        final View.OnClickListener listener_sendCat = v -> {

            text_conversation.removeTextChangedListener(textWatcher_sendCat);
            text_conversation.addTextChangedListener(textWatcher_sendCat);
            if(checkbox_filter_sendCat.isChecked()) {
                text_conversation.setText(text_input_filter_sendCat.getText());
            } else {
                text_conversation.setText("");
            }

            showSoftKeyboard(text_conversation);

            toggleLayoutWidth(inner_column_filter_sendCat, ViewGroup.LayoutParams.MATCH_PARENT,
                    scroll_filter, 0 );

            title_conversation.setText(R.string.filter_title_sendCat);
            button_conversation.setOnClickListener( view -> {

                hideSoftKeyboard(mActivity);
                String text = text_input_filter_sendCat.getText().toString();
                text_conversation.removeTextChangedListener(textWatcher_sendCat);
                text_conversation.setText("");

                if(text.equals("") ) {
                    filterObject.setSendCats(null);
                    conversationFilter.filter("on");
                    checkbox_filter_sendCat.setChecked(false);
                    buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
                } else {
                    filterObject.setSendCats(text.split(","));
                    conversationFilter.filter("on");
                    checkbox_filter_sendCat.setChecked(true);
                    buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
                }

                toggleLayoutWidth(scroll_filter, ViewGroup.LayoutParams.MATCH_PARENT,
                        inner_column_filter_sendCat, 0 );
                title_conversation.setText(R.string.tab_text_3);
                button_conversation.setOnClickListener(null);

            });
        };
        button_filter_sendCat.setOnClickListener( listener_sendCat );
        checkbox_filter_sendCat.setOnCheckedChangeListener((v,isChecked)-> {
            if(isChecked) {
                String text = text_input_filter_sendCat.getText().toString();
                if(text.equals("")) {
                    checkbox_filter_sendCat.setChecked(false);
                    return;
                }
                filterObject.setSendCats(text.split(","));
                conversationFilter.filter("on");
                buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
            } else{
                filterObject.setSendCats(null);
                conversationFilter.filter("on");
                buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
            }
        });

        final AppCompatButton button_filter_room =  child_conversation.findViewById(R.id.button_filter_room);
        final RelativeLayout inner_column_filter_room = child_conversation.findViewById(R.id.inner_column_filter_room);
        final AppCompatTextView text_input_filter_room = child_conversation.findViewById(R.id.text_input_filter_room);
        final AppCompatCheckBox checkbox_filter_room = child_conversation.findViewById(R.id.checkbox_filter_room);
        final TextWatcher textWatcher_room = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String filterStr_room = s.toString();
                    text_input_filter_room.setText(filterStr_room);
                    filterObject.setRooms(filterStr_room.split(","));
                    conversationFilter.filter("on");
                } catch (NullPointerException ignored) {
                }
            }
        };
        final View.OnClickListener listener_room = v -> {

            text_conversation.removeTextChangedListener(textWatcher_room);
            text_conversation.addTextChangedListener(textWatcher_room);
            if(checkbox_filter_room.isChecked()) {
                text_conversation.setText(text_input_filter_room.getText());
            } else {
                text_conversation.setText("");
            }

            showSoftKeyboard(text_conversation);

            toggleLayoutWidth(inner_column_filter_room,ViewGroup.LayoutParams.MATCH_PARENT,
                    scroll_filter, 0  );

            title_conversation.setText(R.string.filter_title_room);
            // 단톡방 필터링 완료, 레이아웃 축소, editText 와 버튼 리스너 해제
            button_conversation.setOnClickListener( view -> {

                hideSoftKeyboard(mActivity);

                String text = text_input_filter_room.getText().toString();
                text_conversation.removeTextChangedListener(textWatcher_room);
                text_conversation.setText("");

                if(text.equals("") ) {
                    filterObject.setRooms(null);
                    checkbox_filter_room.setChecked(false);
                } else {
                    filterObject.setRooms(text.split(","));
                    checkbox_filter_room.setChecked(true);
                }
                conversationFilter.filter("on");
                buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);

                toggleLayoutWidth(inner_column_filter_room, 0,
                        scroll_filter, ViewGroup.LayoutParams.MATCH_PARENT  );

                title_conversation.setText(R.string.tab_text_3);
                button_conversation.setOnClickListener(null);

            });
        };
        button_filter_room.setOnClickListener( listener_room );
        checkbox_filter_room.setOnCheckedChangeListener((v,isChecked)-> {
            if(isChecked) {
                String text = text_input_filter_room.getText().toString();
                if(text.equals("")) {
                    checkbox_filter_room.setChecked(false);
                    return;
                }
                filterObject.setRooms(text.split(","));
            } else{
                filterObject.setRooms(null);
            }
            conversationFilter.filter("on");
            buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
        });

        final AppCompatButton button_filter_time =  child_conversation.findViewById(R.id.button_filter_time);
        final RelativeLayout inner_column_filter_time = child_conversation.findViewById(R.id.inner_column_filter_time);
        final AppCompatCheckBox checkbox_filter_time = child_conversation.findViewById(R.id.checkbox_filter_time);
        final AppCompatTextView text_input_filter_time_after = child_conversation.findViewById(R.id.text_input_filter_time_after);
        final AppCompatTextView text_input_filter_time_before = child_conversation.findViewById(R.id.text_input_filter_time_before);

        final View.OnClickListener listener_time = v -> {

            toggleLayoutWidth(inner_column_filter_time, 0,
                    scroll_filter, ViewGroup.LayoutParams.MATCH_PARENT  );

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity,
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
                               text_input_filter_time_before.setText(String.valueOf(dateValue));
                               conversationFilter.filter("on");
                               buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
                       checkbox_filter_time.setChecked(true);
                       toggleLayoutWidth(inner_column_filter_time, 0,
                               scroll_filter, ViewGroup.LayoutParams.MATCH_PARENT  );

                       allowClose = true;
                   });

                   setButton(DialogInterface.BUTTON_NEGATIVE, "취소",
                           (dialog, which) -> {
                       toggleLayoutWidth(inner_column_filter_time, 0,
                               scroll_filter, ViewGroup.LayoutParams.MATCH_PARENT  );
                               allowClose = true;
                   });

                   setButton(DialogInterface.BUTTON_NEUTRAL, "선택",
                           (dialog, which) -> {
                                   getButton(which).setVisibility(View.INVISIBLE);
                                   getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                                   long dateValue = getDateValueFromDatePicker(getDatePicker());
                                   filterObject.setTime_after(getDateValueFromDatePicker(getDatePicker()));
                                   text_input_filter_time_after.setText(String.valueOf(dateValue));
                                   conversationFilter.filter("on");
                               buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
                                   checkbox_filter_time.setChecked(true);
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
        };
        button_filter_time.setOnClickListener( listener_time );
        checkbox_filter_time.setOnCheckedChangeListener((v,b) -> {
            if(b) {
                String time_before_str = text_input_filter_time_before.getText().toString();
                String time_after_str = text_input_filter_time_after.getText().toString();
                if(time_before_str.equals("") && time_after_str.equals("") ) {
                    checkbox_filter_time.setChecked(false);
                    return;
                }

                filterObject.setTime_before(time_before_str.equals("") ?  -1 : Long.valueOf(time_before_str));
                filterObject.setTime_after(time_after_str.equals("") ?  -1 : Long.valueOf(time_after_str));
                conversationFilter.filter("on");
                buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);

            } else {
                filterObject.setTime_before(-1);
                filterObject.setTime_after(-1);
                conversationFilter.filter("on");
                buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
            }
        });

        final AppCompatButton button_filter_conversation =  child_conversation.findViewById(R.id.button_filter_conversation);
        final RelativeLayout inner_column_filter_conversation = child_conversation.findViewById(R.id.inner_column_filter_conversation);
        final AppCompatTextView text_input_filter_conversation = child_conversation.findViewById(R.id.text_input_filter_conversation);
        final AppCompatCheckBox checkbox_filter_conversation = child_conversation.findViewById(R.id.checkbox_filter_conversation);
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
        final View.OnClickListener listener_conversation = v -> {

            text_conversation.removeTextChangedListener(textWatcher_conversation);
            text_conversation.addTextChangedListener(textWatcher_conversation);
            if(checkbox_filter_conversation.isChecked()) {
                text_conversation.setText(text_input_filter_conversation.getText());
            } else {
                text_conversation.setText("");
            }

            showSoftKeyboard(text_conversation);

            toggleLayoutWidth(inner_column_filter_conversation,ViewGroup.LayoutParams.MATCH_PARENT,
                    scroll_filter, 0  );

            title_conversation.setText(R.string.filter_title_conversation);
            // 단톡방 필터링 완료, 레이아웃 축소, editText 와 버튼 리스너 해제
            button_conversation.setOnClickListener( view -> {

                hideSoftKeyboard(mActivity);

                String text = text_input_filter_conversation.getText().toString();
                text_conversation.removeTextChangedListener(textWatcher_conversation);
                text_conversation.setText("");

                if(text.equals("") ) {
                    filterObject.setConversations(null);
                    checkbox_filter_conversation.setChecked(false);
                } else {
                    filterObject.setConversations(text.split(","));
                    checkbox_filter_conversation.setChecked(true);
                }
                conversationFilter.filter("on");
                buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);

                toggleLayoutWidth(inner_column_filter_conversation, 0,
                        scroll_filter, ViewGroup.LayoutParams.MATCH_PARENT  );

                title_conversation.setText(R.string.tab_text_3);
                button_conversation.setOnClickListener(null);

            });
        };
        button_filter_conversation.setOnClickListener( listener_conversation );
        checkbox_filter_conversation.setOnCheckedChangeListener((v,isChecked)-> {
            if(isChecked) {
                String text = text_input_filter_conversation.getText().toString();
                if(text.equals("")) {
                    checkbox_filter_conversation.setChecked(false);
                    return;
                }
                filterObject.setConversations(text.split(","));
            } else{
                filterObject.setConversations(null);
            }
            conversationFilter.filter("on");
            buildConstraintDoc( conversationRecyclerAdapter, realm, text_filter_summary);
        });
        /*대화 파트 끝*/

        pageViewModel.getText().observe(this, s -> {
            if( s== null)
                return;

            constraintLayout.removeAllViews();
            switch(s) {
                case "search":

                    break;
                case "conversation":
                    constraintLayout.addView(child_conversation);
                    break;
                case "reply":
                    constraintLayout.addView(child_setting);
                    break;
                    default:

            }
        });
        return root;
    }

    private long getDateValueFromDatePicker(DatePicker picker) {
        Calendar calendar_gregorian = new GregorianCalendar(
                picker.getYear(),
                picker.getMonth(),
                picker.getDayOfMonth());
        Date date = calendar_gregorian.getTime();
        return date.getTime();
    }

    private void toggleLayoutWidth(View v1, int w1, View v2, int w2) {
        ViewGroup.LayoutParams lp1 = v1.getLayoutParams();
        lp1.width = w1;
        v1.setLayoutParams(lp1);

        if( v2 != null) {
            ViewGroup.LayoutParams lp2 = v2.getLayoutParams();
            lp2.width = w2;
            v2.setLayoutParams(lp2);
        }
    }



    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager)mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationListener.class.getName().equals(service.service.getClassName())) {
                Log.d(TAG,"알림 설정 됨");
                return true;
            }
        }
        Log.d(TAG,"알림 설정 안됨");
        return false;
    }



    private static int dip2pix(@NonNull Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                context.getResources().getDisplayMetrics());
    }


    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    focusedView.getWindowToken(), 0);
        }
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void buildConstraintDoc( ConversationRecyclerAdapter adapter, Realm realm, AppCompatTextView summaryView) {
        ConversationFilter.ConversationFilterObject filterObject = ((ConversationFilter) adapter.getFilter()).getConversationFilterObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        String[] cs_cats = filterObject.getSendCats();
        String[] cs_rooms = filterObject.getRooms();
        long time_after = filterObject.getTime_after();
        long time_before = filterObject.getTime_before();
        String[] cs_convs = filterObject.getConversations();
        String[] cs_packs = filterObject.getPackages();

        StringBuilder stringBuilder = new StringBuilder();
        if(cs_cats != null) {
            if (cs_cats.length > 0) {
                RealmQuery<Conversation> query_cats = realm.where(Conversation.class).alwaysFalse();
                for(String cs_cat : cs_cats) {
                    query_cats.or().contains(Conversation.FIELD_SENDCAT, cs_cat);
                }
                RealmResults<Conversation> realmResults_cats = query_cats.distinct(Conversation.FIELD_SENDCAT).findAll();
                stringBuilder.append(" ★작성자:  ").append(realmResults_cats.size()).append("명").append("\n");
                for(Conversation conversation : realmResults_cats) {
                    stringBuilder.append("  - ").append(conversation.getSendCat()).append("\n");
                }
            }
        }

        if(cs_rooms != null) {
            if (cs_rooms.length > 0) {
                RealmQuery<Conversation> query_rooms = realm.where(Conversation.class).alwaysFalse();
                for(String cs_room : cs_rooms) {
                    query_rooms.or().contains(Conversation.FIELD_ROOM, cs_room);
                }
                RealmResults<Conversation> realmResults_rooms = query_rooms.distinct(Conversation.FIELD_ROOM).findAll();
                stringBuilder.append("\n ★단톡방:  ").append(realmResults_rooms.size()).append("개").append("\n");
                for(Conversation conversation : realmResults_rooms) {
                    stringBuilder.append("  - ").append(conversation.getCatRoom()).append("\n");
                }
            }
        }

        if(time_after > 0  || time_before > 0) {
            stringBuilder.append("\n ★기간:\n");
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
        }

        if(cs_convs != null) {
            if(cs_convs.length>0) {
                stringBuilder.append("\n ★단어 포함:").append("\n");
                for(String conv : cs_convs) {
                    stringBuilder.append("  - ").append(conv).append("\n");
                }
            }
        }

        if(cs_packs != null) {
            if(cs_packs.length>0) {
                stringBuilder.append("\n ★메신저:").append("\n");
                for(String pack : cs_packs) {
                    stringBuilder.append("  ").append(pack).append("\n");
                }
            }
        }

        String resultStr = stringBuilder.toString();
        resultStr = resultStr.equals("")? " 필터 설정 안됨..." : resultStr;
        summaryView.setText(resultStr);

    }


}