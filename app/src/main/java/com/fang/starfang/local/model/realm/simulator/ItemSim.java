package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.Item;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class ItemSim extends RealmObject {

    public static final String FIELD_ID = "itemID";
    public static final String FIELD_ITEM = "item";
    public static final String FIELD_NO =  "itemNo";
    public static final String FIELD_REINF =  "itemReinforcement";

    @PrimaryKey
    private int itemID;
    private Item item;
    private int itemNo;
    private int itemReinforcement;
    private RealmList<RealmInteger> itemPlusPowers;
    private HeroSim heroWhoHasThis;
    private String specNameGrade6;
    private String specValueGrade6;
    private int specSixID;
    private String specNameGrade12;
    private String specValueGrade12;
    private int specTwelveID;


    public ItemSim() {

    }

    public ItemSim( Item item ) throws RealmPrimaryKeyConstraintException {
            itemID = (int) UUID.randomUUID().getMostSignificantBits();
            this.item = item;
            this.itemNo = item.getItemNo();
            this.itemReinforcement = 0;
            this.heroWhoHasThis = null;
            this.itemPlusPowers = new RealmList<>();
            for( int i = 0; i < Heroes.INIT_STATS.length; i++ ) {
                itemPlusPowers.add(new RealmInteger(0));
            }
            this.specNameGrade6 = null;
            this.specValueGrade6 = null;
            this.specNameGrade12 = null;
            this.specValueGrade12 = null;
            this.specSixID = 0;
            this.specTwelveID = 0;
    }


    public int getItemID() {
        return itemID;
    }


    public int getItemNo() {
        return itemNo;
    }

    public int getItemReinforcement() {
        return itemReinforcement;
    }

    public void setItemReinforcement(int itemReinforcement) {
        this.itemReinforcement = itemReinforcement;
    }

    public HeroSim getHeroWhoHasThis() {
        return heroWhoHasThis;
    }

    public void setHeroWhoHasThis(HeroSim heroWhoHasThis) {
        this.heroWhoHasThis = heroWhoHasThis;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public RealmList<RealmInteger> getItemPlusPowers() {
        return itemPlusPowers;
    }

    public String getSpecNameGrade6() {
        return specNameGrade6;
    }

    public void setSpecNameGrade6(String specNameGrade6) {
        this.specNameGrade6 = specNameGrade6;
    }

    public String getSpecValueGrade6() {
        return specValueGrade6;
    }

    public void setSpecValueGrade6(String specValueGrade6) {
        this.specValueGrade6 = specValueGrade6;
    }

    public int getSpecSixID() {
        return specSixID;
    }

    public void setSpecSixID(int specSixID) {
        this.specSixID = specSixID;
    }

    public String getSpecNameGrade12() {
        return specNameGrade12;
    }

    public void setSpecNameGrade12(String specNameGrade12) {
        this.specNameGrade12 = specNameGrade12;
    }

    public String getSpecValueGrade12() {
        return specValueGrade12;
    }

    public void setSpecValueGrade12(String specValueGrade12) {
        this.specValueGrade12 = specValueGrade12;
    }

    public int getSpecTwelveID() {
        return specTwelveID;
    }

    public void setSpecTwelveID(int specTwelveID) {
        this.specTwelveID = specTwelveID;
    }
}
