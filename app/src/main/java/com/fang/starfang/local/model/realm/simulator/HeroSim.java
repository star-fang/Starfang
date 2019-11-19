package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.Spec;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

//11-13 ~ 할일
/*
1.Item 테이블에 itemNo 컬럼 추가하기 V
2.아이템,보패 생성 구현
3.장수 아이템, 보패 착용 구현
4.장수 승급,레벨,교본작구현 V
5.스탯 산출
6.데미지 산출 시뮬레이션 구현
 */
public class HeroSim extends RealmObject {

    public static final String FIELD_ID = "heroNo";
    private static final Integer[] SPEC_LEVELS = {1, 10, 15, 20, 25, 30, 50, 70, 90};
    private static final Integer[] SPEC_SCORES_BY_LEVEL_INDEX = {2,6,14,24,48,36,48,72,84};
    private static final String[] GROWTH_RATES_GRADE = {"S", "A", "B", "C", "D"};
    private static final Double[] GROWTH_RATES_INIT = {2.5, 2.0, 1.5, 1.0, 0.5};
    private static int[] GROWTH_RATES_OFFSETS = {0,50,70,90,110,200};
    private static Double[] GROWTH_RATES_COEFS = {0.005,0.05,0.025,0.0125,0.0001};
    @PrimaryKey
    private int heroNo;   // 장수 고유 번호
    private int heroLevel; // 1 ~ 99
    private int heroGrade; // 1(~20),2(~40),3(~60),4(~80),5(~99)
    private RealmList<RealmInteger> heroStatsUp; // 교본작
    private RealmList<Integer> heroSpecsChecked; //  체크된 효과 인덱스 (~3개)
    private RealmList<MagicItemSim> heroMagicItemsSlot1; // 보패 슬롯1
    private RealmList<MagicItemSim> heroMagicItemsSlot2; // 보패 슬롯2
    private RealmList<ItemSim> heroItemSlot;  // 무기 방어구 보조구

    public HeroSim() {

    }

    public HeroSim(int heroNo) {
        this.heroNo = heroNo;
        this.heroLevel = 1;
        this.heroGrade = 1;
        this.heroStatsUp = new RealmList<>();
        for( int i = 0; i < Heroes.INIT_STATS.length; i++ ) {
            heroStatsUp.add(new RealmInteger(0));
        }
        this.heroSpecsChecked = null;
        this.heroMagicItemsSlot1 = null;
        this.heroMagicItemsSlot2 = null;
        this.heroItemSlot = null;
    }

    public static Integer getSpecScoreByLevel( int level ) {
        return SPEC_SCORES_BY_LEVEL_INDEX[Arrays.asList(SPEC_LEVELS).indexOf(level)];
    }

    public int getHeroLevel() {
        return heroLevel;
    }

    public void setHeroLevel(int heroLevel) {
        this.heroLevel = heroLevel;
    }

    public int getHeroGrade() {
        return heroGrade;
    }

    public void setHeroGrade(int heroGrade) {
        this.heroGrade = heroGrade;
    }

    public RealmList<RealmInteger> getHeroStatsUp() {
        return heroStatsUp;
    }

    public void setHeroStatsUp(int statUp, int position) {
        RealmInteger integerAtPostion = this.heroStatsUp.get(position);
        if( integerAtPostion != null) {
            integerAtPostion.setIntValue(statUp);
        }
    }

    public RealmList<Integer> getHeroSpecsChecked() {
        return heroSpecsChecked;
    }

    public ArrayList<Integer> getCheckedLevels() {
        ArrayList<Integer> levels = new ArrayList<>();
        if(heroSpecsChecked != null) {
            for( Integer index : heroSpecsChecked ) {
                try {
                    levels.add(SPEC_LEVELS[index]);
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
            }
        }
        return levels;
    }

    public void setHeroSpecsChecked(RealmList<Integer> heroSpecsChecked) {
        this.heroSpecsChecked = heroSpecsChecked;
    }

    public void updateSepcsChecked( ArrayList<Integer> levels) {
        heroSpecsChecked.clear();
        if(levels != null) {
            for( Integer level : levels) {
                heroSpecsChecked.add(levelToIndex(level));
            }
        }
    }

    public String getSumOfSpecScores() {
        int sum = 0;
        if(heroSpecsChecked != null) {
            for(Integer index : heroSpecsChecked ) {
                sum += SPEC_SCORES_BY_LEVEL_INDEX[index];
            }
        }
        return String.valueOf(sum);
    }

    private Integer levelToIndex(int level) {
        return Arrays.asList(SPEC_LEVELS).indexOf(level);
    }


    public RealmList<MagicItemSim> getHeroMagicItemsSlot1() {
        return heroMagicItemsSlot1;
    }

    public void setHeroMagicItemsSlot1(RealmList<MagicItemSim> heroMagicItemsSlot1) {
        this.heroMagicItemsSlot1 = heroMagicItemsSlot1;
    }

    public RealmList<MagicItemSim> getHeroMagicItemsSlot2() {
        return heroMagicItemsSlot2;
    }

    public void setHeroMagicItemsSlot2(RealmList<MagicItemSim> heroMagicItemsSlot2) {
        this.heroMagicItemsSlot2 = heroMagicItemsSlot2;
    }


    public int getHeroNo() {
        return heroNo;
    }

    public RealmList<ItemSim> getHeroItemSlot() {
        return heroItemSlot;
    }

    public void setHeroItemSlot(RealmList<ItemSim> heroItemSlot) {
        this.heroItemSlot = heroItemSlot;
    }

    public static double calcGrowthRateByStatAndGrade(int stat, String grade ) {
        Double rate = GROWTH_RATES_INIT[Arrays.asList(GROWTH_RATES_GRADE).indexOf(grade)];
        for(int i = 0; i < GROWTH_RATES_COEFS.length; i++ ) {
            rate += Math.max(0.0,
                    GROWTH_RATES_COEFS[i] *
                    (double)(
                            Math.min(GROWTH_RATES_OFFSETS[i+1], stat) - GROWTH_RATES_OFFSETS[i]
                    )
            );
        } // end for
        return rate;
    }
}