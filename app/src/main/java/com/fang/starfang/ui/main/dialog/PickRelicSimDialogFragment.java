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

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.ui.creative.UpdateDialogFragment;
import com.fang.starfang.ui.main.adapter.PickRelicSimRealmAdapter;
import com.fang.starfang.util.ScreenUtils;

import java.util.ArrayList;

import io.realm.RealmResults;

public class PickRelicSimDialogFragment extends UpdateDialogFragment {

    private final static String TAG = "FANG_DIALOG_PICK_RELIC";

    public static PickRelicSimDialogFragment newInstance( int heroID, int relicPosition, int relicSlot  ) {
        Bundle args = new Bundle();
        args.putInt(FangConstant.INTENT_KEY_HERO_ID, heroID);
        args.putInt(FangConstant.INTENT_KEY_RELIC_POSITION, relicPosition);
        args.putInt(FangConstant.INTENT_KEY_RELIC_SLOT, relicSlot);
        PickRelicSimDialogFragment pickRelicSimDialogFragment = new PickRelicSimDialogFragment();
        pickRelicSimDialogFragment.setArguments(args);
        return pickRelicSimDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_pick_relic_sim, null);

        Bundle args = getArguments();
        if (args != null) {
            int heroID = args.getInt(FangConstant.INTENT_KEY_HERO_ID);
            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, heroID).findFirst();
            int relicPosition = args.getInt(FangConstant.INTENT_KEY_RELIC_POSITION);
            int relicSlot = args.getInt(FangConstant.INTENT_KEY_RELIC_SLOT);
            if (heroSim != null) {

                final AppCompatTextView text_dialog_pick_relic_sim_info =
                        view.findViewById(R.id.text_dialog_pick_relic_sim_info);
                final AppCompatTextView text_dialog_pick_relic_sim_desc =
                        view.findViewById(R.id.text_dialog_pick_relic_sim_desc);
                final PickRelicSimRealmAdapter pickRelicSimRealmAdapter = new PickRelicSimRealmAdapter(
                        realm.where(RelicSim.class).isNull(RelicSim.FIELD_HERO).findAll(),
                        text_dialog_pick_relic_sim_info,
                        text_dialog_pick_relic_sim_desc);
                final RecyclerView recycler_view_relic_sim =
                        view.findViewById(R.id.recycler_view_relic_sim);
                recycler_view_relic_sim.setLayoutManager(new GridLayoutManager(mContext,
                        ScreenUtils.calculateNoOfColumns(mContext, 75)));
                recycler_view_relic_sim.setAdapter(pickRelicSimRealmAdapter);

                final NumberPicker picker_pick_relic_guardian = view.findViewById(R.id.picker_pick_relic_guardian);
                final NumberPicker picker_pick_relic_prefix = view.findViewById(R.id.picker_pick_relic_prefix);
                final NumberPicker picker_pick_relic_suffix = view.findViewById(R.id.picker_pick_relic_suffix);
                final NumberPicker picker_pick_relic_grade = view.findViewById(R.id.picker_pick_relic_grade);

                final String[] GUARDIANS = resources.getStringArray(R.array.guardians);
                final String GRADE_KOR = resources.getString(R.string.grade_kor);
                final String ALL_PICK_KOR = resources.getString(R.string.all_pick_kor);

                try {
                    RealmResults<RelicSFX> relicGuardians = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_TYPE).findAll();
                    ArrayList<String> relicGuardianList = new ArrayList<>();
                    relicGuardianList.add(ALL_PICK_KOR);
                    for (RelicSFX relicGuardian : relicGuardians) {
                        if (relicGuardian != null) {
                            relicGuardianList.add( GUARDIANS[relicGuardian.getGuardianType() - 1] );
                        }
                    }
                    picker_pick_relic_guardian.setMinValue(0);
                    picker_pick_relic_guardian.setMaxValue(relicGuardians.size());
                    picker_pick_relic_guardian.setDisplayedValues(relicGuardianList.toArray(new String[0]));
                    picker_pick_relic_guardian.setValue(0);

                    RealmResults<RelicPRFX> relicPRFXes = realm.where(RelicPRFX.class).distinct(RelicPRFX.FIELD_NAME).findAll();
                    ArrayList<String> relicPrefixList = new ArrayList<>();
                    relicPrefixList.add(ALL_PICK_KOR);
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
                    relicGradeList.add(ALL_PICK_KOR);
                    for (RelicSFX relicSFXGrade : relicSFXGrades) {
                        if (relicSFXGrade != null) {
                            relicGradeList.add(relicSFXGrade.getRelicSuffixGrade() + GRADE_KOR);
                        }
                    }
                    picker_pick_relic_grade.setMinValue(0);
                    picker_pick_relic_grade.setMaxValue(relicSFXGrades.size());
                    picker_pick_relic_grade.setDisplayedValues(relicGradeList.toArray(new String[0]));

                    RealmResults<RelicSFX> relicSFXes = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_NAME).equalTo(RelicSFX.FIELD_TYPE, FangConstant.GUARDIAN_INIT_VALUE + 1).findAll();
                    ArrayList<String> relicSuffixList = new ArrayList<>();
                    relicSuffixList.add(ALL_PICK_KOR);
                    for (RelicSFX relicSFX:relicSFXes) {
                        if (relicSFX != null) {
                            relicSuffixList.add(relicSFX.getRelicSuffixName());
                        }
                    }
                    picker_pick_relic_suffix.setMinValue(0);
                    picker_pick_relic_suffix.setMaxValue(relicSFXes.size());
                    picker_pick_relic_suffix.setDisplayedValues(relicSuffixList.toArray(new String[0]));

