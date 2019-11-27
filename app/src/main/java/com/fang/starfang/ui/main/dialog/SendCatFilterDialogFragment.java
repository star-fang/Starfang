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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.ui.main.recycler.adapter.SendCatFilterRealmAdapter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilter;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilterObject;
import io.realm.Realm;

public class SendCatFilterDialogFragment extends DialogFragment {

    private static final String TAG = "DIALOG_FILTER_SEND_CAT";
    private Activity mActivity;
    private Realm realm;

    public static SendCatFilterDialogFragment newInstance() {
        return new SendCatFilterDialogFragment();
    }

    public SendCatFilterDialogFragment() {
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
        View view = View.inflate(mActivity, R.layout.dialog_filter_send_cat, null);
        realm = Realm.getDefaultInstance();

        final ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        final SendCatFilterRealmAdapter sendCatFilterRealmAdapter =
                new SendCatFilterRealmAdapter(realm,
                        view.findViewById(R.id.text_filter_sendCat_count),
                        view.findViewById(R.id.text_filter_sendCat_count_desc));

        final RecyclerView recycler_view_filter_send_cat = view.findViewById(R.id.recycler_view_filter_send_cat);
        recycler_view_filter_send_cat.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_filter_send_cat.setAdapter(sendCatFilterRealmAdapter);

        builder.setView(view).setPositiveButton("설정", (dialog, which) -> {
            filterObject.setCheckCat(true);
            ConversationFilter conversationFilter = ConversationFilter.getInstance();
            if( conversationFilter != null ) {
                conversationFilter.filter("on");
            }
            boolean check = filterObject.getSendCatCount()>0;
            filterObject.setCheckCat(check);
            ConstraintDocBuilder docBuilder = ConstraintDocBuilder.getInstance();
            if(docBuilder != null) {
                docBuilder.build();
            }
        });

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        realm.close();
        super.onDismiss(dialog);
        Log.d(TAG,"_ON DISMISS");
    }


}