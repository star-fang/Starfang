package com.fang.starfang.network.task;

import com.fang.starfang.network.Communicate;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Agenda {

	private final static String AGENDA_PHP = "agenda.php";

	public final static String[] MAP = { "산지", "설원", "초원", "몽매", "사막", "성내", "장강", "몽매" };
	private static Agenda agenda = null;

	private Agenda() {
	}

	public static final Agenda getInstance() {
		return (agenda == null) ? (agenda = new Agenda()) : agenda;
	}

	public String getAgenda(String key) {
		key = key.replace("일정", "");

		int start_dw;
		if (key.contains("섬멸")) {
			key = key.replace("섬멸전", "섬멸");
			start_dw = Calendar.MONDAY;
		} else if (key.contains("경쟁")) {
			key = key.replace("경쟁전", "경쟁");
			start_dw = Calendar.WEDNESDAY;
		} else {
			return "(빠직)";
		}

		String map = "";
		for (String tmpStr : MAP) {
			if (key.contains(tmpStr)) {
				map = tmpStr;
				key = key.replace(map, "");
				break;
			}
		}

		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		String dateInfo = key.replaceAll("[^0-9]", "");
		Date date = new Date();
		date.setTime(date.getTime());
		try {

			if (dateInfo.length() == 6) {
				key = key.replace(dateInfo, "");
				date = df.parse(dateInfo);
			} else if (dateInfo.length() == 4) {
				key = key.replace(dateInfo, "");
				dateInfo = "19" + dateInfo;
				date = df.parse(dateInfo);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date date_before = new Date();
		date_before.setTime(date.getTime() - 21 * 24 * 60 * 60 * 1000);
		Date date_after = new Date();
		date_after.setTime(date.getTime() + 22 * 24 * 60 * 60 * 1000);
		String jsonstr = Communicate.toServer(Communicate.SERVER_URL + AGENDA_PHP, key.trim(), df.format(date_before),
				df.format(date_after), map);

		// String dw_now = getDW(date, start_dw);

		if (jsonstr.contains("fail")) {
			return "fail";
		}

		String str = "";
		str += "20" + new SimpleDateFormat("yy.MM.dd").format(date) + "\r\n";

		SimpleDateFormat df_md = new SimpleDateFormat("MM.dd");

		for (JSONObject obj : Communicate.parseJSONObjects(jsonstr)) {

			Date start_date;
			try {
				start_date = df.parse(obj.get("시작").toString());

				Date end_date = new Date();
				end_date.setTime(start_date.getTime() + 6 * 24 * 60 * 60 * 1000);

				str += df_md.format(start_date) + "~" + df_md.format(end_date) + " " + obj.get("맵");
				str += ((start_date.getTime() <= date.getTime())
						&& (end_date.getTime() + 24 * 60 * 60 * 1000 > date.getTime())) ? " V" : "";
				str += "\r\n";
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
		}

		return str;
	}

	private String getDW(Date date, int dw) {

		// System.out.println(date);
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");

		Calendar c = Calendar.getInstance();

		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, dw);

		return formatter.format(c.getTime());

	}

}
