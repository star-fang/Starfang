package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.AppConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.NormalItem;
import com.fang.starfang.ui.main.recycler.adapter.PowersRecyclerAdapter;
import com.fang.starfang.ui.main.recycler.adapter.SpecsRecycleAdapter;
import com.fang.starfang.util.ScreenUtils;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

import io.realm.RealmList;

public class HeroesDialogFragment extends UpdateDialogFragment {

    private static final String TAG = "FANG_HERO_DIALOG";
    private static final int[] MAX_LEVEL_BY_GRADE = {20, 40, 60, 80, 99};
    private final int[] MIN_LEVEL_BY_REINFORCE = {1, 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77};
    private final int[] MAX_LEVEL_BY_REINFORCE = {20, 20, 40, 40, 40, 60, 60, 60, 80, 80, 80, 99};
    //private final int[] MIN_GRADE_BY_REINFORCE = {};
    //private final int[] MAX_GRADE_BY_REINFORCE = {};

    private PowersRecyclerAdapter powerAdapter;

    private AppCompatTextView text_picked_item_name;
    private AppCompatTextView text_picked_item_cate_main;
    private AppCompatTextView[] texts_item_reinforcement;
    private AppCompatTextView[] texts_item_name;
    private View[] cells_hero_item;
    private AppCompatButton button_release_picked_item;
    private AppCompatButton button_reinforce_picked_item;
    private AppCompatButton button_modify_picked_item;
    private RealmList<ItemSim> itemSims;

    private View view;

    public static HeroesDialogFragment newInstance(int heroID) {

        HeroesDialogFragment heroesDialogFragment = new HeroesDialogFragment();
        Bundle args = new Bundle();
        args.putInt(AppConstant.INTENT_KEY_HERO_ID, heroID);
        heroesDialogFragment.setArguments(args);

        return heroesDialogFragment;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            int itemMainCate = intent.getIntExtra(AppConstant.INTENT_KEY_ITEM_CATE_MAIN, -1);
            if (itemMainCate > -1) {
                try {
                    switch (requestCode) {
                        case AppConstant.REQ_CODE_PICK_ITEM_DIALOG_FRAGMENT:
                            String itemName = intent.getStringExtra(AppConstant.INTENT_KEY_ITEM_NAME);
                            String itemReinforce = intent.getStringExtra(AppConstant.INTENT_KEY_ITEM_REINFORCE);
                            int itemID = intent.getIntExtra(AppConstant.INTENT_KEY_ITEM_ID,0);
                            ItemSim itemSim = realm.where(ItemSim.class).equalTo(ItemSim.FIELD_ID, itemID).findFirst();
                            itemSims.set(itemMainCate, itemSim);
                            texts_item_reinforcement[itemMainCate].setText(itemReinforce);
                            texts_item_name[itemMainCate].setText(itemName);
                            text_picked_item_name.setText(itemName);
                            button_release_picked_item.setEnabled(true);
                            button_reinforce_picked_item.setEnabled(itemReinforce != null);
                            Snackbar.make(view,  itemReinforce + " " + itemName + " " + AppConstant.WEAR_KOR, Snackbar.LENGTH_SHORT).show();
                            onUpdateEventListener.updateEvent(AppConstant.RESULT_CODE_SUCCESS_MODIFY_ITEM);
                            break;
                        case AppConstant.REQ_CODE_REINFORCE_ITEM_DIALOG_FRAGMENT:
                            itemReinforce = intent.getStringExtra(AppConstant.INTENT_KEY_ITEM_REINFORCE);
                            texts_item_reinforcement[itemMainCate].setText(intent.getStringExtra(AppConstant.INTENT_KEY_ITEM_REINFORCE));
                            Snackbar.make(view,  itemReinforce + " " + AppConstant.REINFORCE_KOR, Snackbar.LENGTH_SHORT).show();
                            onUpdateEventListener.updateEvent(AppConstant.RESULT_CODE_SUCCESS_MODIFY_ITEM);
                            break;
                        default:
                    } // end switch

                    powerAdapter.notifyDataSetChanged();

                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.d(TAG, e.toString());
                }
            }
        } // end if ok
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        view = View.inflate(mActivity, R.layout.dialog_heroes, null);

