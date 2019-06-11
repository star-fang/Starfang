package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchItemDB {

	private final static String SEARCH_ITEM_PHP = "search_item.php";

	private static SearchItemDB item = null;
	private static int[] ReinforcementValueSevenAandD = { 0, 6, 12, 18, 24, 32, 40, 50, 65, 82, 99, 118, 156 }; // Attack&Defence
	private static int[] ReinforcementValueSevenMandA = { 0, 2, 4, 6, 8, 10, 12, 15, 19, 24, 29, 34, 44 }; // morale&agile
	private static int[] ReinforcementValueSevenAssist = { 0, 3, 6, 9, 12, 15, 19, 23, 27, 32, 37, 42, 47 };

	private SearchItemDB() {
	}

	public static final SearchItemDB getInstance() {
		return (item == null) ? (item = new SearchItemDB()) : item;
	}

	public String searchItem(String key, boolean desc) {
		String str = "";

		String[] keys = key.split(" ");
		int reinVal = 0;
		try {
			if (keys.length == 1) {
				reinVal = Integer.parseInt(key.replaceAll("[^0-9]", "").trim());
				key = key.replaceAll("[0-9]", "");
			} else {
				for (String tmpKey : keys) {

					reinVal = Integer.parseInt(tmpKey.replaceAll("[^0-9]", "").trim());
					key = (reinVal > 0) ? key.replace(tmpKey, "") : key;

				}
			}
		} catch (NumberFormatException nfe) {

		}

		if (reinVal < 0 || reinVal > 12) {
			return "fail";
		}

		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + SEARCH_ITEM_PHP, key.trim());
		if (jsonstr.contains("fail")) {
			return "fail";
		}

		String[] statNames = { "itemStats:공격력", "itemStats:정신력", "itemStats:방어력", "itemStats:순발력", "itemStats:사기", "itemStats:이동력" };

		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) new JSONParser().parse(jsonstr);
			@SuppressWarnings("unchecked")
			Iterator<String> first_keys = jsonObject.keySet().iterator();
			int n = 0;
			while (first_keys.hasNext()) {

				str += ((n > 0) ? "," : "");

				String first_key = first_keys.next();
				String jsonDataString = jsonObject.get(first_key).toString();
				boolean emptyJsonData = jsonDataString.equals("[]");
				// str += (reinVal > 0) ? (reinVal + "강 ") : "";

				boolean isName = first_key.equals("보물 이름 검색");


				str += (!emptyJsonData && !isName ) ? ("\r\n" + first_key.replace("crlf", "\r\n") + "\r\n") : "";


				ArrayList<JSONObject> al = Communicate.parseJSONObjects(jsonDataString);
				str += (!emptyJsonData && !isName ) ? ("검색 결과: " + al.size() + "개\r\n-------------------\r\n") : "";

				// System.out.println(jsonDataString + "\r\n");

				int m = 0;
				for (JSONObject jsonData : al) {
					// str += ((m > 0) ? "," : "");
					//str += ((m > 0) ? "\r\n" : "");
					String gradeStr = jsonData.get("itemGrade").toString();
					int grade;
					try {
						grade = gradeStr.equals("전용") ? 7 : Integer.parseInt(gradeStr);
					} catch(NumberFormatException nfe) {
						grade = 0;
					}
					String cate = jsonData.get("itemSubCate").toString();
					str += "[" + cate + "] " + ((reinVal > 0 && grade == 7) ? ("+" + reinVal) : "") + jsonData.get("itemName")
							+ " (" + gradeStr + ")";

					if ( isName ) {

                        String spec = jsonData.get("itemSpecs:1").toString();
                        String specVal = jsonData.get("itemSpecValues:1").toString();
                        String spec2 = jsonData.get("itemSpecs:2").toString();
                        String spec2Val = jsonData.get("itemSpecValues:2").toString();

						if (!desc) {
							boolean isWeapon = (grade == 7) && !cate.equals("전포") && !cate.equals("갑옷")
									&& !cate.equals("의복") && !cate.equals("보조구");
							boolean isArmor = (grade == 7) && (cate.equals("전포") || cate.equals("갑옷") || cate.equals("의복"));
							boolean isAssist = (grade == 7) && cate.equals("보조구");
							boolean physicalAttack = isWeapon && !cate.equals("선") && !cate.equals("보도");
							boolean nonPhysicalAttack = isWeapon && (cate.equals("선") || cate.equals("보도"));

							for (String statName : statNames) {

								String tmpStat = jsonData.get(statName).toString();
								int basicStat = tmpStat.equals("-") ? 0 : Integer.parseInt(tmpStat);

								boolean plusPhysicalAttack = (physicalAttack && statName.equals("itemStats:공격력"));
								boolean plusNonPhysicalAttack = (nonPhysicalAttack && statName.equals("itemStats:정신력"));
								boolean plusMandA = (isWeapon && (statName.equals("itemStats:순발력") || statName.equals("itemStats:사기")));
								boolean plusDefense = isArmor && statName.equals("itemStats:방어력");
								boolean plusAssistVal = isAssist && !statName.equals("itemStats:이동력");

								//boolean plus = plusMandA || plusNonPhysicalAttack || plusPhysicalAttack || plusDefense
								//		|| plusAssistVal;
								int plusStat = (plusPhysicalAttack || plusNonPhysicalAttack || plusDefense)
										? ReinforcementValueSevenAandD[reinVal] : 0;
								plusStat = plusMandA ? ReinforcementValueSevenMandA[reinVal] : plusStat;
								plusStat = plusAssistVal ? ReinforcementValueSevenAssist[reinVal] : plusStat;
								basicStat += plusStat;
								statName = statName.replace("itemStats:","");
								statName = (statName.length()==2)? statName+"　": statName;
								str += (basicStat == 0) ? "" : ("\r\n" + statName + ": " + basicStat);
								str += (plusStat > 0) ? "(+" + plusStat + ")" : "";

							}



							str += (!spec.equals("")) ? ("\r\n" + spec + " " + ((specVal.equals("-")) ? "" : specVal)) :  "";
							str += (!spec2.equals("")) ? ("\r\n" + spec2 + " " + ((spec2Val.equals("-")) ? "" : spec2Val))
									: "";

						}

						str += desc ? ("\r\n" + jsonData.get("itemDescription").toString().replace(",", " ")) : "";
						String spec_desc = jsonData.get("itemSpecs:1:desc").toString();
						spec_desc = spec_desc.replace("n%", specVal).replace("n(%)", specVal).replace("n", specVal);
						String spec_desc2 = jsonData.get("itemSpecs:2:desc").toString();
						spec_desc2 = spec_desc2.replace("n%", spec2Val).replace("n(%)", spec2Val).replace("n", spec2Val);

                        str += (desc && !spec_desc.equals(""))? ("\r\n\r\n*" + spec + ": " + spec_desc) : "";
                        str += (desc && !spec_desc2.equals(""))? ("\r\n\r\n*" + spec2 + ": " + spec_desc2) : "";

                        str += ",";
						m++;
					}
                    str += "\r\n";
				}

				n++;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NullPointerException npe ) {

		}

		return str;
	}

}
