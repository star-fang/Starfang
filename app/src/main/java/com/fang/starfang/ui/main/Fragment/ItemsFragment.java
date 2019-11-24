package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.ui.main.dialog.AddItemDialogFragment;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsRealmAdapter;
import com.fang.starfang.util.ScreenUtils;

import io.realm.Realm;

public class ItemsFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_ITEM_FRAG";
    private Realm realm;

    static ItemsFragment newInstance(int index) {
        ItemsFragment itemsFragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        itemsFragment.setArguments(bundle);
        return itemsFragment;
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
        final View view = inflater.inflate(R.layout.fragment_items, container, false);
        AppCompatButton button_add_item = view.findViewById(R.id.button_add_item);
        button_add_item.setOnClickListener( v -> {
            FragmentManager manager = getFragmentManager();
            if (manager != null) {
                AddItemDialogFragment.newInstance().show(manager,TAG);
            }
        });

        AppCompatTextView text_item_info = view.findViewById(R.id.text_item_info);
        AppCompatTextView text_item_desc = view.findViewById(R.id.text_item_desc);
        RecyclerView recycler_view_items = view.findViewById(R.id.recycler_view_items);
        recycler_view_items.setLayoutManager(new GridLayoutManager(mActivity, ScreenUtils.calculateNoOfColumns(mActivity,80.0)));
        ItemSimsRealmAdapter itemSimsRealmAdapter = new ItemSimsRealmAdapter(realm, text_item_info, text_item_desc);
        recycler_view_items.setAdapter(itemSimsRealmAdapter);

        return view;

    }

}