        Bundle args = getArguments();
        if (args != null) {
            int heroID = getArguments().getInt(AppConstant.INTENT_KEY_HERO_ID);

            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_HERO + "." + Heroes.FIELD_ID, heroID).findFirst();
            if (heroSim != null) {
                Heroes hero = heroSim.getHero();

                final AppCompatSeekBar seek_bar_hero_grade = view.findViewById(R.id.seek_bar_hero_grade);  // 0 ~ 4
                final AppCompatTextView text_seek_bar_hero_grade_value = view.findViewById(R.id.text_seek_bar_hero_grade_value); // 1 ~ 5
                final AppCompatSeekBar seek_bar_hero_level = view.findViewById(R.id.seek_bar_hero_level); // 0 ~ 98
                final AppCompatEditText text_seek_bar_hero_level_value = view.findViewById(R.id.text_seek_bar_hero_level_value); // 1 ~ 99
                final AppCompatSeekBar seek_bar_hero_reinforce = view.findViewById(R.id.seek_bar_hero_reinforce);
                final AppCompatTextView text_seek_bar_hero_reinforce_value = view.findViewById(R.id.text_seek_bar_hero_reinforce_value); // 0 ~ 11
                // 1, 7, 14, 21, 3

                String heroBranchStr = hero.getHeroBranch() + ((hero.getHeroNo() > 0) ? "계" : "");
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

                Branch branch = heroSim.getHeroBranch();

                final RecyclerView recycler_view_pasv_grades = view.findViewById(R.id.recycler_view_pasv_grades);
                final RecyclerView recycler_view_hero_grades = view.findViewById(R.id.recycler_view_hero_grades);
                final RecyclerView recycler_view_dialog_heroes_cell_power = view.findViewById(R.id.recycler_view_dialog_heroes_cell_power);
                int calculateNoOfColumns = ScreenUtils.calculateNoOfColumns(mActivity, 65.0);
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

                if (branch != null) {
                    RealmString branchGrade = branch.getBranchGrade().get(curGrade);
                    if (branchGrade != null) {
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
                }
                if (branchPasvSpecs != null && branchPasvSpecGrades != null) {
                    for (int i = 0; i < Branch.NUM_PASVS; i++) {
                        titles_pasv.add("승급" + branchPasvSpecGrades.get(i));
                        RealmString branchPasvSpec = branchPasvSpecs.get(i);
                        String branchPasvSpecStr = "";
                        if (branchPasvSpec != null) {
                            branchPasvSpecStr = branchPasvSpec.toString();
                        }
                        specs_pasv.add(branchPasvSpecStr);
                        //Log.d(TAG, "add specs_pasv : " + branchPasvSpecStr);

                        String branchPasvSpecValStr = "";
                        if (branchPasvSpecVals != null) {
                            RealmString branchPasvSpecVal = branchPasvSpecVals.get(i);
                            if (branchPasvSpecVal != null) {
                                branchPasvSpecValStr = branchPasvSpecVal.toString();
                            }
                        }
                        specVals_pasv.add(branchPasvSpecValStr);
                    }
                }
                if (branchSpecs != null) {
                    for (Branch.INIT_SPECS i : Branch.INIT_SPECS.values()) {
                        titles.add(i.name());
                        RealmString branchSpec = branchSpecs.get(i.ordinal());

                        String branchSpecStr = "";
                        if (branchSpec != null) {
                            branchSpecStr = branchSpec.toString();
                        }
                        specs.add(branchSpecStr);
                        //Log.d(TAG, "add specs : " + branchSpecStr);
                        String branchSpecValStr = "";
                        if (branchSpecVals != null) {
                            RealmString branchSpecVal = branchSpecVals.get(i.ordinal());
                            if (branchSpecVal != null) {
                                branchSpecValStr = branchSpecVal.toString();
                            }
                        }
                        specVals.add(branchSpecValStr);
                    }
                }
                if (heroSpecs != null) {
                    for (int i = 0; i < 4; i++) {
                        titles.add(Heroes.INIT_SPECS[i]);
                        String heroSpecStr = "";
                        RealmString heroSpec = heroSpecs.get(i);
                        if (heroSpec != null) {
                            heroSpecStr = heroSpec.toString();
                        }
                        specs.add(heroSpecStr);

                        String heroSpecValStr = "";
                        if (heroSpecVals != null) {
                            RealmString heroSpecVal = heroSpecVals.get(i);
                            if (heroSpecVal != null) {
                                heroSpecValStr = heroSpecVal.toString();
                            }
                        }
                        specVals.add(heroSpecValStr);
                    }

                    for (int i = 4; i < 6; i++) {
                        titles_pasv.add(Heroes.INIT_SPECS[i]);
                        String heroPasvSpecStr = "";
                        RealmString heroPasvSpec = heroSpecs.get(i);
                        if (heroPasvSpec != null) {
                            heroPasvSpecStr = heroPasvSpec.toString();
                        }
                        specs_pasv.add(heroPasvSpecStr);
                        specVals_pasv.add("");
                    }
                }

                final AppCompatTextView text_dialog_heroes_cell_specs_total = view.findViewById(R.id.text_dialog_heroes_cell_specs_total);
                ArrayList<Integer> checkedSpecLevels = heroSim.getCheckedLevelList();
                final SpecsRecycleAdapter pasvAdapter = new SpecsRecycleAdapter(titles_pasv, specs_pasv, specVals_pasv, null, true, null);
                final SpecsRecycleAdapter heroAdapter = new SpecsRecycleAdapter(titles, specs, specVals, checkedSpecLevels, false, text_dialog_heroes_cell_specs_total);
                recycler_view_pasv_grades.setAdapter(pasvAdapter);
                recycler_view_hero_grades.setAdapter(heroAdapter);
                text_dialog_heroes_cell_specs_total.setText(String.valueOf(heroSim.getHeroSpecScoreSum()));

                pasvAdapter.getFilter().filter((curGrade + 1) + "");
                heroAdapter.getFilter().filter((curLevel + 1) + "");

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

                itemSims = heroSim.getHeroItemSims();

                powerAdapter = new PowersRecyclerAdapter(branchStatGGs, heroBaseStats, heroStatsUpList, curLevel + 1, curReinforce + 1, normalItems, itemSims);
                recycler_view_dialog_heroes_cell_power.setAdapter(powerAdapter);

                final AppCompatTextView text_sum_of_plus_stat_cur = view.findViewById(R.id.text_sum_of_plus_stat_cur);
                final AppCompatTextView text_sum_of_plus_stat_max = view.findViewById(R.id.text_sum_of_plus_stat_max);

                final int numberOfStats = Heroes.INIT_STATS.length;
                final AppCompatTextView[] text_base_plus_stat = new AppCompatTextView[numberOfStats];
                final AppCompatEditText[] text_seek_bar_hero_stat_cur = new AppCompatEditText[numberOfStats];
                final AppCompatTextView[] text_seek_bar_hero_stat_max = new AppCompatTextView[numberOfStats];
                final AppCompatSeekBar[] seek_bar_hero_stat = new AppCompatSeekBar[numberOfStats];

                for (int i = 0; i < numberOfStats; i++) {
                    seek_bar_hero_stat[i] = view.findViewById(getResources().getIdentifier("seekbar_hero_stat" + (i + 1), "id", mActivity.getPackageName()));
                    text_base_plus_stat[i] = view.findViewById(getResources().getIdentifier("text_base_plus_stat" + (i + 1), "id", mActivity.getPackageName()));
                    text_seek_bar_hero_stat_cur[i] = view.findViewById(getResources().getIdentifier("text_seekbar_hero_stat" + (i + 1) + "_cur", "id", mActivity.getPackageName()));
                    text_seek_bar_hero_stat_max[i] = view.findViewById(getResources().getIdentifier("text_seekbar_hero_stat" + (i + 1) + "_max", "id", mActivity.getPackageName()));

                    seek_bar_hero_stat[i].setMax(maxPlusStat);
                    text_seek_bar_hero_stat_max[i].setText(String.valueOf(maxPlusStat));
                    RealmInteger baseStat = heroBaseStats.get(i);
                    Integer plusStat = heroStatsUpList.get(i);
                    if (baseStat != null && plusStat != null) {
                        int plusStatInt = plusStat;
                        int totalStat = baseStat.toInt() + plusStatInt;
                        text_base_plus_stat[i].setText(String.valueOf(totalStat));
                        text_seek_bar_hero_stat_cur[i].setText(String.valueOf(plusStat));
                        seek_bar_hero_stat[i].setProgress(plusStatInt);
                    }

                    int finalI = i;
                    seek_bar_hero_stat[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            int sumOfCur = 0;
                            int sumOfMax = 0;
                            for (int i = 0; i < numberOfStats; i++) {
                                sumOfCur += seek_bar_hero_stat[i].getProgress();
                                sumOfMax += seek_bar_hero_stat[i].getMax();
                            }

                            sumOfMax = Math.min(500, sumOfMax);

                            if (sumOfCur > sumOfMax) {
                                seek_bar_hero_stat[finalI].setProgress(progress - 1);
                            } else {
                                text_sum_of_plus_stat_cur.setText(String.valueOf(sumOfCur));
                                text_seek_bar_hero_stat_cur[finalI].setText(String.valueOf(progress));
                                heroStatsUpList.set(finalI, progress);
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
                text_sum_of_plus_stat_max.setText(String.valueOf((curGrade + 1) * 100));

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
                                seek_bar_hero_stat[i].setMax(maxPlusStat);
                                text_seek_bar_hero_stat_max[i].setText(String.valueOf(maxPlusStat));
                                sumOfMax += maxPlusStat;
                            }

                            sumOfMax = Math.min(500, sumOfMax);
                            text_sum_of_plus_stat_max.setText(String.valueOf(sumOfMax));
                        } catch (StringIndexOutOfBoundsException e) {
                            Log.d(TAG, e.toString());
                        }

                        if (branch != null) {
                            RealmString branchGrade = branch.getBranchGrade().get(progress);
                            if (branchGrade != null) {
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
                        heroAdapter.getFilter().filter(curLevel + "");
                        //pasvAdapter.getFilter().filter((curGrade + 1)+"");
                        try {
                            if (curLevel > MAX_LEVEL_BY_GRADE[curGrade]) {
                                seek_bar_hero_grade.setProgress(curGrade + 1);
                            } else if (curGrade > 0) {
                                if (curLevel < MAX_LEVEL_BY_GRADE[curGrade - 1]) {
                                    seek_bar_hero_grade.setProgress(curGrade - 1);
                                }
                            }

                            if (curLevel > MAX_LEVEL_BY_REINFORCE[curReinforce]) {
                                seek_bar_hero_reinforce.setProgress(curReinforce + 1);
                            } else if (curLevel < MIN_LEVEL_BY_REINFORCE[curReinforce]) {
                                seek_bar_hero_reinforce.setProgress(curReinforce - 1);
                            }
                        } catch (StringIndexOutOfBoundsException e) {
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
                        } catch (StringIndexOutOfBoundsException e) {
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

                texts_item_reinforcement = new AppCompatTextView[3];
                texts_item_name = new AppCompatTextView[3];
                cells_hero_item = new View[3];
                texts_item_reinforcement[0] = view.findViewById(R.id.text_item_weapon_reinforcement);
                texts_item_reinforcement[1] = view.findViewById(R.id.text_item_armor_reinforcement);
                texts_item_reinforcement[2] = view.findViewById(R.id.text_item_aid_reinforcement);

                texts_item_name[0] = view.findViewById(R.id.text_item_weapon_name);
                texts_item_name[1] = view.findViewById(R.id.text_item_armor_name);
                texts_item_name[2] = view.findViewById(R.id.text_item_aid_name);

                cells_hero_item[0] = view.findViewById(R.id.cell_hero_weapon);
                cells_hero_item[1] = view.findViewById(R.id.cell_hero_armor);
                cells_hero_item[2] = view.findViewById(R.id.cell_hero_aid);


                button_release_picked_item = view.findViewById(R.id.button_release_picked_item);
                button_reinforce_picked_item = view.findViewById(R.id.button_reinforce_picked_item);
                button_modify_picked_item = view.findViewById(R.id.button_modify_picked_item);
                text_picked_item_name = view.findViewById(R.id.text_picked_item_name);
                text_picked_item_cate_main = view.findViewById(R.id.text_picked_item_cate_main);

                for (int i = 0; i < itemSims.size(); i++) {

                    ItemSim curItemSim = itemSims.get(i);
                    Item curItem = curItemSim == null ? null : curItemSim.getItem();
                    boolean reinforceImpossible = curItem == null
                            || AppConstant.ITEM_GRADE_NO_REINFORCE.equals(curItem.getItemGrade());
                    texts_item_reinforcement[i].setText(reinforceImpossible ? null
                            : "+" + curItemSim.getItemReinforcement());
                    texts_item_name[i].setText(curItem == null ? null : curItem.getItemName());
                    int finalI = i;
                    cells_hero_item[i].setOnClickListener(vPickItem -> {
                        ItemSim pickedItemSim = itemSims.get(finalI);
                        Item pickedItem = pickedItemSim == null ? null : pickedItemSim.getItem();
                        String selectedMainCate = text_picked_item_cate_main.getText().toString();
                        int selectedMainCateIndex = getMainCateIndex(selectedMainCate);

                        if (selectedMainCateIndex == finalI) {
                            button_release_picked_item.setEnabled(false);
                            button_modify_picked_item.setEnabled(false);
                            button_reinforce_picked_item.setEnabled(false);
                            text_picked_item_cate_main.setText(null);
                            text_picked_item_name.setText(null);
                            vPickItem.setBackgroundResource(R.drawable.rect_black);

                        } else {
                            if( selectedMainCateIndex > -1 ) {
                                cells_hero_item[selectedMainCateIndex].setBackgroundResource(R.drawable.rect_black);
                            }
                            vPickItem.setBackgroundResource(R.drawable.rect_checked);
                            text_picked_item_cate_main.setText(getMainCateName(finalI));
                            text_picked_item_name.setText(texts_item_name[finalI].getText().toString());

                            if (pickedItem != null) {
                                button_release_picked_item.setEnabled(true);
                                button_reinforce_picked_item.setEnabled(!texts_item_reinforcement[finalI].getText().toString().isEmpty());

                            } else {
                                button_release_picked_item.setEnabled(false);
                                button_reinforce_picked_item.setEnabled(false);
                            }

                            button_modify_picked_item.setEnabled(true);

                            button_modify_picked_item.setOnClickListener(vModify -> {
                                PickItemSimDialogFragment pickItemSimDialogFragment =
                                        PickItemSimDialogFragment.newInstance(heroSim.getHeroNo(), getItemSubCate(finalI, branch), finalI);
                                pickItemSimDialogFragment.setTargetFragment(HeroesDialogFragment.this, AppConstant.REQ_CODE_PICK_ITEM_DIALOG_FRAGMENT);
                                pickItemSimDialogFragment.show(fragmentManager, TAG);

                            });

                            button_reinforce_picked_item.setOnClickListener(vReinforce -> {
                                ItemSim reinforcingItem = itemSims.get(finalI);
                                if(reinforcingItem != null) {
                                    ReinforceDialogFragment reinforceDialogFragment = ReinforceDialogFragment.newInstance(reinforcingItem.getItemID(), finalI);
                                    reinforceDialogFragment.setTargetFragment(HeroesDialogFragment.this, AppConstant.REQ_CODE_REINFORCE_ITEM_DIALOG_FRAGMENT);
                                    reinforceDialogFragment.show(fragmentManager, TAG);
                                }
                            });

                            button_release_picked_item.setOnClickListener(vRelease -> {
                                ItemSim releasingItemSim = itemSims.get(finalI);
                                Item releasingItem = releasingItemSim == null ? null : releasingItemSim.getItem();
                                if( releasingItem != null ) {
                                    button_release_picked_item.setEnabled(false);
                                    button_reinforce_picked_item.setEnabled(false);
                                    realm.beginTransaction();
                                    heroSim.setHeroItemSim(null, finalI);
                                    releasingItemSim.setHeroWhoHasThis(null);
                                    realm.commitTransaction();
                                    itemSims.set(finalI, null);
                                    texts_item_reinforcement[finalI].setText(null);
                                    texts_item_name[finalI].setText(null);
                                    text_picked_item_name.setText(null);
                                    powerAdapter.notifyDataSetChanged();
                                    onUpdateEventListener.updateEvent(AppConstant.RESULT_CODE_SUCCESS_MODIFY_ITEM);
                                    Snackbar.make(view, releasingItem.getItemName() + " " + AppConstant.RELEASE_KOR, Snackbar.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                } // end for


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
                        int statUp = seek_bar_hero_stat[i].getProgress();
                        heroSim.setHeroPlusStats(statUp, i);
                        sumPlusStats += statUp;

                        RecyclerView.ViewHolder holder = recycler_view_dialog_heroes_cell_power.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            AppCompatTextView actv = holder.itemView.findViewById(R.id.text_dialog_heroes_cell_power);
                            String powerStr = actv.getText().toString();
                            int power = NumberUtils.toInt(powerStr, 0);
                            heroSim.setHeroPowers(power, i);
                            sumPowers += power;
                            //Log.d(TAG, i + " power: " + power);
                        }
                    }
                    String sumSpecScoreStr = text_dialog_heroes_cell_specs_total.getText().toString();
                    int sumSpecScores = NumberUtils.toInt(sumSpecScoreStr, 0);
                    heroSim.setHeroSpecScoreSum(sumSpecScores);
                    heroSim.setHeroPowerSum(sumPowers);
                    heroSim.setHeroPlusStatSum(sumPlusStats);

                    realm.commitTransaction();

                    onUpdateEventListener.updateEvent(AppConstant.RESULT_CODE_SUCCESS_MODIFY_HERO);


                }).setNegativeButton(R.string.cancel_kor, null);

            } // end if heroSim != null

        } // end if args != null
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(params);
            }
        }
    }

    private int getMainCateIndex( String mainCate ) {
        switch( mainCate ) {
            case AppConstant.WEAPON_KOR:
                return 0;
            case AppConstant.ARMOR_KOR:
                return 1;
            case AppConstant.AID_KOR:
                return 2;
            default:
                return -1;
        }
    }

    private String getMainCateName( int index ) {
        switch( index ) {
            case 0:
                return AppConstant.WEAPON_KOR;
            case 1:
                return AppConstant.ARMOR_KOR;
            default:
                return AppConstant.AID_KOR;
        }
    }

    private String getItemSubCate(int position, Branch branch) {
        switch (position) {
            case 0:
                return branch == null ? AppConstant.ALL_PICK_KOR : branch.getBranchWeaponSubCate();
            case 1:
                return branch == null ? AppConstant.ALL_PICK_KOR : branch.getBranchArmorSubCate();
            default:
                return AppConstant.ALL_PICK_KOR;
        }
    }
}
