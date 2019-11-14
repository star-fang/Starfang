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
6.데미지 산출 시뮬레이션 구현
 */
public class HeroSim extends RealmObject {

    public static final String FIELD_ID = "heroNo";

    @PrimaryKey
    private int heroNo;   // 장수 고유 번호
    private int heroGroupID;  // 장수덱
    private int heroLevel; // 1 ~ 99
    private int heroGrade; // 1(~20),2(~40),3(~60),4(~80),5(~99)
    private RealmList<Integer> heroStatsUp;      // 교본작
    private RealmList<Spec> heroSpecsChecked; //  체크된 효과 (~3개)
    private RealmList<RealmString> heroSpecValuesChecked; // 체크된 효과수치( ~3개)
    private RealmList<MagicItemSim> heroMagicItemsSlot1; // 보패 슬롯1
    private RealmList<MagicItemSim> heroMagicItemsSlot2; // 보패 슬롯2
    private ItemSim heroWeapon;  // 무기
    private ItemSim heroArmor; // 방어구
    private ItemSim heroAid; // 보조구

    }