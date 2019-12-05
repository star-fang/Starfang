package com.fang.starfang.ui.main.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.fang.starfang.ui.main.PageViewModel;

import io.realm.Realm;

public class PlaceholderFragment extends Fragment {

    private static final String TAG = "FANG_FRAG";
    static final String ARG_SECTION_NUMBER = "section_number";
    Activity mActivity;
    Realm realm;


    public static PlaceholderFragment getInstance(int index) {
        PlaceholderFragment fragment;
        switch( index ) {
            case 1:
                fragment = ItemsFragment.newInstance(index);
                break;
            case 2:
                fragment = HeroesFragment.newInstance(index);
                break;
            case 3:
                fragment = RelicFragment.newInstance(index);
                break;
            default:
                fragment = null;
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = getIndex();
        pageViewModel.setIndex(index);
        Log.d(TAG,ARG_SECTION_NUMBER + index + "_onCreate");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        realm = Realm.getDefaultInstance();
        if (context instanceof Activity) {
            mActivity=(Activity) context;
        }
        Log.d(TAG,ARG_SECTION_NUMBER + getIndex() +"_onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
        Log.d(TAG,ARG_SECTION_NUMBER + getIndex() +"_onDetach: realm instance closed");
    }

    private int getIndex() {
        int index = 1;
        Bundle args = getArguments();
        if (args != null) {
            index = args.getInt(ARG_SECTION_NUMBER);
        }
        return index;
    }

    public interface OnUpdateEventListener {
        void updateEvent(int code);
    }


}