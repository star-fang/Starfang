package com.fang.starfang.local.model.realm.source;

import com.fang.starfang.local.model.realm.primitive.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// 0406 Heroes RealmList primitive type > RealmString으로 교체
public class Heroes extends RealmObject {

    public static final String PREF_TABLE = "장수 정보";
    public static final String FIELD_ID = "heroNo";
    public static final String FIELD_NAME = "heroName";
    public static final String FIELD_NAME2 = "heroName2";
    public static final String FIELD_SPECS = "heroSpecs";
    public static final String FIELD_BRANCH = "heroBranch";
    public static final String FIELD_LINEAGE = "heroLineage";
    public static final String FIELD_COST = "heroCost";
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
    private RealmList<Integer> heroStats;
    private RealmList<RealmString> heroSpecs;
    private RealmList<RealmString> heroSpecValues;
    private String heroDestiny;
    private String heroName2;

    public int getHeroCost() {
        return heroCost;
    }

    public RealmList<Integer> getHeroStats() {
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

    public void setHeroSpecs(RealmList<RealmString> heroSpecs) {
        this.heroSpecs = heroSpecs;
    }

    public void setHeroSpecValues(RealmList<RealmString> heroSpecValues) {
        this.heroSpecValues = heroSpecValues;
    }

    public void setHeroStats(RealmList<Integer> heroStats) {
        this.heroStats = heroStats;
    }





    /*
      var HeroNo: Int? = 0  // 번호
    var HeroName: String? = null // 이름
    var HeroLine: String? = null // 병종
    var HeroLineage: String? = null // 계보
    var HeroCost: Int? = 0 // 초기 COST
    var HeroStats = arrayOfNulls<String>(5)// 스탯 : 무지통민행
    var HeroSpecs = arrayOfNulls<String>(6) // 특수효과1234,태수,군주
    var HeroSpecVals = arrayOfNulls<String>(4) // 특수효과수치1234
    var HeroDestiny: String? = null // 인연
    var Date_modified: String? = null
     */
}
