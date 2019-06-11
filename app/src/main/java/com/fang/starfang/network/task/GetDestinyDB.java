package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Iterator;

public class GetDestinyDB {

    private final static String DESTINY_PHP = "get_destiny.php";
    private static GetDestinyDB destiny = null;

    private GetDestinyDB() {
    }

    public static final GetDestinyDB getInstance() {
        return (destiny == null) ? (destiny = new GetDestinyDB()) : destiny;
    }

    public String getDestiny(String priKey, int num, boolean shortMSG) {
        priKey = priKey.replace("인연", "").trim();

        String[] s = new String[2];
        s[0] = priKey;
        s[1] = String.valueOf(num);

        String jsonstr = Communicate.toServer(Communicate.SERVER_URL + DESTINY_PHP, s);

        //return jsonstr.replace(","," ");


        String str = "";
        JSONObject object;
        String oneKey = "";


        try {
            object = (JSONObject) new JSONParser().parse(jsonstr);
            oneKey = (String) object.keySet().iterator().next();
            str += oneKey + "\r\n";

        } catch (org.json.simple.parser.ParseException e) {
            return Communicate.PARSE_DESC;


        } catch (ClassCastException cce) {
            for( JSONObject obj : Communicate.parseJSONObjects(jsonstr) ) {
                str += obj.get("key") + "\r\n";

            }
            return str;
        }


        for (JSONObject obj : Communicate.parseJSONObjects("[" + object.get(oneKey).toString() + "]")) {
            Iterator<String> keys = obj.keySet().iterator();

            try {
                str += shortMSG ? "" : "인연의 끈: " + obj.get("desCord").toString() + "\r\n";
                str += shortMSG ? "" : "조건1: " + obj.get("desCondition:1").toString().replace(",", "") + "\r\n";
                str += shortMSG ? "" : "조건2: " + obj.get("desCondition:2").toString().replace(",", "") + "\r\n";
                str += shortMSG ? "" : "조건3: " + obj.get("desCondition:3").toString().replace(",", "") + "\r\n";
            } catch( NullPointerException npe) {

            }

            try {
                str += "지속 효과: " + obj.get("desLastingEffect").toString() + "\r\n";
                str += obj.get("desJoinEffect:1").toString().replace(",", "") + "\r\n";
                str += obj.get("desJoinEffect:2").toString().replace(",", "") + "\r\n";
                str += obj.get("desJoinEffect:3").toString().replace(",", "") + "\r\n";
            } catch( NullPointerException npe) {

            }


        }



        return str;

    }
}
