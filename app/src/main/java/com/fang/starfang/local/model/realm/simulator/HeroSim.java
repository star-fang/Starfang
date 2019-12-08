package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;

import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
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
    private static final Integer[] SPEC_SCORES_BY_LEVEL_INDEX = {2, 6, 14, 24, 48, 36, 48, 72, 84};
    private static final String[] GROWTH_RATES_GRADE = {"S", "A", "B", "C", "D"};
    private static final Double[] GROWTH_RATES_INIT = {2.5, 2.0, 1.5, 1.0, 0.5};
    private static int[] GROWTH_RATES_OFFSETS = {0, 50, 70, 90, 110, 200};
    private static Double[] GROWTH_RATES_COEFS = {0.005, 0.05, 0.025, 0.0125, 0.0001};
    public static final String[] POWERS_KOR = {"공격력", "정신력", "방어력", "순발력", "사기"};

    @PrimaryKey
    private int heroNo;
    private Heroes hero;
    private int heroLevel; // 1 ~ 99
    private int heroGrade; // 1(~20),2(~40),3(~60),4(~80),5(~99)
    private RealmList<RealmInteger> heroPlusStats; // 교본작
    private RealmList<Integer> heroSpecsChecked; //  체크된 효과 인덱스 (~3개)
    private RealmList<RelicSim> heroRelicSlot1; // 보패 슬롯1
    private RealmList<RelicSim> heroRelicSlot2; // 보패 슬롯2
    private int currentRelicSlot; // 1, 2
    private ItemSim heroWeapon;
    private ItemSim heroArmor;
    private ItemSim heroAid;
    private RealmList<RealmInteger> heroPowers;
    private int heroPowerSum;
    private int heroPlusStatSum;
    private int heroSpecScoreSum;
    private int heroReinforcement; // 1 ~ 12
    // 1, 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77
    // r1 -> l 1 ~ 20 // g1
    // r2 -> l 7 ~ 20 // g1
    // r3 -> l 14 ~ 40 // g1, 2
    // r4 -> l 21 ~ 40 // g2
    // r5 -> l 28 ~ 40 // g2
    // r6 -> l 35 ~ 60 // g2, 3
    // r7 -> l 42 ~ 60 // g3
    // r8 -> l 49 ~ 60 // g3
    // r9 -> l 56 ~ 80 // g3, 4
    // r10 -> l 63 ~ 80 // g4
    // r11 -> l 70 ~ 80 // g4
    // r12 -> l 77 ~ 99 // g4, 5

    private Branch heroBranch;

    public HeroSim() {

    }

    public HeroSim(Heroes hero) {
        this.heroNo = hero.getHeroNo();
        this.hero = hero;
        this.heroLevel = 1;
        this.heroGrade = 1;
        this.heroReinforcement = 1;
        this.heroPlusStats = new RealmList<>();
        for (int i = 0; i < Heroes.INIT_STATS.length; i++) {
            heroPlusStats.add(new RealmInteger(0));
        }
        this.heroSpecsChecked = null;
        this.heroRelicSlot1 = new RealmList<>(); // empty slot
        this.heroRelicSlot2 = new RealmList<>(); // position information belong to RelicSim
        this.heroWeapon = null;
        this.heroArmor = null;
        this.heroAid = null;
        this.heroPowers = new RealmList<>();
        for (int i = 0; i < Heroes.INIT_STATS.length; i++) {
            heroPowers.add(new RealmInteger(0));
        }
        this.heroPowerSum = 0;
        this.heroPlusStatSum = 0;
        this.heroBranch = null;
        this.currentRelicSlot = 1;
    }

    public static Integer getSpecScoreByLevel(int level) {
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

    public RealmList<RealmInteger> getHeroPowers() {
        return heroPowers;
    }

    public ArrayList<Integer> getHeroPowersList() {
        ArrayList<Integer> powers = new ArrayList<>();
        if (heroPowers != null) {
            for (RealmInteger power : heroPowers) {
                powers.add(power.toInt());
            }
        }
        return powers;
    }

    public void setHeroPowers(int power, int position) {
        RealmInteger integerAtPosition = this.heroPowers.get(position);
        if (integerAtPosition != null) {
            integerAtPosition.setIntValue(power);
        }
    }

    public void setHeroPlusStats(int statUp, int position) {
        RealmInteger integerAtPosition = this.heroPlusStats.get(position);
        if (integerAtPosition != null) {
            integerAtPosition.setIntValue(statUp);
        }
    }

    public ArrayList<Integer> getHeroPlusStatList() {
        ArrayList<Integer> statsUp = new ArrayList<>();
        if (heroPlusStats != null) {
            for (RealmInteger stat : heroPlusStats) {
                statsUp.add(stat.toInt());
            }
        }
        return statsUp;
    }

    public RealmList<Integer> getHeroSpecsChecked() {
        return heroSpecsChecked;
    }

    public ArrayList<Integer> getCheckedLevelList() {
        ArrayList<Integer> levels = new ArrayList<>();
        if (heroSpecsChecked != null) {
            for (Integer index : heroSpecsChecked) {
                try {
                    levels.add(SPEC_LEVELS[index]);
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
            }
        }
        return levels;
    }

    public void updateSpecsChecked(ArrayList<Integer> levels) {
        heroSpecsChecked.clear();
        if (levels != null) {
            for (Integer level : levels) {
                heroSpecsChecked.add(levelToIndex(level));
            }
        }
    }

    private Integer levelToIndex(int level) {
        return Arrays.asList(SPEC_LEVELS).indexOf(level);
    }


    public static double calcGrowthRateByStatAndGrade(int stat, String grade) {
        Double rate = GROWTH_RATES_INIT[Arrays.asList(GROWTH_RATES_GRADE).indexOf(grade)];
        for (int i = 0; i < GROWTH_RATES_COEFS.length; i++) {
            rate += Math.max(0.0,
                    GROWTH_RATES_COEFS[i] *
                            (double) (
                                    Math.min(GROWTH_RATES_OFFSETS[i + 1], stat) - GROWTH_RATES_OFFSETS[i]
                            )
            );
        } // end for
        return rate;
    }

    public void updateHero(Realm realm) {
        this.hero = realm.where(Heroes.class).equalTo(Heroes.FIELD_ID, heroNo).findFirst();
    }

    public void updateBranch(Realm realm) {
        if (hero != null) {
            this.heroBranch = realm.where(Branch.class).equalTo(Branch.FIELD_ID, hero.getBranchNo()).findFirst();
        }
    }

    public Heroes getHero() {
        return hero;
    }


    public int getHeroNo() {
        return heroNo;
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

    public ItemSim getHeroWeapon() {
        return heroWeapon;
    }

    public void setHeroWeapon(ItemSim heroWeapon) {
        this.heroWeapon = heroWeapon;
    }

    public ItemSim getHeroArmor() {
        return heroArmor;
    }

    public void setHeroArmor(ItemSim heroArmor) {
        this.heroArmor = heroArmor;
    }

    public ItemSim getHeroAid() {
        return heroAid;
    }

    public void setHeroAid(ItemSim heroAid) {
        this.heroAid = heroAid;
    }

    public int getHeroReinforcement() {
        return heroReinforcement;
    }

    public void setHeroReinforcement(int heroReinforcement) {
        this.heroReinforcement = heroReinforcement;
    }

    public RealmList<ItemSim> getHeroItemSims() {
        RealmList<ItemSim> itemSims = new RealmList<>();
        itemSims.add(heroWeapon);
        itemSims.add(heroArmor);
        itemSims.add(heroAid);
        return itemSims;
    }

    public void setHeroItemSim(ItemSim itemSim, int position) {
        switch (position) {
            case 0:
                heroWeapon = itemSim;
                break;
            case 1:
                heroArmor = itemSim;
                break;
            case 2:
                heroAid = itemSim;
            default:
        }
    }

    public void updateBasePower() {
        if (heroBranch != null && hero != null) {
            RealmList<RealmString> branchStatGGs = heroBranch.getBranchStatGGs();
            RealmList<RealmInteger> heroStats = hero.getHeroStats();
            if (branchStatGGs != null && heroStats != null) {
                int powerSum = 0;
                for (int i = 0; i < Heroes.INIT_STATS.length; i++) {
                    RealmString branchStatGG = branchStatGGs.get(i);
                    RealmInteger heroStat = heroStats.get(i);
                    RealmInteger heroPlusStat = heroPlusStats.get(i);
                    String branchStatGGStr = branchStatGG == null ? "S" : branchStatGG.toString();
                    int heroStatInt = heroStat == null ? 0 : heroStat.toInt();
                    int heroPlusStatInt = heroPlusStat == null ? 0 : heroPlusStat.toInt();
                    int statSum = heroStatInt + heroPlusStatInt;
                    double growthRate = calcGrowthRateByStatAndGrade(heroStatInt, branchStatGGStr);
                    int power = (int) Math.floor(Math.max(0, heroPlusStatInt - 100) + statSum / 2.0 + heroLevel * growthRate);
                    setHeroPowers(power, i);
                    powerSum += power;
                } // end for
                setHeroPowerSum(powerSum);
            }
        }

    }

    public Branch getHeroBranch() {
        return heroBranch;
    }


    public int getCurrentRelicSlot() {
        return currentRelicSlot;
    }

    public void setCurrentRelicSlot(int currentRelicSlot) {
        this.currentRelicSlot = currentRelicSlot;
    }

    public RealmList<RelicSim> getHeroRelicSlot(int slot) {
        switch (slot) {
            case 1:
                return heroRelicSlot1;
            case 2:
                return heroRelicSlot2;
            default:
                return null;
        }
    }

    private RelicSim getHeroRelic(RealmList<RelicSim> heroRelicSlot, int position) {
        RelicSim heroRelic = null;
        for (RelicSim relicSim : heroRelicSlot) {
            if (relicSim.getPositionInSlot() == position) {
                heroRelic = relicSim;
                break;
            } // end if
        } // end for
        return heroRelic;
    }

    public void addRelic( RelicSim newRelicSim, int slot, int position) {
        RealmList<RelicSim> heroRelicSlot = getHeroRelicSlot(slot);
        if( heroRelicSlot != null ) {
            RelicSim heroRelic = getHeroRelic( heroRelicSlot, position );
            if( heroRelic != null ) {
                heroRelicSlot.remove(heroRelic);
                heroRelic.setRelicLevel(1);
                heroRelic.setSlot(0);
                heroRelic.setPositionInSlot(0);
                heroRelic.setHeroWhoHasThis(null);
            }
            heroRelicSlot.add(newRelicSim);
            newRelicSim.setSlot(slot);
            newRelicSim.setPositionInSlot(position);
        }
    }

    public int setRelicLevelUp( int slot, int position ) {
        RealmList<RelicSim> heroRelicSlot = getHeroRelicSlot(slot);
        int level = 0;
        if( heroRelicSlot != null ) {
            RelicSim heroRelic = getHeroRelic( heroRelicSlot, position );
            if (heroRelic != null) {
                int currLevel = heroRelic.getRelicLevel();
                level = currLevel == 5 ? 1 : currLevel + 1;
                heroRelic.setRelicLevel(  level );
            } // end if
        } // end if
        return level;
    }

}