package com.fang.starfang.local.model.realm.simulator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemSim extends RealmObject {

    @PrimaryKey
    private int itemID;
    private int itemNo;
    private int itemReinforcement;


}
