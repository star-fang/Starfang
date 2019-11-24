package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fang.starfang.R;

import io.realm.Realm;

public class MagicItemsFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_FRAG_MAGIC_ITEM";
    private Realm realm;

    static MagicItemsFragment newInstance(int index) {
        MagicItemsFragment magicItemsFragment = new MagicItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        magicItemsFragment.setArguments(bundle);
        return magicItemsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG,"_ON CREATE");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
        Log.d(TAG, "_ON DESTROY VIEW : realm instance closed");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"_ON CREATE VIEW");

        realm = Realm.getDefaultInstance();
        final View view = inflater.inflate(R.layout.fragment_magic_item, container, false);

        return view;

    }

}