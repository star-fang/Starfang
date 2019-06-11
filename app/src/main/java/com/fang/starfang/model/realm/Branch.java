package com.fang.starfang.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Branch extends RealmObject {

    public static final String PREF_TABLE = "병종 정보";
    public static final String FIELD_NAME = "branchName";
    public static final String FIELD_NAME2 = "branchName2";
    public static final String[] INIT_STATS = {"공","정","방","순","사"};
    public static final String[] INIT_PASVS = {"승급2","승급3","승급4"};
    public enum INIT_SPECS {Lv01,Lv10,Lv15,Lv20,Lv25}
    public static final String INIT_HP = "HP";
    public static final String INIT_MP = "MP";
    public static final String INIT_EP = "EP";
    public static final String INIT_MOVING = "이동력";


    private String branchName;
    private RealmList<String> branchStatGGs; // growth grades 공정방순사
    private int branchHP;
    private int branchMP;
    private int branchEP;
    private RealmList<String> branchPasvSpecs; // passive specs
    private RealmList<String> branchPasvSpecValues; // passive spec values
    private int branchMoving;
    private String branchWeaponSubCate;
    private String branchArmorSubCate;
    private RealmList<String> branchSpecs; // 10, 15, 20 ,25
    private RealmList<String> branchSpecValues; // 10, 15, 20 ,25
    private RealmList<String> branchHiddenStats; // 공정방순사
    private String branchName2;



    public String getBranchName() {return branchName;}
    public RealmList<String> getBranchStatGGs() {return branchStatGGs;}
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
    public RealmList<String> getBranchPasvSpecs() {return branchPasvSpecs;}
    public RealmList<String> getBranchPasvSpecValues() {return branchPasvSpecValues;}
    public RealmList<String> getBranchSpecs() {return branchSpecs;}
    public RealmList<String> getBranchSpecValues() {return branchSpecValues;}
    public String getBranchName2() {return branchName2;}


}
