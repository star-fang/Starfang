package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class RelicSim extends RealmObject {

    public static final String FIELD_ID = "relicID";
    public static final String FIELD_SUFFIX = "suffix";
    public static final String FIELD_PREFIX = "prefix";
    public static final String FIELD_LEVEL = "relicLevel";
    public static final String FIELD_HERO = "heroWhoHasThis";

    private static RelicSim emptyInstance = null;

    @PrimaryKey
    private int relicID;
    private int prefixID;
    private RelicPRFX prefix; // 접두사, 접두사효과, 영향받는스탯, 레벨 별 수치(1~5)
    private int suffixID;
    private RelicSFX suffix; // 접미사, 등급(1~4), 능력치
    private int relicLevel; // 레벨(1~5)
    private HeroSim heroWhoHasThis;
    private int slot; // 1,2 : 0 is null
    private int positionInSlot; // 1 ~ 4 : 0 is null

    public RelicSim() {
    }

    public RelicSim( RelicSFX suffix, RelicPRFX prefix) throws RealmPrimaryKeyConstraintException {
        this.relicID = (int)UUID.randomUUID().getMostSignificantBits();
        this.suffix = suffix;
        this.suffixID = suffix.getRelicSuffixID();
        this.prefix = prefix;
        this.prefixID = prefix == null ? 0 : prefix.getRelicPrefixID();
        this.relicLevel = 1;
        this.heroWhoHasThis = null;
        this.positionInSlot = 0;
        this.slot = 0;

    }

    public int getPrefixID() {
        return prefixID;
    }

    public void setPrefixID(int prefixID) {
        this.prefixID = prefixID;
    }

    public RelicPRFX getPrefix() {
        return prefix;
    }

    public void updatePrefix(Realm realm) {
        this.prefix = realm.where(RelicPRFX.class).equalTo(RelicPRFX.FIELD_ID, prefixID).findFirst();
    }

    public int getSuffixID() {
        return suffixID;
    }

    public RelicSFX getSuffix() {
        return suffix;
    }

    public void updateSuffix(Realm realm) {
        this.suffix = realm.where(RelicSFX.class).equalTo(RelicPRFX.FIELD_ID, suffixID).findFirst();
    }

    public int getRelicID() {
        return relicID;
    }

    public int getRelicLevel() {
        return relicLevel;
    }

    public String getRelicLevelStr() {
        return "Lv."+relicLevel;
    }

    public void setRelicLevel(int relicLevel) {
        this.relicLevel = relicLevel;
    }

    public HeroSim getHeroWhoHasThis() {
        return heroWhoHasThis;
    }

    public void setHeroWhoHasThis(HeroSim heroWhoHasThis) {
        this.heroWhoHasThis = heroWhoHasThis;
    }

    public int getPositionInSlot() {
        return positionInSlot;
    }

    public void setPositionInSlot(int positionInSlot) {
        this.positionInSlot = positionInSlot;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
