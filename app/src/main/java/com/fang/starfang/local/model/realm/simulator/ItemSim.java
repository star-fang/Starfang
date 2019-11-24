package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.source.Item;

import java.util.UUID;

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

    public ItemSim() {

    }

    public ItemSim( Item item ) throws RealmPrimaryKeyConstraintException {
            itemID = (int) UUID.randomUUID().getMostSignificantBits();
            this.item = item;
            this.itemNo = item.getItemNo();
            this.itemReinforcement = 0;
    }


    public int getItemID() {
        return itemID;
    }

    public Item getItem() {
        return item;
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
}
