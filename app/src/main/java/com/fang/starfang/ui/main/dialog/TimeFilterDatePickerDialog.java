package com.fang.starfang.ui.main.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fang.starfang.ui.main.recycler.filter.ConversationFilter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilterObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeFilterDatePickerDialog extends DatePickerDialog {


    private static final String TAG = "DIALOG_FILTER_TIME";
    private boolean allowClose;
    private ConversationFilterObject filterObject;
    private ConversationFilter conversationFilter;
    private ConstraintDocBuilder docBuilder;

    public TimeFilterDatePickerDialog(
            @NonNull Context context,
            int themeResId,
            @Nullable OnDateSetListener listener,
            int year, int monthOfYear, int dayOfMonth,
            ConversationFilterObject filterObject,
            ConversationFilter conversationFilter,
            ConstraintDocBuilder docBuilder) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
        this.filterObject = filterObject;
        this.conversationFilter = conversationFilter;
        this.docBuilder = docBuilder;

        allowClose = false;

    }

    @Override
    public void show() {
        setTitle("시작 날짜를 선택 하세요!");
        setButton(DialogInterface.BUTTON_POSITIVE, "완료",
                (dialog, which) -> {
            long dateValue = getDateValueFromDatePicker(getDatePicker());
                        filterObject.setTime_before(dateValue);
                        conversationFilter.filter("on");
                        docBuilder.build();
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
                        docBuilder.build();
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


        private long getDateValueFromDatePicker(DatePicker picker) {
        Calendar calendar_gregorian = new GregorianCalendar(
        picker.getYear(),
        picker.getMonth(),
        picker.getDayOfMonth());
        Date date = calendar_gregorian.getTime();
        return date.getTime();
        }
}