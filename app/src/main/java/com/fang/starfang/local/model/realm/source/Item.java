package com.fang.starfang.local.model.realm.source;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Item extends RealmObject {

    public static final String PREF_TABLE = "보물";
    public static final String FIELD_NO = "itemNo";
    public static final String FIELD_NAME = "itemName";
    public static final String FIELD_NAME_NO_BLANK = "itemNameNoBlank";
    public static final String FIELD_SPECS = "itemSpecs";
    public static final String FIELD_SUB_CATE = "itemSubCate";
    public static final String FIELD_GRD = "itemGrade";
    public static final String FIELD_RESTRICT_BRANCH = "itemRestrictionBranch";
    public static final String FIELD_RESTRICT_Hero = "itemRestrictionHero";
    public static final String[] INIT_STATS = {"공격력","정신력","방어력","순발력","사기　"};

    @PrimaryKey
    private int itemNo;
    private String itemName;
    private String itemNameNoBlank;
    private String itemGrade;
    private String itemSubCate;
    private RealmList<RealmInteger> itemStats; // 공정방순사,이동력
    private RealmList<RealmString> itemSpecs;
    private RealmList<RealmString> itemSpecValues;
    private String itemDescription;
    private String itemRestrictionBranch;
    private int itemRestrictionHero;

    public String getItemName() {return itemName;}
    public String getItemGrade() {return itemGrade;}
    public String getItemSubCate() {return itemSubCate;}
    public String getItemDescription() {return itemDescription;}
    public String getItemRestrictionBranch() {return itemRestrictionBranch;}
    public int getItemRestrictionHero() {return itemRestrictionHero;}
    public RealmList<RealmInteger> getItemStats() {return itemStats;}
    public ArrayList<Integer> getItemStatList() {
        ArrayList<Integer> stats = new ArrayList<>();
        if( itemStats != null ) {
            for (RealmInteger stat : itemStats) {
                stats.add(stat.toInt());
            }
        }
        return stats;
    }
    public RealmList<RealmString> getItemSpecs() {return itemSpecs;}
    public RealmList<RealmString> getItemSpecValues() {return itemSpecValues;}
    public void setItemNameNoBlank(String itemNameNoBlank) {this.itemNameNoBlank=itemNameNoBlank;}


    public int getItemNo() {
        return itemNo;
    }
}
