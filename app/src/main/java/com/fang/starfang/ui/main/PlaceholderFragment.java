package com.fang.starfang.ui.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.NotificationListener;
import com.fang.starfang.R;
import com.fang.starfang.local.task.RealmSyncTask;
import com.fang.starfang.view.recycler.ConversationFilter;
import com.fang.starfang.view.recycler.ConversationRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "FANG_SETTING";

    private PageViewModel pageViewModel;

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

        button_sync_all.setOnClickListener(v -> new RealmSyncTask(text_address.getText().toString(), getActivity()).execute(getResources().getStringArray(R.array.pref_list_table)));

        button_notifications_setting.setOnClickListener(v -> {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } );


        switch_bot.setChecked(NotificationListener.getStatus().equals("start"));

        switch_bot.setOnCheckedChangeListener((v,isChecked)->{

            Intent intent = new Intent(getActivity(), NotificationListener.class);
            if(isChecked) {
                if(!isMyServiceRunning()) {
                    Toast.makeText(getActivity(), "알림 읽기 권한 설정 하세요", Toast.LENGTH_SHORT).show();
                    switch_bot.setChecked(false);
                } else {
                    intent.putExtra("status", "start");
                    Toast.makeText(getActivity(), "냥봇 시작", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getActivity()).startService(intent);
                }
            } else {
                intent.putExtra("status","stop");
                Toast.makeText(getActivity(), "냥봇 중지", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getActivity()).startService(intent);
            }
        });

        /*대화 파트*/
        Realm realm = Realm.getDefaultInstance();
        final View child_conversation = inflater.inflate(R.layout.view_conversation,constraintLayout,false);
        RecyclerView recyclerView = child_conversation.findViewById(R.id.conversation_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        final ConversationRecyclerAdapter conversationRecyclerAdapter = new ConversationRecyclerAdapter( realm, recyclerView );
        recyclerView.setAdapter(conversationRecyclerAdapter);

        final AppCompatTextView title_conversation = child_conversation.findViewById(R.id.title_conversation);
        final AppCompatEditText text_conversation = child_conversation.findViewById(R.id.text_conversation);
        final AppCompatButton button_conversation = child_conversation.findViewById(R.id.button_conversation);
        final AppCompatButton button_clear_conversation = child_conversation.findViewById(R.id.button_clear_conversation);
        final NestedScrollView scroll_filter = child_conversation.findViewById(R.id.scroll_filter);

        button_clear_conversation.setOnClickListener( v -> {
            text_conversation.setText("");
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
                    ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setSendCats(filterStr_sendCat.split(","));
                    conversationRecyclerAdapter.getFilter().filter("on");
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

            ViewGroup.LayoutParams layoutParams_column = inner_column_filter_sendCat.getLayoutParams();
            layoutParams_column.width = ViewGroup.LayoutParams.MATCH_PARENT;
            inner_column_filter_sendCat.setLayoutParams(layoutParams_column);

            ViewGroup.LayoutParams layoutParams_scroll = scroll_filter.getLayoutParams();
            layoutParams_scroll.width = 0;
            scroll_filter.setLayoutParams(layoutParams_scroll);

            title_conversation.setText(R.string.filter_title_sendCat);
            // 이름 필터링 완료, 레이아웃 축소, editText 와 버튼 리스너 해제
            button_conversation.setOnClickListener( view -> {

                hideSoftKeyboard(getActivity());

                String text = text_input_filter_sendCat.getText().toString();
                text_conversation.removeTextChangedListener(textWatcher_sendCat);
                text_conversation.setText("");

                ViewGroup.LayoutParams layoutParams_check = checkbox_filter_sendCat.getLayoutParams();
                //boolean noItem = conversationRecyclerAdapter.getItemCount() == 0
                if(text.equals("") ) {
                    checkbox_filter_sendCat.setChecked(false);
                } else {
                    checkbox_filter_sendCat.setChecked(true);
                }

                layoutParams_column.width = 0;
                inner_column_filter_sendCat.setLayoutParams(layoutParams_column);

                layoutParams_scroll.width = ViewGroup.LayoutParams.MATCH_PARENT;
                scroll_filter.setLayoutParams(layoutParams_scroll);

                try {
                    ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setSendCats(text.split(","));
                    conversationRecyclerAdapter.getFilter().filter("on");
                } catch ( NullPointerException ignored) {

                }

                title_conversation.setText("");
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
                ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setSendCats(text.split(","));
                conversationRecyclerAdapter.getFilter().filter("on");
            } else{
                ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setSendCats(null);
                conversationRecyclerAdapter.getFilter().filter("on");
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
                    ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setRooms(filterStr_room.split(","));
                    conversationRecyclerAdapter.getFilter().filter("on");
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

                hideSoftKeyboard(getActivity());

                String text = text_input_filter_room.getText().toString();
                text_conversation.removeTextChangedListener(textWatcher_room);
                text_conversation.setText("");

                ViewGroup.LayoutParams layoutParams_check = checkbox_filter_room.getLayoutParams();
                if(text.equals("") ) {
                    checkbox_filter_room.setChecked(false);
                } else {
                    checkbox_filter_room.setChecked(true);
                }

                toggleLayoutWidth(inner_column_filter_room, 0,
                        scroll_filter, ViewGroup.LayoutParams.MATCH_PARENT  );

                try {
                    ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setRooms(text.split(","));
                    conversationRecyclerAdapter.getFilter().filter("on");
                } catch ( NullPointerException ignored) {

                }

                title_conversation.setText("");
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
                ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setRooms(text.split(","));
                conversationRecyclerAdapter.getFilter().filter("on");
            } else{
                ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setRooms(null);
                conversationRecyclerAdapter.getFilter().filter("on");
            }
        });

        final AppCompatButton button_filter_time =  child_conversation.findViewById(R.id.button_filter_time);
        final RelativeLayout inner_column_filter_time = child_conversation.findViewById(R.id.inner_column_filter_time);
        final AppCompatTextView text_input_filter_time_desc = child_conversation.findViewById(R.id.text_input_filter_time_desc);
        final AppCompatCheckBox checkbox_filter_time = child_conversation.findViewById(R.id.checkbox_filter_time);



        View.OnClickListener commitTimeBefore =  v -> {
            /*
            Calendar calendar = new GregorianCalendar(date_picker_filter.getYear(),
                    date_picker_filter.getMonth(),
                    date_picker_filter.getDayOfMonth());
            Date date = calendar.getTime();
            long time_before = date.getTime();
            ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setTime_before(time_before);
            //text_conversation.setText(new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(date));
            date_picker_filter.setOnClickListener(null);
            button_conversation.setOnClickListener(null);
            conversationRecyclerAdapter.getFilter().filter("on");
            ((RelativeLayout) child_conversation).removeView(dialog_date_picker);
            checkbox_filter_time.setChecked(true);
            toggleLayoutWidth(scroll_filter, ViewGroup.LayoutParams.MATCH_PARENT , inner_column_filter_time, 0);

             */
        };

        View.OnClickListener commitTimeAfter =  v -> {
            /*
            Calendar calendar = new GregorianCalendar(date_picker_filter.getYear(),
                    date_picker_filter.getMonth(),
                    date_picker_filter.getDayOfMonth());
            Date date = calendar.getTime();
            long time_after = date.getTime();
            ((ConversationFilter) conversationRecyclerAdapter.getFilter()).getConversationFilterObject().setTime_after(time_after);
            text_input_filter_time_desc.setText("시작날짜:" + new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(date));
            button_conversation.setOnClickListener(null);
            button_conversation.setOnClickListener(commitTimeBefore);
            conversationRecyclerAdapter.getFilter().filter("on");
            title_date_filter.setText("검색 종료 날짜를 선택 하세요.");
            text_conversation.setFocusableInTouchMode(true);*/
        };


        final View.OnClickListener listener_time = v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, (view, year, monthOfYear, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.KOREA);
                String date = simpleDateFormat.format(newDate.getTime());
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();

            /*
            ((RelativeLayout) child_conversation).addView(dialog_date_picker);
            text_conversation.setText("");
            text_conversation.setFocusable(false);
            toggleLayoutWidth(scroll_filter, 0 , inner_column_filter_time, ViewGroup.LayoutParams.MATCH_PARENT);
            title_date_filter.setText("검색 시작 날짜를 선택 하세요.");
            button_conversation.setOnClickListener(commitTimeAfter);*/
        };

        button_filter_time.setOnClickListener( listener_time );




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

           //textView.setText(s);
        });
        return root;
    }

    private void toggleLayoutWidth(View v1, int w1, View v2, int w2) {
        ViewGroup.LayoutParams lp1 = v1.getLayoutParams();
        lp1.width = w1;
        v1.setLayoutParams(lp1);

        ViewGroup.LayoutParams lp2 = v2.getLayoutParams();
        lp2.width = w2;
        v2.setLayoutParams(lp2);
    }



    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);
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
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

}