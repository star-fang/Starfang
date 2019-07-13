package com.fang.starfang.network.task;

/*
 * 9/19
 * 장수정보, 연합원 정보 조회
 * 
 * 9/20
 * 코스트 계산
 * 
 * 10/04
 * 서버 호스팅
 * 
 * 10/05
 * 특성 검색
 * 
 */


import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.util.KakaoReplier;

public class LambdaFunctionHandler extends AsyncTask<String, Integer, String> {


	private static final String TAG = "COMMUNICATE_HANDLER";
	private Context context;
	private StatusBarNotification sbn;
	private String send_cat;

	private final static String description = "19.04.02이후 서버 설명 삭제 -> 설명냥 을 치라옹";

	private final static String example = "냥봇 12.06 예시"
			+ "\r\n" + "1.장수정보"
			+ "\r\n" + "    > 여포 초선 등애 냥"
			+ "\r\n" + "2.장수목록"
			+ "\r\n" + "    > 이주약탈 주동 경기병 냥"
			+ "\r\n" + "    > 코스트18이하 역전용사 냥"
			+ "\r\n" + "3.효과설명"
			+ "\r\n" + "    > 강공 방깎 피보조 설명 냥"
			+ "\r\n" + "    > 특공 엠공 방능전 설명 냥"
			+ "\r\n" + "4.병종설명"
			+ "\r\n" + "    > 산기 궁수 중기 설명냥"
			+ "\r\n" + "5.보패조합"
			+ "\r\n" + "    > 각방심저 보패조합냥"
			+ "\r\n" + "    > 파동 보패조합냥'";

	private final static String[][] abb_replace = { { "방관", "방어율관통" }, { "공깎", "공격력하강공격" }, { "정깎", "정신력하강공격" },
			{ "방깎", "방어력하강공격" }, { "순발깎", "순발력하강공격" }, { "사기깎", "사기하강공격" }, { "일필", "일격필살" }, { "책모", "책략모방" },
			{ "정보조", "정신력보조" }, { "공보", "공격력보" }, { "방보", "방어력보" }, { "공책", "공격책략" }, { "연책", "연속책략" },
			{ "원책", "원소책략" }, { "화책", "화계책략" }, { "풍책", "풍계책략" }, { "수책", "수계책략" }, { "지책", "지계책략" }, { "방책", "방해계책략" },
			{ "물공", "물리공격" }, { "특공", "특수공격" }, { "회공", "회심공격" }, { "연공", "연속공격" }, { "연반", "연속반격" }, { "엠피", "MP" },
			{ "엠보조", "MP보조" }, { "엠절", "MP절" }, { "엠회", "MP회복" }, { "엠공", "MP공격" }, { "엠방", "MP방어" }, { "체력", "HP" },
			{ "피보조", "HP보조" }, { "피회", "HP회" }, { "주위피", "주위HP" }, { "간면", "간접공격면역" }, { "특면", "특수공격면역" },
			{ "물필", "물리필중" }, { "간피", "간접피해" }, { "근피", "근접피해" }, { "물피", "물리피해" }, { "책피", "책략피해" }, { "책방", "책략방어" }, { "방능", "방어능력" },
			{ "공능", "공격능력" }, { "능전", "능력전환" }, { "공범", "공격범위" }, { "피범", "피해범위" }, { "방어관", "방어율관통" }, { "상면", "상태이상면역" },
            { "상반", "상태이상반사" }, { "산기", "산악기병" }, { "궁수", "궁병" } };

	private final static String[][] abb_replace2 = { { "상태이상태이상면역역", "상태이상면역" },
			{ "상태이상태이상반사사", "상태이상반사" },{"역발산악기병","역발산기"} };

	private final static String command_key = "냥";
	private final static String command_agenda = "일정";
	private final static String command_terminate = "퇴치";
	private final static String command_terrain = "지형";
	private final static String command_relation = "상성";
	private final static String command_move = "소모";
	private final static String command_key_private = "멍";
	private final static String command_ideapocket = "지낭";
	private final static String command_ambition = "패기";
	private final static String command_item = "보물";
	private final static String command_magic_item = "보패";
	private final static String command_description = "설명";
	private final static String command_example = "예시";
	private final static String command_destiny = "인연";

	public LambdaFunctionHandler(Context c, String sender, StatusBarNotification _sbn) {
		sbn = _sbn;
		send_cat = sender;
		context = c;

	}

	@Override
	protected String doInBackground(String... params) {
		String command = params[0];
		handleRequest(command);
		return "Executed";
	}


