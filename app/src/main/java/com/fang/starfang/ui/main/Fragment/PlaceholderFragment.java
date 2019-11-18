package com.fang.starfang.ui.main.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.fang.starfang.ui.main.PageViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String TAG = "FANG_HOLDER_FRAG";
    static final String ARG_SECTION_NUMBER = "section_number";
    Activity mActivity;


    public static PlaceholderFragment getInstance(int index) {
        PlaceholderFragment fragment;
        switch( index ) {
            case 1:
                fragment = HeroesFragment.newInstance(index);
                break;
            case 2 :
                fragment = ConversationFragment.newInstance(index);
            break;
            default:
                fragment = null;
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"_ON CREATE");
        PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG,"_ON ATTATCH");
        if (context instanceof Activity){
            mActivity=(Activity) context;
        }
    }


}