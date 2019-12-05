package com.fang.starfang.ui.main.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.fang.starfang.AppConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;

import io.realm.RealmResults;

public class AddRelicDialogFragment extends UpdateDialogFragment{
    private static final String TAG = "FANG_DIALOG_ADD_RELIC";
    private static final int GUARDIAN_INIT_VALUE = 0;
    private static final int GRADE_INIT_VALUE = 3;
    private static final String[] guardians = {"청룡","주작","백호","현무"};


    public static AddRelicDialogFragment newInstance() {
        return new AddRelicDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_add_relic, null);

        final NumberPicker picker_add_relic_guardian = view.findViewById(R.id.picker_add_relic_guardian);
        final NumberPicker picker_add_relic_prefix = view.findViewById(R.id.picker_add_relic_prefix);
        final NumberPicker picker_add_relic_suffix = view.findViewById(R.id.picker_add_relic_suffix);
        final NumberPicker picker_add_relic_grade = view.findViewById(R.id.picker_add_relic_grade);
        final NumberPicker picker_add_relic_num = view.findViewById(R.id.picker_add_relic_num);

        try {
            RealmResults<RelicSFX> relicGuardians = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_TYPE).findAll();
            String[] relicGuardiansStr = new String[relicGuardians.size()];
            for(int i = 0; i < relicGuardiansStr.length; i++) {
                RelicSFX relicGuardian = relicGuardians.get(i);
                if(relicGuardian != null) {
                    relicGuardiansStr[i] = guardians[relicGuardian.getGuardianType()-1];
                }
            }
            picker_add_relic_guardian.setMinValue(0);
            picker_add_relic_guardian.setMaxValue(relicGuardiansStr.length - 1);
            picker_add_relic_guardian.setDisplayedValues(relicGuardiansStr);

            RealmResults<RelicPRFX> relicPRFXes = realm.where(RelicPRFX.class).distinct(RelicPRFX.FIELD_NAME).findAll();
            String[] relicPRFXesStr = new String[relicPRFXes.size()];
            for (int i = 0; i < relicPRFXesStr.length; i++) {
                RelicPRFX relicPRFX = relicPRFXes.get(i);
                if (relicPRFX != null) {
                    relicPRFXesStr[i] = relicPRFX.getRelicPrefixName();
                }
            }
            picker_add_relic_prefix.setMinValue(0);
            picker_add_relic_prefix.setMaxValue(relicPRFXesStr.length - 1);
            picker_add_relic_prefix.setDisplayedValues(relicPRFXesStr);

            RealmResults<RelicSFX> relicSFXGrades = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_GRD).findAll();
            String[] relicSFXGradesStr = new String[relicSFXGrades.size()];
            for (int i = 0; i < relicSFXGradesStr.length; i++) {
                RelicSFX relicSFXGrade = relicSFXGrades.get(i);
                if (relicSFXGrade != null) {
                    relicSFXGradesStr[i] = relicSFXGrade.getRelicSuffixGrade() + AppConstant.GRADE_KOR;
                }
            }
            picker_add_relic_grade.setMinValue(0);
            picker_add_relic_grade.setMaxValue(relicSFXGradesStr.length - 1);
            picker_add_relic_grade.setDisplayedValues(relicSFXGradesStr);

            RealmResults<RelicSFX> relicSFXes = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_NAME).equalTo(RelicSFX.FIELD_TYPE,  GUARDIAN_INIT_VALUE + 1).findAll();
            String[] relicSFXesStr = new String[relicSFXes.size()];
            for (int i = 0; i < relicSFXesStr.length; i++) {
                RelicSFX relicSFX = relicSFXes.get(i);
                if (relicSFX != null) {
                    relicSFXesStr[i] = relicSFX.getRelicSuffixName();
                }
            }
            picker_add_relic_suffix.setMinValue(0);
            picker_add_relic_suffix.setMaxValue(relicSFXesStr.length - 1);
            picker_add_relic_suffix.setDisplayedValues(relicSFXesStr);

            picker_add_relic_guardian.setOnValueChangedListener((picker, oldVal, newVal) -> {
                RealmResults<RelicSFX> newRelicSFXes = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_NAME).equalTo(RelicSFX.FIELD_TYPE, newVal + 1).findAll();
                for (int i = 0; i < relicSFXesStr.length; i++) {
                    RelicSFX relicSFX = newRelicSFXes.get(i);
                    if (relicSFX != null) {
                        relicSFXesStr[i] = relicSFX.getRelicSuffixName();
                    }
                }

                try {
                    picker_add_relic_suffix.setDisplayedValues(null);
                    picker_add_relic_suffix.setMinValue(0);
                    picker_add_relic_suffix.setMaxValue(relicSFXesStr.length - 1);
                    picker_add_relic_suffix.setDisplayedValues(relicSFXesStr);
                } catch( ArrayIndexOutOfBoundsException e ) {
                    Log.d(TAG, "suffix error :" + e.toString());
                }
            });
            picker_add_relic_guardian.setValue(GUARDIAN_INIT_VALUE);
            picker_add_relic_grade.setValue(GRADE_INIT_VALUE);

            builder.setView(view).setPositiveButton("추가", ((dialog, which) -> {
                String guardian = relicGuardiansStr[picker_add_relic_guardian.getValue()];
                String prefix = relicPRFXesStr[picker_add_relic_prefix.getValue()];
                String suffix = relicSFXesStr[picker_add_relic_suffix.getValue()];
                int grade = picker_add_relic_grade.getValue() + 1;
                Log.d(TAG, guardian + " " + prefix + " " + suffix + " " + grade);
            }));
        } catch ( IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            Log.d(TAG,e.toString());
        }

        return builder.create();
    }
}