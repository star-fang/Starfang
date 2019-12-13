package com.fang.starfang.ui.conversation.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.ui.common.UpdateDialogFragment;
import com.fang.starfang.ui.conversation.ConstraintDocBuilder;
import com.fang.starfang.ui.conversation.adapter.SendCatFilterRealmAdapter;
import com.fang.starfang.ui.conversation.adapter.ConversationFilter;
import com.fang.starfang.ui.conversation.adapter.ConversationFilterObject;

public class SendCatFilterDialogFragment extends UpdateDialogFragment {

    public static SendCatFilterDialogFragment newInstance() {
        return new SendCatFilterDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_filter_send_cat, null);

        final ConversationFilterObject filterObject = ConversationFilterObject.getInstance();
        final SendCatFilterRealmAdapter sendCatFilterRealmAdapter =
                new SendCatFilterRealmAdapter(realm,
                        view.findViewById(R.id.text_filter_sendCat_count),
                        view.findViewById(R.id.text_filter_sendCat_count_desc));

        final RecyclerView recycler_view_filter_send_cat = view.findViewById(R.id.recycler_view_filter_send_cat);
        recycler_view_filter_send_cat.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_filter_send_cat.setAdapter(sendCatFilterRealmAdapter);

        builder.setView(view).setPositiveButton(R.string.setting_kor, (dialog, which) -> {
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

}