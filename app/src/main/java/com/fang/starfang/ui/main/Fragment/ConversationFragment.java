package com.fang.starfang.ui.main.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.NotificationListener;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.task.PrefixHandler;
import com.fang.starfang.view.recycler.ConversationRecyclerAdapter;
import com.fang.starfang.view.recycler.Filter.ConversationFilter;
import com.fang.starfang.view.recycler.Filter.ConversationFilterObject;
import com.fang.starfang.view.recycler.RoomFilterRecyclerAdapter;
import com.fang.starfang.view.recycler.SendCatFilterRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ConversationFragment extends PlaceholderFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "FANG_CONV_FRAG";
    private static WeakReference<ConversationFragment> conversationFragmentWeakReference = null;
    private View child_conversation;

    static ConversationFragment getInstance() {
        if( conversationFragmentWeakReference == null ) {
            ConversationFragment conversationFragment = new ConversationFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, 2);
            conversationFragment.setArguments(bundle);
            conversationFragmentWeakReference = new WeakReference<>(conversationFragment);
        }
        return conversationFragmentWeakReference.get();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"_ON CREATE");

        /*대화 파트*/
        Realm realm = Realm.getDefaultInstance();
        child_conversation = View.inflate(mActivity, R.layout.view_conversation, null );
        final RecyclerView recyclerView = child_conversation.findViewById(R.id.conversation_recycler_view);
        final ConversationRecyclerAdapter conversationRecyclerAdapter = new ConversationRecyclerAdapter( realm );
        final View filter_summary_layout = child_conversation.findViewById(R.id.filter_summary_layout);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(conversationRecyclerAdapter);
        recyclerView.scrollToPosition(conversationRecyclerAdapter.getItemCount() - 1);
        final ConversationFilter conversationFilter = ((ConversationFilter)conversationRecyclerAdapter.getFilter());
        final ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        conversationFilter.filter("on");
        realm.removeAllChangeListeners();
        realm.addChangeListener(o -> {
            conversationRecyclerAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(conversationRecyclerAdapter.getItemCount()-1);
            Log.d(TAG,"realm changed!");
        });

        final AppCompatTextView title_conversation = child_conversation.findViewById(R.id.title_conversation);
        final AppCompatEditText text_conversation = child_conversation.findViewById(R.id.text_conversation);
        final AppCompatButton button_conversation = child_conversation.findViewById(R.id.button_conversation);
        final AppCompatButton button_clear_conversation = child_conversation.findViewById(R.id.button_clear_conversation);
        final View inner_column_filter = child_conversation.findViewById((R.id.inner_column_filter));
        final AppCompatTextView text_filter_summary = child_conversation.findViewById(R.id.text_filter_summary);
        final AppCompatButton button_summary_filter = child_conversation.findViewById(R.id.button_summary_filter);
        final View scroll_summary_filter = child_conversation.findViewById(R.id.scroll_summary_filter);
        final AppCompatButton button_hide_filters = child_conversation.findViewById(R.id.button_hide_filters);
        final AppCompatButton button_show_filters = child_conversation.findViewById(R.id.button_show_filters);
        final View row_filter = child_conversation.findViewById(R.id.row_filter);
        buildConstraintDoc( realm, text_filter_summary);

        button_summary_filter.setOnClickListener(v -> {
            if( scroll_summary_filter.getLayoutParams().height == 0 ) {
                changeLayoutSize(scroll_summary_filter, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            } else {
                changeLayoutSize(scroll_summary_filter,dip2pix(mActivity,120), 0);
            }
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
                    new PrefixHandler(mActivity,null,null, null,true, NotificationListener.getName() ).execute(text);
                } else {
                    int itemCount = conversationRecyclerAdapter.getItemCount();
                    Log.d(TAG,"count: " + itemCount);
                    recyclerView.scrollToPosition(itemCount - 1);
                }
                text_conversation.setText("");

            } else {
                hideSoftKeyboard(mActivity);
            }


        };
        button_conversation.setOnClickListener(searchButton_default_listener);
        button_clear_conversation.setOnClickListener( v -> text_conversation.setText(""));

        final View.OnClickListener showButton_default_listener = v -> {
            changeLayoutSize(row_filter,-5,dip2pix(mActivity,100));
            changeLayouWeight(button_show_filters,0.0f);
            button_show_filters.setOnClickListener(null);
        };
        final View.OnClickListener hideButton_default_listener = v -> {
            changeLayoutSize(row_filter,-5,0);
            changeLayouWeight(button_show_filters,1.0f);
            button_show_filters.setOnClickListener(showButton_default_listener);
        };
        button_hide_filters.setOnClickListener(hideButton_default_listener);

        final SendCatFilterRecyclerAdapter sendCatFilterRecyclerAdapter = new SendCatFilterRecyclerAdapter(realm, child_conversation);
        final AppCompatButton button_filter_sendCat =  child_conversation.findViewById(R.id.button_filter_sendCat);
        final View inner_column_filter_sendCat = child_conversation.findViewById(R.id.inner_column_filter_sendCat);
        final AppCompatButton button_filter_sendCat_commit = child_conversation.findViewById(R.id.button_filter_sendCat_commit);

        if( filterObject.isCatChecked()) {
            button_filter_sendCat.setBackgroundResource(R.drawable.round_button_checked);
        }
        button_filter_sendCat.setOnLongClickListener( view -> {
            filter_summary_layout.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(sendCatFilterRecyclerAdapter);
            changeLayouWeight(inner_column_filter,0.0f);
            changeLayouWeight(inner_column_filter_sendCat,1.0f);
            title_conversation.setText(R.string.filter_title_sendCat);
            return true;
        });
        button_filter_sendCat.setOnClickListener( view -> {
            boolean checked =  filterObject.isCatChecked();
            if( checked ) {
                filterObject.setCheckCat(false);
                conversationFilter.filter("on");
                button_filter_sendCat.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(child_conversation,"작성자 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckCat(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getSendCatCount()>0;
                if( check ) {
                    button_filter_sendCat.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(child_conversation,"작성자 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckCat(false);
                    Snackbar.make(child_conversation,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(child_conversation,"작성자 필터 ON",Snackbar.LENGTH_SHORT).show();
            } else {
                button_filter_sendCat.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(child_conversation,"작성자 필터 OFF",Snackbar.LENGTH_SHORT).show();
            }
            filterObject.setCheckCat(check);
            recyclerView.setAdapter(conversationRecyclerAdapter);

            changeLayouWeight(inner_column_filter,1.0f);
            changeLayouWeight(inner_column_filter_sendCat,0.0f);
            buildConstraintDoc( realm, text_filter_summary);
            title_conversation.setText(R.string.tab_text_3);

        });

        final RoomFilterRecyclerAdapter roomFilterRecyclerAdapter = new RoomFilterRecyclerAdapter(realm, child_conversation);
        final AppCompatButton button_filter_room =  child_conversation.findViewById(R.id.button_filter_room);
        final View inner_column_filter_room = child_conversation.findViewById(R.id.inner_column_filter_room);
        final AppCompatButton button_filter_room_commit = child_conversation.findViewById(R.id.button_filter_room_commit);

        if( filterObject.isRoomChecked()) {
            button_filter_room.setBackgroundResource(R.drawable.round_button_checked);
        }
        button_filter_room.setOnLongClickListener( view -> {
            filter_summary_layout.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(roomFilterRecyclerAdapter);
            changeLayouWeight(inner_column_filter,0.0f);
            changeLayouWeight(inner_column_filter_room,1.0f);
            title_conversation.setText(R.string.filter_title_room);
            return true;
        });
        button_filter_room.setOnClickListener( view -> {
            boolean checked =  filterObject.isRoomChecked();
            if( checked ) {
                filterObject.setCheckRoom(false);
                conversationFilter.filter("on");
                button_filter_room.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(child_conversation,"단톡방 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckRoom(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getRoomCount()>0;
                if( check ) {
                    button_filter_room.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(child_conversation,"단톡방 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckRoom(false);
                    Snackbar.make(child_conversation,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(child_conversation,"단톡방 필터 ON",Snackbar.LENGTH_SHORT).show();
            } else {
                button_filter_room.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(child_conversation,"단톡방 필터 OFF",Snackbar.LENGTH_SHORT).show();
            }
            filterObject.setCheckRoom(check);
            recyclerView.setAdapter(conversationRecyclerAdapter);

            changeLayouWeight(inner_column_filter,1.0f);
            changeLayouWeight(inner_column_filter_room,0.0f);
            buildConstraintDoc( realm, text_filter_summary);
            title_conversation.setText(R.string.tab_text_3);

        });

        final AppCompatButton button_filter_time =  child_conversation.findViewById(R.id.button_filter_time);

        if( filterObject.isTimeChecked()) {
            button_filter_time.setBackgroundResource(R.drawable.round_button_checked);
        }
        button_filter_time.setOnLongClickListener( v -> {
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
                                conversationFilter.filter("on");
                                buildConstraintDoc( realm, text_filter_summary);
                                button_filter_time.setBackgroundResource(R.drawable.round_button_checked);
                                Snackbar.make(child_conversation,"기간 필터 ON",Snackbar.LENGTH_SHORT).show();

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
        button_filter_time.setOnClickListener( v -> {
            boolean checked = filterObject.isTimeChecked();
            if(checked) {
                filterObject.setCheckTime(false);
                conversationFilter.filter("on");
                button_filter_time.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(child_conversation,"기간 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckTime(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getTime_before() >= 0;
                if( check ) {
                    button_filter_time.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(child_conversation,"기간 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckTime(false);
                    Snackbar.make(child_conversation,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
                }
            }
            buildConstraintDoc( realm, text_filter_summary);
        });

        final AppCompatButton button_filter_conversation =  child_conversation.findViewById(R.id.button_filter_conversation);
        final RelativeLayout inner_column_filter_conversation = child_conversation.findViewById(R.id.inner_column_filter_conversation);
        final AppCompatTextView text_input_filter_conversation = child_conversation.findViewById(R.id.text_input_filter_conversation);
        final AppCompatButton button_filter_conversation_commit =  child_conversation.findViewById(R.id.button_filter_conversation_commit);
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
            showSoftKeyboard(mActivity, text_conversation);
            changeLayouWeight(inner_column_filter,0.0f);
            changeLayouWeight(inner_column_filter_conversation,1.0f);
            title_conversation.setText(R.string.filter_title_conversation);
            return true;
        } );

        button_filter_conversation.setOnClickListener( v-> {
            boolean checked = filterObject.isConvChecked();
            if(checked) {
                filterObject.setCheckConv(false);
                conversationFilter.filter("on");
                button_filter_conversation.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(child_conversation,"단어 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                filterObject.setCheckConv(true);
                conversationFilter.filter("on");
                boolean check = filterObject.getConvCount() > 0;
                if( check ) {
                    button_filter_conversation.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(child_conversation,"단어 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckConv(false);
                    Snackbar.make(child_conversation,"길게 눌러서 필터 설정 하세요",Snackbar.LENGTH_SHORT).show();
                }
            }
            buildConstraintDoc( realm, text_filter_summary);
        });


        button_filter_conversation_commit.setOnClickListener( view -> {

            hideSoftKeyboard(mActivity);
            String text = text_input_filter_conversation.getText().toString();
            text= text.trim();
            text_conversation.removeTextChangedListener(textWatcher_conversation);
            text_conversation.setText("");

            if(text.equals("") ) {
                filterObject.setCheckConv(false);
                filterObject.setConversations(new String[]{});
                button_filter_conversation.setBackgroundResource(R.drawable.round_button);
                Snackbar.make(child_conversation,"단어 필터 OFF",Snackbar.LENGTH_SHORT).show();
            } else {
                //filterObject.setCheckConv(true);
                filterObject.setConversations(text.split(","));
                conversationFilter.filter("on");
                if( filterObject.getConvCount() > 0) {
                    button_filter_conversation.setBackgroundResource(R.drawable.round_button_checked);
                    Snackbar.make(child_conversation,"단어 필터 ON",Snackbar.LENGTH_SHORT).show();
                } else {
                    filterObject.setCheckConv(false);
                    button_filter_conversation.setBackgroundResource(R.drawable.round_button);
                    Snackbar.make(child_conversation,"단어 필터 OFF",Snackbar.LENGTH_SHORT).show();

                }
            }

            buildConstraintDoc(  realm, text_filter_summary);
            changeLayouWeight(inner_column_filter,1.0f);
            changeLayouWeight(inner_column_filter_conversation,0.0f);


            title_conversation.setText(R.string.tab_text_3);

        });

        /*대화 파트 끝*/
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"_ON CREATE VIEW");

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        final ConstraintLayout constraintLayout = root.findViewById(R.id.constraintLayout);
        constraintLayout.removeAllViews();
        constraintLayout.addView(child_conversation);

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

    private void showSoftKeyboard(Activity activiry, View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    activiry.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void buildConstraintDoc(Realm realm, AppCompatTextView summaryView) {
        ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
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
