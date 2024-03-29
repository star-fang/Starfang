package com.fang.starfang.local.task;

import android.content.Context;
import android.util.Log;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.CustomAgenda;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.source.Agenda;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Destiny;
import com.fang.starfang.local.model.realm.source.Dot;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.local.model.realm.source.Magic;
import com.fang.starfang.local.model.realm.source.RelicCombination;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.local.model.realm.source.Relation;
import com.fang.starfang.local.model.realm.source.Spec;
import com.fang.starfang.local.model.realm.source.TVpair;
import com.fang.starfang.local.model.realm.source.Terrain;
import com.fang.starfang.local.model.realm.primitive.RealmString;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mozilla.javascript.Scriptable;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;


class CatLambda {
    private WeakReference<Context> context;
    private String catRoom;

    private static final String TAG = "FANG_MOD_CAT";

    private enum COMMAND_CERTAIN_ENUM {
        COMMAND_DEST, COMMAND_TER, COMMAND_MOV, COMMAND_DESC,
        COMMAND_ITEM, COMMAND_AGENDA, COMMAND_RELIC,
        COMMAND_RELATION, COMMAND_DOT, COMMAND_COMB,
        COMMAND_CALC, COMMAND_MAGIC, COMMAND_REG_AGENDA, COMMAND_DEL_AGENDA, COMMAND_DESC_CAT, COMMAND_DEFAULT
    }

    private static final String[] COMMAND_CERTAIN = {
            "인연", "지형", "소모", "설명",
            "보물", "일정", "보패",
            "상성", "도트", "조합", "계산", "책략", "등록", "삭제", "냥"};

    private static final String[] weekNames = {"일", "월", "화", "수", "목", "금", "토"};

    private static final String[] PRFX_COMMAND = {
            "", "", "이동력", "",
            "", "", "",
            "병종", "", "보패", "수식", "", "일정", "일정", ""};
    private static final String[] SFX_COMMAND = {
            "", "상성", "", "",
            "", "", "",
            "", "", "", "", "", "", "", ""};

    private static final String CRLF = "\r\n";
    private static final String BLANK_DOUBLE = "  ";
    private static final String BLANK_TRIPLE = "   ";
    private static final String EMPTY = "";
    private static final String DASH = "-";
    private static final String EQUALS = "=";
    private static final String COMMA = ",";
    private static final String SEPARATOR = "----------------------------\n";
    private static final String STAR_FILLED = "★";
    private static final String COLON = ":";
    private static final String RANGE_EMPTY = "□";
    private static final String RANGE_FULL = "■";
    private static final String RIGHT_ARROW = "→";
    private static final String LEFT_ARROW = "←";
    private static final String SLASH = "/";
    private static final String PLUS = "+";
    private static final String BLANK = " ";

    private static final String REGEX_SPACE = "\\s+";
    private static final String REGEX_EXCEPT_DIGITS = "[^0-9]";
    private static final String REGEX_DIGITS = "[0-9]";

    CatLambda(Context context, String catRoom) {
        this.context = new WeakReference<>(context);
        this.catRoom = catRoom;
    }

