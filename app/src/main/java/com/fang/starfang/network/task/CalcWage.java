package com.fang.starfang.network.task;

import android.util.Log;

import com.fang.starfang.network.Communicate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


public class CalcWage {
    private final static String WAGE_PHP = "wage.php";
    private final static String[] CMD1 = {"신규","변경","탈퇴","완전삭제","조회","입력","계산"};
    private final static String[] CMD2 = {"기여도","분노","연의임무","제작임무","퇴치임무","경고","스티커","포상","주급","출첵", "감면사용", "교환사용"};
    private final static String[] CMD3 = {"보존권","은전","식량","제련도구","공예도구","은사","금사","경험열매"};
    private final static String[] ALLIANCE = {"지낭","패기","명달로"};

    private static CalcWage wage = null;

    private CalcWage() {
    }

    public static final CalcWage getInstance() {
        return (wage == null) ? (wage = new CalcWage()) : wage;
    }

    public String getWage(String key) {

        String cmd1 = "";
        String cmd2 = "";
        String cmd3 = "";
        String alliance = "";
        String dateStr;

        String[] what = new String[10];

        StringTokenizer st = new StringTokenizer(key, "/" );
        key = st.nextToken();
        try {
            dateStr = st.nextToken();
        } catch( NoSuchElementException nsee ) {
            dateStr = "";
        }



        for( String tmpCmd : CMD1 ) {
            if( key.contains(tmpCmd)) {
                cmd1 = tmpCmd;
                key = key.replace(tmpCmd,"");
                break;
            }
        }


        for( String tmpCmd : CMD2 ) {
            if( key.contains(tmpCmd)) {
                cmd2 = tmpCmd;
                key = key.replace(tmpCmd,"");
                break;
            }
        }

        for( String tmpCmd : CMD3 ) {
            if( key.contains(tmpCmd)) {
                cmd3 = tmpCmd;
                key = key.replace(tmpCmd,"");
                break;
            }
        }


        for( String tmpAlliance : ALLIANCE ) {
            if( key.contains(tmpAlliance)) {
                alliance = tmpAlliance;
                key = key.replace(tmpAlliance,"");
                break;
            }
        }




        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        String dateInfo = dateStr.replaceAll("[^0-9]", "");
        Date date = new Date();
        String year = new SimpleDateFormat( "yy").format( date );

        try {

            if (dateInfo.length() == 6) {

                date = df.parse(dateInfo);
            } else if (dateInfo.length() == 4) {

                dateInfo = year + dateInfo;
                date = df.parse(dateInfo);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }






        Date tmpDate = new Date();
        tmpDate.setTime(date.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( tmpDate );

        int week = calendar.get( Calendar.WEEK_OF_YEAR );



        what[0] = cmd1;
        what[1] = cmd2;
        what[2] = week+"";
        what[3] = "20"+year;
        what[4] = df.format(date);
        what[5] = alliance;
        what[6] = cmd3;

        StringTokenizer st2 = new StringTokenizer( key, " " );

        int i = 0;
        while( st2.hasMoreTokens() ) {
            what[7+i] = st2.nextToken();
            i++;
        }


        //명령1, 명령2, 주차,연도, 날짜, 연합, 변경내용....
        String jsonstr = Communicate.toServer(Communicate.SERVER_URL + WAGE_PHP, what );
        Log.d("fang", jsonstr);


        // String dw_now = getDW(date, start_dw);

        if (jsonstr.contains("fail")) {
            return "fail";
        }
        jsonstr = jsonstr.replace(",", "*");
        jsonstr = jsonstr.replace("//", "\r\n");

        String str = jsonstr;


        return str;
    }
}
