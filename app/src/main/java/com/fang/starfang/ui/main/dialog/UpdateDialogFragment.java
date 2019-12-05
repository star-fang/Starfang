package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.fang.starfang.ui.main.Fragment.PlaceholderFragment;

import io.realm.Realm;

public class UpdateDialogFragment extends DialogFragment {
    private static final String TAG = "FANG_UDF";
    PlaceholderFragment.OnUpdateEventListener onUpdateEventListener;
    Activity mActivity;
    Realm realm;
    FragmentManager fragmentManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        realm = Realm.getDefaultInstance();
        fragmentManager = getFragmentManager();

        Log.d(TAG, "_onAttach");
        if (context instanceof Activity) {
            Activity activity = (Activity)context;
            mActivity = activity;
            try {
                onUpdateEventListener = (PlaceholderFragment.OnUpdateEventListener) activity;
            } catch( ClassCastException e) {
                Log.d(TAG, activity.toString() + " must implement onSomeEventListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
        Log.d(TAG,"_onDetach");
    }

}