    String handleRequest(String req, Realm realm) {

        COMMAND_CERTAIN_ENUM certainCMD = COMMAND_CERTAIN_ENUM.COMMAND_DEFAULT;
        for (COMMAND_CERTAIN_ENUM certain : COMMAND_CERTAIN_ENUM.values()) {
            try {
                int certainIndex = certain.ordinal();
                if (certainIndex < COMMAND_CERTAIN.length) {
                    String probKey = COMMAND_CERTAIN[certainIndex];
                    String suffix = SFX_COMMAND[certainIndex];
                    String prefix = PRFX_COMMAND[certainIndex];

                    String reqWithoutSFX = req;
                    try {
                        if (!suffix.isEmpty())
                            reqWithoutSFX = (req.substring(req.length() - suffix.length()).equals(suffix)) ?
                                    req.substring(0, req.length() - suffix.length()) : req;
                    } catch (StringIndexOutOfBoundsException e) {
                        Log.e(TAG,Log.getStackTraceString(e));
                    }
                    int sfxIndex = reqWithoutSFX.length() - probKey.length();
                    if ((sfxIndex > -1 && sfxIndex < reqWithoutSFX.length())) {
                        try {
                            if (reqWithoutSFX.substring(sfxIndex).equals(probKey)) {
                                certainCMD = certain;
                                req = req.replace(prefix, EMPTY);
                                req = req.replace(suffix, EMPTY).trim();
                                req = req.substring(0, req.length() - probKey.length()).trim();
                                if (req.replaceAll(REGEX_SPACE, EMPTY).isEmpty()) {
                                    req = null;
                                }
                                break;
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            Log.e(TAG,Log.getStackTraceString(e));
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }
        }


        HandleLocalDB dotByPoints = (q -> {

            StringBuilder emptyResult = new StringBuilder();
            emptyResult.append("도트 목록").append(CRLF).append(SEPARATOR);
            RealmResults<Dot> dots = realm.where(Dot.class).findAll();
            for (Dot dot : dots) {
                emptyResult.append(dot.getDotName()).append(CRLF);
            }

            if (q == null)
                return emptyResult.toString();


            Dot dot = realm.where(Dot.class).equalTo(Dot.FIELD_NAME, q.trim()).findFirst();

            StringBuilder lambdaResult = new StringBuilder();
            try {
                if (dot != null) {
                    lambdaResult.append(drawRangeView(dot.getDotPoints().split(COMMA)));
                }
            } catch (NullPointerException e) {
                return emptyResult.toString();
            }

            return lambdaResult.toString();
        });

        // 장수 이름으로 정보 검색 : 조조 냥
        HandleLocalDB heroesByName = (q -> {
            if (q == null) {
                return null;
            }

            Log.d(TAG, "heroesByName Activated: " + q);

            LinkedList<String> rQueue = new LinkedList<>(Arrays.asList(q.split(REGEX_SPACE)));

            if (rQueue.isEmpty()) return null;

            int insert_hero_num = rQueue.size();
            int searched_hero_count = 0;
            int total_cost = 0;

            HashMap<String, Integer> map_destiny = new HashMap<>();
            StringBuilder lambdaResult = new StringBuilder();

            lambdaResult.append(
                    (insert_hero_num > 1) ?
                            SEPARATOR + "병종계열 장수이름 COST\r\n" + SEPARATOR : EMPTY);

            while (!rQueue.isEmpty()) {
                String nameToSearch = rQueue.remove();
                if (!nameToSearch.isEmpty()) {

                    RealmResults<Heroes> realmHero =
                            realm.where(Heroes.class)
                                    .equalTo(Heroes.FIELD_NAME, nameToSearch)
                                    .or().contains(Heroes.FIELD_NAME2, nameToSearch)
                                    .findAll();

                    if (realmHero.isEmpty()) {
                        return null;
                    }

                    for (Heroes tmpHero : realmHero) {
                        ++searched_hero_count;
                        int init_cost = tmpHero.getHeroCost();
                        total_cost += (init_cost + 10);

                        String line = tmpHero.getHeroBranch();
                        line = (insert_hero_num > 1) ?
                                String.format("%-4s", line).replace(' ', '　') :
                                line;

                        String name = tmpHero.getHeroName();
                        name = (insert_hero_num > 1) ?
                                String.format("%-4s", name).replace(' ', '　') :
                                name;

                        lambdaResult.append((insert_hero_num > 1) ? searched_hero_count + ". " : EMPTY)
                                .append(line).append(BLANK).append(name)
                                .append(insert_hero_num == 1 ? (tmpHero.getHeroName2() != null ? "  a.k.a. " + tmpHero.getHeroName2() : EMPTY)
                                        + CRLF + Heroes.INIT_COST + ": "
                                        + init_cost + RIGHT_ARROW
                                        + (init_cost + 3) + RIGHT_ARROW
                                        + (init_cost + 5) + RIGHT_ARROW
                                        + (init_cost + 8) + RIGHT_ARROW
                                        : BLANK).append(init_cost + 10)
                                .append(CRLF);

                        String destinies = tmpHero.getHeroDestiny();
                        if (destinies != null) {
                            for (String destiny : destinies.split(COLON)) {
                                int putValue = 1;
                                Integer curDestinyValue = map_destiny.get(destiny);
                                if (curDestinyValue != null) {
                                    putValue = curDestinyValue + 1;
                                }
                                map_destiny.put(destiny, putValue);
                            }
                        }

                        if (insert_hero_num == 1) {
                            lambdaResult.append(Heroes.INIT_LINEAGE).append(COLON).append(BLANK)
                                    .append(tmpHero.getHeroLineage()).append(CRLF);

                            if (destinies != null) {
                                String[] destinyArr = destinies.split(COLON);
                                if (destinyArr.length == 1) {
                                    lambdaResult.append(Heroes.INIT_DESTINY + COLON + BLANK).append(destinyArr[0]).append(CRLF);
                                } else if (destinyArr.length > 1) {
                                    for (int i = 0; i < destinyArr.length; i++) {
                                        lambdaResult.append(Heroes.INIT_DESTINY).append((i + 1)).append(COLON).append(BLANK).append(destinyArr[i]).append(CRLF);
                                    }
                                }
                            }

                            for (int i = 0; i < Heroes.INIT_STATS.length; i++)
                                lambdaResult.append(Heroes.INIT_STATS[i])
                                        .append(tmpHero.getHeroStats().get(i)).append(BLANK);
                            lambdaResult.append(CRLF);

                            lambdaResult.append("교본작: 최대 +").append((init_cost + 16) * 5).append(CRLF);

                            for (int i = 0; i < Heroes.INIT_SPECS.length; i++) {
                                String val = EMPTY;
                                try {
                                    if (i < 4) {
                                        RealmString tmpSpecValue = tmpHero.getHeroSpecValues().get(i);
                                        if (tmpSpecValue != null) {
                                            val = tmpSpecValue.toString();
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    Log.e(TAG,Log.getStackTraceString(e));

                                }
                                lambdaResult.append(Heroes.INIT_SPECS[i]).append(COLON).append(BLANK)
                                        .append(tmpHero.getHeroSpecs().get(i)).append(BLANK)
                                        .append(val).append(CRLF);

                            }

                            String personality = tmpHero.getHeroPersonality();
                            if (personality != null) {
                                lambdaResult.append("성격: ").append(personality).append(CRLF);
                            }
                            lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
                        }
                    }

                    lambdaResult.append((rQueue.isEmpty() || insert_hero_num > 1) ? EMPTY : ",\r\n");
                } else {
                    insert_hero_num--;
                }
            }


            if (insert_hero_num == searched_hero_count) {
                if (insert_hero_num > 1)
                    lambdaResult.append(SEPARATOR).append("TOTAL COST: [").append(BLANK_DOUBLE).append(total_cost).append(BLANK_DOUBLE).append("]").append(CRLF);

                if (insert_hero_num > 2 && insert_hero_num < 6 && total_cost != 99) {
                    lambdaResult.append("섬멸전").append(COLON).append(BLANK_DOUBLE).append(99)
                            .append(BLANK).append(DASH).append(BLANK)
                            .append(total_cost)
                            .append(BLANK).append(EQUALS).append(BLANK)
                            .append(99 - total_cost).append(CRLF);
                }

                if (insert_hero_num > 4 && insert_hero_num < 8 && total_cost != 145) {
                    lambdaResult.append("경쟁전").append(COLON).append(BLANK).append(145)
                            .append(BLANK).append(DASH).append(BLANK)
                            .append(total_cost)
                            .append(BLANK).append(EQUALS).append(BLANK)
                            .append(145 - total_cost).append(CRLF);
                }


                Set<String> set = map_destiny.keySet();
                for (String key : set.toArray(new String[0])) {
                    //Log.d( TAG, key +": " + map_destiny.get(key));
                    if (!key.isEmpty()) {
                        Destiny des = realm.where(Destiny.class).equalTo(Destiny.FIELD_NAME, key).findFirst();
                        try {
                            Integer mapDestinyValue = map_destiny.get(key);
                            if (mapDestinyValue != null && des != null) {
                                String desValueStr = des.getdesJoinEffect().get(0);
                                if (desValueStr != null) {
                                    if (NumberUtils.toInt(desValueStr.substring(0, 1)) <= mapDestinyValue) {
                                        lambdaResult.append(SEPARATOR).append(des.getDesName()).append(CRLF);
                                        for (String joinEffect : des.getdesJoinEffect()) {
                                            if (joinEffect != null) {
                                                String[] joinEffectSplit = joinEffect.split(COLON);
                                                lambdaResult.append(NumberUtils.isDigits(joinEffectSplit[0]) ? EMPTY : joinEffectSplit[0] + " 시 ");
                                                lambdaResult.append(joinEffectSplit[1]).append(BLANK).append(joinEffectSplit[2]).append(CRLF);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (NullPointerException e) {
                            Log.e(TAG,Log.getStackTraceString(e));
                        }
                    }
                }

            } else if (insert_hero_num > 1) {
                RealmResults<Heroes> whoHaveSecondName =
                        realm.where(Heroes.class).isNotNull(Heroes.FIELD_NAME2)
                                .and().notEqualTo(Heroes.FIELD_ID, 0)
                                .sort(Heroes.FIELD_NAME).findAll();
                lambdaResult.append(",동명 장수는 따로 입력 하라옹\r\n");

                for (Heroes tmpHero : whoHaveSecondName) {

                    String line = tmpHero.getHeroBranch();
                    String name = tmpHero.getHeroName();

                    String rep = String.format("%-4s", line).replace(' ', '　')
                            + BLANK + String.format("%-4s", name).replace(' ', '　')
                            + " ▷ " + tmpHero.getHeroName2();
                    lambdaResult.append(rep).append(CRLF);
                }

            }


            return lambdaResult.toString();
        });

        // 장수 특성으로 이름 검색 : 연환공격 냥
        HandleLocalDB heroesBySpec = (q -> {

            if (q == null) {
                return null;
            }

            Log.d(TAG, "heroesBySpec Activated: " + q);

            int cost_more = 0;
            int cost_below = 0;
            int cost_equal = 0;
            int level_pivot = 0;

            if (q.contains("특성")) {
                // 30, 50, 70, 90 : 30 ~ 49, 50 ~ 69, 70 ~ 89, 90 ~ 99
                // 0 1 2 3  0 / 20 = 0, 20 / 20 = 1, 40 / 20 = 2, 60 / 20 = 3
                level_pivot = NumberUtils.toInt(q.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY), 0);
                level_pivot = level_pivot < 30 ? 0 : level_pivot;
                level_pivot = level_pivot < 50 && level_pivot >= 30 ? 30 : level_pivot;
                level_pivot = level_pivot < 70 && level_pivot >= 50 ? 50 : level_pivot;
                level_pivot = level_pivot < 90 && level_pivot >= 70 ? 70 : level_pivot;
                level_pivot = Math.min(level_pivot, 90);
                q = q.replace("특성", EMPTY).replaceAll(REGEX_DIGITS, EMPTY).trim();
                if (q.replaceAll(REGEX_SPACE, EMPTY).isEmpty())
                    return null;
                //Log.d(TAG,level_pivot + "특성");
            } else if (q.contains("~")) {
                String beforePivot = q.substring(0, q.indexOf("~"));
                String afterPivot = q.substring(q.indexOf("~"));
                cost_more = NumberUtils.toInt(beforePivot.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY), 10) - 10;
                cost_below = NumberUtils.toInt(afterPivot.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY), 10) - 10;
                q = q.replace("~", EMPTY).replaceAll(REGEX_DIGITS, EMPTY).trim();
            } else {
                cost_equal = NumberUtils.toInt(q.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY), 10) - 10;
                q = q.replaceAll(REGEX_DIGITS, EMPTY).trim();
            }


            LinkedList<String> rQueue = new LinkedList<>(Arrays.asList(q.split(BLANK)));

            if (rQueue.isEmpty()) return null;
            String qBranch = null;
            String qLineage = null;
            ArrayList<String> specList = new ArrayList<>();
            while (!rQueue.isEmpty()) {
                String each = rQueue.remove();
                Branch branch = findBranchByName(each, realm);
                if (branch != null) {
                    qBranch = branch.getBranchName();
                } else {
                    String validLineageName = findLineageName(each, realm);
                    if (validLineageName != null) {
                        qLineage = validLineageName;
                        //Log.d(TAG,"Lineage: " + qLineage);
                    } else if (!each.equals("코스트") && !each.equals("코") && !each.equals("장수")) {
                        //Log.d(TAG,"spec: " + each);
                        specList.add(each);
                    }
                }
            }


            if (qBranch == null && qLineage == null
                    && cost_below == 0 && cost_equal == 0 && cost_more == 0
                    && specList.isEmpty())
                return null;


            int rowSize = 1; // 1 ~ query 갯수
            int colSize = 0; // 0 ~ 입력효과갯수
            String[][] validSpecs = new String[specList.size()][];
            StringBuilder oneSpec = new StringBuilder();
            for (String specEach : specList)
                oneSpec.append(specEach);
            if (!oneSpec.toString().replace(BLANK, EMPTY).isEmpty()) {

                RealmResults<Spec> specs = realm.where(Spec.class)
                        .equalTo(Spec.FIELD_NAME_NO_BLANK, oneSpec.toString(), Case.INSENSITIVE)
                        .or().equalTo(Spec.FIELD_NAME2, oneSpec.toString()).findAll();
                if (specs.isEmpty())
                    specs = realm.where(Spec.class)
                            .contains(Spec.FIELD_NAME_NO_BLANK, oneSpec.toString(), Case.INSENSITIVE)
                            .or().contains(Spec.FIELD_NAME2, oneSpec.toString()).findAll();

                if (!specs.isEmpty()) {
                    specList.clear();
                    specList.add(oneSpec.toString());
                }

                for (String probSpec : specList) {
                    if (probSpec.length() < 2) return null;
                    specs = specList.size() == 1 ? specs : realm.where(Spec.class)
                            .equalTo(Spec.FIELD_NAME_NO_BLANK, probSpec, Case.INSENSITIVE)
                            .or().equalTo(Spec.FIELD_NAME2, probSpec).findAll();
                    if (specs.isEmpty()) {
                        specs = specList.size() == 1 ? specs : realm.where(Spec.class)
                                .contains(Spec.FIELD_NAME_NO_BLANK, probSpec, Case.INSENSITIVE)
                                .or().contains(Spec.FIELD_NAME2, probSpec).findAll();
                        if (specs.isEmpty()) return null;
                    }
                    validSpecs[colSize] = new String[specs.size()];
                    for (int i = 0; i < specs.size(); i++) {
                        Spec curSpec = specs.get(i);
                        if (curSpec != null) {
                            validSpecs[colSize][i] = curSpec.getSpecName();
                        }
                    }
                    rowSize *= specs.size();
                    colSize++;
                }
                Log.d(TAG, "rowSize:" + rowSize + " colSize:" + colSize);

                if (rowSize > 500)
                    return rowSize + context.get().getResources().getString(R.string.desc_too_many_combs);
            }
            StringBuilder lambdaResult = new StringBuilder();

            int valid_spec_count = 0;
            int valid_hero_count_total = 0;
            StringBuilder descForTooManyResults = new StringBuilder();
            for (int i = 0; i < rowSize; i++) {
                ArrayList<String> keyList = new ArrayList<>();
                for (int j = 0; j < colSize; j++) {
                    int base = 1;
                    int underBase = 1;
                    for (int k = j; k < colSize; k++) {
                        try {
                            base *= validSpecs[k].length;
                            if (validSpecs.length > k + 1) {
                                underBase *= validSpecs[k + 1].length;
                            }
                        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                            Log.e(TAG,Log.getStackTraceString(e));
                        }
                    }
                    int specNo = i % base / underBase;
                    keyList.add(validSpecs[j][specNo]);
                }
                RealmQuery<Heroes> query = realm.where(Heroes.class);
                query = (qBranch != null) ? query.equalTo(Heroes.FIELD_BRANCH, qBranch) : query;
                query = (qLineage != null) ? query.equalTo(Heroes.FIELD_LINEAGE, qLineage) : query;

                for (String key : keyList)
                    query.equalTo(Heroes.FIELD_SPECS + "." + RealmString.VALUE, key);
                query = (cost_equal > 0) ? query.equalTo(Heroes.FIELD_COST, cost_equal) : query;
                RealmResults<Heroes> heroes = query.greaterThanOrEqualTo(Heroes.FIELD_COST, cost_more > 0 ? cost_more : 4)
                        .and().lessThanOrEqualTo(Heroes.FIELD_COST, cost_below > 0 ? cost_below : 15)
                        .and().notEqualTo(Heroes.FIELD_ID, 0)
                        .findAll()
                        .sort(Heroes.FIELD_COST, Sort.ASCENDING)
                        .sort(Heroes.FIELD_BRANCH);

                if (!heroes.isEmpty()) {


                    boolean searchOneSpec = keyList.size() == 1;
                    descForTooManyResults.append(++valid_spec_count).append(".");

                    StringBuilder lambdaEachResult = new StringBuilder();
                    lambdaEachResult.append(level_pivot > 0 ? "*" + level_pivot + "특성: " : EMPTY);
                    for (String key : keyList) {
                        descForTooManyResults.append(key.replace(BLANK, EMPTY)).append(BLANK);
                        lambdaEachResult.append(level_pivot == 0 ? "*" : EMPTY).append(key).append(CRLF);
                    }
                    descForTooManyResults.append(CRLF);
                    lambdaEachResult.append(qBranch != null ? "*계열: " + qBranch + CRLF : EMPTY);
                    lambdaEachResult.append(qLineage != null ? "*계보: " + qLineage + CRLF : EMPTY);
                    lambdaEachResult.append(cost_below > 0 ? "*COST: " + (cost_below + 10) + "이하" + CRLF : EMPTY);
                    lambdaEachResult.append(cost_more > 0 ? "*COST: " + (cost_more + 10) + "이상" + CRLF : EMPTY);
                    lambdaEachResult.append(cost_equal > 0 ? "*COST: " + (cost_equal + 10) + CRLF : EMPTY);
                    lambdaEachResult.append(level_pivot == 0 ? "검색 결과: " + heroes.size() + "개" + CRLF : BLANK);

                    StringBuilder heroListBuilder = new StringBuilder();
                    heroListBuilder.append(SEPARATOR).append("병종계열 장수이름 COST")
                            .append(level_pivot > 0 ? " 수치" : EMPTY).append(CRLF).append(SEPARATOR);

                    int count_each = 0;
                    for (Heroes hero : heroes) {

                        // 30 50 70 90 => 0 1 2 3
                        if (level_pivot == 0) {
                            heroListBuilder.append(String.format("%-4s", hero.getHeroBranch()).replace(' ', '　'))
                                    .append(BLANK).append(String.format("%-4s", hero.getHeroName()).replace(' ', '　'))
                                    .append(BLANK).append(hero.getHeroCost() + 10).append(CRLF);
                            count_each++;
                        } else {
                            RealmString tmpHeroSpec = hero.getHeroSpecs().get((level_pivot - 30) / 20);
                            if (tmpHeroSpec != null) {
                                if (tmpHeroSpec.toString().equals(keyList.get(0))) {

                                    //Log.d(TAG,((level_pivot-30)/20)+"번째 특성: "+ hero.getHeroSpecs().get((level_pivot-30)/20) + " = " + keyList.get(0));
                                    heroListBuilder.append(String.format("%-4s", hero.getHeroBranch()).replace(' ', '　'))
                                            .append(BLANK).append(String.format("%-4s", hero.getHeroName()).replace(' ', '　'))
                                            .append(BLANK).append(String.format("%-4s", (hero.getHeroCost() + 10) + BLANK).replace(' ', '　'))
                                            .append(searchOneSpec ? hero.getHeroSpecValues().get((level_pivot - 30) / 20) : EMPTY).append(CRLF);
                                    count_each++;
                                }
                            }
                        }
                    }

                    lambdaEachResult.append(level_pivot > 0 ? "검색 결과: " + count_each + "개" + CRLF : EMPTY);
                    lambdaEachResult.append(heroListBuilder);
                    valid_hero_count_total += count_each;
                    lambdaResult.append(count_each > 0 ? lambdaEachResult.toString() + FangConstant.CONSTRAINT_SEPARATOR + CRLF : EMPTY);
                }
            }

            String result;
            if (valid_spec_count == 0 || valid_hero_count_total == 0)
                result = null;
            else if (valid_spec_count > 6)
                result = "검색 결과가 너무 많다옹.. (" + valid_spec_count + "개)" + CRLF
                        + "다음 조합 중에 하나를 입력하라옹!" + CRLF
                        + SEPARATOR + descForTooManyResults;
            else
                result = lambdaResult.toString();


            return result;
        });

        // 특성으로 설명 검색 : 연속책략 설명냥
        HandleLocalDB descBySpec = (q -> {

            if (q == null) {
                return context.get().getResources().getString(R.string.description);
            }

            Log.d(TAG, "descBySpec Activated : " + q);

            ArrayList<String> specList = new ArrayList<>(Arrays.asList(q.split(BLANK)));

            StringBuilder lambdaResult = new StringBuilder();
            StringBuilder oneSpec = new StringBuilder();
            for (String spec : specList)
                oneSpec.append(spec);

            RealmResults<Spec> realmSpec = realm.where(Spec.class)
                    .equalTo(Spec.FIELD_NAME_NO_BLANK, oneSpec.toString(), Case.INSENSITIVE)
                    .or().equalTo(Spec.FIELD_NAME2, oneSpec.toString()).findAll();
            if (realmSpec.isEmpty())
                realmSpec = realm.where(Spec.class)
                        .contains(Spec.FIELD_NAME_NO_BLANK, oneSpec.toString(), Case.INSENSITIVE)
                        .or().contains(Spec.FIELD_NAME2, oneSpec.toString()).findAll();

            if (!realmSpec.isEmpty()) {
                specList.clear();
                specList.add(oneSpec.toString());
            }

            for (String probSpec : specList) {
                if (!probSpec.isEmpty()) {
                    realmSpec = specList.size() == 1 ? realmSpec : realm.where(Spec.class)
                            .equalTo(Spec.FIELD_NAME_NO_BLANK, probSpec, Case.INSENSITIVE)
                            .or().equalTo(Spec.FIELD_NAME2, probSpec).findAll();
                    if (realmSpec.isEmpty())
                        realmSpec = specList.size() == 1 ? realmSpec : realm.where(Spec.class)
                                .contains(Spec.FIELD_NAME_NO_BLANK, probSpec, Case.INSENSITIVE)
                                .or().contains(Spec.FIELD_NAME2, probSpec).findAll();

                    if (realmSpec.isEmpty())
                        return null;

                    if (realmSpec.size() > 4) {
                        StringBuilder descForTooManyResults = new StringBuilder();
                        descForTooManyResults.append(probSpec).append(COLON).append(" 검색 결과 ")
                                .append(realmSpec.size()).append("개")
                                .append(CRLF).append(SEPARATOR);
                        for (Spec spec : realmSpec)
                            descForTooManyResults.append(spec.getSpecName()).append(CRLF);

                        lambdaResult.append(descForTooManyResults).append(COMMA);
                    } else {
                        for (Spec spec : realmSpec) {
                            lambdaResult.append(spec.getSpecName()).
                                    append(spec.getSpecName2() != null ? "\r\na.k.a. " + spec.getSpecName2().replace(COLON, "，") + CRLF : CRLF);
                            int specAccFlag = spec.getSpecAccumulate();
                            if (specAccFlag >= 0) {
                                lambdaResult.append("*동일 효과 중첩 ").append((specAccFlag == 0) ? "불가" : "가능").append(CRLF);
                            }

                            String triggerTile = spec.getSpecTriggerTile();
                            if (triggerTile != null) {
                                lambdaResult.append("*트리거 지형: ").append(triggerTile).append(CRLF);
                            }

                            int triggerType = spec.getSpecTriggerType();
                            if (triggerType > 0) {
                                lambdaResult.append("*트리거 타입: ").append(triggerType).append(CRLF);
                            }

                            String specParent = spec.getSpecParent();
                            if (specParent != null) {
                                lambdaResult.append("*상속 효과: ").append(specParent).append(CRLF);
                            }

                            lambdaResult.append(CRLF).append(spec.getSpecDescription()).append(COMMA);

                        }
                    }
                }
            }
            return lambdaResult.toString();
        });


        // 아이템 설명 : 번우 설명냥
        HandleLocalDB descByItem = (q -> {
            if (q == null) {
                return null;
            }

            Log.d(TAG, "descByItem Activated : " + q);

            LinkedList<String> rQueue = new LinkedList<>(Arrays.asList(q.split(BLANK)));

            StringBuilder lambdaResult = new StringBuilder();
            while (!rQueue.isEmpty()) {
                String probItem = rQueue.remove();
                RealmResults<Item> itemResult = realm.where(Item.class).contains(Item.FIELD_NAME_NO_BLANK, probItem).or()
                        .contains(Item.FIELD_NAME, probItem).findAll();
                if (itemResult.isEmpty()) return null;
                for (Item item : itemResult) {

                    String itemSpecOne = null;
                    String itemSpecTwo = null;

                    lambdaResult.append("[").append(item.getItemSubCate()).append("] ").append(item.getItemName()).append(" (")
                            .append(STAR_FILLED).append(item.getItemGrade()).append(")").append(CRLF).append(item.getItemDescription());

                    try {
                        RealmString tmpItemSpec = item.getItemSpecs().get(0);
                        if (tmpItemSpec != null) {
                            itemSpecOne = tmpItemSpec.toString();
                        }

                        if (itemSpecOne != null) {
                            Spec spec = realm.where(Spec.class).equalTo(Spec.FIELD_NAME, itemSpecOne).findFirst();
                            if (spec != null) {
                                lambdaResult.append(CRLF).append(CRLF).append("*").append(itemSpecOne).append(COLON).append(BLANK);

                                RealmString itemSpecValue = item.getItemSpecValues().get(0);
                                if (itemSpecValue != null) {
                                    lambdaResult.append(spec.getSpecDescription().replace("n(%)", "n%")
                                            .replace("n%", itemSpecValue.toString()));
                                } else {
                                    lambdaResult.append(spec.getSpecDescription());
                                }
                            }
                        }

                        RealmString tmpItemSpecTwo = item.getItemSpecs().get(1);
                        if (tmpItemSpecTwo != null) {
                            itemSpecTwo = tmpItemSpecTwo.toString();
                        }

                        if (itemSpecTwo != null) {
                            Spec specTwo = realm.where(Spec.class).equalTo(Spec.FIELD_NAME, itemSpecTwo).findFirst();
                            if (specTwo != null) {
                                lambdaResult.append(CRLF).append(CRLF).append("*").append(itemSpecTwo).append(COLON).append(BLANK);

                                RealmString itemSpecValue = item.getItemSpecValues().get(1);
                                if (itemSpecValue != null) {
                                    lambdaResult.append(specTwo.getSpecDescription().replace("n(%)", "n%")
                                            .replace("n%", itemSpecValue.toString()));
                                } else {
                                    lambdaResult.append(specTwo.getSpecDescription());
                                }
                            }
                        }

                    } catch (NullPointerException e) {
                        Log.e(TAG,Log.getStackTraceString(e));
                    }

                    lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);


                }
            }

            return lambdaResult.toString();
        });


        // 병종 정보 검색 : 수군 설명냥
        HandleLocalDB descByBranch = (q -> {

            if (q == null) {
                return null;
            }

            Log.d(TAG, "descByBranch Activated : " + q);

            int grade = NumberUtils.toInt(q.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY), 0);
            boolean gradeDecisive = grade > 0 && grade < 6;

            grade = !gradeDecisive ? 5 : grade;
            q = q.replaceAll(REGEX_DIGITS, EMPTY);
            if (gradeDecisive) {
                q = q.replace("단계", EMPTY).replace("승급", BLANK);
            }

            LinkedList<String> rQueue = new LinkedList<>(Arrays.asList(q.split(BLANK)));

            StringBuilder lambdaResult = new StringBuilder();
            boolean branchDecisive = false;
            while (!rQueue.isEmpty()) {
                String probBranch = rQueue.remove();
                if (!probBranch.isEmpty()) {
                    Branch branch = findBranchByName(probBranch, realm);
                    if (branch != null) {
                        branchDecisive = true;
                        RealmList<RealmString> branchGrades = branch.getBranchGrade();
                        String branchGradeNameStr = EMPTY;
                        if (branchGrades != null && gradeDecisive) {
                            RealmString branchGradeName = branchGrades.get(grade - 1);
                            if (branchGradeName != null) {
                                branchGradeNameStr = branchGradeName.toString();
                            }
                        }
                        lambdaResult.append(gradeDecisive ? STAR_FILLED + grade + BLANK : EMPTY)
                                .append(branch.getBranchName()).append(BLANK)
                                .append(gradeDecisive ? DASH + BLANK + branchGradeNameStr : EMPTY).append(CRLF);
                        for (int i = 0; i < Branch.INIT_STATS.length; i++)
                            lambdaResult.append(Branch.INIT_STATS[i])
                                    .append(branch.getBranchStatGGs()
                                            .get(i)).append(BLANK);

                        lambdaResult.append(CRLF).append(branch.getBranchOtherStats());

                        lambdaResult.append("*부대 효과(").append(grade).append("차 승급 기준)").append(CRLF);
                        int specFiltered = 0;
                        for (int i = 0; i < Branch.NUM_PASVS; i++) {
                            RealmInteger branchPasvSpecGrade = branch.getBranchPasvSpecGrades().get(i);
                            if (branchPasvSpecGrade != null) {
                                if (grade >= branchPasvSpecGrade.toInt()) {
                                    specFiltered += 1;
                                    lambdaResult.append("   승급").append(branchPasvSpecGrade)
                                            .append(": ").append(branch.getBranchPasvSpecs().get(i));
                                    RealmString BranchPasvSpecValueRealmString = branch.getBranchPasvSpecValues().get(i);
                                    if (BranchPasvSpecValueRealmString != null) {
                                        String val = BranchPasvSpecValueRealmString.toString();
                                        String valCur = val;
                                        if (val.contains(SLASH)) {
                                            // 3/5/7%
                                            // [3/5/7]
                                            String[] valSplit = val.split(SLASH);
                                            try {
                                                valCur = valSplit[grade - branchPasvSpecGrade.toInt()]
                                                        .replaceAll(REGEX_EXCEPT_DIGITS, BLANK).trim();
                                            } catch (ArrayIndexOutOfBoundsException e) {
                                                Log.e(TAG,Log.getStackTraceString(e));
                                            }
                                            if (val.contains("[")) {
                                                valCur = "[" + valCur + "]";
                                            } else if (val.contains("%")) {
                                                valCur = valCur + "%";
                                            }
                                        }
                                        lambdaResult.append(BLANK).append(valCur);
                                    }
                                    lambdaResult.append(CRLF);
                                }
                            }
                        }
                        if (specFiltered == 0) {
                            lambdaResult.append(BLANK_TRIPLE).append("적용되는 효과 없음\r\n");
                        }
                        lambdaResult.append("*장수 효과").append(CRLF);
                        for (Branch.INIT_SPECS spec : Branch.INIT_SPECS.values()) {
                            lambdaResult.append(BLANK_TRIPLE).append(spec.name()).append(COLON)
                                    .append(BLANK).append(branch.getBranchSpecs().get(spec.ordinal()));
                            RealmString branchSpecValuesRealmString = branch.getBranchSpecValues().get(spec.ordinal());
                            if (branchSpecValuesRealmString != null) {
                                lambdaResult.append(BLANK).append(branchSpecValuesRealmString.toString());
                            }
                            lambdaResult.append(CRLF);
                        }


                        if (branchGrades != null && !gradeDecisive) {
                            lambdaResult.append("*승급 단계").append(CRLF);
                            int branchGradeSize = branchGrades.size();
                            for (int i = 0; i < branchGradeSize; i++) {
                                RealmString branchGrade = branchGrades.get(i);
                                lambdaResult.append(BLANK_TRIPLE)
                                        .append(STAR_FILLED).append(i + 1).append(COLON);
                                if (branchGrade != null) {
                                    lambdaResult.append(BLANK).append(branchGrade.toString());
                                }
                                lambdaResult.append(CRLF);
                            } // end for
                        } // end if(branchGrades != null)

                        lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR).append(CRLF);
                    } // end if branch != null
                }
            } // end while


            return branchDecisive ? lambdaResult.toString() : null;
        });

        // 지형 정보 검색 : 노전차 지형냥
        COMMAND_CERTAIN_ENUM finalCertainCMD = certainCMD;
        HandleLocalDB terrainInfoByKey = (q -> {

            if (q == null) {
                return "병종 또는 지형을 입력하라옹!";
            }

            Log.d(TAG, "terrainInfoByKey Activated : " + q);

            LinkedList<String> rQueue = new LinkedList<>(Arrays.asList(q.split(BLANK)));

            boolean isTerCMD = (finalCertainCMD == COMMAND_CERTAIN_ENUM.COMMAND_TER);
            StringBuilder lambdaResult = new StringBuilder();
            if (rQueue.size() > 2 || rQueue.isEmpty())
                return null;

            String key1 = rQueue.remove();

            Branch validBranch = findBranchByName(key1, realm);
            boolean firstKeyIsBranch = (validBranch != null);
            key1 = firstKeyIsBranch ? validBranch.getBranchName() : key1;

            if (rQueue.isEmpty()) {  // key가 하나뿐
                if (firstKeyIsBranch) {
                    Terrain terrain = realm.where(Terrain.class).equalTo(Terrain.FIELD_BRANCH_NAME, key1).findFirst();
                    if (terrain == null) return null;
                    lambdaResult.append(terrain.getBranchName()).append(BLANK)
                            .append(PRFX_COMMAND[finalCertainCMD.ordinal()])
                            .append(COMMAND_CERTAIN[finalCertainCMD.ordinal()])
                            .append(SFX_COMMAND[finalCertainCMD.ordinal()]).append(CRLF)
                            .append(SEPARATOR);
                    int iForCRLF = 0;


                    for (TVpair pair : isTerCMD ? terrain.getTerrainSyns() : terrain.getMovingCost()) {

                        lambdaResult.append(pair.getPaddTvTerrainName(4)).append(COLON)
                                .append(pair.getPaddTvValue(isTerCMD ? 3 : 1));
                        lambdaResult.append((iForCRLF++) % 2 == 0 ? BLANK + BLANK : CRLF);
                    }
                } else { // keyIsTerrain
                    RealmResults<Terrain> terrains = realm.where(Terrain.class).findAll();

                    int iForCRLF = 0;
                    lambdaResult.append(key1).append(BLANK)
                            .append(PRFX_COMMAND[finalCertainCMD.ordinal()])
                            .append(COMMAND_CERTAIN[finalCertainCMD.ordinal()])
                            .append(SFX_COMMAND[finalCertainCMD.ordinal()]).append(CRLF)
                            .append(SEPARATOR);
                    for (Terrain terrain : terrains) {
                        RealmList<TVpair> tvs = isTerCMD ? terrain.getTerrainSyns() : terrain.getMovingCost();
                        boolean terrainIsValid = false;
                        for (TVpair pair : tvs) {
                            if (pair.getTvTerrainName().contains(key1)) {
                                lambdaResult.append(terrain.getPaddBranchName(4))
                                        .append(COLON).append(pair.getPaddTvValue(isTerCMD ? 3 : 1));
                                terrainIsValid = true;
                                break;
                            }
                        }
                        if (!terrainIsValid) return null;
                        lambdaResult.append((iForCRLF++) % 2 == 0 ? BLANK + BLANK : CRLF);
                    }

                }
            } else { // key가 2개
                String key2 = rQueue.remove();
                String branchKey = firstKeyIsBranch ? key1 : key2;
                String terrainKey = firstKeyIsBranch ? key2 : key1;
                Terrain terrain = realm.where(Terrain.class).equalTo(Terrain.FIELD_BRANCH_NAME, branchKey).findFirst();
                if (terrain == null) return null;

                lambdaResult.append(branchKey).append(BLANK).append(terrainKey)
                        .append(PRFX_COMMAND[finalCertainCMD.ordinal()])
                        .append(COMMAND_CERTAIN[finalCertainCMD.ordinal()])
                        .append(SFX_COMMAND[finalCertainCMD.ordinal()]).append(COLON).append(BLANK);
                boolean terrainIsValid = false;
                for (TVpair pair : isTerCMD ? terrain.getTerrainSyns() : terrain.getMovingCost()) {
                    if (pair.getTvTerrainName().contains(terrainKey)) {
                        lambdaResult.append("[").append(pair.getTvValue()).append("]");
                        terrainIsValid = true;
                        break;
                    }
                }
                if (!terrainIsValid) return null;
            }
            return lambdaResult.toString();
        });


        // 인연 검색
        HandleLocalDB destinyByKey = (q -> {
            if (q == null) {
                StringBuilder nameResult = new StringBuilder("인연 전체 목록" + CRLF + SEPARATOR);
                for (Destiny destiny : realm.where(Destiny.class).findAll())
                    nameResult.append(destiny.getDesName()).append(CRLF);
                return nameResult.toString();
            }

            Log.d(TAG, "destinyByKey Activated : " + q);


            StringBuilder lambdaResult = new StringBuilder();
            RealmResults<Destiny> destinies = realm.where(Destiny.class).contains(Destiny.FIELD_NAME_NO_BLANK, q.replace(BLANK, EMPTY)).findAll();
            if (destinies.isEmpty()) {
                HashMap<String, Integer> map_destiny = new HashMap<>();
                for (String probHero : q.split(BLANK)) {
                    RealmResults<Heroes> heroes = realm.where(Heroes.class).equalTo(Heroes.FIELD_NAME, probHero)
                            .or().contains(Heroes.FIELD_NAME2, probHero).findAll();
                    for (Heroes hero : heroes) {
                        String desName = hero.getHeroDestiny();
                        if (desName != null)
                            for (String desNameEach : desName.split(COLON))
                                map_destiny.put(desNameEach, 1);
                    }
                }
                RealmQuery<Destiny> destinyQuery = realm.where(Destiny.class);

                for (String des : map_destiny.keySet())
                    destinyQuery.equalTo(Destiny.FIELD_NAME, des).or();
                destinyQuery.alwaysFalse();
                destinies = destinyQuery.findAll();
            }


            if (!destinies.isEmpty()) {
                for (Destiny destiny : destinies) {
                    lambdaResult.append(destiny.getDesName()).append(CRLF);
                    try {
                        String desCord = destiny.getDesCord();
                        if (desCord != null)
                            lambdaResult.append(Destiny.INIT_CORD).append(desCord).append(CRLF);
                        for (int i = 0; i < destiny.getdesCondition().size(); i++) {
                            String condition = destiny.getdesCondition().get(i);
                            if (condition != null)
                                lambdaResult.append(Destiny.INIT_CONDITIONS[i]).append(condition).append(CRLF);
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG,Log.getStackTraceString(e));
                    }
                    try {
                        lambdaResult.append(Destiny.INIT_LASTING_EFFECT).append(destiny.getDesLastingEffect()).append(CRLF);
                        for (String effect : destiny.getdesJoinEffect()) {
                            String[] effectSplit = effect.split(COLON);
                            lambdaResult.append(effectSplit[0]).append(NumberUtils.isDigits(effectSplit[0]) ? "명 이상 출진: "
                                    : "시 발동: ").append(effectSplit[1]).append(BLANK).append(effectSplit[2]).append(CRLF);
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG,Log.getStackTraceString(e));
                    }
                    lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
                }
            } else {
                return null;
            }

            return lambdaResult.toString();

        });

        // 보물 이름, 특성 검색 : 7 보인갑 냥 / 전용 무기 냥
        HandleLocalDB itemByKey = (q -> {

            if (q == null) {
                return "보물 이름 종류 특수효과를 입력하라옹";
            }

            Log.d(TAG, "itemByKey Activated: " + q);

            String reinfOrGRD = q.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY);
            q = q.replaceAll(REGEX_DIGITS, EMPTY);

            String[] ignores = {"강", "등급", PLUS, "성"};
            ArrayList<String> ignoreList = new ArrayList<>(Arrays.asList(ignores));
            LinkedList<String> qList = new LinkedList<>(Arrays.asList(q.split(BLANK)));
            ArrayList<String> nsList = new ArrayList<>();
            RealmResults<ItemCate> cates = null;
            String insertCate = null;
            while (!qList.isEmpty()) {
                String qEach = qList.remove();
                if (qEach.equals("전용") || qEach.equals("연의") || qEach.equals("제작")) {
                    reinfOrGRD = qEach;
                } else if (!ignoreList.contains(qEach)) {
                    RealmResults<ItemCate> mainCate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_MAIN_CATE, qEach).findAll();
                    cates = (cates == null && !mainCate.isEmpty()) ? mainCate : cates;
                    if (mainCate.isEmpty()) {
                        RealmResults<ItemCate> subCate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_SUB_CATE, qEach).findAll();
                        cates = (cates == null && !subCate.isEmpty()) ? subCate : cates;
                        if (subCate.isEmpty())
                            nsList.add(qEach);
                        else
                            insertCate = qEach;
                    } else {
                        insertCate = qEach;
                    }
                }
            }

            StringBuilder nameOrSpecBuilder = new StringBuilder();
            for (String name : nsList)
                nameOrSpecBuilder.append(name);
            String nameOrSpec = nameOrSpecBuilder.toString();
            //Log.d(TAG,"nameOrSpec = " + nameOrSpec);

            StringBuilder lambdaResult = new StringBuilder();

            RealmQuery<Item> itemQuery = realm.where(Item.class);
            if (cates != null) {
                itemQuery.beginGroup();
                for (ItemCate cate : cates) {
                    itemQuery.equalTo(Item.FIELD_SUB_CATE, cate.getItemSubCate()).or();
                }
                itemQuery.alwaysFalse().endGroup();
            }

            RealmResults<Item> itemsByName = nameOrSpec.length() < 2 ? itemQuery.and().alwaysFalse().findAll() :
                    itemQuery.and().contains(Item.FIELD_NAME_NO_BLANK, nameOrSpec).findAll();
            if (!itemsByName.isEmpty()) {
                Log.d(TAG, "Item Search type A");
                for (Item item : itemsByName) {
                    Reinforcement reinforcement = new Reinforcement(realm, item);
                    StringBuilder builderForPower = new StringBuilder();
                    String reinfStr = EMPTY;
                    for (int i = 0; i < Item.INIT_STATS.length; i++) {
                        RealmInteger itemPowerRealmInteger = item.getItemStats().get(i);
                        int basePower = itemPowerRealmInteger == null ? 0 : itemPowerRealmInteger.toInt();

                        int plusPower = 0;
                        if (!reinfOrGRD.isEmpty()) {
                            int reinfVal = NumberUtils.toInt(reinfOrGRD, 0);
                            plusPower = reinforcement.reinforce(i, reinfVal);
                            basePower += plusPower;
                            if (plusPower > 0) {
                                reinfStr = PLUS + reinfVal + BLANK;
                            }
                        }

                        String powerStr = basePower == 0 ? null : plusPower == 0 ? (basePower + EMPTY) :
                                basePower + " (+" + plusPower + ")";
                        if (powerStr != null) {
                            builderForPower.append(BLANK_DOUBLE).append(Item.INIT_STATS[i])
                                    .append(COLON).append(BLANK).append(powerStr).append(CRLF);
                        }
                    }


                    lambdaResult.append("[").append(item.getItemSubCate()).append("]").append(BLANK).append(reinfStr)
                            .append(item.getItemName()).append(BLANK).append("(").
                            append(STAR_FILLED).append(item.getItemGrade()).append(")").append(CRLF);

                    if (builderForPower.length() > 0) {
                        lambdaResult.append("*능력치").append(CRLF).append(builderForPower.toString());
                    }

                    StringBuilder builderForSpec = new StringBuilder();
                    for (int i = 0; i < item.getItemSpecValues().size(); i++) {
                        RealmString itemSpec = item.getItemSpecs().get(i);
                        if (itemSpec != null) {
                            String itemSpecStr = itemSpec.toString();
                            if (!itemSpecStr.isEmpty()) {
                                builderForSpec.append(BLANK_DOUBLE).append(itemSpec);
                                RealmString itemSpecValue = item.getItemSpecValues().get(i);
                                if (itemSpecValue != null) {
                                    builderForSpec.append(BLANK).append(itemSpecValue.toString());
                                }
                                builderForSpec.append(CRLF);
                            }
                        }
                    } // end for

                    if (builderForSpec.length() > 0) {

                        String restrictHeroStr = EMPTY;
                        int restrictionHero = item.getItemRestrictionHero();
                        if (restrictionHero > 0) {
                            Heroes hero = realm.where(Heroes.class).equalTo(Heroes.FIELD_ID, restrictionHero).findFirst();
                            if (hero != null) {
                                restrictHeroStr = BLANK + "(" + hero.getHeroBranch() + BLANK + hero.getHeroName() + ")";
                            }
                        }

                        lambdaResult.append("*보물 효과").append(restrictHeroStr).append(CRLF).append(builderForSpec.toString());
                    }


                    String restrictionBranch = item.getItemRestrictionBranch();
                    if (restrictionBranch == null) {
                        ItemCate sub_cate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_SUB_CATE, item.getItemSubCate()).findFirst();
                        if (sub_cate != null) {
                            restrictionBranch = sub_cate.getItemRestrictionBranch();
                        }
                    }
                    if (restrictionBranch != null) {
                        lambdaResult.append("*착용 가능").append(CRLF);

                        int i = 0;
                        for (String branchNoStr : restrictionBranch.split(COMMA)) {
                            int branchNo = NumberUtils.toInt(branchNoStr);
                            Branch branch = realm.where(Branch.class).equalTo(Branch.FIELD_ID, branchNo).findFirst();
                            if (branch != null) {
                                lambdaResult.append(BLANK_DOUBLE).append(StringUtils.rightPad(branch.getBranchName(), 4, '　')).append((i++) % 2 == 0 ? EMPTY : CRLF);
                            }
                        }  // end for
                    }
                    lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
                }
            } else {

                RealmResults<Spec> specs = nameOrSpec.isEmpty() ? null :
                        realm.where(Spec.class).contains(Spec.FIELD_NAME_NO_BLANK, nameOrSpec, Case.INSENSITIVE)
                                .or().contains(Spec.FIELD_NAME2, nameOrSpec).findAll();

                final String reinfOrGrdReplacement = reinfOrGRD.isEmpty() ? EMPTY : "*" + reinfOrGRD + CRLF;
                if (specs == null) {
                    Log.d(TAG, "Item Search type B");
                    itemQuery = realm.where(Item.class);
                    if (cates != null) {
                        itemQuery.beginGroup();
                        for (ItemCate cate : cates)
                            itemQuery.equalTo(Item.FIELD_SUB_CATE, cate.getItemSubCate()).or();
                        itemQuery.alwaysFalse().endGroup();
                    }
                    itemQuery = reinfOrGRD.isEmpty() ? itemQuery : itemQuery.and().equalTo(Item.FIELD_GRD, reinfOrGRD);
                    RealmResults<Item> itemsByCATE = itemQuery.findAll().sort(Item.FIELD_SUB_CATE);

                    if (itemsByCATE.isEmpty()) return null;

                    lambdaResult.append(insertCate != null ? "*" + insertCate + CRLF : EMPTY);
                    lambdaResult.append(reinfOrGrdReplacement);
                    lambdaResult.append("검색 결과: ").append(itemsByCATE.size()).append("개")
                            .append(CRLF).append(SEPARATOR);
                    for (Item item : itemsByCATE) {
                        lambdaResult.append("[").append(item.getItemSubCate()).append("]").append(BLANK)
                                .append(item.getItemName()).append(BLANK);
                        if (reinfOrGRD.isEmpty())
                            lambdaResult.append("(").append(STAR_FILLED).append(item.getItemGrade()).append(")");
                        lambdaResult.append(CRLF);
                    }

                } else if (!specs.isEmpty()) {
                    Log.d(TAG, "Item Search type C");
                    int count = 0;
                    for (Spec spec : specs) {
                        itemQuery = realm.where(Item.class);
                        if (cates != null) {
                            itemQuery.beginGroup();
                            for (ItemCate cate : cates)
                                itemQuery.equalTo(Item.FIELD_SUB_CATE, cate.getItemSubCate()).or();
                            itemQuery.alwaysFalse().endGroup();
                        }

                        itemQuery = reinfOrGRD.isEmpty() ? itemQuery : itemQuery.and().equalTo(Item.FIELD_GRD, reinfOrGRD);
                        RealmResults<Item> itemsBySpec = itemQuery.and()
                                .equalTo(Item.FIELD_SPECS + "." + RealmString.VALUE, spec.getSpecName()).findAll();
                        count += itemsBySpec.size();
                        if (!itemsBySpec.isEmpty()) {
                            lambdaResult.append("*").append(spec.getSpecName()).append(CRLF);
                            lambdaResult.append(insertCate != null ? "*" + insertCate + CRLF : EMPTY);
                            lambdaResult.append(reinfOrGrdReplacement);
                            lambdaResult.append("검색 결과: ").append(itemsBySpec.size()).append("개")
                                    .append(CRLF).append(SEPARATOR);
                            for (Item item : itemsBySpec) {
                                lambdaResult.append("[").append(item.getItemSubCate()).append("]").append(BLANK)
                                        .append(item.getItemName()).append(BLANK);
                                if (reinfOrGRD.isEmpty())
                                    lambdaResult.append("(").append(STAR_FILLED).append(item.getItemGrade()).append(")");
                                lambdaResult.append(CRLF);
                            }
                            lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
                        }
                    }
                    if (count == 0)
                        return null;
                } else {
                    return null;
                }

            }

            return lambdaResult.toString();
        });

        // 병종 상성 검색
        HandleLocalDB relationByKey = (q -> {
            if (q == null) {
                return "병종을 입력하라옹!";
            }

            Log.d(TAG, "relationByKey Activated: " + q);

            ArrayList<String> branchList = new ArrayList<>();
            for (String probBranch : q.split(BLANK)) {
                if (!probBranch.isEmpty() && probBranch.length() > 1) {
                    RealmResults<Branch> branches = realm.where(Branch.class).equalTo(Branch.FIELD_NAME, probBranch)
                            .or().contains(Branch.FIELD_NAME2, probBranch).findAll();
                    branches = branches.isEmpty() ? realm.where(Branch.class)
                            .contains(Branch.FIELD_NAME, probBranch).findAll() : branches;
                    if (branches.isEmpty())
                        return "그런 병종 없다옹";
                    else {
                        Branch branch = branches.first();
                        if (branch != null) {
                            branchList.add(branch.getBranchName());
                        }
                    }
                }
            }

            StringBuilder lambdaResult = new StringBuilder();

            if (branchList.size() > 2)
                return "공격 / 피격 2개 병종만 입력하라옹";
            else if (branchList.size() == 2) {
                String branchName1 = branchList.get(0);
                String branchName2 = branchList.get(1);
                Relation attackRelation = realm.where(Relation.class)
                        .equalTo(Relation.FIELD_ATTACKER, branchName1).and()
                        .equalTo(Relation.FIELD_DEFENDER, branchName2).findFirst();
                Relation defenseRelation = realm.where(Relation.class)
                        .equalTo(Relation.FIELD_ATTACKER, branchName2).and()
                        .equalTo(Relation.FIELD_DEFENDER, branchName1).findFirst();

                if (attackRelation != null) {
                    lambdaResult.append(String.format("%-4s", attackRelation.getBranchAttacker()).replace(' ', '　'))
                            .append(BLANK).append(RIGHT_ARROW).append(BLANK)
                            .append(String.format("%-4s", attackRelation.getBranchDefender()).replace(' ', '　')).append(COLON).append(BLANK)
                            .append(attackRelation.getRelationValue()).append(CRLF);
                }

                if (defenseRelation != null) {
                    lambdaResult.append(String.format("%-4s", defenseRelation.getBranchAttacker()).replace(' ', '　'))
                            .append(BLANK).append(RIGHT_ARROW).append(BLANK)
                            .append(String.format("%-4s", defenseRelation.getBranchDefender()).replace(' ', '　')).append(COLON).append(BLANK)
                            .append(defenseRelation.getRelationValue()).append(CRLF);
                }

            } else if (branchList.size() == 1) {
                String branchName = branchList.get(0);
                RealmResults<Relation> attackRelations = realm.where(Relation.class)
                        .equalTo(Relation.FIELD_ATTACKER, branchName).findAll();
                RealmResults<Relation> defenseRelations = realm.where(Relation.class)
                        .equalTo(Relation.FIELD_DEFENDER, branchName).findAll();

                lambdaResult.append(branchName).append(CRLF).append("vs　　　 공격　　피격")
                        .append(CRLF).append(SEPARATOR);
                for (int i = 0; i < attackRelations.size(); i++) {
                    Relation attack = attackRelations.get(i);
                    Relation defense = defenseRelations.get(i);

                    if (attack != null && defense != null) {
                        if (attack.getBranchDefender().equals(defense.getBranchAttacker()))
                            lambdaResult.append(String.format("%-4s", attack.getBranchDefender())
                                    .replace(' ', '　')).append(BLANK)
                                    .append(attack.getRelationValue()).append("　　")
                                    .append(defense.getRelationValue()).append(CRLF);
                    }
                }

            } else {
                return "병종을 입력하라옹!";
            }

            return lambdaResult.toString();
        });

        // 보패 정보 검색
        HandleLocalDB relicByKey = (q -> {
            //각 보패냥, 단단한 보패냥, 근접피해감소 보패냥

            RealmResults<RelicSFX> sfxes = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_NAME).findAll();
            RealmResults<RelicPRFX> prfxes = realm.where(RelicPRFX.class).findAll();

            if (q == null) {
                StringBuilder descEmptyCommand = new StringBuilder("접두사 또는 접미사를 입력하게냥" + CRLF + "*접미사 : ");
                int i = 0;
                for (RelicSFX sfx : sfxes) {
                    descEmptyCommand.append((i++) % 7 == 0 ? CRLF : EMPTY).append(BLANK).append(sfx.getRelicSuffixName());
                }
                descEmptyCommand.append(CRLF + "*접두사 : ");
                i = 0;
                for (RelicPRFX prfx : prfxes) {
                    descEmptyCommand.append((i++) % 3 == 0 ? CRLF : EMPTY).append(BLANK).append(prfx.getRelicPrefixName());
                }
                return descEmptyCommand.toString();
            }

            Log.d(TAG, "relicByKey Activated: " + q);

            int statValue = NumberUtils.toInt(q.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY), 200);
            statValue = (statValue < 0 || statValue > 200) ? 200 : statValue;
            q = q.replaceAll(REGEX_DIGITS, EMPTY).replaceAll(REGEX_SPACE, EMPTY);

            ArrayList<Boolean> qIsSfxList = new ArrayList<>();

            for (char probSFX : q.toCharArray()) {
                boolean qIsSFX = false;
                for (RelicSFX sfx : sfxes) {
                    if (sfx.getRelicSuffixName().equals(probSFX + EMPTY)) {
                        qIsSFX = true;
                        //Log.d(TAG,probSFX+"is suffix");
                        break;
                    }
                }
                qIsSfxList.add(qIsSFX);
            }

            boolean qIsSFXes = true;
            for (boolean qIsSFX : qIsSfxList)
                qIsSFXes &= qIsSFX;

            StringBuilder lambdaResult = new StringBuilder();


            if (qIsSFXes) {

                // 각 보패냥   ,,   각 항 심 보패냥
                String[] initStat = {"공", "정", "방", "순", "사", "HP", "MP"};
                for (char probSFX : q.toCharArray()) {
                    lambdaResult.append("접미사 ").append(probSFX).append(" 스탯 정보").append(CRLF).append(SEPARATOR);
                    //lambdaResult.append("등급 공 정 방 순 사 HP MP").append(CRLF).append(SEPARATOR);
                    RealmResults<RelicSFX> sfxGrades = realm.where(RelicSFX.class).equalTo(RelicSFX.FIELD_NAME, probSFX + EMPTY).findAll();

                    for (RelicSFX sfxGrade : sfxGrades) {

                        lambdaResult.append("[").append(sfxGrade.getRelicSuffixGrade()).append("등급").append("] ");
                        // 공 정 방 순 사 HP MP
                        int i = 0;
                        try {
                            for (int power : sfxGrade.getRelicSuffixPowers()) {
                                lambdaResult.append(power != 0 ? initStat[i] + StringUtils.leftPad(power + BLANK, 3, '0') : EMPTY);
                                i++;
                                //lambdaResult.append(StringUtils.leftPad(stat+EMPTY,2,'0')).append(BLANK);
                            }
                        } catch (NullPointerException e) {
                            Log.e(TAG,Log.getStackTraceString(e));
                        }

                        lambdaResult.append(CRLF);
                    }


                    lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
                }
            } else {
                if (q.length() < 2) {
                    return "접두사 또는 효과를 2글자 이상 입력하게냥";
                }
                RealmResults<RelicPRFX> prfxNames = realm.where(RelicPRFX.class).contains(RelicPRFX.FIELD_NAME, q).findAll();

                if (!prfxNames.isEmpty()) {
                    for (RelicPRFX prfxName : prfxNames) {
                        lambdaResult.append(prfxName.getRelicPrefixName()).append(BLANK).append("(")
                                .append(prfxName.getRelicPrefixSpec()).append(")").append(CRLF)
                                .append(prfxName.getRelicPrefixStat()).append(BLANK).append(statValue);

                        RealmList<Double> prefixValues = prfxName.getRelicPrefixValue();
                        if (prefixValues != null) {
                            for (int i = 0; i < prefixValues.size(); i++) {
                                Double prefixValue = prefixValues.get(i);
                                if (prefixValue != null) {
                                    lambdaResult.append(CRLF).append("Lv.").append((i + 1)).append(COLON).append(BLANK).append(BLANK)
                                            .append((prefixValue * (double) statValue / 200.0));
                                }
                            }
                        }
                        lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
                    }
                } else {
                    RealmResults<Spec> specs = realm.where(Spec.class).contains(Spec.FIELD_NAME, "보패:").and().
                            beginGroup().contains(Spec.FIELD_NAME_NO_BLANK, q).or().contains(Spec.FIELD_NAME2, q).endGroup().findAll();
                    if (specs.isEmpty())
                        return null;
                    for (Spec spec : specs) {
                        RelicPRFX prfxName = realm.where(RelicPRFX.class).equalTo(RelicPRFX.FIELD_SPEC, spec.getSpecName().replace("보패: ", EMPTY)).findFirst();
                        if (prfxName != null) {
                            lambdaResult.append(prfxName.getRelicPrefixName()).append(BLANK).append("(")
                                    .append(prfxName.getRelicPrefixSpec()).append(")").append(CRLF)
                                    .append(prfxName.getRelicPrefixStat()).append(BLANK).append(statValue);

                            RealmList<Double> prefixValues = prfxName.getRelicPrefixValue();
                            if (prefixValues != null) {
                                for (int i = 0; i < prefixValues.size(); i++) {
                                    Double prefixValue = prefixValues.get(i);
                                    if (prefixValue != null) {
                                        lambdaResult.append(CRLF).append("Lv.").append((i + 1)).append(COLON).append(BLANK_DOUBLE)
                                                .append((prefixValue * (double) statValue / 200.0));
                                    }
                                }
                            }

                        }
                        lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
                    }
                }

            }

            return lambdaResult.toString();

        });

