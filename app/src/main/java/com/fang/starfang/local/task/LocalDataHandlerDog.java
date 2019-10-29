package com.fang.starfang.local.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.local.model.realm.UnionBranch;
import com.fang.starfang.local.model.realm.UnionSkill;
import com.fang.starfang.util.KakaoReplier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;



public class LocalDataHandlerDog extends AsyncTask<String, Integer, String> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String sendCat;
    private String catRoom;
    private StatusBarNotification sbn;

    private static final String TAG = "LOCAL_HANDLER";

    private enum COMMAND_CERTAIN_ENUM_UNION {
        COMMAND_BRANCH_UNION,COMMAND_SPEC_UNION,COMMAND_SKILL_UNION,COMMAND_DESC_UNION, COMMAND_PIRATE,COMMAND_PIRATED, COMMAND_PIRATE_R, COMMAND_DEFAULT_UNION }
    private static final String[] COMMAND_CERTAIN_UNION = {
            "병종","능력", "스킬", "설명","약탈","당함","보조"};

    private static final String[] PRFX_COMMAND_UNION = {"","","","","","","약탈"};
    private static final String[] SFX_COMMAND_UNION = {"","","","","","",""};

    private static final String CRLF = "\r\n";
    private static final String BLANK = " ";
    private static final String EMPTY = "";
    private static final String DASH = "-";
    private static final String COMMA = ",";
    private static final String SEPARATOR = "-------------------------------\n";
    private final static String[] cmdMine = { "동광", "서광", "남광", "북광", "중광" };

    public LocalDataHandlerDog(Context c, String sender, String room, StatusBarNotification _sbn ) {
        context = c;
        sendCat = sender;
        catRoom = room;
        sbn = _sbn;
    }


    @Override
    protected String doInBackground(String... strings) {
        try(Realm realm = Realm.getDefaultInstance()) {
            handleRequest( strings[0], realm);
        } catch ( RuntimeException ignore ) {

        }
        return null;
    }


    private boolean handleRequest( String req, Realm realm ) {


            req = req.substring(0, req.length() - 1).trim();
            COMMAND_CERTAIN_ENUM_UNION certainCMD = COMMAND_CERTAIN_ENUM_UNION.COMMAND_DEFAULT_UNION;
            for( COMMAND_CERTAIN_ENUM_UNION certain : COMMAND_CERTAIN_ENUM_UNION.values() ) {
                try {
                    String probKey = COMMAND_CERTAIN_UNION[certain.ordinal()];
                    String suffix = SFX_COMMAND_UNION[certain.ordinal()];
                    String prefix = PRFX_COMMAND_UNION[certain.ordinal()];

                    String reqWithoutSFX = req;
                    try {
                        if (!suffix.isEmpty())
                            reqWithoutSFX = (req.substring(req.length() - suffix.length()).equals(suffix)) ?
                                    req.substring(0, req.length() - suffix.length() ): req;
                    } catch( StringIndexOutOfBoundsException  ignore ) { }

                    try {
                        if (reqWithoutSFX.substring(reqWithoutSFX.length() - probKey.length()).equals(probKey)) {
                            certainCMD = certain;
                            req = req.replace(prefix, EMPTY);
                            req = req.replace(suffix, EMPTY).trim();
                            req = req.substring(0, req.length() - probKey.length()).trim();
                            break;
                        }
                    } catch( StringIndexOutOfBoundsException  ignore ) { }

                } catch (ArrayIndexOutOfBoundsException ignore) { }
            }


            // 연합전 병종 정보 검색 : 상병 병종멍, 병종멍, 기마대 병종멍
            HandleLocalDB searchBranchInfo = ( q -> {

                Log.d(TAG,"searchBranchInfo activated");

                q = q.replace(BLANK,EMPTY);
                StringBuilder lambdaResult = new StringBuilder();

                if( q.isEmpty() ) {
                    return null;
                }

                if( q.equals("전체")) {
                    q = "";
                }

                RealmResults<UnionBranch> uBranchResult = realm.where(UnionBranch.class).contains(UnionBranch.FIELD_CLASS,q).findAll();

                if( !uBranchResult.isEmpty() ) {
                    int ubSize = uBranchResult.size();
                    lambdaResult.append("연합전 " + q + " 병종" + CRLF);
                    lambdaResult.append("검색 결과: " + ubSize + "개" + CRLF);

                    lambdaResult.append(SEPARATOR).append("병종계열 병과분류 등급").append(CRLF).append(SEPARATOR);


                    for (UnionBranch uBranch : uBranchResult) {
                        lambdaResult.append(String.format("%-4s", uBranch.getuBranch()).replace(' ', '　'))
                                .append(BLANK).append(String.format("%-4s", uBranch.getuBranchClass()).replace(' ', '　'))
                                .append(BLANK).append(uBranch.getuBranckGrade()).append(CRLF);
                    }

                    return lambdaResult.toString();
                }


                uBranchResult = realm.where(UnionBranch.class).contains(UnionBranch.FIELD_NAME,q).findAll();

                if( !uBranchResult.isEmpty() ) {
                    for (UnionBranch uBranch : uBranchResult) {
                        lambdaResult.append("[").append(uBranch.getuBranchClass()).append("]")
                                .append(BLANK).append(uBranch.getuBranch())
                                .append(BLANK).append(uBranch.getuBranckGrade()).append(CRLF);

                        lambdaResult.append("HP").append(uBranch.getuBranchHP()).append(BLANK)
                                .append("EP").append(uBranch.getuBranchEP()).append(BLANK)
                                .append("이동력").append(uBranch.getuBranchMove()).append(CRLF);

                        lambdaResult.append("*병종 능력치(상대값)").append(CRLF);
                        lambdaResult.append(" -공격력: ").append(uBranch.getuBranchAttackPower()).append(CRLF);
                        lambdaResult.append(" -정신력: ").append(uBranch.getuBranchMentalPower()).append(CRLF);
                        lambdaResult.append(" -방어력: ").append(uBranch.getuBranchDefensePower()).append(CRLF);
                        lambdaResult.append(" -순발력: ").append(uBranch.getuBranchAgilityPower()).append(CRLF);
                        lambdaResult.append(" -사기　: ").append(uBranch.getuBranchMoralePower()).append(CRLF);

                        lambdaResult.append("*병종 능력").append(CRLF);
                        for (int i = 0; i < 4; i++) {
                            String val = uBranch.getuBranchSpecValue().get(i);
                            String spec = uBranch.getuBranchSpec().get(i);
                            if( spec != null ) {
                                lambdaResult.append(" -")
                                        .append(uBranch.getuBranchSpec().get(i)).append(BLANK)
                                        .append(val == null ? EMPTY : val).append(CRLF);
                            }
                        }

                        lambdaResult.append("*병종 설명").append(CRLF);
                        lambdaResult.append(uBranch.getuBranchDesc());
                        lambdaResult.append(COMMA).append(CRLF);
                    }
                } else {
                    return null;
                }


                return lambdaResult.toString();
            } );


            // 연합전 스킬 정보 검색 : 초열 스킬멍, 스킬멍, 원융노병 스킬멍
            HandleLocalDB searchSkillInfo = ( q-> {

                StringBuilder lambdaResult = new StringBuilder();

                if(q.isEmpty()) {
                    return null;
                }

                RealmResults<UnionBranch> uBranchResult = realm.where(UnionBranch.class).contains(UnionBranch.FIELD_NAME,q).findAll();

                if(!uBranchResult.isEmpty()) {
                    for(UnionBranch uBranch : uBranchResult) {
                        lambdaResult.append(uBranch.getuBranch() + " 스킬").append(CRLF)
                                .append(SEPARATOR);
                        String skills = uBranch.getuBranchSkill();
                        for(String skill: skills.split(",")) {
                            skill = skill.trim();
                            UnionSkill uSkill = realm.where(UnionSkill.class).equalTo(UnionSkill.FIELD_NAME,skill).findFirst();
                            lambdaResult.append("[").append(uSkill.getuSkillType()).append("] ").append(skill).append(CRLF);
                        }

                        lambdaResult.append(COMMA).append(CRLF);
                    }
                    return lambdaResult.toString();
                }

                RealmResults<UnionSkill> uSkillResult = realm.where(UnionSkill.class).contains(UnionSkill.FIELD_NAME,q).findAll();

                if( !uSkillResult.isEmpty() ) {
                    for( UnionSkill uSkill : uSkillResult ) {
                        lambdaResult.append("[").append(uSkill.getuSkillType()).append("]")
                                .append(uSkill.getuSkillName()).append(CRLF)
                                .append("*소모EP: ").append(uSkill.getuSkillEP()).append(CRLF)
                                .append("*쿨타임: ").append(uSkill.getuSkillCooldown()).append("초").append(CRLF)
                                .append("스킬 파워:" ).append(uSkill.getuSkillPower()).append(CRLF)
                                .append("효과 범위: ").append(uSkill.getuSkillEffectArea()).append(CRLF)
                                .append("시전 범위: ").append(uSkill.getuSkillTargetArea()).append(CRLF)
                                .append(uSkill.getuSkillDesc()).append(COMMA).append(CRLF);
                    }

                    return lambdaResult.toString();
                }


                return null;
            } );

            Log.d(TAG,"searchSkillInfo activated");

            String result = null;
            switch (certainCMD) {
                case COMMAND_BRANCH_UNION:
                    result = searchBranchInfo.handle(req);
                    result = (result==null)?"병종 이름을 정확하게 입력해 주세요!" : result;
                    break;
                case COMMAND_SPEC_UNION:
                    break;
                case COMMAND_SKILL_UNION :
                    result = searchSkillInfo.handle(req);
                    result = (result==null)?"스킬 이름이나 병종을 정확하게 입력해 주세요!" : result;
                    break;
                case COMMAND_DESC_UNION:
                    result = "설명 준비중";
                    break;
                case COMMAND_PIRATE:
                    result = pirate(req,false);
                    break;
                case COMMAND_PIRATE_R:
                    result = pirate(req,true);
                    break;
                case COMMAND_PIRATED:
                    result = pirated(req);
                    break;
                default:
                    result = searchBranchInfo.handle(req);
                    result = (result==null)?searchSkillInfo.handle(req) : result;
                    result = (result==null)?"병종 -> 책략 검색 결과 : 없음" : result;

            }


            if(result != null) {
                KakaoReplier replier = new KakaoReplier(context,sendCat,sbn);
                replier.execute(result,"[L]");
                return true;
            }
        return false;
    }

    private interface HandleLocalDB {
        String handle(String req);
    }



    private final String[] loot1 = { "식량", "제작", "강화", "보존", "은전" };
    private final String[] loot2 = { "강화", "은전", "은전", "제작", "보존" };
    private final String[] loot1PRFX = { "", "보물", "보물", "강화", "" };
    private final String[] loot1SFX = { "", "허가서", "허가서", "권", "" };
    private final String[] loot1Unit = { "", "장", "장", "장", "" };
    private final String[] loot2PRFX = { "보물", "", "", "보물", "강화" };
    private final String[] loot2SFX = { "허가서", "", "", "허가서", "권" };
    private final String[] loot2Unit = { "장", "", "", "장", "장" };
    private final int[] pirateUnitHour1 = { 500000, 6, 8, 5, 250000 };
    private final int[] pirateUnitHour2 = { 2, 20000, 40000, 1, 1 };



    private String pirated(String priKey) {
            // 12시 5분
            // 12:5

        boolean noMine = true;
        int num = 0;
        for (; num < cmdMine.length; num++) {
            if (priKey.contains(cmdMine[num])) {
                noMine = false;
                break;
            }
        }
        String str = "";


            if (noMine) {
                return "어디를 약탈 당했냐옹?";
            } else {

                String val_hm = "";
                if (priKey.contains(":")) {
                    val_hm = priKey.replaceAll("[^:0-9]", "").trim();

                } else if (priKey.contains("시")) {
                    val_hm = priKey.replaceAll("[^시0-9]", "");
                    val_hm = val_hm.replace("시", ":").trim();

                }

                if (val_hm.equals("")) {
                    return "언제 당했냐옹? (hh:mm or hh시mm분)";
                } else {
                    Date date_now = new Date();
                    Date date_yesterday = new Date();
                    date_now.setTime(date_now.getTime());
                    date_yesterday.setTime(date_yesterday.getTime() - 24 * 60 * 60 * 1000);
                    SimpleDateFormat df_ymd = new SimpleDateFormat("yy/MM/dd", Locale.KOREA);
                    String date_now_ymd = df_ymd.format(date_now);
                    String date_yesterday_ymd = df_ymd.format(date_yesterday);
                    SimpleDateFormat df_ymdhm = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.KOREA);

                    try {
                        Date date_raped = new Date();
                        String val_ymdhm = date_now_ymd + " " + val_hm;
                        date_raped.setTime(df_ymdhm.parse(val_ymdhm).getTime());
                        int time = (int) (date_now.getTime() - date_raped.getTime()) / 1000;
                        if (time < 0) {
                            val_ymdhm = date_yesterday_ymd + " " + val_hm;
                            date_raped.setTime(df_ymdhm.parse(val_ymdhm).getTime());
                            time = (int) (date_now.getTime() - date_raped.getTime()) / 1000;
                        }

                        int time_hour = time / 3600;
                        int time_minute = time % 3600 / 60;
                        int time_second = time % 3600 % 60;

                        String timeStr = ((time_hour > 0) ? (time_hour + "시간 ") : "")
                                + ((time_minute > 0) ? (time_minute + "분 ") : "")
                                + ((time_second > 0) ? (time_second + "초 ") : "");

                        str += "현재시간 : " + df_ymdhm.format(date_now) + "\r\n";
                        str += "먹힌시간 : " + val_ymdhm + "\r\n(" + timeStr + "경과)\r\n\r\n";
                        double val1 = Math.round(
                                ((double) pirateUnitHour1[num] / 3600.0) * time * 100) / 100.0;
                        double val2 = Math.round(
                                ((double) pirateUnitHour2[num] / 3600.0) * time * 100) / 100.0;
                        double val11 = Math.round( Math.floor(val1) * 1.2 * 100) / 100.0;
                        double val22 = Math.round( Math.floor(val2) * 1.2 * 100) / 100.0;
                        str += loot1[num] + " : " + val1 + "\r\n(약탈보조 : " + val11 + ")" + "\r\n";
                        str += loot2[num] + " : " + val2 + "\r\n(약탈보조 : " + val22 + ")";

                        return str;
                    } catch (ParseException e) {

                        // e.printStackTrace();
                        return "\"[광이름] 00시00분 약탈당함\"<양식 맞추라옹";
                    }

                }

            }
    }


    private String pirate(String priKey, boolean assistant) {
        String str = "";

        boolean noMine = true;
        int num = 0;
        for (; num < cmdMine.length; num++) {
            if (priKey.contains(cmdMine[num])) {
                noMine = false;
                break;
            }
        }


        String val = priKey.replaceAll("[^0-9]", "");

        if (val.equals("")) {
            str += "1시간 약탈량 ";
            str += assistant ? "(약탈 보조)\r\n" : "\r\n";
            str += "광명 | 품목  수량\r\n";
            if (noMine) {
                for (int i = 0; i < loot1.length; i++) {
                    str += cmdMine[i] + " | " + loot1[i] + "  ";
                    str += assistant ? (double) pirateUnitHour1[i] * 1.2 + "\r\n" : pirateUnitHour1[i] + "\r\n";
                    str += cmdMine[i] + " | " + loot2[i] + "  ";
                    str += assistant ? (double) pirateUnitHour2[i] * 1.2 + "\r\n" : pirateUnitHour2[i];
                    str += (i < loot1.length - 1) ? "\r\n\r\n" : "";
                }
            } else {
                str += cmdMine[num] + " | " + loot1[num] + "  ";
                str += assistant ? (double) pirateUnitHour1[num] * 1.2 + "\r\n" : pirateUnitHour1[num] + "\r\n";
                str += cmdMine[num] + " | " + loot2[num] + "  ";
                str += assistant ? (double) pirateUnitHour2[num] * 1.2 + "\r\n" : pirateUnitHour2[num];
            }

        } else if (!noMine) {

            double pirateUnitSec;

            String loot = "";
            String lootSFX = "";
            String lootPRFX = "";
            String lootUnit = "";
            if (priKey.contains(loot2[num])) {
                loot = loot2[num];
                lootSFX = loot2SFX[num];
                lootPRFX = loot2PRFX[num];
                lootUnit = loot2Unit[num];
                pirateUnitSec = assistant ? ((double) pirateUnitHour2[num] / 3600.0 * 1.2)
                        : ((double) pirateUnitHour2[num] / 3600.0);
            } else {
                loot = loot1[num];
                lootSFX = loot1SFX[num];
                lootPRFX = loot1PRFX[num];
                lootUnit = loot1Unit[num];
                pirateUnitSec = assistant ? ((double) pirateUnitHour1[num] / 3600.0 * 1.2)
                        : ((double) pirateUnitHour1[num] / 3600.0);
            }

            double doubleVal = Double.parseDouble(val);
            double time = doubleVal / pirateUnitSec;
            int hour = (int) (time / 3600);
            int minute = (int) ((time % 3600) / 60);
            int second = (int) ((time % 3600) % 60);

            double time_remain = (double) (3600 * 3) - time;
            int hour_remain = (int) (time_remain / 3600);
            int minute_remain = (int) ((time_remain % 3600) / 60);
            int second_remain = (int) ((time_remain % 3600) % 60);

            double time_error = 1 / pirateUnitSec;
            int minute_error = (int) (time_error / 60);
            int second_error = (int) (time_error % 60);

            Date dateNow = new Date();
            Date dateFull = new Date();
            // Date dateMax = new Date();
            Date datePirate = new Date();

            dateNow.setTime(dateNow.getTime());
            dateFull.setTime(dateFull.getTime() + (int) (time_remain * 1000));
            datePirate.setTime(datePirate.getTime() + - (int) (time * 1000));
            SimpleDateFormat df = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.KOREA);
            String dateNowInfo = df.format(dateNow);
            String dateFullInfo = df.format(dateFull);
            // String dateMaxInfo = df.format(dateMax);
            String datePirateInfo = df.format(datePirate);

            str += cmdMine[num] + " " + lootPRFX + loot + lootSFX + ":" + val + lootUnit + "\r\n";
            str += "점령 : " + ((hour > 0) ? (hour + "시간 ") : "") + minute + "분 " + second + "초 경과\r\n";
            str += "잔여 : " + ((hour_remain > 0) ? (hour_remain + "시간 ") : "") + minute_remain + "분 " + second_remain
                    + "초 남음\r\n";
            str += "오차 : " + ((minute_error > 0) ? (minute_error + "분 ") : "")
                    + ((second_error > 0 || minute_error == 0) ? (second_error + "초 ") : "") + "\r\n\r\n";
            str += "현재시간 : " + dateNowInfo + "\r\n";
            str += "점령시간 : " + datePirateInfo + " ± 오차\r\n";
            str += "풀광시간 : " + dateFullInfo + " ± 오차";
            // str += "풀광(최대) : " + dateMaxInfo;

        }

        return str;
    }

}