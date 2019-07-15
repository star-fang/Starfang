package com.fang.starfang.view;

import android.app.Service;
import android.content.Context;
import android.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fang.starfang.R;

public class ProgressBarPreference extends Preference {

    private LayoutInflater mInflator;

    public ProgressBarPreference(Context context) {
        super(context);
        mInflator = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    public ProgressBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflator = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public ProgressBarPreference(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        mInflator = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    private ProgressBar mProgressBar;
    private TextView mLabel;
    private int lastReqProgress=-1;
    private int lastReqMax=-1;
    private String lastLabel;

    @Override
    protected View onCreateView(ViewGroup parent) {

        View myLayout=mInflator.inflate(R.layout.progressbarpreference, null, false);
        ((ViewGroup)myLayout.findViewById(R.id.preference_super_container)).addView(super.onCreateView(parent));
        mProgressBar=(ProgressBar) myLayout.findViewById(R.id.preference_progress_bar);
        mLabel=(TextView) myLayout.findViewById(R.id.preference_progress_label);
        if (lastReqProgress>-1){
            mProgressBar.setProgress(lastReqProgress);
        }
        if (lastReqMax>-1){
            mProgressBar.setMax(lastReqMax);
        }
        if (lastLabel!=null){
            mLabel.setText(lastLabel);
        }

        return myLayout;
    }


    public void setProgress(int value){
        if (mProgressBar!=null){
            mProgressBar.setProgress(value);
        } else {
            lastReqProgress=value;
        }

    }

    public void setMax(int value){
        if (mProgressBar!=null){
            int savedprogress=mProgressBar.getProgress();
            mProgressBar.setMax(0);
            mProgressBar.setMax(value);
            mProgressBar.setProgress(savedprogress);
        } else {
            lastReqMax=value;
        }

    }


    public void setLabel(String text){
        if (lastLabel!=null){
            mLabel.setText(text);
        } else {
            lastLabel=text;
        }
    }



}
