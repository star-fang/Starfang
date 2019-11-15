package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Heroes;

import io.realm.Realm;
import io.realm.RealmList;

public class HeroesDialogFragment extends DialogFragment {

    private static final String TAG = "FANG_HERO_DIALOG";
    private Activity mActivity;
    private static final int[] MAX_LEVEL_BY_GRADE = {20,40,60,80,99};

    public static HeroesDialogFragment newInstance( int heroNo  ) {

        Realm realm = Realm.getDefaultInstance();
        HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID,heroNo).findFirst();
        if(heroSim == null) {
            heroSim = new HeroSim(heroNo);
            realm.beginTransaction();
            realm.copyToRealm(heroSim);
            realm.commitTransaction();
        }

        HeroesDialogFragment heroesDialogFragment = new HeroesDialogFragment();
        Bundle args = new Bundle();
        args.putInt("heroNo",heroNo);
        heroesDialogFragment.setArguments(args);

        return heroesDialogFragment;

    }

    public  HeroesDialogFragment() {

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

        Realm realm  = Realm.getDefaultInstance();
        Heroes hero = realm.where(Heroes.class).equalTo(Heroes.FIELD_ID,heroNo).findFirst();
        HeroSim heroSim = realm.where(HeroSim.class).equalTo(Heroes.FIELD_ID,heroNo).findFirst();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_heroes, null);
        if( hero != null && heroSim != null ) {
            ((AppCompatTextView) view.findViewById(R.id.dialog_title_branch)).setText(hero.getHeroBranch());
            ((AppCompatTextView) view.findViewById(R.id.dialog_title_name)).setText(hero.getHeroName());
            final AppCompatSeekBar seekbar_hero_grade = view.findViewById(R.id.seekbar_hero_grade);  // 0 ~ 4
            seekbar_hero_grade.setProgress(heroSim.getHeroGrade() - 1);

            //final AppCompatTextView text_seekbar_hero_grade = view.findViewById(R.id.text_seekbar_hero_grade); // 악사, 철거병,...
            final AppCompatTextView text_seekbar_hero_grade_value = view.findViewById(R.id.text_seekbar_hero_grade_value); // 1 ~ 5
            text_seekbar_hero_grade_value.setText(String.valueOf(heroSim.getHeroGrade()));


            final AppCompatSeekBar seekbar_hero_level = view.findViewById(R.id.seekbar_hero_level); // 0 ~ 98
            seekbar_hero_level.setProgress(heroSim.getHeroLevel() - 1);

            final AppCompatTextView text_seekbar_hero_level_value = view.findViewById(R.id.text_seekbar_hero_level_value); // 1 ~ 99
            text_seekbar_hero_level_value.setText(String.valueOf(heroSim.getHeroLevel()));

            int curGrade = seekbar_hero_grade.getProgress(); // 0 ~ 4
            int maxPlusStat = (curGrade < 4) ? MAX_LEVEL_BY_GRADE[curGrade] :
                    (hero.getHeroCost() + 16) * 5;

            RealmList<RealmInteger> heroBaseStats = hero.getHeroStats();
            RealmList<RealmInteger> heroPlusStats = heroSim.getHeroStatsUp();

            final AppCompatTextView text_sum_of_plus_stat_cur = view.findViewById(R.id.text_sum_of_plus_stat_cur);
            final AppCompatTextView text_sum_of_plus_stat_max = view.findViewById(R.id.text_sum_of_plus_stat_max);

            final int numberOfStats = Heroes.INIT_STATS.length;
            final AppCompatTextView[] text_base_plus_stat = new AppCompatTextView[numberOfStats];
            final AppCompatTextView[] text_seekbar_hero_stat_cur = new AppCompatTextView[numberOfStats];
            final AppCompatTextView[] text_seekbar_hero_stat_mx = new AppCompatTextView[numberOfStats];
            final AppCompatSeekBar[] seekbar_hero_stat = new AppCompatSeekBar[numberOfStats];

            int sum_of_plus_stat_cur_int = 0;
            for (int i = 0; i < numberOfStats; i++) {
                seekbar_hero_stat[i] = view.findViewById(getResources().getIdentifier("seekbar_hero_stat" + (i + 1), "id", mActivity.getPackageName()));
                text_base_plus_stat[i] = view.findViewById(getResources().getIdentifier("text_base_plus_stat" + (i + 1), "id", mActivity.getPackageName()));
                text_seekbar_hero_stat_cur[i] = view.findViewById(getResources().getIdentifier("text_seekbar_hero_stat" + (i + 1) + "_cur", "id", mActivity.getPackageName()));
                text_seekbar_hero_stat_mx[i] = view.findViewById(getResources().getIdentifier("text_seekbar_hero_stat" + (i + 1) + "_mx", "id", mActivity.getPackageName()));
                text_seekbar_hero_stat_mx[i].setText(String.valueOf(maxPlusStat));

                seekbar_hero_stat[i].setMax(maxPlusStat);
                RealmInteger baseStat = heroBaseStats.get(i);
                RealmInteger plusStat = heroPlusStats.get(i);
                if (baseStat != null && plusStat != null) {
                    int plusStatInt = plusStat.toInt();
                    int totalStat = baseStat.toInt() + plusStatInt;
                    text_base_plus_stat[i].setText(String.valueOf(totalStat));
                    text_seekbar_hero_stat_cur[i].setText(plusStat.toString());
                    seekbar_hero_stat[i].setProgress(plusStatInt);
                    sum_of_plus_stat_cur_int += plusStatInt;
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

            }

            int sum_of_plus_stat_cur_max = Math.min(500, maxPlusStat * numberOfStats);
            text_sum_of_plus_stat_cur.setText(String.valueOf(sum_of_plus_stat_cur_int));
            text_sum_of_plus_stat_max.setText(String.valueOf(sum_of_plus_stat_cur_max));

            seekbar_hero_grade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text_seekbar_hero_grade_value.setText(String.valueOf(progress + 1));
                    int curLevel = seekbar_hero_level.getProgress() + 1;  // 1 ~ 99
                    if (curLevel > MAX_LEVEL_BY_GRADE[progress]) {
                        seekbar_hero_level.setProgress(MAX_LEVEL_BY_GRADE[progress] - 1);
                    } else if (progress > 0) {
                        if (curLevel < MAX_LEVEL_BY_GRADE[progress - 1]) {
                            seekbar_hero_level.setProgress(MAX_LEVEL_BY_GRADE[progress - 1] - 1);
                        }
                    }
                    int maxPlusStat = (progress < 4) ? MAX_LEVEL_BY_GRADE[progress] :
                            (hero.getHeroCost() + 16) * 5;
                    int sumOfMax = 0;
                    for (int i = 0; i < numberOfStats; i++) {
                        text_seekbar_hero_stat_mx[i].setText(String.valueOf(maxPlusStat));
                        seekbar_hero_stat[i].setMax(maxPlusStat);
                        sumOfMax += maxPlusStat;
                    }

                    sumOfMax = Math.min(500, sumOfMax);
                    text_sum_of_plus_stat_max.setText(String.valueOf(sumOfMax));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });

            seekbar_hero_level.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int curGrade = seekbar_hero_grade.getProgress(); // 0 ~ 4
                    if (progress + 1 > MAX_LEVEL_BY_GRADE[curGrade]) {
                        seekbar_hero_grade.setProgress(curGrade + 1);
                    } else if (curGrade > 0) {
                        if (progress + 1 < MAX_LEVEL_BY_GRADE[curGrade - 1]) {
                            seekbar_hero_grade.setProgress(curGrade - 1);
                        }
                    }

                    text_seekbar_hero_level_value.setText(String.valueOf(progress + 1));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


            builder.setView(view).setPositiveButton("변경", (dialog, which) -> {
                realm.beginTransaction();
                heroSim.setHeroGrade(seekbar_hero_grade.getProgress() + 1);
                heroSim.setHeroLevel(seekbar_hero_level.getProgress() + 1);
                for (int i = 0; i < Heroes.INIT_STATS.length; i++) {
                    heroSim.setHeroStatsUp(seekbar_hero_stat[i].getProgress(), i);
                }
                realm.commitTransaction();

            }).setNegativeButton("취소", null);

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

}
