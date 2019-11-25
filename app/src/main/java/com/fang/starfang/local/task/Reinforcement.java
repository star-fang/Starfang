package com.fang.starfang.local.task;

import android.util.Log;

import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.local.model.realm.source.ItemReinforcement;

import org.apache.commons.lang3.math.NumberUtils;

import io.realm.Realm;
import io.realm.RealmList;

public class Reinforcement {
    private final static String TAG = "FANG_REINFORCE";
        private Realm realm;
        private ItemCate itemCate;
        private int itemGrade;
        private RealmList<RealmString> reinforcementTypes;

        public Reinforcement(Realm realm, Item item) {
            this.realm = realm;
            String itemGradeStr = item.getItemGrade();

            if( itemGradeStr.equals("연의") ) {
                itemGrade = 0;
            } else {
                itemGrade = NumberUtils.toInt(itemGradeStr,7);
            }
            this.itemCate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_SUB_CATE, item.getItemSubCate()).findFirst();
            if( itemCate != null) {
                this.reinforcementTypes = itemCate.getItemReinforcementTypes();
            }
            Log.d(TAG,"constructed");
        }

        // statIndex : 0 ~ 4 : 공 정 방 순 사
        // reinfIndex : 1 ~ 12
        public int reinforce(int statIndex, int reinfIndex) {
            int returnValue = 0;

            if( itemCate != null &&  reinforcementTypes != null && reinfIndex > 0 && reinfIndex < 13) {
                //Log.d(TAG,"reinforce start: " + statIndex + " : " + reinfIndex);
                RealmString reinforcementType = reinforcementTypes.get(statIndex);
                if (reinforcementType != null) {
                    //Log.d(TAG,"reinforce type : " + reinforcementType.toString() );
                    ItemReinforcement itemReinforcement = realm.where(ItemReinforcement.class)
                            .equalTo(ItemReinforcement.FIELD_GRD, itemGrade).and()
                            .equalTo(ItemReinforcement.FIELD_TYPE, reinforcementType.toString()).findFirst();

                    if (itemReinforcement != null) {
                        //Log.d(TAG,"ItemReinforcement object: " + itemReinforcement.toString() );
                        RealmList<RealmInteger> reinfValues = itemReinforcement.getReinfValues();
                        if(reinfValues != null) {
                            //Log.d(TAG,"reinfValues: " + reinfValues.toString() );
                            reinfIndex -= 1;
                            RealmInteger reinfValue = reinfValues.get(reinfIndex);
                            if(reinfValue != null ) {
                                returnValue = reinfValue.toInt();
                                //Log.d(TAG,"reinforce :" + returnValue);
                            }
                        }
                    }
                }
            }

            return returnValue;
        }

        /*

         private static int[] ReinforcementValueSevenAandD = { 0, 6, 12, 18, 24, 32, 40, 50, 65, 82, 99, 118, 156 }; // Attack&Defence
        private static int[] ReinforcementValueSevenMandA = { 0, 2, 4, 6, 8, 10, 12, 15, 19, 24, 29, 34, 44 }; // morale&agile
        private static int[] ReinforcementValueSevenAssist = { 0, 3, 6, 9, 12, 15, 19, 23, 27, 32, 37, 42, 47 };


        int reinforce(Item item, int statIndex, String reinValStr) {
            String gradeStr = item.getItemGrade();
            int reinVal = NumberUtils.toInt(reinValStr,0);
            reinVal = reinVal < 0 ? 0 : reinVal > 12 ? 12 : reinVal;
            int grade = NumberUtils.toInt(gradeStr,7);
            if(  gradeStr.equals("연의") )
                grade = 0;

            String cate = item.getItemSubCate();


                boolean isWeapon = (grade == 7) && !cate.equals("전포") && !cate.equals("갑옷")
                        && !cate.equals("의복") && !cate.equals("보조구");
                boolean isArmor = (grade == 7) && (cate.equals("전포") || cate.equals("갑옷") || cate.equals("의복"));
                boolean isAssist = (grade == 7) && cate.equals("보조구");
                boolean physicalAttack = isWeapon && !cate.equals("선") && !cate.equals("보도");
                boolean nonPhysicalAttack = isWeapon && (cate.equals("선") || cate.equals("보도"));



            boolean plusPhysicalAttack = (physicalAttack && statIndex == 0); // 공
            boolean plusNonPhysicalAttack = (nonPhysicalAttack && statIndex == 1); // 정
            boolean plusMandA = (isWeapon && (statIndex == 3 || statIndex == 4)); // 순 사
            boolean plusDefense = isArmor && statIndex == 2;  //방
            boolean plusAssistVal = isAssist && statIndex != 5;

            int plusStat = (plusPhysicalAttack || plusNonPhysicalAttack || plusDefense)
                    ? ReinforcementValueSevenAandD[reinVal] : 0;
            plusStat = plusMandA ? ReinforcementValueSevenMandA[reinVal] : plusStat;
            plusStat = plusAssistVal ? ReinforcementValueSevenAssist[reinVal] : plusStat;

            return plusStat;
        }
         */

    }


