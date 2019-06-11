package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SearchMagicItemDB {

	private final static String SEARCH_MAGIC_COMB_PHP = "search_magic.php";
	private final static String PREFIX_INFO_PHP = "prefix.php";
	// private final static String SEARCH_MAGIC_INFO_PHP =
	// "search_magic_info.php";

	private static SearchMagicItemDB item = null;

	private SearchMagicItemDB() {
	}

	public static final SearchMagicItemDB getInstance() {
		return (item == null) ? (item = new SearchMagicItemDB()) : item;
	}

	public String searchMagicItem(String priKey) {
		String[] magicSFX = { "각", "항", "저", "방", "심", "미", "기", "정", "귀", "유", "성", "장", "익", "진" };
		String combCMD = "조합";
		String[] msg = new String[9];
		String str = "";

		// priKey = priKey.replaceAll("[0-9]", "");

		int j = 0;
		int k = 4;
		if (priKey.contains(combCMD)) {
			for (String tmpSFX : magicSFX) {
				if (priKey.contains(tmpSFX)) {
					if (tmpSFX.equals("진")
							&& priKey.substring(priKey.indexOf("진") + 1, priKey.indexOf("진") + 2).equals("화")) {

					} else {
						msg[j++] = tmpSFX;
						priKey = priKey.replace(tmpSFX, "");
					}
					// System.out.println(tmpSFX + " 추가");
				}
			}

			priKey = priKey.replace(combCMD, "");

			StringTokenizer st = new StringTokenizer(priKey);
			while (st.hasMoreTokens()) {
				msg[k++] = st.nextToken();
			}

			String jsonstr = Communicate.toServer(Communicate.SERVER_URL + SEARCH_MAGIC_COMB_PHP, msg);

			try {
				ArrayList<JSONObject> al = Communicate.parseJSONObjects(jsonstr);

				str += "검색 결과: " + al.size() + "개\r\n";
				str += (al.size() == 100) ? "검색 결과는 100개까지만 표시됩니다. 조건을 추가하여 검색범위를 줄여보세요\r\n" : "";
				str += "-------------------\r\n";

				for (JSONObject obj : Communicate.parseJSONObjects(jsonstr)) {
					str += obj.get("C") + " [" + obj.get("B") + "등급] " + obj.get("A") + "\r\n";
				}
				str = str.substring(0, str.length() - 2);
			} catch (NullPointerException npe) {
				str = Communicate.NULL_POINTER_DESC;
			}
		} else {
			String tmpStat = priKey.replaceAll("[^0-9]", "");
			String msg1 = (tmpStat != null) ? priKey.replace(tmpStat, "") : priKey;

			String jsonstr = Communicate.toServer(Communicate.SERVER_URL + PREFIX_INFO_PHP, msg1.trim(), tmpStat);
			ArrayList<JSONObject> al = Communicate.parseJSONObjects(jsonstr);
			for (JSONObject object : al) {
				str += object.get("접두사") + "(" + object.get("효과") + ")\r\n";
				str += object.get("스탯") + "\r\n";
				for (int level = 1; level < 6; level++) {
					str += "Lv." + level + ":  " + object.get("Lv." + level) + "\r\n";
				}
				str += ",";

			}

		}

		return str;
	}

}
