package com.fang.starfang.local.model.realm.simulator;

import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.source.Spec;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

//11-13 ~ 할일
/*
1.Item 테이블에 itemNo 컬럼 추가하기
2.아이템,보패 생성 구현
3.장수 아이템, 보패 착용 구현
4.장수 승급,레벨,교본작구현
5.스탯 산출
 */
public class HeroSim extends RealmObject {

    public static final String FIELD_ID = "heroNo";

    @PrimaryKey
    private int heroNo;
    private int heroGroupID;
    private int heroLevel; // 1 ~ 99
    private int heroGrade; // 1(~20),2(~40),3(~60),4(~80),5(~99)
    private RealmList<Integer> heroStatsUp;
    private RealmList<Spec> heroSpecsChecked; // 1 ~ 3
    private RealmList<RealmString> heroSpecValuesChecked; // 1 ~ 3
    private RealmList<MagicItemSim> heroMagicItemsSlot1; // 1 ~ 4
    private RealmList<MagicItemSim> heroMagicItemsSlot2; // 1 ~ 4
    private ItemSim heroWeapon;
    private ItemSim heroArmor;
    private ItemSim heroAid;




    }