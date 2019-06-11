package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class GetRelationDB {

	private final static String RELATION_PHP = "relation.php";
	private static GetRelationDB relation = null;

	private GetRelationDB() {
	}

	public static final GetRelationDB getInstance() {
		return (relation == null) ? (relation = new GetRelationDB()) : relation;
	}

	public String getRelation(String priKey) {
		priKey = priKey.replace("병종", "").replace("상성", "").trim();
		StringTokenizer st = new StringTokenizer(priKey);

		String[] s = new String[2];

		try {
			s[0] = st.nextToken();
			s[1] = st.nextToken();
		} catch (NoSuchElementException nsee) {

		}

		/*
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
		*/

		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + RELATION_PHP, s);

		String str = "";
		JSONObject object;
		String oneKey = "";
		try {
			object = (JSONObject) new JSONParser().parse(jsonstr);
			oneKey = (String) object.keySet().iterator().next();
			str += oneKey + " 공격/피격 상성\r\n";

		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			return Communicate.PARSE_DESC;
		}
		int start_info = jsonstr.indexOf(":{");
		String terrainInfo = jsonstr.substring(start_info + 1).replace("\"", "");
		terrainInfo = terrainInfo.substring(1, terrainInfo.length() - 2);
		// System.out.println( "terrainInfo : " + terrainInfo );
		StringTokenizer stt = new StringTokenizer(terrainInfo, ",");

		//int knn = 0;
		while (stt.hasMoreTokens()) {
			str += stt.nextToken() + "\r\n";
			//str += (knn % 2 == 0) ? "   " : "\r\n";
			//knn++;
		}

		/*
		 * json parse는 for (JSONObject obj : Communicate.parseJSONObjects("[" +
		 * object.get(oneKey).toString() + "]")) { Iterator<String> keys =
		 * obj.keySet().iterator(); int kn = 0; while (keys.hasNext()) { String
		 * key = keys.next(); String val = obj.get(key).toString(); str += key +
		 * ":" + val; str += (kn%2==0)? " / ":"\r\n"; kn++; } }
		 */

		return str;
	}

}
