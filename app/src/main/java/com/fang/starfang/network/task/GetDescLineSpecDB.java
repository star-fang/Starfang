package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;

import java.util.StringTokenizer;

public class GetDescLineSpecDB {

	private static GetDescLineSpecDB line = null;
	private final static String GET_DESC_PHP = "get_desc.php";
	private final static String EXT_PHP = "exterminate.php";
	private final static String TOO_MANY_DESC = "검색 결과가 너무 많다옹.. 좀더 자세하게 입력하라옹";

	private GetDescLineSpecDB() {
	}

	public static final GetDescLineSpecDB getInstance() {
		return (line == null) ? (line = new GetDescLineSpecDB()) : line;
	}

	public String getDescLineSpecDB(String priKey) {
		StringTokenizer st = new StringTokenizer(priKey);
		String[] sta = new String[st.countTokens() + 1];

		int i = 0;
		if (st.countTokens() == 0) {
			return "fail";
		}
		while (st.hasMoreTokens()) {
			sta[i + 1] = st.nextToken();
			i++;
		}
		sta[0] = String.valueOf(i);

		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + GET_DESC_PHP, sta);

		String str = "";
		int n = 0;
		try {
			for (JSONObject obj : Communicate.parseJSONObjects(jsonstr)) {
				
				str += ((n > 0) ? "," : "");
				
				if (obj.get("branchName") == null) {
					str += "\r\n" + obj.get("A") + "\r\n" + obj.get("B").toString().replace(",", " ") + "\r\n";
				} else {
					String line = obj.get("branchName").toString();
					str += "\r\n" + line + "\r\n";
					str += "공" + obj.get("branchStatGGs:공격력") + " ";
					str += "정" + obj.get("branchStatGGs:정신력") + " ";
					str += "방" + obj.get("branchStatGGs:방어력") + " ";
					str += "순" + obj.get("branchStatGGs:순발력") + " ";
					str += "사" + obj.get("branchStatGGs:사기") + " \r\n";
					str += "HP" + obj.get("branchHP") + " ";
					String mp = obj.get("branchMP").toString();
					str += (mp.equals("0") || mp.isEmpty())? "" : "MP"+mp+" ";
					String ep = obj.get("branchEP").toString();
					str += (ep.equals("0") || ep.isEmpty())? "" : "EP"+ep+" ";
					str += "이동력" + obj.get("branchMoving") + " ";
					str += "\r\n*부대효과 : ";
					str += "\r\n승급 2: " + obj.get("branchPasvSpecs:1") + " " + obj.get("branchPasvSpecValues:1");
					str += "\r\n승급 3: " + obj.get("branchPasvSpecs:2") + " " + obj.get("branchPasvSpecValues:2");
					str += "\r\n승급 4: " + obj.get("branchPasvSpecs:3") + " " + obj.get("branchPasvSpecValues:3");
					str += "\r\n*장수효과 : ";
					str += "\r\nLv01: " + obj.get("branchSpecs:1") + " " + obj.get("branchSpecValues:1");
					str += "\r\nLv10: " + obj.get("branchSpecs:10") + " " + obj.get("branchSpecValues:10");
					str += "\r\nLv15: " + obj.get("branchSpecs:15") + " " + obj.get("branchSpecValues:15");
					str += "\r\nLv20: " + obj.get("branchSpecs:20") + " " + obj.get("branchSpecValues:20");
					str += "\r\nLv25: " + obj.get("branchSpecs:25") + " " + obj.get("branchSpecValues:25")+ "\r\n";
				}
				

				n++;
			}

			str = str.substring(2, str.length() - 2);

		} catch (NullPointerException npe) {
			return "fail";
		} catch (StringIndexOutOfBoundsException sioobe) {
			return "fail";
		}

		return (n>9)? TOO_MANY_DESC : str;
	}

	public String getExt(String priKey, boolean desc) {
		priKey = priKey.replace("퇴치", "").trim();

		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + EXT_PHP, priKey);

		int n = 1;
		String str = "";

		try {
			for (JSONObject obj : Communicate.parseJSONObjects(jsonstr)) {
				str += "\r\n" + n + "." + obj.get("B").toString().replace(",", "") + " (" + obj.get("C") + "마리)";
				str += desc ? ("\r\n" + obj.get("D").toString().replace(",", "")+"\r\n") : "";
				
				n++;
			}
		} catch (NullPointerException npe) {
			return "fail";

		}

		try {
			str = desc? str.substring(2, str.length() - 2) : str;
		} catch (StringIndexOutOfBoundsException sioobe) {
			return "fail";
		}

		return str;

	}

}