        // 보패 조합 검색
        HandleLocalDB relicCombByKey = (q -> {
            //각 조합냥

            RealmResults<RelicSFX> sfxes = realm.where(RelicSFX.class).distinct(RelicSFX.FIELD_NAME).findAll();

            if (q == null) {
                StringBuilder descEmptyCommand = new StringBuilder("접미사 조합 또는 조합효과를 입력하게냥" + CRLF + "*접미사 : ");
                int i = 0;
                for (RelicSFX sfx : sfxes) {
                    descEmptyCommand.append((i++) % 7 == 0 ? CRLF : EMPTY).append(BLANK).append(sfx.getRelicSuffixName());
                }
                RealmResults<RelicCombination> combs = realm.where(RelicCombination.class).distinct(RelicCombination.FIELD_SPEC).findAll();
                descEmptyCommand.append(CRLF).append("*조합효과 : ");
                i = 0;
                for (RelicCombination comb : combs) {
                    descEmptyCommand.append((i++) % 2 == 0 ? CRLF : EMPTY).append(BLANK).append(StringUtils.rightPad(comb.getRelicCombinationSpec(), 6, '　'));
                }
                return descEmptyCommand.toString();
            }

            Log.d(TAG, "relicCombByKey Activated: " + q);

            q = q.replaceAll(REGEX_DIGITS, EMPTY).replaceAll(REGEX_SPACE, EMPTY);
            ArrayList<Boolean> qIsSfxList = new ArrayList<>();

            for (char probSFX : q.toCharArray()) {
                boolean qIsSFX = false;
                for (RelicSFX sfx : sfxes) {
                    if (sfx.getRelicSuffixName().equals(probSFX + EMPTY)) {
                        qIsSFX = true;
                        break;
                    }
                }
                qIsSfxList.add(qIsSFX);
            }

            boolean qIsSFXes = true;
            for (boolean qIsSFX : qIsSfxList)
                qIsSFXes &= qIsSFX;

            StringBuilder lambdaResult = new StringBuilder();

            RealmResults<RelicCombination> combinations;
            if (qIsSFXes) {
                // 각 조합냥
                RealmQuery<RelicCombination> combQuery = realm.where(RelicCombination.class).alwaysTrue();

                for (char probSFX : q.toCharArray())
                    combQuery.and().equalTo(RelicCombination.FIELD_SFX + "." + RealmString.VALUE, probSFX + EMPTY);

                combinations = combQuery.findAll();


            } else {
                if (q.length() < 2)
                    return "조합 효과를 2글자 이상 입력하게냥";
                combinations = realm.where(RelicCombination.class).contains(RelicCombination.FIELD_SPEC, q).findAll();
            }

            if (combinations.isEmpty()) {
                return "그런 보패 조합 없다냥...";
            }

            lambdaResult.append("보패 조합 검색 결과: ").append(combinations.size()).append("개").append(CRLF)
                    .append(SEPARATOR);

            for (RelicCombination combination : combinations) {
                for (int i = 0; i < 4; i++)
                    lambdaResult.append(combination.getRelicSFXes().get(i));
                lambdaResult.append(BLANK).append("[").append(combination.getRelicCombinationGrade())
                        .append("등급").append("]").append(BLANK).append(combination.getRelicCombinationSpec()).append(CRLF);
            }


            return lambdaResult.toString();

        });

