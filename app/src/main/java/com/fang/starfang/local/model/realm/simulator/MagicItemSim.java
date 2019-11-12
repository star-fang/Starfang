package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.source.MagicItemPRFX;
import com.fang.starfang.local.model.realm.source.MagicItemSFX;

import io.realm.annotations.PrimaryKey;

public class MagicItemSim {

    @PrimaryKey
    private String magicItemID;
    private MagicItemPRFX prfx;
    private MagicItemSFX sfx;
    private int magicItemLevel;

    public MagicItemPRFX getPrfx() {
        return prfx;
    }

    public void setPrfx(MagicItemPRFX prfx) {
        this.prfx = prfx;
    }

    public MagicItemSFX getSfx() {
        return sfx;
    }

    public void setSfx(MagicItemSFX sfx) {
        this.sfx = sfx;
    }

    public int getMagicItemLevel() {
        return magicItemLevel;
    }

    public void setMagicItemLevel(int magicItemLevel) {
        this.magicItemLevel = magicItemLevel;
    }
}
