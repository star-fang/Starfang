package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.source.Heroes;

import java.util.ArrayList;
import java.util.Arrays;

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
    public static final String FIELD_HERO = "hero";
    public static final String FIELD_GRADE = "heroGrade";
    public static final String FIELD_LEVEL = "heroLevel";
    private static final Integer[] SPEC_LEVELS = {1, 10, 15, 20, 25, 30, 50, 70, 90};
    private static final Integer[] SPEC_SCORES_BY_LEVEL_INDEX = {2,6,14,24,48,36,48,72,84};
    private static final String[] GROWTH_RATES_GRADE = {"S", "A", "B", "C", "D"};
    private static final Double[] GROWTH_RATES_INIT = {2.5, 2.0, 1.5, 1.0, 0.5};
    private static int[] GROWTH_RATES_OFFSETS = {0,50,70,90,110,200};
    private static Double[] GROWTH_RATES_COEFS = {0.005,0.05,0.025,0.0125,0.0001};
    public static final String[] POWERS_KOR = {"공격력","정신력","방어력","순발력","사기"};

    @PrimaryKey
    private int heroNo;
    private Heroes hero;
    private int heroLevel; // 1 ~ 99
    private int heroGrade; // 1(~20),2(~40),3(~60),4(~80),5(~99)
    private RealmList<RealmInteger> heroPlusStats; // 교본작
    private RealmList<Integer> heroSpecsChecked; //  체크된 효과 인덱스 (~3개)
    private RealmList<MagicItemSim> heroMagicItemsSlot1; // 보패 슬롯1
    private RealmList<MagicItemSim> heroMagicItemsSlot2; // 보패 슬롯2
    private RealmList<ItemSim> heroItemSlot;  // 무기 방어구 보조구
    private RealmList<RealmInteger> heroPowers;
    private int heroPowerSum;
    private int heroPlusStatSum;
    private int heroSpecScoreSum;

    public HeroSim() {

    }

    public HeroSim(Heroes hero) {
        this.heroNo = hero.getHeroNo();
        this.hero = hero;
        this.heroLevel = 1;
        this.heroGrade = 1;
        this.heroPlusStats = new RealmList<>();
        for( int i = 0; i < Heroes.INIT_STATS.length; i++ ) {
            heroPlusStats.add(new RealmInteger(0));
        }
        this.heroSpecsChecked = null;
        this.heroMagicItemsSlot1 = null;
        this.heroMagicItemsSlot2 = null;
        this.heroItemSlot = null;
        this.heroPowers = new RealmList<>();
        for( int i = 0; i < Heroes.INIT_STATS.length; i++ ) {
            heroPowers.add(new RealmInteger(0));
        }
        this.heroPowerSum = 0;
        this.heroPlusStatSum = 0;
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

    public RealmList<RealmInteger> getHeroPlusStats() {
        return heroPlusStats;
    }

    public ArrayList<Integer> getHeroPowersList() {
        ArrayList<Integer> powers = new ArrayList<>();
        if(heroPowers != null) {
            for( RealmInteger power : heroPowers) {
                powers.add( power.toInt());
            }
        }
        return powers;
    }

    public void setHeroPowers(int power, int position) {
        RealmInteger integerAtPosition = this.heroPowers.get(position);
        if( integerAtPosition != null ) {
            integerAtPosition.setIntValue(power);
        }
    }

    public void setHeroPlusStats(int statUp, int position) {
        RealmInteger integerAtPosition = this.heroPlusStats.get(position);
        if( integerAtPosition != null) {
            integerAtPosition.setIntValue(statUp);
        }
    }

    public ArrayList<Integer> getHeroPlusStatList() {
        ArrayList<Integer> statsUp = new ArrayList<>();
        if(heroPlusStats != null) {
            for( RealmInteger stat : heroPlusStats) {
                statsUp.add( stat.toInt() );
            }
        }
        return statsUp;
    }

    public RealmList<Integer> getHeroSpecsChecked() {
        return heroSpecsChecked;
    }

    public ArrayList<Integer> getCheckedLevelList() {
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

    public void updateSpecsChecked( ArrayList<Integer> levels) {
        heroSpecsChecked.clear();
        if(levels != null) {
            for( Integer level : levels) {
                heroSpecsChecked.add( levelToIndex(level));
            }
        }
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

    public void setHero( Heroes hero ) {
        this.hero = hero;
    }
    public Heroes getHero() {
        return hero;
    }


    public int getHeroNo() {
        return heroNo;
    }

    public void setHeroNo(int heroNo) {
        this.heroNo = heroNo;
    }

    public int getHeroPowerSum() {
        return heroPowerSum;
    }

    public void setHeroPowerSum(int heroPowerSum) {
        this.heroPowerSum = heroPowerSum;
    }

    public int getHeroPlusStatSum() {
        return heroPlusStatSum;
    }

    public void setHeroPlusStatSum(int heroPlusStatSum) {
        this.heroPlusStatSum = heroPlusStatSum;
    }

    public int getHeroSpecScoreSum() {
        return heroSpecScoreSum;
    }

    public void setHeroSpecScoreSum(int heroSpecScoreSum) {
        this.heroSpecScoreSum = heroSpecScoreSum;
    }
}