	private void handleRequest(String command) {

		String answer = "";

		// String sheet_key = "";
		String primary_key = "";

		if (command.contains("그냥")) {
			return;
		}

		if ( command.substring(0, 1).equals(command_key)
				|| command.substring(command.length() - 1, command.length()).equals(command_key)) {

			Log.d(TAG, "command_key activated!");
				primary_key = command.replace(command_key, "").trim();

				if (primary_key.replace(" ", "").equals("")) {
					answer = "(꺄아)";
				} else if (primary_key.replace(" ", "").length() < 2) {
					answer = "2글자 이상 입력하라옹!";
				} else {

					for (int i = 0; i < abb_replace.length; i++) {
						if (primary_key.contains(abb_replace[i][0])) {
							primary_key = primary_key.replace(abb_replace[i][0], abb_replace[i][1]);
						}
					}

					for (int i = 0; i < abb_replace2.length; i++) {
						if (primary_key.contains(abb_replace2[i][0])) {
							primary_key = primary_key.replace(abb_replace2[i][0], abb_replace2[i][1]);
						}
					}

					if (primary_key.contains(command_description)) {
						primary_key = primary_key.replace(command_description, "");
						answer = GetDescLineSpecDB.getInstance().getDescLineSpecDB(primary_key);
						answer = answer.equals("fail") ? (SearchHeroesDB.getInstance().getHeroesInfoDB(primary_key))
								: answer;
						answer = answer.equals("fail") ? (SearchItemDB.getInstance().searchItem(primary_key, true))
								: answer;
						answer = answer.equals("fail") ? GetDescLineSpecDB.getInstance().getExt(primary_key, true)
								: answer;
						answer = answer.equals("fail") ? description : answer;

					} else if (primary_key.contains(command_example)) {

						answer = example;

					} else if (primary_key.contains(command_magic_item)) {

						primary_key = primary_key.replace(command_magic_item, "");
						answer = SearchMagicItemDB.getInstance().searchMagicItem(primary_key);
					} else if (primary_key.contains(command_terminate)) {
						answer = GetDescLineSpecDB.getInstance().getExt(primary_key, false);
					} else if (primary_key.contains(command_agenda)) {
						answer = Agenda.getInstance().getAgenda(primary_key);
					} else if(primary_key.contains(command_destiny)) {
						answer = GetDestinyDB.getInstance().getDestiny(primary_key,99, false);
					} else if (primary_key.contains(command_terrain)) {
						answer = GetTerrainDB.getInstance().getTerrain(primary_key);
					} else if (primary_key.contains(command_relation)) {
						answer = GetRelationDB.getInstance().getRelation(primary_key);
					} else if (primary_key.contains(command_move)) {
						answer = GetMovingCostDB.getInstance().getMovingCost(primary_key);
					} else if (primary_key.contains(command_item)) {
						primary_key = primary_key.replace(command_item, "");
						answer = SearchItemDB.getInstance().searchItem(primary_key, false);
						answer = (answer.equals("fail")) ? "(깜짝)" : answer;

					} else {

						answer = SearchHeroesDB.getInstance().getHeroesInfoDB(primary_key);
						answer = (answer.equals("fail")) ? SearchHeroesDB.getInstance().searchHeroDB(primary_key)
								: answer;
						answer = (answer.equals("fail")) ? SearchItemDB.getInstance().searchItem(primary_key, false)
								: answer;
						answer = (answer.equals("fail")) ? "(훌쩍)" : answer;
					}

				}


		} else if(  command.substring(0, 1).equals(command_key_private)
				|| command.substring(command.length() - 1, command.length()).equals(command_key_private) ) {

			Log.d(TAG,"coommand_key_private activated!");

			command = command.substring(0, 1).replace(command_key_private,"") +
					command.substring(1, command.length() - 1) + command.substring(command.length() - 1, command.length()).replace(command_key_private,"");



			if (command.contains(command_ideapocket) || command.contains(command_ambition)) {
               answer = CalcWage.getInstance().getWage( command );
			} else if (command.contains("약탈") || command.contains("역명송")) {
			answer = SuckEDB.getInstance().ravenSuck(command);
		} else if (command.contains("설명") && command.contains("명곧")) {

			//answer = SuckEDB.getInstance().descSuckEDB();

		} else {

			/*
			try {
				answer = SuckEDB.getInstance().suckEDBv2(command);
			} catch (NullPointerException e) {
				answer = description;
			}
*/
			// answer = description;

		}
		}


		KakaoReplier replier = new KakaoReplier(context,send_cat,sbn);
		replier.execute(answer, "[S]");





	}


}