        // 책략 이름 검색
        HandleLocalDB searchMagicByName = (q -> {

            if (q == null) {
                return null;
            }

            Log.d(TAG, "searchMagicByName Activated");

            q = q.replaceAll(REGEX_SPACE, EMPTY);

            RealmResults<Magic> magics = realm.where(Magic.class).contains(Magic.FIELD_NAME, q).findAll();

            if (magics.isEmpty()) return null;

            StringBuilder lambdaResult = new StringBuilder();
            for (Magic magic : magics) {
                int tmpEP = magic.getMagicEP();
                int tmpMP = magic.getMagicMP();
                String tmpHealType = magic.getMagicHealType();
                String tmpDamageType = magic.getMagicDamageType();
                String tmpAccuType = magic.getMagicAccuType();
                int tmpStreak = magic.getMagicCanStreakCast();
                int tmpObstructive = magic.getMagicObstructiveSkill();
                lambdaResult.append("[").append(magic.getMagicSkillType()).append("]책략 ").append(magic.getMagicName()).append(CRLF)
                        .append(tmpEP == 0 ? tmpMP == 0 ? "*소모 자원 없음" : "*소모MP: " + magic.getMagicMP() : "*소모EP: " + tmpEP)
                        .append(CRLF + "*책략 계수: ").append(magic.getMagicSkillPower())
                        .append(CRLF + "최대 명중률: ").append(magic.getMagicAccu())
                        .append(CRLF + "*효과 범위: ").append(magic.getMagicEffectArea())
                        .append(CRLF + "*시전 범위: ").append(magic.getMagicTargetArea())
                        .append(tmpAccuType.equals("Normal") ? EMPTY : CRLF + "*적중 타입: " + tmpAccuType)
                        .append(tmpDamageType.equals("None") ? EMPTY : CRLF + "*데미지 타입: " + tmpDamageType)
                        .append(tmpHealType.equals(DASH) ? EMPTY : CRLF + "*치료 타입: " + tmpHealType)
                        .append(tmpStreak == 0 ? EMPTY : CRLF + "*연속 발동 가능")
                        .append(tmpObstructive == 0 ? EMPTY : CRLF + "*방해계 책략")
                        .append(CRLF).append(magic.getMagicDesc()).append(FangConstant.CONSTRAINT_SEPARATOR);
            }
            return lambdaResult.toString();
        });

