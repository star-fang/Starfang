package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.ui.main.dialog.AddItemDialogFragment;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFloatingRealmAdapter;
import com.fang.starfang.ui.main.recycler.custom.DiagonalScrollRecyclerView;

import io.realm.Realm;

public class ItemsFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_FRAG_ITEM";
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

        RecyclerView recycler_view_items_fixed = view.findViewById(R.id.recycler_view_items_fixed);
        RecyclerView recycler_view_items_floating = view.findViewById(R.id.recycler_view_items_floating);
        recycler_view_items_fixed.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_items_floating.setLayoutManager(new LinearLayoutManager(mActivity));
        DiagonalScrollRecyclerView recycler_view_items_content = view.findViewById(R.id.recycler_view_items_content);
        recycler_view_items_content.setRecyclerView(recycler_view_items_floating);

        ItemSimsFixedRealmAdapter itemSimsFixedRealmAdapter = new ItemSimsFixedRealmAdapter(realm);
        recycler_view_items_fixed.setAdapter(itemSimsFixedRealmAdapter);

        ItemSimsFloatingRealmAdapter itemSimsFloatingRealmAdapter = new ItemSimsFloatingRealmAdapter(realm);
        recycler_view_items_floating.setAdapter(itemSimsFloatingRealmAdapter);


        final RecyclerView.OnScrollListener[] itemRecyclerViewListeners =
                new RecyclerView.OnScrollListener[2];
        itemRecyclerViewListeners[0] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recycler_view_items_fixed.removeOnScrollListener(itemRecyclerViewListeners[1]);
                recycler_view_items_fixed.scrollBy(0,dy);
                recycler_view_items_fixed.addOnScrollListener(itemRecyclerViewListeners[1]);
            }
        };

        itemRecyclerViewListeners[1] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recycler_view_items_floating.removeOnScrollListener(itemRecyclerViewListeners[0]);
                recycler_view_items_floating.scrollBy(0,dy);
                recycler_view_items_floating.addOnScrollListener(itemRecyclerViewListeners[0]);
            }
        };

        recycler_view_items_floating.addOnScrollListener(itemRecyclerViewListeners[0]);
        recycler_view_items_fixed.addOnScrollListener(itemRecyclerViewListeners[1]);


        return view;

    }

}