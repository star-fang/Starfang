package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import io.realm.Realm;

public class UpdateDialogFragment extends DialogFragment {
    private static final String TAG = "FANG_UDF";
    OnUpdateEventListener onUpdateEventListener;
    Activity mActivity;
    Realm realm;
    FragmentManager fragmentManager;
    private boolean dismissNormally;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        realm = Realm.getDefaultInstance();
        fragmentManager = getFragmentManager();
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
        void updateEvent(int code);
        boolean dialogAttached();
        void dialogDetached();
    }

}
