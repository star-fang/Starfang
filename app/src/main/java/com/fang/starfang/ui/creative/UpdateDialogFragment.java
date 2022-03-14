package com.fang.starfang.ui.creative;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import io.realm.Realm;

public class UpdateDialogFragment extends DialogFragment {
    private static final String TAG = "FANG_UDF";
    protected ArrayList<OnUpdateEventListener> listeners;
    protected Context mContext;
    protected Realm realm;
    protected FragmentManager fragmentManager;
    protected Resources resources;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        realm = Realm.getDefaultInstance();
        fragmentManager = getParentFragmentManager();
        listeners = new ArrayList<>();
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment instanceof OnUpdateEventListener && fragment.isVisible()) {
                Log.d(TAG, "listener: " + fragment.getClass().getName());
                listeners.add((OnUpdateEventListener) fragment);
            }
        }
        resources = getResources();
        mContext = context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
        Log.d(TAG, "_onDetach");
    }


    public interface OnUpdateEventListener {
        void updateEvent(int resultCode, String message, int[] pos);
    }

}