        HandleLocalDB searchMagicByType = (q -> {

            if (q == null) {
                return null;
            }

            Log.d(TAG, "searchMagicByType Activated");
            q = q.replaceAll(REGEX_SPACE, EMPTY);

            StringBuilder lambdaResult = new StringBuilder();
            RealmResults<Magic> magicTypes = realm.where(Magic.class).distinct(Magic.FIELD_TYPE).findAll();

            int count = 0;
            for (Magic type : magicTypes) {


                if (type.getMagicSkillType().contains(q)) {

                    Log.d(TAG, type.getMagicSkillType());
                    RealmResults<Magic> magics = realm.where(Magic.class).equalTo(Magic.FIELD_TYPE, type.getMagicSkillType()).findAll();
                    lambdaResult.append("*[").append(type.getMagicSkillType()).append("]속성 책략").append(CRLF);
                    lambdaResult.append("검색 결과: ").append(magics.size()).append("개")
                            .append(CRLF).append(SEPARATOR);
                    for (Magic magic : magics) {
                        lambdaResult.append(magic.getMagicName()).append(CRLF);
                        count++;
                    }
                    lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
                }
            }


            return count == 0 ? null : lambdaResult.toString();
        });


        HandleLocalDB searchMagicByBranch = (q -> {

            if (q == null) {
                return null;
            }

            Log.d(TAG, "searchMagicByBranch Activated");
            q = q.replaceAll(REGEX_SPACE, EMPTY);

            Branch branch = findBranchByName(q, realm);
            if (branch == null) {
                return null;
            }

            StringBuilder lambdaResult = new StringBuilder();

            lambdaResult.append("*").append(branch.getBranchName()).append("계 책략").append(CRLF).append(SEPARATOR);


            if (branch.getBranchMagic().equals(DASH)) return "책략 없음";

            for (String each : branch.getBranchMagic().split(COLON)) {
                Magic magic = realm.where(Magic.class).equalTo(Magic.FIELD_NAME, each).findFirst();
                if (magic != null) {
                    lambdaResult.append("[").append(magic.getMagicSkillType()).append("]").append(BLANK).append(magic.getMagicName()).append(CRLF);
                }
            } // end for

            return lambdaResult.toString();


        });


