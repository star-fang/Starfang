package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.ui.main.recycler.adapter.ItemsRealmAdapter;
import com.fang.starfang.util.ScreenUtils;

import io.realm.Realm;

public class AddItemDialogFragment extends DialogFragment {

    private static final String TAG = "FANG_ADD_ITEM_DIALOG";
    private Activity mActivity;
    private Realm realm;

    public static AddItemDialogFragment newInstance() {
        return new AddItemDialogFragment();
    }

    public AddItemDialogFragment() {
        Log.d(TAG, "constructed");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "_ON ATTATCH");
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_add_item, null);
        realm = Realm.getDefaultInstance();
        final RecyclerView recycler_view_all_items = view.findViewById(R.id.recycler_view_all_items);
        recycler_view_all_items.setLayoutManager(new GridLayoutManager(mActivity, ScreenUtils.calculateNoOfColumns(mActivity,80)));
        ItemsRealmAdapter itemsRealmAdapter = new ItemsRealmAdapter(realm);
        recycler_view_all_items.setAdapter(itemsRealmAdapter);

        builder.setView(view).setPositiveButton("추가", ( dialog, v ) -> {

        });

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        realm.close();
        super.onDismiss(dialog);
    }


}