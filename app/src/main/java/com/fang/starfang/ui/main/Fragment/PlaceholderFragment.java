package com.fang.starfang.ui.main.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fang.starfang.ui.main.PageViewModel;

import io.realm.Realm;

public class PlaceholderFragment extends Fragment {

    private static final String TAG = "FANG_FRAG";
    static final String ARG_SECTION_NUMBER = "section_number";
    Context mActivity;
    Realm realm;


    public static PlaceholderFragment getInstance(int index) {
        switch( index ) {
            case 1: return HeroesFragment.newInstance(index);
            case 2: return ItemsFragment.newInstance(index);
            case 3: return RelicFragment.newInstance(index);
            default: return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
                //ViewModelProviders.of(this).get(PageViewModel.class);
        int index = getIndex();
        pageViewModel.setIndex(index);
        Log.d(TAG,ARG_SECTION_NUMBER + index + "_onCreate");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        realm = Realm.getDefaultInstance();
        //if( context instanceof  Activity ) {
            mActivity = context;
        //}
        //Log.d(TAG,ARG_SECTION_NUMBER + getIndex() +"_onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
        //Log.d(TAG,ARG_SECTION_NUMBER + getIndex() +"_onDetach: realm instance closed");
    }

    private int getIndex() {
        int index = 1;
        Bundle args = getArguments();
        if (args != null) {
            index = args.getInt(ARG_SECTION_NUMBER);
        }
        return index;
    }

}