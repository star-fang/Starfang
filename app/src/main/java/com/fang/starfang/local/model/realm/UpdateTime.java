package com.fang.starfang.local.model.realm;

import io.realm.RealmObject;

public class UpdateTime extends RealmObject {

    public static String FIELD_TABLE = "prefTable";

    private String prefTable;
    private String latestUpadateTime = "0";

    public String getPrefTable() {
        return prefTable;
    }

    public void setPrefTable(String prefTable) {
        this.prefTable = prefTable;
    }

    public String getLatestUpadateTime() {
        return latestUpadateTime;
    }

    public void setLatestUpadateTime(String latestUpadateTime) {
        this.latestUpadateTime = latestUpadateTime;
    }
}
