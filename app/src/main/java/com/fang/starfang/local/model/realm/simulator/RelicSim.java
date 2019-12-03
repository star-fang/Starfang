package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RelicSim extends RealmObject {

    @PrimaryKey
    private String relicID;
    private RelicPRFX prfx; // 접두사, 접두사효과, 영향받는스탯, 레벨 별 수치(1~5)
    private RelicSFX sfx; // 접미사, 등급(1~4), 능력치
    private int magicItemLevel; // 레벨(1~5)

    public RelicPRFX getPrfx() {
        return prfx;
    }

    public void setPrfx(RelicPRFX prfx) {
        this.prfx = prfx;
    }

    public RelicSFX getSfx() {
        return sfx;
    }

    public void setSfx(RelicSFX sfx) {
        this.sfx = sfx;
    }

    public int getMagicItemLevel() {
        return magicItemLevel;
    }

    public void setMagicItemLevel(int magicItemLevel) {
        this.magicItemLevel = magicItemLevel;
    }
}
