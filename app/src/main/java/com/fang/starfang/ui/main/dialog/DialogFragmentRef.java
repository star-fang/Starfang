package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


public class DialogFragmentRef extends DialogFragment {

    private static final String TAG = "FANG_DIALOG";
    private Activity mActivity;

    public static DialogFragmentRef newInstance() {

        DialogFragmentRef ref = new DialogFragmentRef();

        return ref;

    }

    public DialogFragmentRef() {
        Log.d(TAG, "constructed");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "_ON ATTATCH");
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        return builder.create();
    }
}