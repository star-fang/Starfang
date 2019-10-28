package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SuckEDB {

	private final static String[] cmdMine = { "동광", "서광", "남광", "북광", "중광" };
	private final static String SUCKE_V2_PHP = "suckeV2.php";
	private final static String pw = "ㄱㄴㅇ";

	private static SuckEDB sucke = null;
	
	private SuckEDB() {}

	public static final SuckEDB getInstance() {
		return (sucke == null) ? (sucke = new SuckEDB()) : sucke;
	}

	public String suckEDBv2(String priKey) throws NullPointerException {
		String str = "";
		String[] abb_before = { "위나스","위너스","명성요양원", "명송요양원", "명성휴게소", "명송휴게소", "휴게소", "명성", "베레기", "베레인",
				"마크툽s", "폭스vm", "쿠레레c", "로치", "플라섹서", "섹서토닉", "뉴발란스", "틀소", "suck", "짜져장군", "빤스런", "코동이", "프리더" };
		String[] abb_after = { "도나스","도나스","요양원", "요양원", "휴게송", "휴게송", "휴게송", "명송", "berein", "berein", "마크툽",
				"폭스", "쿠레레", "roach", "플라토닉", "섹서", "newbalance", "tlso", "굉장한석이", "율장군", "삼십육계주위상", "코끼리동산", "프라임리더" };

		priKey = priKey.toLowerCase();
		for (int i = 0; i < abb_before.length; i++) {
			if (priKey.contains(abb_before[i])) {
				priKey = priKey.replace(abb_before[i], abb_after[i]);
			}
		}

		String[] cmdSuck = { "추가수급", "수급", "신규", "조회", "초기화", "변경" };
		String cmd = "";
		for (String tempSuck : cmdSuck) {
			if (priKey.contains(tempSuck)) {
				priKey = priKey.replace(tempSuck, "");
				cmd = tempSuck;
				break;
			}
		}

		if (cmd.isEmpty()) {
			return "(아잉)";
		}

		String ally = "";
		String[] cmdAlly = { "명송", "곧", "휴게송", "요양원", "현자", "패왕", "막공", "갈비군", "비뚜미","천무","도나스","드뀨"};
		for (String tmpAlly : cmdAlly) {
			if (priKey.contains(tmpAlly)) {
				priKey = priKey.replace(tmpAlly, "");
				ally = tmpAlly;
				break;
			}
		}

		boolean desBool = false;
		String desCmd = "비고";
		if (priKey.contains(desCmd)) {
			desBool = true;
			priKey = priKey.replace(desCmd, "");

		}

		String[] cmdMineRep = { "C", "D", "E", "F", "G" };
		String mine = "";
		String mineRep = "";
		int mineI = 0;
		for (String tmpMine : cmdMine) {
			if (priKey.contains(tmpMine)) {
				mine = tmpMine;
				mineRep = cmdMineRep[mineI];
				priKey = priKey.replace(tmpMine, "");

				break;
			}
			mineI++;
		}

		String dateInfo = "";
		String tempDate = priKey.replaceAll("[^0-9]", "");

		if (tempDate.length() == 6) {
			priKey = priKey.replace(tempDate, "");
			// System.out.println("pri:" + priKey);
			dateInfo = tempDate;

			if (!priKey.contains(pw) && !cmd.equals("조회")) {
				return "다른날짜 정보 변경은 비밀번호와 함께 입력해야 한다옹! (비밀번호는 Starfang에게 묻기)";
			}
		}

		priKey = priKey.replace(pw, "");

		if (dateInfo.equals("")) {
			SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
			Date date = new Date();
			date.setTime(date.getTime());
			dateInfo = df.format(date);
		}

		String[] nameAndDes = priKey.trim().split(" ");
		priKey = priKey.replace(nameAndDes[0], "").trim();

		String name = nameAndDes[0];
		String des = priKey;

		str += dateInfo;
		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + SUCKE_V2_PHP, cmd, name, mineRep, ally, des,
				dateInfo);

		int countMine = (cmd.equals("추가수급")) ? 2 : 1;

		if (jsonstr.contains("newone")) {

			if (ally == "") {
				ally = "[연합없음]";
			}
			str += "\r\n" + ally + " " + name + " 신규추가 성공";

		}

		if (jsonstr.contains("succ")) {
			// str += "\r\n수급 추가완료\r\n";
			str += "\r\n" + name + " " + mine + " 수급추가 성공";
			// str += "비고:[" + cmd[4] + "]";

			if (des.equals("")) {
				//str += "\r\n >\"변경 " + name + " (연합명) (비고)\"를 입력하여 연합명과 비고 변경 가능하다옹.";
			}
		} else if (jsonstr.equals("already")) {
			str += "\r\n이미 " + countMine + "번 이상 점령 당한놈이다옹.";
			str += (countMine == 1) ? ("\r\n2번 점령한거면 \"" + name + " " + mine + " 추가수급\" 입력하라옹!") : "";
		} else if (jsonstr.equals("alter")) {
			str += "\r\n" + name + " 변경 완료다옹.";
			// str += "국명:[" + cmd[1] + "], 연합:[" + cmd[3] + "]\r\n";
			// str += "비고:[" + cmd[4] + "]";
		} else if (jsonstr.equals("clear")) {
			str += "\r\n" + name + " 초기화 완료!";
		} else if (jsonstr.equals("beforeone")) {
			str += "\r\n" + name + "는 이미 목록에 있다옹..";
		} else if (jsonstr.equals("noone")) {
			str += "\r\n" + name + "없다옹! (짜증)";
		} else if (jsonstr.contains("[")) {
			try {
				ArrayList<JSONObject> al = Communicate.parseJSONObjects(jsonstr);

				// String aaa = msg[2];
				str += "\r\n명곧 " + al.size() + "마리\r\n";
				str += "----------\r\n";
				str += desBool ? "비고\r\n" : "동서남북중\r\n";
				int sumPub = 0;

				for (JSONObject obj : Communicate.parseJSONObjects(jsonstr)) {
					String minePlusDes = obj.get("C").toString();
					String[] miness = minePlusDes.split(":");
					String mineInfo = "";
					if (!desBool) {
						int sumPri = 0;
						for (int i = 0; i < 5; i++) {
							try {
								if (miness[i].equals("NULL") || miness[i].equals("")) {
									miness[i] = "0";
								}
								mineInfo += miness[i];
								sumPri += Integer.parseInt(miness[i]);

								mineInfo += (i != 4) ? ".." : "";
							} catch (ArrayIndexOutOfBoundsException aioobe) {
								mineInfo += "0";
								mineInfo += (i != 4) ? ".." : "";
							}
						}

						sumPub += sumPri;

						mineInfo += " [" + sumPri + "]";
					} else {
						try {
							mineInfo += (miness[5].equals("NULL")) ? "비고없음" : miness[5];
						} catch (ArrayIndexOutOfBoundsException aioobe) {
							mineInfo += "비고없음";
						}
					}

					str += mineInfo + obj.get("B") + " " + obj.get("A") + " " + "\r\n";

				}

				str += "----------\r\n";
				str += "총 수급 : " + sumPub + "개";
			} catch (NullPointerException npe) {
				str = Communicate.NULL_POINTER_DESC;
			}
		}

		return str;
	}

	public String ravenSuck(String priKey) {
		// 동 서 남 북 중
		priKey = priKey.replace("역명송", "약탈당함");
		String str = "";

		String[] loot1 = { "식량", "제작", "강화", "보존", "은전" };
		String[] loot2 = { "강화", "은전", "은전", "제작", "보존" };
		String[] loot1PRFX = { "", "보물", "보물", "강화", "" };
		String[] loot1SFX = { "", "허가서", "허가서", "권", "" };
		String[] loot1Unit = { "", "장", "장", "장", "" };
		String[] loot2PRFX = { "보물", "", "", "보물", "강화" };
		String[] loot2SFX = { "허가서", "", "", "허가서", "권" };
		String[] loot2Unit = { "장", "", "", "장", "장" };
		int[] ravenUnitHour1 = { 500000, 6, 8, 5, 250000 };
		int[] ravenUnitHour2 = { 2, 20000, 40000, 1, 1 };

		boolean assistant = false;
		if (priKey.contains("보조")) {
			assistant = true;
		}

		boolean noMine = true;
		int num = 0;
		for (; num < cmdMine.length; num++) {
			if (priKey.contains(cmdMine[num])) {
				noMine = false;
				break;
			}
		}

		if (priKey.contains("당함")) {

			// 12시 5분
			// 12:5
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

				if (val_hm == "") {
					return "언제 당했냐옹? (hh:mm or hh시mm분)";
				} else {
					Date date_now = new Date();
					Date date_yesterday = new Date();
					date_now.setTime(date_now.getTime());
					date_yesterday.setTime(date_yesterday.getTime() - 24 * 60 * 60 * 1000);
					SimpleDateFormat df_ymd = new SimpleDateFormat("yy/MM/dd");
					String date_now_ymd = df_ymd.format(date_now);
					String date_yesterday_ymd = df_ymd.format(date_yesterday);
					// SimpleDateFormat df_hm = new SimpleDateFormat("HH:mm");
					// String date_hm = df_hm.format(date_now);
					// SimpleDateFormat df_mdhm = new SimpleDateFormat("MM/dd
					// HH:mm");
					SimpleDateFormat df_ymdhm = new SimpleDateFormat("yy/MM/dd HH:mm");

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
								((double) ravenUnitHour1[num] / 3600.0) * time * (assistant ? 1.2 : 1) * 100) / 100.0;
						double val2 = Math.round(
								((double) ravenUnitHour2[num] / 3600.0) * time * (assistant ? 1.2 : 1) * 100) / 100.0;
						double val11 = Math.round(val1 * 1.2 * 100) / 100.0;
						double val22 = Math.round(val2 * 1.2 * 100) / 100.0;
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

		String val = priKey.replaceAll("[^0-9]", "");

		if (val.equals("")) {
			str += "1시간 약탈량 ";
			str += assistant ? "(약탈 보조)\r\n" : "\r\n";
			str += "광명 | 품목  수량\r\n";
			// str += "ㅡㅡ | ㅡㅡ ㅡㅡ\r\n";
			if (noMine) {
				for (int i = 0; i < loot1.length; i++) {
					str += cmdMine[i] + " | " + loot1[i] + "  ";
					str += assistant ? (double) ravenUnitHour1[i] * 1.2 + "\r\n" : ravenUnitHour1[i] + "\r\n";
					str += cmdMine[i] + " | " + loot2[i] + "  ";
					str += assistant ? (double) ravenUnitHour2[i] * 1.2 + "\r\n" : ravenUnitHour2[i];
					str += (i < loot1.length - 1) ? "\r\n\r\n" : "";
				}
			} else {
				str += cmdMine[num] + " | " + loot1[num] + "  ";
				str += assistant ? (double) ravenUnitHour1[num] * 1.2 + "\r\n" : ravenUnitHour1[num] + "\r\n";
				str += cmdMine[num] + " | " + loot2[num] + "  ";
				str += assistant ? (double) ravenUnitHour2[num] * 1.2 + "\r\n" : ravenUnitHour2[num];
			}

		} else if (!noMine) {

			double ravenUnitSec;

			String loot = "";
			String lootSFX = "";
			String lootPRFX = "";
			String lootUnit = "";
			if (priKey.contains(loot2[num])) {
				loot = loot2[num];
				lootSFX = loot2SFX[num];
				lootPRFX = loot2PRFX[num];
				lootUnit = loot2Unit[num];
				ravenUnitSec = assistant ? ((double) ravenUnitHour2[num] / 3600.0 * 1.2)
						: ((double) ravenUnitHour2[num] / 3600.0);
			} else {
				loot = loot1[num];
				lootSFX = loot1SFX[num];
				lootPRFX = loot1PRFX[num];
				lootUnit = loot1Unit[num];
				ravenUnitSec = assistant ? ((double) ravenUnitHour1[num] / 3600.0 * 1.2)
						: ((double) ravenUnitHour1[num] / 3600.0);
			}

			double doubleVal = Double.parseDouble(val);
			double time = doubleVal / ravenUnitSec;
			int hour = (int) (time / 3600);
			int minute = (int) ((time % 3600) / 60);
			int second = (int) ((time % 3600) % 60);

			double time_remain = (double) (3600 * 3) - time;
			int hour_remain = (int) (time_remain / 3600);
			int minute_remain = (int) ((time_remain % 3600) / 60);
			int second_remain = (int) ((time_remain % 3600) % 60);

			double time_error = 1 / ravenUnitSec;
			int minute_error = (int) (time_error / 60);
			int second_error = (int) (time_error % 60);

			Date dateNow = new Date();
			Date dateFull = new Date();
			// Date dateMax = new Date();
			Date dateRaven = new Date();

			dateNow.setTime(dateNow.getTime());
			dateFull.setTime(dateFull.getTime() + (int) (time_remain * 1000));
			dateRaven.setTime(dateRaven.getTime() + - (int) (time * 1000));
			SimpleDateFormat df = new SimpleDateFormat("MM/dd HH:mm:ss");
			String dateNowInfo = df.format(dateNow);
			String dateFullInfo = df.format(dateFull);
			// String dateMaxInfo = df.format(dateMax);
			String dateRavenInfo = df.format(dateRaven);

			str += cmdMine[num] + " " + lootPRFX + loot + lootSFX + ":" + val + lootUnit + "\r\n";
			str += "점령 : " + ((hour > 0) ? (hour + "시간 ") : "") + minute + "분 " + second + "초 경과\r\n";
			str += "잔여 : " + ((hour_remain > 0) ? (hour_remain + "시간 ") : "") + minute_remain + "분 " + second_remain
					+ "초 남음\r\n";
			str += "오차 : " + ((minute_error > 0) ? (minute_error + "분 ") : "")
					+ ((second_error > 0 || minute_error == 0) ? (second_error + "초 ") : "") + "\r\n\r\n";
			str += "현재시간 : " + dateNowInfo + "\r\n";
			str += "점령시간 : " + dateRavenInfo + " ± 오차\r\n";
			str += "풀광시간 : " + dateFullInfo + " ± 오차";
			// str += "풀광(최대) : " + dateMaxInfo;

		}

		return str;
	}

	public String descSuckEDB() {
		String str = "";

		SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd hh:mm");
		Date date = new Date();
		date.setTime(date.getTime());

		str += "\r\n" + "1.변경+명곧이름+연합명+비고: 연합명 비고 변경\r\n" + "    예시.가리 곧 ㅄ 변경\r\n" +"*연합목록: 명송 곧 휴게송 요양원 현자 패왕 막공 갈비군 비뚜미 천무 도나스\r\n"+ "2.수급+격전지+명곧이름: 격전지 1번점령\r\n"
				+ "    예시.중광 가리 수급\r\n" + "3.추가수급+격전지+명곧이름: 격전지 2번점령\r\n" + "    예시.중광 가리 추가수급\r\n"
				+ "4.초기화+명곧이름: 점령횟수초기화\r\n" + "    예시.선비451 초기화\r\n" + "5.신규+연합+명곧이름: 신규명곧등록\r\n"
				+ "    예시.신규 용가리 곧\r\n" + "6.조회+(연합): 명곧리스트\r\n" + "    예시.명송 181016 조회\r\n"
				+ "7.날짜6자리(예시.181016)입력: 해당날짜반영\r\n" + "  날짜 생략: 당일 반영 ";
		str += df.format(date) + "\r\n";

		return str;

	}

}