                    picker_pick_relic_guardian.setOnValueChangedListener((picker, oldVal, newVal) -> {
                        RealmResults<RelicSFX> newRelicSFXes = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_NAME).equalTo(RelicSFX.FIELD_TYPE, newVal).findAll();
                        relicSuffixList.clear();
                        relicSuffixList.add(ALL_PICK_KOR);
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

                            String cs = newVal + FangConstant.CONSTRAINT_SEPARATOR
                                    + relicPrefixList.get(picker_pick_relic_prefix.getValue()) + FangConstant.CONSTRAINT_SEPARATOR
                                    + picker_pick_relic_grade.getValue() + FangConstant.CONSTRAINT_SEPARATOR
                                    + relicSuffixList.get(picker_pick_relic_suffix.getValue());

                            pickRelicSimRealmAdapter.getFilter().filter( cs.replace(ALL_PICK_KOR,"") );

                        } catch (ArrayIndexOutOfBoundsException e) {
                            Log.e(TAG,Log.getStackTraceString(e));
                        }
                    });

                    picker_pick_relic_prefix.setOnValueChangedListener((picker, oldVal, newVal) -> {
                        String cs = picker_pick_relic_guardian.getValue() + FangConstant.CONSTRAINT_SEPARATOR
                                + relicPrefixList.get(newVal) + FangConstant.CONSTRAINT_SEPARATOR
                                + picker_pick_relic_grade.getValue() + FangConstant.CONSTRAINT_SEPARATOR
                                + relicSuffixList.get(picker_pick_relic_suffix.getValue());
                        pickRelicSimRealmAdapter.getFilter().filter( cs.replace(ALL_PICK_KOR,"") );
                    });

                    picker_pick_relic_grade.setOnValueChangedListener((picker, oldVal, newVal) -> {
                        String cs = picker_pick_relic_guardian.getValue() + FangConstant.CONSTRAINT_SEPARATOR
                                + relicPrefixList.get(picker_pick_relic_prefix.getValue()) + FangConstant.CONSTRAINT_SEPARATOR
                                + newVal + FangConstant.CONSTRAINT_SEPARATOR
                                + relicSuffixList.get(picker_pick_relic_suffix.getValue());
                        pickRelicSimRealmAdapter.getFilter().filter( cs.replace(ALL_PICK_KOR,"") );
                    });

                    picker_pick_relic_suffix.setOnValueChangedListener((picker, oldVal, newVal) -> {
                        String cs = picker_pick_relic_guardian.getValue() + FangConstant.CONSTRAINT_SEPARATOR
                                + relicPrefixList.get(picker_pick_relic_prefix.getValue()) + FangConstant.CONSTRAINT_SEPARATOR
                                + picker_pick_relic_grade.getValue() + FangConstant.CONSTRAINT_SEPARATOR
                                + relicSuffixList.get(newVal);
                        pickRelicSimRealmAdapter.getFilter().filter( cs.replace(ALL_PICK_KOR, "") );
                    });
                    builder.setView(view).setPositiveButton(R.string.wear_kor, (dialog, which) -> {

                        RelicSim selectedRelic = pickRelicSimRealmAdapter.getSelectedRelic();
                        if( selectedRelic != null ) {
                            int relicID = selectedRelic.getRelicID();
                            realm.executeTransactionAsync( bgRealm -> {
                                HeroSim bgHeroSim = bgRealm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, heroID).findFirst();
                                RelicSim bgRelic = bgRealm.where(RelicSim.class).equalTo(RelicSim.FIELD_ID, relicID).findFirst();
                                if( bgHeroSim != null && bgRelic != null ) {
                                    bgRelic.setHeroWhoHasThis(bgHeroSim);
                                    bgHeroSim.addRelic(bgRelic,relicSlot,relicPosition);
                                    bgHeroSim.setRelicCombinations(relicSlot, bgRealm);
                                }
                            }, () -> {
                                RelicPRFX relicPrefix = selectedRelic.getPrefix();
                                RelicSFX relicSuffix = selectedRelic.getSuffix();
                                String message = heroSim.getHero().getHeroName() + ": " + (relicPrefix != null ? relicPrefix.getRelicPrefixName() : "" ) + " " +
                                        relicSuffix.getNameStarGrade() + " " + resources.getString(R.string.wear_kor);
                                for(OnUpdateEventListener listener : listeners ) {
                                    listener.updateEvent(FangConstant.RESULT_CODE_SUCCESS, message, null);
                                }
                            });


                        }
                    }).setNegativeButton(R.string.cancel_kor, null);

                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }

            }
        }
        return builder.create();
    }
}
