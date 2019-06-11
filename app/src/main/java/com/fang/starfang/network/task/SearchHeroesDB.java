package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class SearchHeroesDB {

	private final static String[] list_lineage = { "난세간웅", "낭고중달", "대현량사", "군신운장", "동래자의", "임협원직", "패왕본초", "백언소후",
			"벽안자염", "등후사재", "상산자룡", "영웅문대", "봉추사원", "단명백부", "미주공근", "노장한승", "용장익덕", "만족맹기", "마왕패도", "칠금만왕", "황숙현덕",
			"문소황후", "와룡공명", "문명황후", "비장봉선", "발탁무장", "풍운아만", "태조고제", "패왕항우", "난세여걸" };
	private final static String[][] abb_hero = {
			{ "맹덕", "아만", "장자방", "인공장군", "미맹돈", "검후돈", "마갈량", "착갈량", "마갈", "착갈", "득규" , "항우", "시부랄","쏘혀"},
			{ "조조", "조조", "장량", "장량", "하후돈", "하후돈", "제갈량", "제갈량", "제갈량", "제갈량", "엄정", "항적","아즈치","쏘혀냥" },
			{ "군주", "검사", "현자", "도사", "경기병", "검사", "마왕", "현자", "마왕", "현자", "궁병", "효기병","일개교위","바지사장" } };
	private final static String[][] abb_replace = { { "태수", "" }, { "군주효과", "" } };

	private final static String SEARCH_HEROES_PHP = "search_heroes_v2.php";
	private final static String GET_HEROES_PHP = "get_heroes.php";
	private final static String EMPTY_DESC = "해당 효과를 가진 장수가 없습니다옹. '보물' '보패' '설명' 조건을 추가하여 검색 해보라옹";
	private final static String OVERFLOW_DESC = "검색 카테고리가 너무 많습니다옹. 검색할 특성이름을 더 자세하게 입력해보라옹. \r\neg. 보조냥(X) 전징세보조냥(O)";

	private static SearchHeroesDB heroes = null;

	private SearchHeroesDB() {
	}

	public static final SearchHeroesDB getInstance() {
		return (heroes == null) ? (heroes = new SearchHeroesDB()) : heroes;
	}

	@SuppressWarnings("unchecked")
	public String searchHeroDB(String priKey) {
		priKey = priKey.replace("의 패", "").replace("의패", "");

		// 0계열 1계보 2코일치 3코이상 4코이하 5코초과 6코미만 7~10효과1~4 11태수 12군주 13정렬기준 14오름내림
		String[] msg = new String[15];

		for (int i = 0; i < abb_replace.length; i++) {
			if (priKey.contains(abb_replace[i][0])) {
				priKey = priKey.replace(abb_replace[i][0], abb_replace[i][1]);
			}
		}

		if( priKey.contains("~")) {

			String beforePivot = priKey.substring(0,priKey.indexOf("~"));
			String afterPivot = priKey.substring(priKey.indexOf("~"));
			int beforeValue = 4;
			int afterValue = 15;
			try {
				beforeValue = Integer.parseInt(beforePivot.replaceAll("[^0-9]", ""))-10;
			} catch ( NumberFormatException nfe ) {

			}

			try {
				afterValue = Integer.parseInt(afterPivot.replaceAll("[^0-9]", "")) - 10;
			} catch ( NumberFormatException nfe ) {

			}
			msg[3] = beforeValue+"";
			msg[4] = afterValue+"";

			priKey = priKey.replace("코스트","").replace("~", "").replaceAll("[0-9]", "");

		} else if (priKey.contains("코스트")) {
			String[] cmdStr = { "이상", "이하", "초과", "미만" };
			String[] cmdStrRep = { "A", "B", "C", "D" };
			boolean involveCmd = false;
			for (int i = 0; i < 4; i++) {
				if (priKey.contains(cmdStr[i])) {

					involveCmd = true;
					break;
				}
			}

			for (int i = 0; i < 4; i++) {
				priKey = priKey.replace(cmdStr[i], cmdStrRep[i]);
			}

			// System.out.println("priKey:" + priKey);

			String costStr = priKey.replaceAll("[^0-9]", " ").trim().replaceAll(" +", " ");
			// System.out.println("costStr:" + costStr);

			if (involveCmd) {

				String cmdStrs = priKey.replaceAll("[^ABCD]", " ").trim().replaceAll(" +", " ");
				// System.out.println("cmdStr:" + cmdStrs);
				String[] costStrs = costStr.split(" ");
				String[] cmdStrss = cmdStrs.split(" ");

				for (int i = 0; i < 4; i++) {
					int j = 0;
					for (String tmpCmd : cmdStrss) {

						if (tmpCmd.equals(cmdStrRep[i])) {
							try {
								msg[3 + i] = (Integer.parseInt(costStrs[j]) - 10) + "";
							} catch (NumberFormatException nfe) {
								return Communicate.NUN_FORMAT_DESC;
							}
							break;
						}
						j++;
					}
				}

			} else {
				try {
					msg[2] = (Integer.parseInt(costStr) - 10) + "";
				} catch (NumberFormatException nfe) {
					return Communicate.NUN_FORMAT_DESC;
				}
			}

			for (int i = 0; i < 4; i++) {
				priKey = priKey.replace(cmdStrRep[i], "");
			}
			priKey = priKey.replace("코스트", "").replaceAll("[0-9]", "");

		}

		for (String temp : Communicate.list_line) {
			if (priKey.contains(temp.substring(0, 2))) {

				msg[0] = temp;
				priKey = priKey.replace(temp, "");
				priKey = priKey.replace(temp.substring(0, 2), "");

				break;
			}
		}

		for (String temp : list_lineage) {
			if (priKey.contains(temp)) {
				msg[1] = temp;
				priKey = priKey.replace(temp, "");
				break;
			}
		}

		StringTokenizer st = new StringTokenizer(priKey);

		int i = 0;
		while (st.hasMoreTokens()) {

			msg[7 + i] = st.nextToken();

			i++;

		}

		boolean emptyCmd = true;

		for (String tmpCmd : msg) {
			if (tmpCmd != null) {
				emptyCmd = false;
				break;
			}
		}

		if (emptyCmd) {
			return "(흑흑)";
		}

		// B이름 C계열 D계보 E코스트 F~J스탯 K,M,O,Q30~90효과 L,N,P,R수치 S태수 T군주
		String str = "";
		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + SEARCH_HEROES_PHP, msg);

		if (jsonstr.contains("fail")) {
			return "fail";
		}

		int cate_n = 0;

		try {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonstr);
			Iterator<String> first_keys = jsonObject.keySet().iterator();

			while (first_keys.hasNext()) {

				str += ((cate_n > 0) ? "," : "");

				String first_key = first_keys.next();
				String jsonDataString = jsonObject.get(first_key).toString();
				boolean emptyJsonData = jsonDataString.equals("[]");
				str += (!emptyJsonData) ? ("\r\n" + first_key.replace("crlf", "\r\n") ) : "";

				ArrayList<JSONObject> al = Communicate.parseJSONObjects(jsonDataString);
				str += (!emptyJsonData) ? ("검색 결과: " + al.size() + "개\r\n---------------------------\n장수이름 병종계열 COST\r\n---------------------------\r\n") : "";

				// System.out.println(jsonDataString + "\r\n");

                Collections.sort(al,new MyJSONComparator("COST"));
				Collections.sort(al,new MyJSONComparator("계열"));

				int elem_n = 0;
				for (JSONObject jsonData : al) {
					//Iterator<String> second_keys = jsonData.keySet().iterator();
					str += jsonData.get("이름").toString() + " " +
							jsonData.get("계열").toString() + " " +
							jsonData.get("COST").toString() + "\r\n";
					//while (second_keys.hasNext()) {
					//	String second_key = second_keys.next();
					//	String data = jsonData.get(second_key).toString();
					//	str += data + " ";
					//}
					//str += "\r\n";
					elem_n++;
				}

				cate_n += (elem_n == 0) ? 0 : 1;

			}

			// for (JSONObject obj : Communicate.parseJSONObjects("[" +
			// object.get(oneKey).toString() + "]")) { Iterator<String> keys =
			// obj.keySet().iterator(); int kn = 0; while (keys.hasNext()) {
			// String
			// key = keys.next(); String val = obj.get(key).toString(); str +=
			// key +
			// ":" + val; str += (kn%2==0)? " / ":"\r\n"; kn++; } }

		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Communicate.PARSE_DESC;
		}

		// System.out.println("dtd:" + str);
		if (str.replace(",", "").equals("")) {
			return "fail";
		}

		return (cate_n < 10) ? str : OVERFLOW_DESC;

	}

	public String getHeroesInfoDB(String priKey) {

		StringTokenizer st = new StringTokenizer(priKey);

		String[] sta = new String[(st.countTokens() * 2) + 1];
		// System.out.println("장수검색: " + priKey + " 냥 : " + st.countTokens() *
		// 2);
		int i = 0;
		while (st.hasMoreTokens()) {

			sta[2 * i + 1] = st.nextToken();
			int j = 0;

			for (String tmp : abb_hero[0]) {

				if (sta[(2 * i + 1)].equals(tmp)) {
					sta[(2 * i + 1)] = abb_hero[1][j];
					sta[(2 * i + 2)] = abb_hero[2][j];
					break;
				}
				j++;
			}

			i++;
		}

		sta[0] = String.valueOf(i);

		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + GET_HEROES_PHP, sta);

		String str = "";

		int total_cost = 0;
		int num_hero = 0;
		int insert_num_hero = Integer.parseInt(sta[0]);
		// B이름 C계열 D계보 E코스트 F~J스탯 K,M,O,Q30~90효과 L,N,P,R수치 S태수 T군주


		HashMap<String, Integer> map_destiny = new HashMap<>();

		try {


			for (JSONObject obj : Communicate.parseJSONObjects(jsonstr)) {
				str += "\r\n" + obj.get("계열") + " " + obj.get("이름") + "\r\n";

				String costStr = obj.get("COST").toString();

				int cost = 0;
				try {
					cost = Integer.parseInt(costStr);
					str += "COST: " + cost + ">" + (cost + 3) + ">" + (cost + 5) + ">" + (cost + 8) + ">" + (cost + 10)
							+ " " + "\r\n";
					
				} catch (NumberFormatException nfe) {
				}

				String destiny = obj.get("인연").toString();
				if(map_destiny.containsKey(destiny)) {
					int des_val = map_destiny.get(destiny);
					map_destiny.put(destiny,des_val+1);

				} else {
					map_destiny.put(destiny, 1);
				}

				if (insert_num_hero < 2) {

					str += "계보: " + obj.get("계보").toString() + "\r\n";

						str += (!destiny.isEmpty())? ("인연: "+destiny + "\r\n") : "";

					str += obj.get("스탯").toString() + "\r\n";
					str += "교본작: 최대 +" + ((cost+16)*5) + "\r\n";
					str += "Lv30: " + obj.get("Lv30").toString() + "\r\n";
					str += "Lv50: " + obj.get("Lv50").toString() + "\r\n";
					str += "Lv70: " + obj.get("Lv70").toString() + "\r\n";
					str += "Lv90: " + obj.get("Lv90").toString() + "\r\n";
					str += "태수: " + obj.get("태수").toString() + "\r\n";
					str += "군주: " + obj.get("군주").toString();
					str += "\r\n,";
				}

				num_hero++;
				total_cost += (cost + 10);

			}





			str = str.substring(2, str.length() - 3);

		} catch (NullPointerException npe) {
			return "fail";
		} catch (StringIndexOutOfBoundsException sioobe) {
			return "fail";
		}

		str += (num_hero > 2 && num_hero == insert_num_hero) ? ",\r\n코스트총합: " + total_cost : "";
		str += (num_hero > 5 && num_hero <= 7 && num_hero == insert_num_hero && (145-total_cost) != 0) ? "\r\n145 - "+total_cost+" = "+ (145-total_cost) : "";
		str += (num_hero > 2 && num_hero <= 5 && num_hero == insert_num_hero&& (99-total_cost) != 0 ) ? "\r\n99 - "+total_cost+" = "+ (99-total_cost) : "";


			for (String key : map_destiny.keySet()) {
				//Log.d("fang",key);
                if( !key.isEmpty()) {
                    String desStr = GetDestinyDB.getInstance().getDestiny(key, map_destiny.get(key), true);
                    str += (desStr.equals(Communicate.PARSE_DESC)) ? "" : ",\r\n" + desStr;
                }
			}




		return str;
	}


    class MyJSONComparator implements Comparator<JSONObject> {

		private final String key;

		MyJSONComparator(String _key) {
			key = _key;
		}

        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            String v1 = (String) o1.get(key);
            String v3 = (String) o2.get(key);
            return v1.compareTo(v3);
        }
    }


	/*
	 * 
	 * 
	 * ArrayList<JSONObject> al = Communicate.parseJSONObjects(jsonstr);
	 * 
	 * str += "검색 결과: " + al.size() + "개\r\n"; str += (al.size() == 100) ?
	 * "검색 결과는 100개까지만 표시됩니다. 조건을 추가하여 검색범위를 줄여보세요\r\n" : ""; str +=
	 * "-------------------\r\n";
	 * 
	 * for (JSONObject obj : Communicate.parseJSONObjects(jsonstr)) { String
	 * hname = obj.get("B").toString(); String hline = obj.get("C").toString();
	 * 
	 * i = 0; for (String tmpName : abb_hero[1]) { if (hname.equals(tmpName) &&
	 * hline.equals(abb_hero[2][i])) { hname = abb_hero[0][i]; } i++; }
	 * 
	 * int cost = Integer.parseInt(obj.get("E").toString()) + 10;
	 * 
	 * str += (msg[0] == null) ? (hline + " ") : ""; str += hname + " " + cost +
	 * "\r\n"; // + hspecString + "\r\n";
	 * 
	 * }
	 * 
	 * str = str.substring(2, str.length() - 2);
	 */
}
