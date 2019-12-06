package com.fang.starfang.ui.main.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.AppConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.ui.main.recycler.adapter.PickRelicSimRealmAdapter;
import com.fang.starfang.util.ScreenUtils;

import java.util.ArrayList;

import io.realm.RealmResults;

public class PickRelicSimDialogFragment extends UpdateDialogFragment {

    private final static String TAG = "FANG_DIALOG_PICK_RELIC";

    public static PickRelicSimDialogFragment newInstance( int heroID, int relicPosition, int relicSlot  ) {
        Bundle args = new Bundle();
        args.putInt(AppConstant.INTENT_KEY_HERO_ID, heroID);
        args.putInt(AppConstant.INTENT_KEY_RELIC_POSITION, relicPosition);
        args.putInt(AppConstant.INTENT_KEY_RELIC_SLOT, relicSlot);
        PickRelicSimDialogFragment pickRelicSimDialogFragment = new PickRelicSimDialogFragment();
        pickRelicSimDialogFragment.setArguments(args);
        return pickRelicSimDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_pick_relic_sim, null);

        Bundle args = getArguments();
        if (args != null) {
            int heroID = args.getInt(AppConstant.INTENT_KEY_HERO_ID);
            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, heroID).findFirst();
            int relicPosition = args.getInt(AppConstant.INTENT_KEY_RELIC_POSITION);
            int relicSlot = args.getInt(AppConstant.INTENT_KEY_RELIC_SLOT);
            if (heroSim != null) {

                final AppCompatTextView text_dialog_pick_relic_sim_info =
                        view.findViewById(R.id.text_dialog_pick_relic_sim_info);
                final AppCompatTextView text_dialog_pick_relic_sim_desc =
                        view.findViewById(R.id.text_dialog_pick_relic_sim_desc);
                final PickRelicSimRealmAdapter pickRelicSimRealmAdapter = new PickRelicSimRealmAdapter(realm,
                        text_dialog_pick_relic_sim_info,
                        text_dialog_pick_relic_sim_desc);
                final RecyclerView recycler_view_relic_sim =
                        view.findViewById(R.id.recycler_view_relic_sim);
                recycler_view_relic_sim.setLayoutManager(new GridLayoutManager(mActivity,
                        ScreenUtils.calculateNoOfColumns(mActivity, 75)));
                recycler_view_relic_sim.setAdapter(pickRelicSimRealmAdapter);

                final NumberPicker picker_pick_relic_guardian = view.findViewById(R.id.picker_pick_relic_guardian);
                final NumberPicker picker_pick_relic_prefix = view.findViewById(R.id.picker_pick_relic_prefix);
                final NumberPicker picker_pick_relic_suffix = view.findViewById(R.id.picker_pick_relic_suffix);
                final NumberPicker picker_pick_relic_grade = view.findViewById(R.id.picker_pick_relic_grade);

                try {
                    RealmResults<RelicSFX> relicGuardians = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_TYPE).findAll();
                    ArrayList<String> relicGuardianList = new ArrayList<>();
                    relicGuardianList.add(AppConstant.ALL_PICK_KOR);
                    for (RelicSFX relicGuardian : relicGuardians) {
                        if (relicGuardian != null) {
                            relicGuardianList.add( AppConstant.guardians[relicGuardian.getGuardianType() - 1] );
                        }
                    }
                    picker_pick_relic_guardian.setMinValue(0);
                    picker_pick_relic_guardian.setMaxValue(relicGuardians.size());
                    picker_pick_relic_guardian.setDisplayedValues(relicGuardianList.toArray(new String[0]));
                    picker_pick_relic_guardian.setValue(0);

                    RealmResults<RelicPRFX> relicPRFXes = realm.where(RelicPRFX.class).distinct(RelicPRFX.FIELD_NAME).findAll();
                    ArrayList<String> relicPrefixList = new ArrayList<>();
                    relicPrefixList.add(AppConstant.ALL_PICK_KOR);
                    for (RelicPRFX relicPRFX : relicPRFXes) {
                        if (relicPRFX != null) {
                            relicPrefixList.add(relicPRFX.getRelicPrefixName());
                        }
                    }
                    picker_pick_relic_prefix.setMinValue(0);
                    picker_pick_relic_prefix.setMaxValue(relicPRFXes.size());
                    picker_pick_relic_prefix.setDisplayedValues(relicPrefixList.toArray(new String[0]));
                    picker_pick_relic_prefix.setValue(0);

                    RealmResults<RelicSFX> relicSFXGrades = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_GRD).findAll();
                    ArrayList<String> relicGradeList = new ArrayList<>();
                    relicGradeList.add(AppConstant.ALL_PICK_KOR);
                    for (RelicSFX relicSFXGrade : relicSFXGrades) {
                        if (relicSFXGrade != null) {
                            relicGradeList.add(relicSFXGrade.getRelicSuffixGrade() + AppConstant.GRADE_KOR);
                        }
                    }
                    picker_pick_relic_grade.setMinValue(0);
                    picker_pick_relic_grade.setMaxValue(relicSFXGrades.size());
                    picker_pick_relic_grade.setDisplayedValues(relicGradeList.toArray(new String[0]));

