package com.fang.starfang.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class ScreenUtils {

    public static int dip2pix(@NonNull Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                context.getResources().getDisplayMetrics());
    }

    public static void changeLayouWeight(View view, float weight) {

        try {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (weight >= 0) {
                params.weight = weight;
            }

            view.setLayoutParams(params);
        } catch (ClassCastException ignored) {
        }
    }

    public static void changeLayoutSize(View view, int width, int height) {

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(width > -5 ) {
            params.width = width;
        }

        if(height >= -5 ) {
            params.height = height;
        }

        view.setLayoutParams(params);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();

        if (focusedView != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    focusedView.getWindowToken(), 0);
        }
    }

    public static  void showSoftKeyboard(Activity activiry, View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    activiry.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    public static int calculateNoOfColumns(Context context, double columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        double screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        double screenHeightDp = displayMetrics.heightPixels / displayMetrics.density;
        double noOfColumns = ( 0.9 * screenWidthDp / columnWidthDp);
        if(screenWidthDp >= screenHeightDp*(4.0/3.0)) {
            noOfColumns /= 2.0;
        }
        return (int)Math.floor(noOfColumns);
    }
}
