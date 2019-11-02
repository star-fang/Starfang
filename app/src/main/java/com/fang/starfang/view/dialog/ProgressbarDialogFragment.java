package com.fang.starfang.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fang.starfang.R;

public class ProgressbarDialogFragment  extends DialogFragment {
    private ProgressBar progressBar;
    private TextView textView;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getTextView() {
        return textView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_progressbar, null);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.progress_textView);

        builder.setView(view).setNegativeButton("취소",null);

        return builder.create();
    }

}
