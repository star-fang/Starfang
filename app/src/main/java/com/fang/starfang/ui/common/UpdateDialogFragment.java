package com.fang.starfang.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import io.realm.Realm;

public class UpdateDialogFragment extends DialogFragment {
    private static final String TAG = "FANG_UDF";
    protected OnUpdateEventListener onUpdateEventListener;
    protected Activity mActivity;
    protected Realm realm;
    protected FragmentManager fragmentManager;
    protected Resources resources;
    private boolean dismissNormally;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        realm = Realm.getDefaultInstance();
        fragmentManager = getFragmentManager();
        resources = getResources();
        dismissNormally = true;

        Log.d(TAG, "_onAttach");
        if (context instanceof Activity) {
            Activity activity = (Activity)context;
            mActivity = activity;
            try {
                onUpdateEventListener = (OnUpdateEventListener) activity;
                boolean anotherIsShown  = onUpdateEventListener.dialogAttached();
                if( anotherIsShown ) {
                    Log.d(TAG, "another dialog is shown");
                    dismissNormally = false;
                    super.dismiss();
                }
            } catch( ClassCastException e) {
                Log.d(TAG, activity.toString() + " must implement onSomeEventListener");
            }
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
        if( dismissNormally ) {
            Log.d(TAG,"_onDetach : normal");
            onUpdateEventListener.dialogDetached();
        } else {
            Log.d(TAG, "_onDetach : not normal");
        }
    }


    public interface OnUpdateEventListener {
        void updateEvent(int code, String message);
        boolean dialogAttached();
        void dialogDetached();
    }

}