        HandleLocalDB getAgenda = q -> {

            if (q == null) {
                return null;
            }

            Log.d(TAG, "getAgenda Activated");

            StringBuilder lambdaResult = new StringBuilder();
            String division;
            if (q.contains("섬멸")) {
                q = q.replace("섬멸전", EMPTY);
                q = q.replace("섬멸", EMPTY);
                division = "섬멸";
            } else if (q.contains("경쟁")) {
                q = q.replace("경쟁전", EMPTY);
                q = q.replace("경쟁", EMPTY);
                division = "경쟁";
            } else {
                return null;
            }


            String dateInfo = q.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY);
            Date date = new Date();
            SimpleDateFormat df_ymd = new SimpleDateFormat("yyMMdd", Locale.KOREA);
            try {
                if (dateInfo.length() == 6) {
                    q = q.replace(dateInfo, EMPTY);
                    date = df_ymd.parse(dateInfo);
                } else if (dateInfo.length() == 4) {
                    q = q.replace(dateInfo, EMPTY);
                    dateInfo = (new SimpleDateFormat("yy", Locale.KOREA).format(date)) + dateInfo;
                    date = df_ymd.parse(dateInfo);
                }

            } catch (ParseException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            Date date_before = new Date();
            Date date_after = new Date();
            if (date != null) {
                date_before.setTime(date.getTime() - 21 * 24 * 60 * 60 * 1000);
                date_after.setTime(date.getTime() + 22 * 24 * 60 * 60 * 1000);
            }

            int dateBeforeInt = Integer.parseInt(df_ymd.format(date_before));
            int dateAfterInt = Integer.parseInt(df_ymd.format(date_after));
            String map = q.trim();


            if (date != null) {
                lambdaResult.append(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(date)).
                        append(BLANK).append(division).append("일정").append(CRLF).append(SEPARATOR);
            }

            RealmResults<Agenda> agendaRealmResults;
            if (map.equals(EMPTY)) {
                agendaRealmResults = realm.where(Agenda.class).between(Agenda.FIELD_START, dateBeforeInt, dateAfterInt).and()
                        .equalTo(Agenda.FIELD_DIV, division).findAll();
            } else {
                agendaRealmResults = realm.where(Agenda.class).and().contains(Agenda.FIELD_MAP, map)
                        .equalTo(Agenda.FIELD_DIV, division).findAll();
            }


            if (agendaRealmResults.isEmpty()) {
                return "[날짜4~6자리]or[맵] [경쟁or섬멸] 일정냥 <<< 이렇게 입력하게냥";
            }

            SimpleDateFormat df_md = new SimpleDateFormat("MM.dd", Locale.KOREA);

            for (Agenda agenda : agendaRealmResults) {

                try {
                    Date start_date = df_ymd.parse(agenda.getAgendaStart() + EMPTY);
                    Date end_date = new Date();
                    if (start_date != null) {
                        end_date.setTime(start_date.getTime() + 6 * 24 * 60 * 60 * 1000);
                        if (date != null) {
                            lambdaResult.append(df_md.format(start_date)).append("~").append(df_md.format(end_date)).append(BLANK).append(agenda.getAgendaMap())
                                    .append(((start_date.getTime() <= date.getTime())
                                            && (end_date.getTime() + 24 * 60 * 60 * 1000 > date.getTime())) ? " V" : EMPTY)
                                    .append(CRLF);
                        }
                    }

                } catch (ParseException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }
            }


            return lambdaResult.toString();
        };

