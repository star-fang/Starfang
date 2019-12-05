package com.fang.starfang.local.model.realm.source;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// 190406 Heroes RealmList primitive type : String > RealmString
public class Heroes extends RealmObject {

    public static final String PREF_TABLE = "장수 정보";
    public static final String FIELD_ID = "heroNo";
    public static final String FIELD_NAME = "heroName";
    public static final String FIELD_NAME2 = "heroName2";
    public static final String FIELD_SPECS = "heroSpecs";
    public static final String FIELD_BRANCH = "heroBranch";
    public static final String FIELD_LINEAGE = "heroLineage";
    public static final String FIELD_BRANCH_ID = "branchNo";
    public static final String FIELD_COST = "heroCost";
    public static final String FIELD_STATS = "heroStats";
    public static final String INIT_DYNASTY = "인연";
    public static final String INIT_LINEAGE = "계보";
    public static final String INIT_COST = "COST";
    public static final String[] INIT_STATS = {"무","지","통","민","행"};
    public static final String[] INIT_SPECS = {"Lv30","Lv50","Lv70","Lv90","태수","군주"};

    @PrimaryKey
    private int heroNo;
    private String heroName;
    private String heroBranch;
    private String heroLineage;
    private int heroCost;
    private RealmList<RealmInteger> heroStats;
    private RealmList<RealmString> heroSpecs;
    private RealmList<RealmString> heroSpecValues;
    private String heroDestiny;
    private String heroName2;
    private int branchNo;
    private String heroPersonality;



    public int getHeroCost() {
        return heroCost;
    }

    public RealmList<RealmInteger> getHeroStats() {
        return heroStats;
    }

    public int getHeroNo() {
        return heroNo;
    }

    public String getHeroDestiny() {
        return heroDestiny;
    }

    public String getHeroBranch() {
        return heroBranch;
    }

    public String getHeroLineage() {
        return heroLineage;
    }

    public String getHeroName() {
        return heroName;
    }

    public RealmList<RealmString> getHeroSpecs() {
        return heroSpecs;
    }

    public RealmList<RealmString> getHeroSpecValues() {
        return heroSpecValues;
    }

    public String getHeroName2() {return heroName2;}

    public void setHeroCost(int heroCost) {
        this.heroCost = heroCost;
    }

    public void setHeroDestiny(String heroDestiny) {
        this.heroDestiny = heroDestiny;
    }

    public void setHeroBranch(String heroBranch) {
        this.heroBranch = heroBranch;
    }

    public void setHeroLineage(String heroLineage) {
        this.heroLineage = heroLineage;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    public void setHeroNo(int heroNo) {
        this.heroNo = heroNo;
    }

    public int getBranchNo() {
        return branchNo;
    }

    public String getHeroPersonality() {
        return heroPersonality;
    }
}
