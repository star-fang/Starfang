package com.fang.starfang.ui.main.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.ui.creative.UpdateDialogFragment;
import com.fang.starfang.ui.main.adapter.ManageRelicSimRealmAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

public class ManageRelicSuffixDialogFragment extends UpdateDialogFragment {

    public static ManageRelicSuffixDialogFragment getInstance( int guardianType, int suffixNo ) {

        ManageRelicSuffixDialogFragment manageRelicSuffixDialogFragment =
                new ManageRelicSuffixDialogFragment();

        Bundle args = new Bundle();

        Realm realm_tmp = Realm.getDefaultInstance();
        RealmResults<RelicSFX> sfxes = realm_tmp.where(RelicSFX.class).equalTo(RelicSFX.FIELD_TYPE, guardianType).and()
                .equalTo(RelicSFX.FIELD_GRD,4).and().findAll();
        if( sfxes != null ) {
            RelicSFX sfx = sfxes.get(suffixNo);
            if( sfx != null ) {
                int suffixID = sfx.getRelicSuffixID();
                args.putInt(FangConstant.INTENT_KEY_SUFFIX_ID, suffixID);
            }
        }
        manageRelicSuffixDialogFragment.setArguments(args);
        realm_tmp.close();
        return manageRelicSuffixDialogFragment;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_manage_relic_suffix, null);
        Bundle args = this.getArguments();
        if (args != null) {
            final int suffixID = args.getInt(FangConstant.INTENT_KEY_SUFFIX_ID);
            RelicSFX sfx = realm.where(RelicSFX.class).equalTo(RelicSFX.FIELD_ID, suffixID).findFirst();
            if (sfx != null) {
                AppCompatTextView text_title_manage_suffix = view.findViewById(R.id.text_title_manage_suffix);
                AppCompatEditText text_suffix_count = view.findViewById(R.id.text_suffix_count);
                AppCompatButton button_minus_suffix = view.findViewById(R.id.button_minus_suffix);
                AppCompatButton button_plus_suffix = view.findViewById(R.id.button_plus_suffix);
                LinearLayout layout_edit_relic_group = view.findViewById(R.id.layout_edit_relic_group);
                AppCompatTextView text_selected_relic_suffix = view.findViewById(R.id.text_selected_relic_suffix);
                AppCompatTextView text_selected_relic_count = view.findViewById(R.id.text_selected_relic_count);
                AppCompatButton button_blend_relic = view.findViewById(R.id.button_blend_relic);
                AppCompatButton button_wear_off_relic = view.findViewById(R.id.button_wear_off_relic);
                AppCompatButton button_delete_relic = view.findViewById(R.id.button_delete_relic);
                RecyclerView recycler_view_relic_suffix = view.findViewById(R.id.recycler_view_relic_suffix);
                AppCompatButton button_confirm_manage_relic = view.findViewById(R.id.button_confirm_manage_relic);

                text_title_manage_suffix.setText(sfx.getNameStarGrade());
                RealmResults<RelicSim> relicSims = realm.where(RelicSim.class).equalTo(RelicSim.FIELD_SUFFIX
                        + "." + RelicSFX.FIELD_ID, suffixID).findAll();
                String relicSimCount = relicSims.size() + "";
                text_suffix_count.setText(relicSimCount);
                layout_edit_relic_group.setVisibility(View.GONE);
                ManageRelicSimRealmAdapter manageRelicSimRealmAdapter = new ManageRelicSimRealmAdapter(
                        relicSims, layout_edit_relic_group, text_selected_relic_count
                );
                recycler_view_relic_suffix.setLayoutManager(new LinearLayoutManager(mContext));
                recycler_view_relic_suffix.setAdapter(manageRelicSimRealmAdapter);

                button_confirm_manage_relic.setOnClickListener(v -> {
                    this.dismiss();
                });


            }
        }

        builder.setView(view);
        return builder.create();
    }
}
