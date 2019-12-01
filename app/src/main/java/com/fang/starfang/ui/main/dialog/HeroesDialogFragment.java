package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.NormalItem;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFloatingRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.PowersRecyclerAdapter;
import com.fang.starfang.ui.main.recycler.adapter.SpecsRecycleAdapter;
import com.fang.starfang.util.ScreenUtils;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

public class HeroesDialogFragment extends DialogFragment {

    private static final String TAG = "FANG_HERO_DIALOG";
    private Activity mActivity;
    private static final int[] MAX_LEVEL_BY_GRADE = {20,40,60,80,99};
    private final int[] MIN_LEVEL_BY_REINFORCE = {1,7,14,21,28,35,42,49,56,63,70,77};
    private final int[] MAX_LEVEL_BY_REINFORCE = {20,20,40,40,40,60,60,60,80,80,80,99};
    private Realm realm;

    public static HeroesDialogFragment newInstance( int heroNo  ) {

        HeroesDialogFragment heroesDialogFragment = new HeroesDialogFragment();
        Bundle args = new Bundle();
        args.putInt("heroNo",heroNo);
        heroesDialogFragment.setArguments(args);

        return heroesDialogFragment;

    }

    public  HeroesDialogFragment() {
        Log.d(TAG, "constructed");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG,"_ON ATTATCH");
        if (context instanceof Activity){
            mActivity=(Activity) context;
        }
    }

   @NonNull
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState ) {

        Bundle args = getArguments();
       int heroNo = 0;
        if(args != null) {
            heroNo = getArguments().getInt("heroNo");
        }
       AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
       View view = View.inflate(mActivity, R.layout.dialog_heroes, null);
        realm  = Realm.getDefaultInstance();
        HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_HERO+"."+Heroes.FIELD_ID,heroNo).findFirst();
        if( heroSim != null ) {
        Heroes hero = heroSim.getHero();

            final AppCompatSeekBar seek_bar_hero_grade = view.findViewById(R.id.seek_bar_hero_grade);  // 0 ~ 4
            final AppCompatTextView text_seek_bar_hero_grade_value = view.findViewById(R.id.text_seek_bar_hero_grade_value); // 1 ~ 5
            final AppCompatSeekBar seek_bar_hero_level = view.findViewById(R.id.seek_bar_hero_level); // 0 ~ 98
            final AppCompatEditText text_seek_bar_hero_level_value = view.findViewById(R.id.text_seek_bar_hero_level_value); // 1 ~ 99
            final AppCompatSeekBar seek_bar_hero_reinforce = view.findViewById(R.id.seek_bar_hero_reinforce);
            final AppCompatTextView text_seek_bar_hero_reinforce_value = view.findViewById(R.id.text_seek_bar_hero_reinforce_value); // 0 ~ 11
            // 1, 7, 14, 21, 3

            String heroBranchStr = hero.getHeroBranch() + ((hero.getHeroNo()>0)? "계" : "");
            ((AppCompatTextView) view.findViewById(R.id.dialog_title_branch)).setText(heroBranchStr);
            ((AppCompatTextView) view.findViewById(R.id.dialog_title_name)).setText(hero.getHeroName());

            int curLevel = heroSim.getHeroLevel() - 1; // 0 ~ 98
            seek_bar_hero_level.setProgress(curLevel);
            text_seek_bar_hero_level_value.setText(String.valueOf(curLevel + 1));

            int curGrade = heroSim.getHeroGrade() - 1; // 0 ~ 4
            seek_bar_hero_grade.setProgress(curGrade);
            text_seek_bar_hero_grade_value.setText(String.valueOf(curGrade + 1));

            int curReinforce = heroSim.getHeroReinforcement() - 1; // 0 ~ 11
            seek_bar_hero_reinforce.setProgress(curReinforce);
            text_seek_bar_hero_reinforce_value.setText(String.valueOf(curReinforce + 1));

            Branch branch = realm.where(Branch.class).equalTo(Branch.FIELD_ID,hero.getBranchNo()).findFirst();

            final RecyclerView recycler_view_pasv_grades = view.findViewById(R.id.recycler_view_pasv_grades);
            final RecyclerView recycler_view_hero_grades = view.findViewById(R.id.recycler_view_hero_grades);
            final RecyclerView recycler_view_dialog_heroes_cell_power = view.findViewById(R.id.recycler_view_dialog_heroes_cell_power);
            int calculateNoOfColumns = ScreenUtils.calculateNoOfColumns(mActivity,65.0);
            recycler_view_pasv_grades.setLayoutManager(new GridLayoutManager(mActivity, calculateNoOfColumns));
            recycler_view_hero_grades.setLayoutManager(new GridLayoutManager(mActivity, calculateNoOfColumns));
            recycler_view_dialog_heroes_cell_power.setLayoutManager(new LinearLayoutManager(mActivity));
            RealmList<RealmString> heroSpecs = hero.getHeroSpecs();   // 0 ~ 3 : pick & 4,5 : pasv
            RealmList<RealmString> heroSpecVals = hero.getHeroSpecValues(); // 0 ~ 3
            RealmList<RealmString> branchSpecs = null; // 0 ~ 4
            RealmList<RealmString> branchSpecVals = null; // 0 ~ 4
            RealmList<RealmString> branchPasvSpecs = null; // 0 ~ 2
            RealmList<RealmString> branchPasvSpecVals = null; // 0 ~ 2
            RealmList<RealmString> branchStatGGs = null;
            RealmList<RealmInteger> branchPasvSpecGrades = null;
            String branchWeaponSubCate = null;
            String branchArmorSubCate = null;
            String branchNormalAidType = null;

            ArrayList<String> titles_pasv = new ArrayList<>(); // 0 ~ 4
            ArrayList<String> specs_pasv = new ArrayList<>();
            ArrayList<String> specVals_pasv = new ArrayList<>();

            ArrayList<String> titles = new ArrayList<>(); // 0 ~ 8
            ArrayList<String> specs = new ArrayList<>();
            ArrayList<String> specVals = new ArrayList<>();

            if(branch != null) {
                RealmString branchGrade = branch.getBranchGrade().get(curGrade);
                if( branchGrade != null ) {
                    ((AppCompatTextView) view.findViewById(R.id.dialog_title_branch_grade)).setText(branchGrade.toString());
                }

                branchSpecs = branch.getBranchSpecs();
                branchSpecVals = branch.getBranchSpecValues();
                branchPasvSpecs = branch.getBranchPasvSpecs();
                branchPasvSpecVals = branch.getBranchPasvSpecValues();
                branchStatGGs = branch.getBranchStatGGs();
                branchPasvSpecGrades = branch.getBranchPasvSpecGrades();
                branchWeaponSubCate = branch.getBranchWeaponSubCate();
                branchArmorSubCate = branch.getBranchArmorSubCate();
                branchNormalAidType = branch.getBranchNormalAidType();
                //Log.d(TAG, branchWeaponSubCate + "\n" + branchArmorSubCate + "\n" + branchNormalAidType);
            }
            if(branchPasvSpecs != null && branchPasvSpecGrades != null) {
                for (int i = 0; i < Branch.NUM_PASVS; i++) {
                    titles_pasv.add("승급" + branchPasvSpecGrades.get(i));
                    RealmString branchPasvSpec = branchPasvSpecs.get(i);
                    String branchPasvSpecStr = "";
                    if( branchPasvSpec != null) {
                        branchPasvSpecStr = branchPasvSpec.toString();
                    }
                    specs_pasv.add(branchPasvSpecStr);
                    //Log.d(TAG, "add specs_pasv : " + branchPasvSpecStr);

                    String branchPasvSpecValStr = "";
                    if(branchPasvSpecVals != null) {
                        RealmString branchPasvSpecVal = branchPasvSpecVals.get(i);
                        if(branchPasvSpecVal != null) {
                            branchPasvSpecValStr = branchPasvSpecVal.toString();
                        }
                    }
                    specVals_pasv.add(branchPasvSpecValStr);
                }
            }
            if(branchSpecs != null) {
                for (Branch.INIT_SPECS i : Branch.INIT_SPECS.values()) {
                    titles.add(i.name());
                    RealmString branchSpec = branchSpecs.get(i.ordinal());

                    String branchSpecStr = "";
                    if( branchSpec != null) {
                        branchSpecStr = branchSpec.toString();
                    }
                    specs.add(branchSpecStr);
                    //Log.d(TAG, "add specs : " + branchSpecStr);
                    String branchSpecValStr = "";
                    if(branchSpecVals != null) {
                        RealmString branchSpecVal = branchSpecVals.get(i.ordinal());
                        if(branchSpecVal != null) {
                            branchSpecValStr = branchSpecVal.toString();
                        }
                    }
                    specVals.add(branchSpecValStr);
                }
            }
            if(heroSpecs != null) {
                for(int i = 0; i < 4; i++ ) {
                    titles.add(Heroes.INIT_SPECS[i]);
                    String heroSpecStr = "";
                    RealmString heroSpec = heroSpecs.get(i);
                    if(heroSpec != null ) {
                        heroSpecStr = heroSpec.toString();
                    }
                    specs.add(heroSpecStr);

                    String heroSpecValStr = "";
                    if(heroSpecVals != null ) {
                        RealmString heroSpecVal = heroSpecVals.get(i);
                        if( heroSpecVal != null) {
                            heroSpecValStr = heroSpecVal.toString();
                        }
                    }
                    specVals.add(heroSpecValStr);
                }

                for(int i = 4; i < 6; i++) {
                    titles_pasv.add(Heroes.INIT_SPECS[i]);
                    String heroPasvSpecStr = "";
                    RealmString heroPasvSpec = heroSpecs.get(i);
                    if(heroPasvSpec != null) {
                        heroPasvSpecStr = heroPasvSpec.toString();
                    }
                    specs_pasv.add(heroPasvSpecStr);
                    specVals_pasv.add("");
                }
            }

            final AppCompatTextView text_dialog_heroes_cell_specs_total= view.findViewById(R.id.text_dialog_heroes_cell_specs_total);
            ArrayList<Integer> checkedSpecLevels = heroSim.getCheckedLevelList();
            final SpecsRecycleAdapter pasvAdapter = new SpecsRecycleAdapter(titles_pasv,specs_pasv,specVals_pasv, null, true, null);
            final SpecsRecycleAdapter heroAdapter = new SpecsRecycleAdapter(titles,specs,specVals, checkedSpecLevels, false, text_dialog_heroes_cell_specs_total);
            recycler_view_pasv_grades.setAdapter(pasvAdapter);
            recycler_view_hero_grades.setAdapter(heroAdapter);
            text_dialog_heroes_cell_specs_total.setText(String.valueOf(heroSim.getHeroSpecScoreSum()));

            pasvAdapter.getFilter().filter((curGrade + 1)+"");
            heroAdapter.getFilter().filter(( curLevel + 1 ) + "");

            int maxPlusStat = (curGrade < 4) ? MAX_LEVEL_BY_GRADE[curGrade] :
                    (hero.getHeroCost() + 16) * 5;

            RealmList<RealmInteger> heroBaseStats = hero.getHeroStats();
            ArrayList<Integer> heroStatsUpList = heroSim.getHeroPlusStatList();

            RealmList<NormalItem> normalItems = new RealmList<>();

            NormalItem normalWeapon = branchWeaponSubCate == null ? null : realm.where(NormalItem.class).equalTo(NormalItem.FIELD_CATE_SUB, branchWeaponSubCate).findFirst();
            NormalItem normalArmor = branchArmorSubCate == null ? null : realm.where(NormalItem.class).equalTo(NormalItem.FIELD_CATE_SUB, branchArmorSubCate).findFirst();
            NormalItem normalAid = branchNormalAidType == null ? null : realm.where(NormalItem.class).equalTo(NormalItem.FIELD_TYPE, branchNormalAidType).findFirst();

            normalItems.add(normalWeapon);
            normalItems.add(normalArmor);
            normalItems.add(normalAid);

            final PowersRecyclerAdapter powerAdapter = new PowersRecyclerAdapter(branchStatGGs,heroBaseStats,heroStatsUpList,curLevel + 1, curReinforce + 1, normalItems);
            recycler_view_dialog_heroes_cell_power.setAdapter(powerAdapter);

            final AppCompatTextView text_sum_of_plus_stat_cur = view.findViewById(R.id.text_sum_of_plus_stat_cur);
            final AppCompatTextView text_sum_of_plus_stat_max = view.findViewById(R.id.text_sum_of_plus_stat_max);

            final int numberOfStats = Heroes.INIT_STATS.length;
            final AppCompatTextView[] text_base_plus_stat = new AppCompatTextView[numberOfStats];
            final AppCompatEditText[] text_seekbar_hero_stat_cur = new AppCompatEditText[numberOfStats];
            final AppCompatTextView[] text_seekbar_hero_stat_max = new AppCompatTextView[numberOfStats];
            final AppCompatSeekBar[] seekbar_hero_stat = new AppCompatSeekBar[numberOfStats];

            for (int i = 0; i < numberOfStats; i++) {
                seekbar_hero_stat[i] = view.findViewById(getResources().getIdentifier("seekbar_hero_stat" + (i + 1), "id", mActivity.getPackageName()));
                text_base_plus_stat[i] = view.findViewById(getResources().getIdentifier("text_base_plus_stat" + (i + 1), "id", mActivity.getPackageName()));
                text_seekbar_hero_stat_cur[i] = view.findViewById(getResources().getIdentifier("text_seekbar_hero_stat" + (i + 1) + "_cur", "id", mActivity.getPackageName()));
                text_seekbar_hero_stat_max[i] = view.findViewById(getResources().getIdentifier("text_seekbar_hero_stat" + (i + 1) + "_max", "id", mActivity.getPackageName()));

                seekbar_hero_stat[i].setMax(maxPlusStat);
                text_seekbar_hero_stat_max[i].setText(String.valueOf(maxPlusStat));
                RealmInteger baseStat = heroBaseStats.get(i);
                Integer plusStat = heroStatsUpList.get(i);
                if (baseStat != null && plusStat != null) {
                    int plusStatInt = plusStat;
                    int totalStat = baseStat.toInt() + plusStatInt;
                    text_base_plus_stat[i].setText(String.valueOf(totalStat));
                    text_seekbar_hero_stat_cur[i].setText(String.valueOf(plusStat));
                    seekbar_hero_stat[i].setProgress(plusStatInt);
                }

                int finalI = i;
                seekbar_hero_stat[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        int sumOfCur = 0;
                        int sumOfMax = 0;
                        for (int i = 0; i < numberOfStats; i++) {
                            sumOfCur += seekbar_hero_stat[i].getProgress();
                            sumOfMax += seekbar_hero_stat[i].getMax();
                        }

                        sumOfMax = Math.min(500, sumOfMax);

                        if (sumOfCur > sumOfMax) {
                            seekbar_hero_stat[finalI].setProgress(progress - 1);
                        } else {
                            text_sum_of_plus_stat_cur.setText(String.valueOf(sumOfCur));
                            text_seekbar_hero_stat_cur[finalI].setText(String.valueOf(progress));
                            heroStatsUpList.set(finalI,progress);
                            powerAdapter.notifyDataSetChanged();
                            RealmInteger baseStat = heroBaseStats.get(finalI);
                            if (baseStat != null) {
                                int basePlusProgressInt = baseStat.toInt() + progress;
                                text_base_plus_stat[finalI].setText(String.valueOf(basePlusProgressInt));
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

            } // end for

            text_sum_of_plus_stat_cur.setText(String.valueOf(heroSim.getHeroPlusStatSum()));
            text_sum_of_plus_stat_max.setText(String.valueOf( (curGrade + 1 ) * 100 ) );

            seek_bar_hero_grade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text_seek_bar_hero_grade_value.setText(String.valueOf(progress + 1));
                    int curLevel = seek_bar_hero_level.getProgress() + 1;  // 1 ~ 99
                    int curGrade = progress + 1; // 1 ~ 5
                    pasvAdapter.getFilter().filter(curGrade + "");
                    try {
                        if (curLevel > MAX_LEVEL_BY_GRADE[progress]) {
                            seek_bar_hero_level.setProgress(MAX_LEVEL_BY_GRADE[progress] - 1);
                        } else if (progress > 0) {
                            if (curLevel < MAX_LEVEL_BY_GRADE[progress - 1]) {
                                seek_bar_hero_level.setProgress(MAX_LEVEL_BY_GRADE[progress - 1] - 1);
                            }
                        }
                        int maxPlusStat = (progress < 4) ? MAX_LEVEL_BY_GRADE[progress] :
                                (hero.getHeroCost() + 16) * 5;
                        int sumOfMax = 0;
                        for (int i = 0; i < numberOfStats; i++) {
                            seekbar_hero_stat[i].setMax(maxPlusStat);
                            text_seekbar_hero_stat_max[i].setText(String.valueOf(maxPlusStat));
                            sumOfMax += maxPlusStat;
                        }

                        sumOfMax = Math.min(500, sumOfMax);
                        text_sum_of_plus_stat_max.setText(String.valueOf(sumOfMax));
                    } catch ( StringIndexOutOfBoundsException e ) {
                        Log.d(TAG, e.toString());
                    }

                    if(branch != null) {
                        RealmString branchGrade = branch.getBranchGrade().get(progress);
                        if( branchGrade != null ) {
                            ((AppCompatTextView) view.findViewById(R.id.dialog_title_branch_grade)).setText(branchGrade.toString());
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });

            seek_bar_hero_level.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { // 0 ~ 98
                    int curGrade = seek_bar_hero_grade.getProgress(); // 0 ~ 4
                    int curReinforce = seek_bar_hero_reinforce.getProgress();  // 0 ~ 11
                    int curLevel = progress + 1; // 1 ~ 99;

                    powerAdapter.setLevel(curLevel);
                    powerAdapter.notifyDataSetChanged();
                    heroAdapter.getFilter().filter(curLevel+"");
                    //pasvAdapter.getFilter().filter((curGrade + 1)+"");
                    try {
                        if (curLevel > MAX_LEVEL_BY_GRADE[curGrade]) {
                            seek_bar_hero_grade.setProgress(curGrade + 1);
                        } else if (curGrade > 0) {
                            if (curLevel < MAX_LEVEL_BY_GRADE[curGrade - 1]) {
                                seek_bar_hero_grade.setProgress(curGrade - 1);
                            }
                        }

                        if( curLevel > MAX_LEVEL_BY_REINFORCE[curReinforce] ) {
                            seek_bar_hero_reinforce.setProgress(curReinforce + 1);
                        } else if( curLevel < MIN_LEVEL_BY_REINFORCE[curReinforce]) {
                            seek_bar_hero_reinforce.setProgress(curReinforce - 1);
                        }
                    } catch( StringIndexOutOfBoundsException e) {
                        Log.d(TAG, e.toString());
                    }

                    text_seek_bar_hero_level_value.setText(String.valueOf(progress + 1));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seek_bar_hero_reinforce.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int curLevel = seek_bar_hero_level.getProgress() + 1; // 0 ~ 99
                    int curReinforce = progress + 1; // 1 ~ 12

                    powerAdapter.setReinforce(curReinforce);
                    powerAdapter.notifyDataSetChanged();

                    try {
                        int minLevelByReinforce = MIN_LEVEL_BY_REINFORCE[progress];
                        int maxLevelByReinforce = MAX_LEVEL_BY_REINFORCE[progress];

                        if (curLevel < minLevelByReinforce) {
                            seek_bar_hero_level.setProgress(minLevelByReinforce - 1);
                        } else if (curLevel > maxLevelByReinforce) {
                            seek_bar_hero_level.setProgress(maxLevelByReinforce - 1);
                        }
                    } catch( StringIndexOutOfBoundsException e) {
                        Log.d(TAG, e.toString());
                    }

                    text_seek_bar_hero_reinforce_value.setText(String.valueOf(curReinforce));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            builder.setView(view).setPositiveButton(R.string.modify_kor, (dialog, which) -> {
                realm.beginTransaction();
                int heroGrade = seek_bar_hero_grade.getProgress() + 1;
                int heroLevel = seek_bar_hero_level.getProgress() + 1;
                int heroReinforce = seek_bar_hero_reinforce.getProgress() + 1;
                heroSim.setHeroGrade(heroGrade);
                heroSim.setHeroLevel(heroLevel);
                heroSim.setHeroReinforcement(heroReinforce);
                heroSim.updateSpecsChecked(checkedSpecLevels);

                int sumPowers = 0;
                int sumPlusStats = 0;
                for (int i = 0; i < Heroes.INIT_STATS.length; i++) {
                    int statUp = seekbar_hero_stat[i].getProgress();
                    heroSim.setHeroPlusStats(statUp, i);
                    sumPlusStats += statUp;

                    RecyclerView.ViewHolder holder = recycler_view_dialog_heroes_cell_power.findViewHolderForAdapterPosition(i);
                    if(holder != null ) {
                        AppCompatTextView actv = holder.itemView.findViewById(R.id.text_dialog_heroes_cell_power);
                        String powerStr = actv.getText().toString();
                        int power = NumberUtils.toInt(powerStr,0);
                        heroSim.setHeroPowers(power, i);
                        sumPowers += power;
                        Log.d(TAG, i + " power: " + power);
                    }
                }
                String sumSpecScoreStr = text_dialog_heroes_cell_specs_total.getText().toString();
                int sumSpecScroes = NumberUtils.toInt(sumSpecScoreStr, 0);
                heroSim.setHeroSpecScoreSum(sumSpecScroes);
                heroSim.setHeroPowerSum(sumPowers);
                heroSim.setHeroPlusStatSum(sumPlusStats);

                realm.commitTransaction();

                HeroesFixedRealmAdapter fixedInstance = HeroesFixedRealmAdapter.getInstance();
                if(fixedInstance != null ) {
                    fixedInstance.notifyDataSetChanged();
                }
                HeroesFloatingRealmAdapter floatingInstance = HeroesFloatingRealmAdapter.getInstance();
                if( floatingInstance != null ) {
                    floatingInstance.notifyDataSetChanged();
                }


            }).setNegativeButton(R.string.cancel_kor, null);



        }


       return builder.create();
   }

   @Override
   public void onResume() {
       super.onResume();
       Dialog dialog = getDialog();
       if( dialog != null ) {
           Window window = dialog.getWindow();
           if( window != null ) {
               WindowManager.LayoutParams params = window.getAttributes();
               params.width = WindowManager.LayoutParams.MATCH_PARENT;
               params.height = WindowManager.LayoutParams.MATCH_PARENT;
               window.setAttributes(params);
           }
       }
   }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        realm.close();
        super.onDismiss(dialog);
    }

}
