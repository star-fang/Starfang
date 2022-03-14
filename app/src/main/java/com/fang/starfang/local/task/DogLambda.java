package com.fang.starfang.local.task;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class DogLambda {

    private static final String TAG = "FANG_DOG";

    private enum COMMAND_CERTAIN_ENUM_UNION {
        COMMAND_DESC_UNION, COMMAND_PIRATE, COMMAND_PIRATED, COMMAND_PIRATE_R, COMMAND_DEFAULT_UNION
    }

    private static final String[] COMMAND_CERTAIN_UNION = {
            "설명", "약탈", "당함", "보조"};

    private static final String[] PRFX_COMMAND_UNION = {"", "", "", "약탈"};
    private static final String[] SFX_COMMAND_UNION = {"", "", "보조", ""};

    private static final String BLANK = " ";
    private static final String EMPTY = "";
    private final static String[] cmdMine = {"동광", "서광", "남광", "북광", "중광"};

    String handleRequest(String req) {


        req = req.substring(0, req.length() - 1).trim();
        COMMAND_CERTAIN_ENUM_UNION certainCMD = COMMAND_CERTAIN_ENUM_UNION.COMMAND_DEFAULT_UNION;
        for (COMMAND_CERTAIN_ENUM_UNION certain : COMMAND_CERTAIN_ENUM_UNION.values()) {
            try {
                String probKey = COMMAND_CERTAIN_UNION[certain.ordinal()];
                String suffix = SFX_COMMAND_UNION[certain.ordinal()];
                String prefix = PRFX_COMMAND_UNION[certain.ordinal()];

                String reqWithoutSFX = req;
                try {
                    if (!suffix.equals(BLANK)) {
                        reqWithoutSFX = (req.substring(req.length() - suffix.length()).equals(suffix)) ?
                                req.substring(0, req.length() - suffix.length()) : req;
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }

                try {
                    if (reqWithoutSFX.substring(reqWithoutSFX.length() - probKey.length()).equals(probKey)) {
                        certainCMD = certain;
                        req = req.replace(prefix, EMPTY);
                        req = req.replace(suffix, EMPTY).trim();
                        req = req.substring(0, req.length() - probKey.length()).trim();
                        break;
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }
        }


        String result;
        switch (certainCMD) {
            case COMMAND_PIRATE:
                result = pirate(req, false);
                break;
            case COMMAND_PIRATE_R:
                result = pirate(req, true);
                break;
            case COMMAND_PIRATED:
                result = pirated(req);
                break;
            default:
                result = "starfang-c3971.firebaseapp.com";

        }

        return result;
    }

    private final String[] loot1 = {"식량", "제작", "강화", "보존", "은전"};
    private final String[] loot2 = {"강화", "은전", "은전", "제작", "보존"};
    private final String[] loot1PRFX = {"", "보물", "보물", "강화", ""};
    private final String[] loot1SFX = {"", "허가서", "허가서", "권", ""};
    private final String[] loot1Unit = {"", "장", "장", "장", ""};
    private final String[] loot2PRFX = {"보물", "", "", "보물", "강화"};
    private final String[] loot2SFX = {"허가서", "", "", "허가서", "권"};
    private final String[] loot2Unit = {"장", "", "", "장", "장"};
    private final int[] pirateUnitHour1 = {500000, 6, 8, 5, 250000};
    private final int[] pirateUnitHour2 = {2, 20000, 40000, 1, 1};


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
                    //Date date_raped = new Date();
                    String val_ymdhm = date_now_ymd + " " + val_hm;
                    Date date_raped = df_ymdhm.parse(val_ymdhm);
                    //date_raped.setTime(df_ymdhm.parse(val_ymdhm).getTime());
                    if (date_raped != null) {
                        int time = (int) (date_now.getTime() - date_raped.getTime()) / 1000;
                        if (time < 0) {
                            val_ymdhm = date_yesterday_ymd + " " + val_hm;
                            date_raped = df_ymdhm.parse(val_ymdhm);
                            //date_raped.setTime(df_ymdhm.parse(val_ymdhm).getTime());
                            if (date_raped != null) {
                                time = (int) (date_now.getTime() - date_raped.getTime()) / 1000;
                            }
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
                        double val11 = Math.round(Math.floor(val1) * 1.2 * 100) / 100.0;
                        double val22 = Math.round(Math.floor(val2) * 1.2 * 100) / 100.0;
                        str += loot1[num] + " : " + val1 + "\r\n(약탈보조 : " + val11 + ")" + "\r\n";
                        str += loot2[num] + " : " + val2 + "\r\n(약탈보조 : " + val22 + ")";
                    }
                    return str;
                } catch (ParseException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                    return "\"[광이름] 00시00분 약탈당함\"<양식 맞추라옹";
                }

            }

        }
    }


    private String pirate(String priKey, boolean assistant) {
        StringBuilder str = new StringBuilder();

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
            str.append("1시간 약탈량 ");
            str.append(assistant ? "(약탈 보조)\r\n" : "\r\n");
            str.append("광명 | 품목  수량\r\n");
            if (noMine) {
                for (int i = 0; i < loot1.length; i++) {
                    str.append(cmdMine[i]).append(" | ").append(loot1[i]).append("  ");
                    str.append(assistant ? (double) pirateUnitHour1[i] * 1.2 + "\r\n" : pirateUnitHour1[i] + "\r\n");
                    str.append(cmdMine[i]).append(" | ").append(loot2[i]).append("  ");
                    str.append(assistant ? (double) pirateUnitHour2[i] * 1.2 + "\r\n" : pirateUnitHour2[i]);
                    str.append((i < loot1.length - 1) ? "\r\n\r\n" : "");
                }
            } else {
                str.append(cmdMine[num]).append(" | ").append(loot1[num]).append("  ");
                str.append(assistant ? (double) pirateUnitHour1[num] * 1.2 + "\r\n" : pirateUnitHour1[num] + "\r\n");
                str.append(cmdMine[num]).append(" | ").append(loot2[num]).append("  ");
                str.append(assistant ? (double) pirateUnitHour2[num] * 1.2 + "\r\n" : pirateUnitHour2[num]);
            }

        } else if (!noMine) {

            double pirateUnitSec;

            String loot;
            String lootSFX;
            String lootPRFX;
            String lootUnit;
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
            datePirate.setTime(datePirate.getTime() + -(int) (time * 1000));
            SimpleDateFormat df = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.KOREA);
            String dateNowInfo = df.format(dateNow);
            String dateFullInfo = df.format(dateFull);
            // String dateMaxInfo = df.format(dateMax);
            String datePirateInfo = df.format(datePirate);

            str.append(cmdMine[num]).append(" ").append(lootPRFX).append(loot).append(lootSFX).append(":").append(val).append(lootUnit).append("\r\n");
            str.append("점령 : ").append((hour > 0) ? (hour + "시간 ") : "").append(minute).append("분 ").append(second).append("초 경과\r\n");
            str.append("잔여 : ").append((hour_remain > 0) ? (hour_remain + "시간 ") : "").append(minute_remain).append("분 ").append(second_remain).append("초 남음\r\n");
            str.append("오차 : ").append((minute_error > 0) ? (minute_error + "분 ") : "").append((second_error > 0 || minute_error == 0) ? (second_error + "초 ") : "").append("\r\n\r\n");
            str.append("현재시간 : ").append(dateNowInfo).append("\r\n");
            str.append("점령시간 : ").append(datePirateInfo).append(" ± 오차\r\n");
            str.append("풀광시간 : ").append(dateFullInfo).append(" ± 오차");
            // str += "풀광(최대) : " + dateMaxInfo;

        }

        return str.toString();
    }

}
