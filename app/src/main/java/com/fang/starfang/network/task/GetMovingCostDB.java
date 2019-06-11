package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class GetMovingCostDB {

	private final static String MOVING_PHP = "moving.php";
	private static GetMovingCostDB cost = null;

	private GetMovingCostDB() {
	}

	public static final GetMovingCostDB getInstance() {
		return (cost == null) ? (cost = new GetMovingCostDB()) : cost;
	}

	public String getMovingCost(String priKey) {
		priKey = priKey.replace("이동", "").replace("력", "").replace("소모", "").trim();
		StringTokenizer st = new StringTokenizer(priKey);

		String[] s = new String[2];

		try {
			s[0] = st.nextToken();
			s[1] = st.nextToken();
		} catch (NoSuchElementException nsee) {

		}

		if (s[0] != null && s[1] != null) {
			for (String tmpStr : Communicate.list_line) {
				if (tmpStr.contains(s[1])) {
					String swap = s[0];
					s[0] = s[1];
					s[1] = swap;
					break;
				}
			}
		}

		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + MOVING_PHP, s);

		String str = "";
		JSONObject object;
		String oneKey = "";
		try {
			object = (JSONObject) new JSONParser().parse(jsonstr);
			oneKey = (String) object.keySet().iterator().next();
			str += oneKey + " 이동력 소모\r\n";

		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			return Communicate.PARSE_DESC;
		}
		int start_info = jsonstr.indexOf(":{");
		String terrainInfo = jsonstr.substring(start_info + 1).replace("\"", "");
		terrainInfo = terrainInfo.substring(1, terrainInfo.length() - 2);
		// System.out.println( "terrainInfo : " + terrainInfo );
		StringTokenizer stt = new StringTokenizer(terrainInfo, ",");

		int knn = 0;
		while (stt.hasMoreTokens()) {
			str += stt.nextToken();
			str += (knn % 2 == 0) ? "   " : "\r\n";
			knn++;
		}


		return str;
	}

}
