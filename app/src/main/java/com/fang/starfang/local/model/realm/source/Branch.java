package com.fang.starfang.local.model.realm.source;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Branch extends RealmObject {

    public static final String PREF_TABLE = "병종 정보";
    public static final String FIELD_ID = "branchNo";
    public static final String FIELD_NAME = "branchName";
    public static final String FIELD_NAME2 = "branchName2";
    public static final String FIELD_GRADE = "branchGrade";
    public static final String FIELD_CATE_WEAPON = "branchWeaponSubCate";
    public static final String FIELD_CATE_ARMOR = "branchArmorSubCate";
    public static final String FIELD_NORMAL_TYPE = "branchNormalAidType";
    public static final String[] INIT_STATS = {"공","정","방","순","사"};
    public static final int NUM_PASVS = 3;

    public String getBranchNormalAidType() {
        return branchNormalAidType;
    }
    //public static final String[] INIT_PASVS = {"승급2","승급3","승급4"};

    public enum INIT_SPECS {Lv01,Lv10,Lv15,Lv20,Lv25}
    public static final String INIT_HP = "HP";
    public static final String INIT_MP = "MP";
    public static final String INIT_EP = "EP";
    public static final String INIT_MOVING = "이동력";
    public static String updateTime;

    private int branchNo;
    private String branchName;
    private RealmList<RealmString> branchStatGGs; // growth grades 공정방순사  S A B C D
    private int branchHP;
    private int branchMP;
    private int branchEP;
    private RealmList<RealmString> branchPasvSpecs; // passive specs
    private RealmList<RealmString> branchPasvSpecValues; // passive spec values
    private RealmList<RealmInteger> branchPasvSpecGrades;
    private int branchMoving;
    private String branchWeaponSubCate;
    private String branchArmorSubCate;
    private RealmList<RealmString> branchSpecs; // 10, 15, 20 ,25
    private RealmList<RealmString> branchSpecValues; // 10, 15, 20 ,25
    private RealmList<Double> branchHiddenStats; // 공정방순사
    private String branchName2;
    private String branchMagic;
    private RealmList<RealmString> branchGrade;
    private int branchHPgg;
    private String branchNormalAidType;


    public RealmList<RealmString> getBranchGrade() {
        return branchGrade;
    }
    public String getBranchName() {return branchName;}
    public RealmList<RealmString> getBranchStatGGs() {return branchStatGGs;}
    public int getBranchHP() {return branchHP;}
    public int getBranchMP() {return branchMP;}
    public int getBranchEP() {return branchEP;}
    public int getBranchMoving() {return branchMoving;}
    public String getBranchOtherStats() {
        String str = "";
        str += INIT_HP + branchHP + " ";
        str += branchMP == 0 ? "" : INIT_MP + branchMP + " ";
        str += branchEP == 0 ? "" : INIT_EP + branchEP + " ";
        str += INIT_MOVING + branchMoving + "\r\n";
        return str;
    }
    public RealmList<RealmString> getBranchPasvSpecs() {return branchPasvSpecs;}
    public RealmList<RealmString> getBranchPasvSpecValues() {return branchPasvSpecValues;}
    public RealmList<RealmString> getBranchSpecs() {return branchSpecs;}
    public RealmList<RealmString> getBranchSpecValues() {return branchSpecValues;}
    public String getBranchName2() {return branchName2;}
    public String getBranchMagic() {return branchMagic;}
    public int getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(int branchNo) {
        this.branchNo = branchNo;
    }


    public String getBranchWeaponSubCate() {
        return branchWeaponSubCate;
    }

    public String getBranchArmorSubCate() {
        return branchArmorSubCate;
    }

    public RealmList<RealmInteger> getBranchPasvSpecGrades() {
        return branchPasvSpecGrades;
    }

    public int getBranchHPgg() {
        return branchHPgg;
    }

}
