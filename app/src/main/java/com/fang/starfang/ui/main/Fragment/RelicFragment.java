package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fang.starfang.R;

public class RelicFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_FRAG_MAGIC_ITEM";

    static RelicFragment newInstance(int index) {
        RelicFragment magicItemsFragment = new RelicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        magicItemsFragment.setArguments(bundle);
        return magicItemsFragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_magic_item, container, false);

        return view;

    }

}