        HandleLocalDB getCustomAgenda = q -> {

            if (catRoom == null) {
                return "단톡방에서만 사용가능한 기능입니다.";
            }

            StringBuilder lambdaResult = new StringBuilder();
            if (q == null) {

                RealmResults<CustomAgenda> allCustomAgenda = realm.where(CustomAgenda.class).equalTo(CustomAgenda.FIELD_ROOM, catRoom).findAll();
                lambdaResult.append(catRoom).append(CRLF).append("등록된 모든 일정들").append(CRLF).append(SEPARATOR);
                for (CustomAgenda customAgenda : allCustomAgenda) {
                    lambdaResult.append(customAgenda.getTitle()).append(CRLF);
                }

                lambdaResult.append(SEPARATOR).append("제목 + 일정냥 으로 조회가능");
                return lambdaResult.toString();
            }


            String dateInfo = q.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY);
            Date date = new Date();
            date.setTime(date.getTime());
            SimpleDateFormat df_ymd = new SimpleDateFormat("yyMMdd", Locale.KOREA);
            try {
                if (dateInfo.length() == 6) {
                    q = q.replace(dateInfo, EMPTY);
                    date = df_ymd.parse(dateInfo);
                } else if (dateInfo.length() == 4) {
                    q = q.replace(dateInfo, EMPTY);
                    dateInfo = (new SimpleDateFormat("yy", Locale.KOREA).format(date)) + dateInfo;
                    date = df_ymd.parse(dateInfo);
                }

            } catch (ParseException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            RealmResults<CustomAgenda> customAgendaRealmResults =
                    realm.where(CustomAgenda.class).equalTo(CustomAgenda.FIELD_ROOM, catRoom).and().contains(CustomAgenda.FIELD_TITLE, q).findAll();

            for (CustomAgenda customAgenda : customAgendaRealmResults) {
                RealmList<RealmString> guardians = customAgenda.getGuardians();
                String remarks = customAgenda.getRemarks();
                lambdaResult.append(customAgenda.getTitle()).append(BLANK).append(remarks == null ? EMPTY :
                        "- " + remarks).append(CRLF);
                Date curDate = new Date();
                if (date != null) {
                    curDate.setTime(date.getTime());
                }
                try {
                    Date startDate = df_ymd.parse(customAgenda.getAgendaStart() + EMPTY);

                    String startDateStr = startDate == null ? EMPTY :
                            new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).
                                    format(startDate
                                    );
                    switch (customAgenda.getInterval()) {
                        case 7:

                            Calendar calendar = Calendar.getInstance();
                            if (startDate != null) {
                                calendar.setTime(startDate);
                            }

                            int repeatWeekNum = calendar.get(Calendar.DAY_OF_WEEK);
                            lambdaResult.append("시작 날짜: ").append(startDateStr
                            ).append(CRLF)
                                    .append("매주 ").append(weekNames[repeatWeekNum - 1]).append("요일 반복").append(CRLF)
                                    .append(SEPARATOR);

                            calendar.setTime(curDate);
                            int currentWeekNum = calendar.get(Calendar.DAY_OF_WEEK);

                            int correctionValue = 0;
                            if (currentWeekNum < repeatWeekNum) {
                                correctionValue = repeatWeekNum - currentWeekNum;
                            } else if (currentWeekNum > repeatWeekNum) {
                                correctionValue = 7 + repeatWeekNum - currentWeekNum;
                            }

                            Log.d(TAG, "cor: " + correctionValue);

                            if (correctionValue != 0) {
                                curDate.setTime(curDate.getTime() + correctionValue * 24 * 60 * 60 * 1000);
                            }

                            int diffWeek = 0;
                            if (startDate != null) {
                                diffWeek = (int) ((curDate.getTime() - startDate.getTime()) / (7 * 24 * 60 * 60 * 1000));
                            }
                            lambdaResult.append(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(curDate))
                                    .append(BLANK).append(guardians.size() > 0 ? guardians.get(diffWeek % guardians.size()) + BLANK : EMPTY)
                                    .append(LEFT_ARROW);


                            for (int i = 0; i < 6; i++) {
                                curDate.setTime(curDate.getTime() + 7 * 24 * 60 * 60 * 1000);
                                diffWeek++;
                                lambdaResult.append(CRLF).append(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(curDate))
                                        .append(BLANK).append(guardians.size() > 0 ? guardians.get(diffWeek % guardians.size()) + BLANK : EMPTY);
                            }


                            break;
                        case 1:
                            lambdaResult.append("시작날짜: ").append(startDateStr).append(CRLF)
                                    .append("매일 반복").append(CRLF).append(SEPARATOR);

                            int diffDate = 0;
                            if (startDate != null) {
                                diffDate = (int) ((curDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
                            }
                            lambdaResult.append(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(curDate))
                                    .append(BLANK).append(guardians.size() > 0 ? guardians.get(diffDate % guardians.size()) + BLANK : EMPTY)
                                    .append(LEFT_ARROW).append("오늘");

                            for (int i = 0; i < 6; i++) {
                                curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
                                diffDate++;
                                lambdaResult.append(CRLF).append(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(curDate))
                                        .append(BLANK).append(guardians.size() > 0 ? guardians.get(diffDate % guardians.size()) + BLANK : EMPTY);
                            }

                            break;
                        default:
                            lambdaResult.append("날짜: ").append(startDateStr);

                    }

                } catch (ParseException | ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }

                lambdaResult.append(FangConstant.CONSTRAINT_SEPARATOR);
            }


            return lambdaResult.toString();
        };

        // 지낭 각성사신 매주 수요일 등록냥
        HandleLocalDB createAgenda = q -> {
            if (catRoom == null) {
                return "단톡방에서만 사용가능한 기능입니다.";
            }

            if (q == null) {
                return "\"제목 - 내용:반복 + [매주 ?요일 or 매일] 등록냥\"\r\neg. 패기 각성:청룡 청룡 주작 매주 수요일 등록냥 ";
            }

            String dateInfo = q.replaceAll(REGEX_EXCEPT_DIGITS, EMPTY);
            SimpleDateFormat df_ymd = new SimpleDateFormat("yyMMdd", Locale.KOREA);
            //SimpleDateFormat df_md = new SimpleDateFormat("MMdd", Locale.KOREA);

            Date date = new Date();

            try {
                if (dateInfo.length() == 6) {
                    q = q.replace(dateInfo, EMPTY);
                    date = df_ymd.parse(dateInfo);
                } else if (dateInfo.length() == 4) {
                    q = q.replace(dateInfo, EMPTY);
                    dateInfo = (new SimpleDateFormat("yy", Locale.KOREA).format(date)) + dateInfo;
                    date = df_ymd.parse(dateInfo);
                }

            } catch (ParseException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }

            if (date == null) {
                return null;
            }

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(date);

            int interval = 0;

            int currentWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int repeatWeek = -1;

            if (q.contains("매일")) {
                interval = 1;
                q = q.replace("매일", EMPTY);
            } else if (q.contains("매주")) {
                interval = 7;
                q = q.replace("매주", EMPTY);
                if (q.contains("요일")) {
                    int weekIndex = q.indexOf("요일");
                    String probWeekName = q.substring(weekIndex - 1, weekIndex);
                    Log.d(TAG, "weekIndex:" + weekIndex + ", probWeekName:" + probWeekName);
                    for (int i = 0; i < 7; i++) {
                        if (probWeekName.equals(weekNames[i])) {
                            repeatWeek = i + 1;
                        }
                    }

                    // 일 월 화 수 목 금 토
                    // 1 2 3 4 5 6 7

                    q = q.replace(probWeekName + "요일", BLANK);

                    Log.d(TAG, "rep : " + repeatWeek + ", cur : " + currentWeek);
                    if (repeatWeek > -1 && repeatWeek != currentWeek) {
                        // cur : 2  repeat : 4
                        // cur : 3  rep : 1 => cor : 5
                        int correctionValue;
                        if (currentWeek < repeatWeek) {
                            correctionValue = repeatWeek - currentWeek;
                        } else {
                            correctionValue = 7 + repeatWeek - currentWeek;
                        }

                        Log.d(TAG, "cor: " + correctionValue);

                        if (correctionValue != 0) {
                            date.setTime(date.getTime() + correctionValue * 24 * 60 * 60 * 1000);
                        }

                    }
                }
            }


            int agendaStart = NumberUtils.toInt(df_ymd.format(date));

            String title = q;
            String remarks = null;
            RealmList<RealmString> guardians = new RealmList<>();
            try {
                if (q.contains(COLON)) {
                    String[] contents = q.split(COLON);
                    String guardiansStr = contents[1].trim();
                    for (String guardianStr : guardiansStr.split(BLANK)) {
                        guardians.add(new RealmString(guardianStr));
                    }
                    q = contents[0];
                    title = q;
                }

                if (q.contains(DASH)) {
                    String[] contents = q.split(DASH);
                    title = contents[0];
                    remarks = contents[1];
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG,Log.getStackTraceString(e));
                return "입력 오류발생";
            }


            try {
                CustomAgenda customAgenda = new CustomAgenda(
                        agendaStart,
                        title,
                        remarks,
                        catRoom,
                        guardians,
                        interval
                );

                realm.beginTransaction();
                realm.copyToRealm(customAgenda);
                realm.commitTransaction();


                Log.d(TAG, "new agenda:" + customAgenda.getCustomID());
            } catch (RealmPrimaryKeyConstraintException e) {
                Log.e(TAG,Log.getStackTraceString(e));
                return "오류발생: 다시 시도하라옹";
            }


            return "일정 등록 성공";
        };

        HandleLocalDB deleteAgenda = q -> {
            if (catRoom == null) {
                return "단톡방에서만 사용가능한 기능입니다.";
            }

            StringBuilder lambdaResult = new StringBuilder();

            if (q == null) {
                RealmResults<CustomAgenda> customAgendaRealmResults = realm.where(CustomAgenda.class).equalTo(CustomAgenda.FIELD_ROOM, catRoom).findAll();
                if (customAgendaRealmResults.size() == 0) {
                    return "등록된 일정이 없다옹";
                }

                lambdaResult.append(catRoom).append(CRLF).append("일정 - 내용 [일정ID]").append(CRLF).append(SEPARATOR);
                for (CustomAgenda customAgenda : customAgendaRealmResults) {
                    String title = customAgenda.getTitle();
                    String remarks = customAgenda.getRemarks();
                    int id = customAgenda.getCustomID();
                    lambdaResult.append(BLANK).append(title).append(BLANK).append(remarks == null ? EMPTY : "- " + remarks + BLANK).append("[").append(id).append("]").append(CRLF);
                }
                lambdaResult.append(SEPARATOR).append("\"[ID] 삭제냥\" 으로 삭제하라옹\r\neg. \"1234567 삭제냥\"");
                return lambdaResult.toString();
            }

            CustomAgenda customAgenda = realm.where(CustomAgenda.class).equalTo(CustomAgenda.FIELD_ID, NumberUtils.toInt(q)).findFirst();
            if (customAgenda == null) {
                return "일정 ID 입력 오류 :  \"삭제냥\"을 입력하여 ID를 확인하라옹";
            }

            String title = customAgenda.getTitle();
            String remarks = customAgenda.getRemarks();
            int interval = customAgenda.getInterval();

            lambdaResult.append(title).append(remarks == null ? EMPTY : " - " + remarks).append(interval == 0 ? EMPTY :
                    "(" + interval + "일 마다 반복)").append(" 일정 삭제 완료");
            realm.beginTransaction();
            customAgenda.deleteFromRealm();
            realm.commitTransaction();


            return lambdaResult.toString();
        };

        HandleLocalDB calc = q -> {
            if (q == null) {
                return "[수식] 계산냥 << 이렇게 입력하라옹";
            }

            try {
                org.mozilla.javascript.Context mContext = org.mozilla.javascript.Context.enter();
                mContext.setOptimizationLevel(-1);
                Scriptable scope = mContext.initSafeStandardObjects();
                Object resultObject = mContext.evaluateString(scope, q, "<cmd>", 1, null);
                if (resultObject != null) {
                    return resultObject.toString();
                } else {
                    return "계산 결과: null";
                }
            } catch (Exception e) {
                Log.e(TAG,Log.getStackTraceString(e));
                return "계산 오류 발생";
            }
        };

        String result;

        switch (certainCMD) {
            case COMMAND_TER:
            case COMMAND_MOV:
                result = terrainInfoByKey.handle(req);
                result = (result == null) ? "잘못된 입력이라옹" : result;
                break;
            case COMMAND_DESC:
            case COMMAND_DESC_CAT:
                result = descByBranch.handle(req);
                result = (result == null) ? descBySpec.handle(req) : result;
                result = (result == null) ? descByItem.handle(req) : result;
                result = (result == null) ? "?ㅅ?" : result;
                break;
            case COMMAND_DEST:
                result = destinyByKey.handle(req);
                result = (result == null) ? "그런 인연 없다냥..." : result;
                break;
            case COMMAND_ITEM:
                result = itemByKey.handle(req);
                result = (result == null) ? "그런 보물 없다냥..." : result;
                break;
            case COMMAND_RELATION:
                result = relationByKey.handle(req);
                break;
            case COMMAND_RELIC:
                result = relicByKey.handle(req);
                result = (result == null) ? relicCombByKey.handle(req) : result;
                result = (result == null) ? "보패정보 -> 보패조합 검색결과 : 없음" : result;
                break;
            case COMMAND_COMB:
                result = relicCombByKey.handle(req);
                break;
            case COMMAND_DOT:
                result = dotByPoints.handle(req);
                break;
            case COMMAND_AGENDA:
                result = getAgenda.handle(req);
                result = (result == null) ? getCustomAgenda.handle(req) : result;
                break;
            case COMMAND_REG_AGENDA:
                result = createAgenda.handle(req);
                break;
            case COMMAND_DEL_AGENDA:
                result = deleteAgenda.handle(req);
                break;
            case COMMAND_CALC:
                result = calc.handle(req);
                break;
            case COMMAND_MAGIC:
                result = searchMagicByName.handle(req);
                result = (result == null) ? searchMagicByBranch.handle(req) : result;
                result = (result == null) ? searchMagicByType.handle(req) : result;
                result = (result == null) ? "책략 이름 또는 속성을 입력하라옹" : result;
                break;
            default:
                result = heroesByName.handle(req);
                result = (result == null) ? heroesBySpec.handle(req) : result;
                result = (result == null) ? itemByKey.handle(req) : result;
                result = (result == null) ? searchMagicByName.handle(req) : result;
                result = (result == null) ? descBySpec.handle(req) : result;
                result = (result == null) ? destinyByKey.handle(req) : result;
                result = (result == null) ? "장수->특성->보물->책략->설명->인연 검색결과: 없음" : result;
        }


        return result;

    }