                    RealmResults<RelicSFX> relicSFXes = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_NAME).equalTo(RelicSFX.FIELD_TYPE, AppConstant.GUARDIAN_INIT_VALUE + 1).findAll();
                    ArrayList<String> relicSuffixList = new ArrayList<>();
                    relicSuffixList.add(AppConstant.ALL_PICK_KOR);
                    for (RelicSFX relicSFX:relicSFXes) {
                        if (relicSFX != null) {
                            relicSuffixList.add(relicSFX.getRelicSuffixName());
                        }
                    }
                    picker_pick_relic_suffix.setMinValue(0);
                    picker_pick_relic_suffix.setMaxValue(relicSFXes.size());
                    picker_pick_relic_suffix.setDisplayedValues(relicSuffixList.toArray(new String[0]));

                    picker_pick_relic_guardian.setOnValueChangedListener((picker, oldVal, newVal) -> {
                        RealmResults<RelicSFX> newRelicSFXes = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_NAME).equalTo(RelicSFX.FIELD_TYPE, newVal + 1).findAll();
                        relicSuffixList.clear();
                        relicSuffixList.add(AppConstant.ALL_PICK_KOR);
                        for (RelicSFX relicSFX : newRelicSFXes) {
                            if (relicSFX != null) {
                                relicSuffixList.add(relicSFX.getRelicSuffixName());
                            }
                        }

                        try {
                            picker_pick_relic_suffix.setDisplayedValues(null);
                            picker_pick_relic_suffix.setMinValue(0);
                            picker_pick_relic_suffix.setMaxValue(newRelicSFXes.size());
                            picker_pick_relic_suffix.setDisplayedValues(relicSuffixList.toArray(new String[0]));


                            String guardianType = relicGuardianList.get(newVal);
                            String prefix = relicPrefixList.get(picker_pick_relic_prefix.getValue());
                            String grade = relicGradeList.get(picker_pick_relic_grade.getValue());
                            String suffix = relicSuffixList.get(picker_pick_relic_suffix.getValue());
                            String cs = guardianType + AppConstant.CONSTRAINT_SEPARATOR
                                    + prefix + AppConstant.CONSTRAINT_SEPARATOR
                                    + grade + AppConstant.CONSTRAINT_SEPARATOR
                                    + suffix;

                            pickRelicSimRealmAdapter.getFilter().filter( cs );

                        } catch (ArrayIndexOutOfBoundsException e) {
                            Log.d(TAG, "suffix error :" + e.toString());
                        }
                    });

                    picker_pick_relic_prefix.setOnValueChangedListener((picker, oldVal, newVal) -> {
                        String guardianType = relicGuardianList.get(picker_pick_relic_grade.getValue());
                        String prefix = relicPrefixList.get(newVal);
                        String grade = relicGradeList.get(picker_pick_relic_grade.getValue());
                        String suffix = relicSuffixList.get(picker_pick_relic_suffix.getValue());
                        String cs = guardianType + AppConstant.CONSTRAINT_SEPARATOR
                                + prefix + AppConstant.CONSTRAINT_SEPARATOR
                                + grade + AppConstant.CONSTRAINT_SEPARATOR
                                + suffix;
                        pickRelicSimRealmAdapter.getFilter().filter( cs );
                    });

                    picker_pick_relic_grade.setOnValueChangedListener((picker, oldVal, newVal) -> {
                        String guardianType = relicGuardianList.get(picker_pick_relic_grade.getValue());
                        String prefix = relicPrefixList.get(picker_pick_relic_prefix.getValue());
                        String grade = relicGradeList.get(newVal);
                        String suffix = relicSuffixList.get(picker_pick_relic_suffix.getValue());
                        String cs = guardianType + AppConstant.CONSTRAINT_SEPARATOR
                                + prefix + AppConstant.CONSTRAINT_SEPARATOR
                                + grade + AppConstant.CONSTRAINT_SEPARATOR
                                + suffix;
                        pickRelicSimRealmAdapter.getFilter().filter( cs );
                    });

                    picker_pick_relic_suffix.setOnValueChangedListener((picker, oldVal, newVal) -> {
                        String guardianType = relicGuardianList.get(picker_pick_relic_grade.getValue());
                        String prefix = relicPrefixList.get(picker_pick_relic_prefix.getValue());
                        String grade = relicGradeList.get(picker_pick_relic_grade.getValue());
                        String suffix = relicSuffixList.get(newVal);
                        String cs = guardianType + AppConstant.CONSTRAINT_SEPARATOR
                                + prefix + AppConstant.CONSTRAINT_SEPARATOR
                                + grade + AppConstant.CONSTRAINT_SEPARATOR
                                + suffix;
                        pickRelicSimRealmAdapter.getFilter().filter( cs );
                    });
                    builder.setView(view).setPositiveButton(R.string.wear_kor, (dialog, which) -> {

                        RelicSim selectedRelic = pickRelicSimRealmAdapter.getSelectedRelic();
                        if( selectedRelic != null ) {
                            realm.beginTransaction();
                            selectedRelic.setHeroWhoHasThis(heroSim);
                            heroSim.setHeroRelicSlot(selectedRelic,relicSlot,relicPosition);
                            realm.commitTransaction();
                            onUpdateEventListener.updateEvent(AppConstant.RESULT_CODE_SUCCESS_MODIFY_RELIC);
                        }
                    }).setNegativeButton(R.string.cancel_kor, null);

                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    Log.d(TAG, e.toString());
                }

            }
        }
        return builder.create();
    }
}
