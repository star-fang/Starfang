package com.fang.starfang.local.task;

import com.fang.starfang.local.model.realm.source.Item;

import org.apache.commons.lang3.math.NumberUtils;

public class Reinforcement {

        private static int[] ReinforcementValueSevenAandD = { 0, 6, 12, 18, 24, 32, 40, 50, 65, 82, 99, 118, 156 }; // Attack&Defence
        private static int[] ReinforcementValueSevenMandA = { 0, 2, 4, 6, 8, 10, 12, 15, 19, 24, 29, 34, 44 }; // morale&agile
        private static int[] ReinforcementValueSevenAssist = { 0, 3, 6, 9, 12, 15, 19, 23, 27, 32, 37, 42, 47 };
        private static Reinforcement instance = null;

        public static Reinforcement getInstance() {
            return (instance == null)? (instance = new Reinforcement()) : instance;
        }
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

    }