    private interface HandleLocalDB {
        String handle(String req);
    }

    private Branch findBranchByName(String proBranch, Realm realm) {
        if (proBranch.isEmpty() || proBranch.length() < 2) return null;
        Branch branch = null;
        RealmResults<Branch> branches = realm.where(Branch.class).contains(Branch.FIELD_NAME, proBranch)
                .or().contains(Branch.FIELD_NAME2, proBranch).findAll();
        if (!branches.isEmpty()) {
            branch = branches.size() > 1 ? branches.where().equalTo(Branch.FIELD_NAME, proBranch)
                    .or().contains(Branch.FIELD_NAME2, proBranch).findFirst() : branches.first();
        }
        return branch;
    }

    private String findLineageName(String probLineage, Realm realm) {
        String lineageName = null;
        if (probLineage.length() > 3) {
            Heroes lineage = realm.where(Heroes.class).
                    contains(Heroes.FIELD_LINEAGE, probLineage).findFirst();
            if (lineage != null)
                lineageName = lineage.getHeroLineage();
        }
        return lineageName;
    }

    private String drawRangeView(String... points) {

        boolean[][] sparseMatrix = new boolean[13][13];

        for (String point : points) {
            String[] value = point.split(BLANK);
            int row = NumberUtils.toInt(value[0]);
            int col = NumberUtils.toInt(value[1]);
            sparseMatrix[row][col] = true;
        }

        StringBuilder drawResult = new StringBuilder();
        for (boolean[] row : sparseMatrix) {
            for (boolean bool : row)
                drawResult.append(bool ? RANGE_FULL : RANGE_EMPTY);
            drawResult.append(CRLF);
        }

        return drawResult.toString();
    }



            /*
            HandleLocalDB createOrGetMemo = q -> {
                if(catRoom == null) {
                    return "단톡방에서만 사용가능한 기능입니다.";
                }

                StringBuilder lambdaResult = new StringBuilder();

                RealmResults<Memo> memos = realm.where(Memo.class).equalTo(Memo.FIELD_ROOM,catRoom).and().equalTo(Memo.FIELD_NAME,sendCat).findAll();
                if(q.isEmpty()) {
                    if(memos.isEmpty()) {
                        return "[" + catRoom + "]" + sendCat + "님의 메모가 존재하지 않습니다.";
                    }
                    lambdaResult.append("[").append(catRoom).append("]").append(CRLF).append(sendCat).append("님의 메모").append(CRLF)
                            .append("메모삭제는 [메모 번호] + 메모 삭제 냥").append(COMMA).append(CRLF);
                    for(Memo memo : memos ) {
                        lambdaResult.append("작성일자 :").append(memo.getMemoTimestamp()).append(CRLF)
                                .append("메모 번호: ").append(memo.getMemoID()).append(CRLF).append(SEPARATOR)
                                .append(memo.getMemoText()).append(COMMA).append(CRLF);
                    }
                } else {

                    Memo memo = new Memo();

                    try {
                        Number number = realm.where(Memo.class).max(Memo.FIELD_ID);
                        if (number != null) {
                            number = number.intValue() + 1;
                        } else {
                            number = 0;
                        }
                        memo.setMemoID(number.intValue());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return "메모 추가 실패: Starfang에게 문의 하라옹";
                    }

                    memo.setMemoTimestamp(new SimpleDateFormat("yyMMdd HH:mm:ss",Locale.KOREA).format(new Date()));
                    memo.setMemoName(sendCat);
                    memo.setMemoRoom(catRoom);
                    memo.setMemoText(q);
                    realm.beginTransaction();
                    realm.copyToRealm(memo);
                    realm.commitTransaction();

                    lambdaResult.append(memo.getMemoTimestamp()).append(BLANK).append("메모 추가 성공!" +
                            "\r\n*확인 방법: 메모 냥" +
                            "\r\n\n*삭제 방법: [메모 번호] 메모 삭제 냥(본인만 가능)" +
                            "\r\n\r\n!메모는 냥봇 상황에 따라 언제든 초기화 될수 있으니 주의 바랍니다.");
                }


                return lambdaResult.toString();
            };

            HandleLocalDB deleteMemo = q -> {
                if(catRoom == null) {
                    return "삭제 실패: 단톡방에서만 사용가능한 기능입니다.";
                }

                int id = NumberUtils.toInt(q.replaceAll(REGEX_EXCEPT_DIGITS,EMPTY),-1);
                if( id < 0 ) {
                    return "삭제 실패: 삭제할 문서 번호를 입력 하세요!";
                }
                RealmResults<Memo> memos = realm.where(Memo.class).equalTo(Memo.FIELD_ID,id).equalTo(Memo.FIELD_ROOM,catRoom).and().equalTo(Memo.FIELD_NAME,sendCat).findAll();
                if(memos.isEmpty()) {
                    return "삭제 실패: [" + catRoom + "]" + sendCat + "님의 메모 중에 " + id + "번 메모가 존재하지 않습니다.";
                } else {
                    StringBuilder lambdaResult = new StringBuilder();
                    lambdaResult.append("메모 삭제 성공!").append(CRLF);
                    for( Memo memo : memos ) {
                        lambdaResult.append(memo.getMemoText()).append(CRLF);
                    }
                    realm.beginTransaction();
                    memos.deleteAllFromRealm();
                    realm.commitTransaction();
                    return lambdaResult.toString();

                }

            };

             */
}
