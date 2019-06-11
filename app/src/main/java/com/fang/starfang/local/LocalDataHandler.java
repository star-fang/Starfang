package com.fang.starfang.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.R;
import com.fang.starfang.model.realm.Branch;
import com.fang.starfang.model.realm.Destiny;
import com.fang.starfang.model.realm.Dot;
import com.fang.starfang.model.realm.Heroes;
import com.fang.starfang.model.realm.Item;
import com.fang.starfang.model.realm.ItemCate;
import com.fang.starfang.model.realm.MagicItemCombination;
import com.fang.starfang.model.realm.MagicItemPRFX;
import com.fang.starfang.model.realm.MagicItemSFX;
import com.fang.starfang.model.realm.Relation;
import com.fang.starfang.model.realm.Spec;
import com.fang.starfang.model.realm.TVpair;
import com.fang.starfang.model.realm.TermSynergy;
import com.fang.starfang.model.realm.Terrain;
import com.fang.starfang.model.realm.primitive.RealmString;
import com.fang.starfang.network.task.LambdaFunctionHandler;
import com.fang.starfang.util.KakaoReplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;



// 4/23 todo list
/*
1. 퇴치임무 로컬화, 공백시 안내 멘트
2. 병종, 효과 설명 동시에 입력시 처리
3. 섬멸, 경쟁 일정 로컬화
 */
public class LocalDataHandler extends AsyncTask<String, Integer, String> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String sendCat;
    private StatusBarNotification sbn;

    private static final String TAG = "LOCAL_HANDLER";
    private static final String COMMAND_BOT = "냥";
    private enum COMMAND_CERTAIN_ENUM {
        COMMAND_DEST,COMMAND_TER,COMMAND_MOV,COMMAND_DESC,
        COMMAND_SYN,COMMAND_ITEM,COMMAND_AGENDA,COMMAND_MAGIC,
        COMMAND_RELATION,COMMAND_EXTERMINATE,COMMAND_DOT,COMMAND_COMB,COMMAND_DEFAULT}
    private static final String[] COMMAND_CERTAIN = {
            "인연","지형","소모","설명",
            "시너지","보물","일정","보패",
            "상성","퇴치","도트","조합"};
    private static final String[] PRFX_COMMAND = {"","","이동력","","몽매","","","","병종","","","보패"};
    private static final String[] SFX_COMMAND = {"","상성","","","","","","","","","",""};
    private static final String CRLF = "\r\n";
    private static final String BLANK = " ";
    private static final String EMPTY = "";
    private static final String DASH = "-";
    private static final String COMMA = ",";
    private static final String SEPARATOR = "-------------------------------\n";
    private static final String RANGE_EMPTY = "□";
    private static final String RANGE_FULL = "■";

    public LocalDataHandler(Context c, String sender, StatusBarNotification _sbn ) {
        context = c;
        sendCat = sender;
        sbn = _sbn;
    }


    @Override
    protected String doInBackground(String... strings) {
        try(Realm realm = Realm.getDefaultInstance()) {
            if( !handleRequest( strings[0], realm) )
                new LambdaFunctionHandler(context, sendCat, sbn).execute(strings[0]);
        }
        return null;
    }


    private boolean handleRequest( String req, Realm realm ) {

        if (req.substring(req.length() - 1, req.length()).equals(COMMAND_BOT)) {
            req = req.substring(0, req.length() - 1).trim();

            COMMAND_CERTAIN_ENUM certainCMD = COMMAND_CERTAIN_ENUM.COMMAND_DEFAULT;
            for( COMMAND_CERTAIN_ENUM certain : COMMAND_CERTAIN_ENUM.values() ) {
                try {
                    String probKey = COMMAND_CERTAIN[certain.ordinal()];
                    String suffix = SFX_COMMAND[certain.ordinal()];
                    String prefix = PRFX_COMMAND[certain.ordinal()];

                    String reqWithoutSFX = req;
                    try {
                        if (!suffix.isEmpty())
                            reqWithoutSFX = (req.substring(req.length() - suffix.length(), req.length()).equals(suffix)) ?
                                    req.substring(0, req.length() - suffix.length() ): req;
                    } catch( StringIndexOutOfBoundsException  ignore ) { }

                    try {
                        if (reqWithoutSFX.substring(reqWithoutSFX.length() - probKey.length(), reqWithoutSFX.length()).equals(probKey)) {
                            certainCMD = certain;
                            req = req.replace(prefix, EMPTY);
                            req = req.replace(suffix, EMPTY).trim();
                            req = req.substring(0, req.length() - probKey.length()).trim();
                            break;
                        }
                    } catch( StringIndexOutOfBoundsException  ignore ) { }

                } catch (ArrayIndexOutOfBoundsException ignore) { }
            }


            SearchLocalDB dotByPoints = ( q-> {

                StringBuilder emptyResult = new StringBuilder();
                emptyResult.append("도트 목록").append(CRLF).append(SEPARATOR);
                RealmResults<Dot> dots = realm.where(Dot.class).findAll();
                for(Dot dot: dots)
                    emptyResult.append(dot.getDotName()).append(CRLF);
                if(q.replace(BLANK,EMPTY).isEmpty())
                    return emptyResult.toString();


                Dot dot = realm.where(Dot.class).equalTo(Dot.FIELD_NAME,q.trim()).findFirst();

                StringBuilder lambdaResult = new StringBuilder();
                try {
                    lambdaResult.append(drawRangeView(13, 13, dot.getDotPoints().split(COMMA)));
                } catch( NullPointerException e) {
                    return emptyResult.toString();
                }

                return lambdaResult.toString();
            });

            // 장수 이름으로 정보 검색 : 조조 냥
            SearchLocalDB heroesByName = ( q -> {

                if(q.replace(BLANK,EMPTY).isEmpty())
                    return null;

                LinkedList<String> rQueue = new LinkedList<>();
                rQueue.addAll(Arrays.asList(q.split(BLANK)));

                if( rQueue.isEmpty() ) return null;

                int insert_hero_num = rQueue.size();
                int searched_hero_count = 0;
                int total_cost = 0;

                HashMap<String, Integer> map_destiny = new HashMap<>();
                StringBuilder lambdaResult = new StringBuilder();

                lambdaResult.append(
                        (insert_hero_num>1)?
                                SEPARATOR + "병종계열 장수이름 COST\r\n" + SEPARATOR: EMPTY);

                while( !rQueue.isEmpty() ) {
                    String nameToSearch = rQueue.remove();
                    if(!nameToSearch.isEmpty()) {

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
                                .append(insert_hero_num == 1 ? CRLF + Heroes.INIT_COST + ": "
                                        + init_cost + "→"
                                        + (init_cost + 3) + "→"
                                        + (init_cost + 5) + "→"
                                        + (init_cost + 8) + "→"
                                        : BLANK).append(init_cost + 10)
                                .append(CRLF);

                        String destinies = tmpHero.getHeroDestiny();
                        if (destinies != null) {
                            for(String destiny : destinies.split(":"))
                            map_destiny.put(destiny, map_destiny.containsKey(destiny) ?
                                    (map_destiny.get(destiny) + 1) : 1);
                        }

                        if (insert_hero_num == 1) {
                            lambdaResult.append(Heroes.INIT_LINEAGE).append(": ")
                                    .append(tmpHero.getHeroLineage()).append(CRLF);

                            lambdaResult.append(destinies != null ?
                                    Heroes.INIT_DYNASTY + ": " + destinies + CRLF : EMPTY);

                            for (int i = 0; i < Heroes.INIT_STATS.length; i++)
                                lambdaResult.append(Heroes.INIT_STATS[i])
                                        .append(tmpHero.getHeroStats().get(i)).append(BLANK);
                            lambdaResult.append(CRLF);

                            lambdaResult.append("교본작: 최대 +").append((init_cost+16)*5).append(CRLF);

                            for (int i = 0; i < Heroes.INIT_SPECS.length; i++) {
                                String val = null;
                                try {
                                    val = (i < 4) ? tmpHero.getHeroSpecValues().get(i).toString() : EMPTY;
                                } catch (ArrayIndexOutOfBoundsException ignore) {

                                }
                                lambdaResult.append(Heroes.INIT_SPECS[i]).append(": ")
                                        .append(tmpHero.getHeroSpecs().get(i)).append(BLANK)
                                        .append(val != null ? val : EMPTY).append(CRLF);

                            }

                            lambdaResult.append(COMMA);
                        }
                    }

                    lambdaResult.append((rQueue.isEmpty() || insert_hero_num > 1) ? EMPTY : ",\r\n");
                } else {
                        insert_hero_num--;
                    }
        }


        if( insert_hero_num == searched_hero_count ) {
            if (insert_hero_num > 1)
                lambdaResult.append(SEPARATOR).append("TOTAL COST: [  ").append(total_cost).append("  ]").append(CRLF);

            if (insert_hero_num > 2 && insert_hero_num < 6 && total_cost != 99)
                lambdaResult.append("섬멸전:  ").append(99).append(" - ").append(total_cost).append(" = ").append(99 - total_cost).append(CRLF);

            if (insert_hero_num > 4 && insert_hero_num < 8 && total_cost != 145)
                lambdaResult.append("경쟁전: ").append(145).append(" - ").append(total_cost).append(" = ").append(145 - total_cost).append(CRLF);


                Set set = map_destiny.keySet();
                Iterator iterator = set.iterator();
                while(iterator.hasNext()) {
                    String key = iterator.next().toString();
                    //Log.d( TAG, key +": " + map_destiny.get(key));
                    if (!key.isEmpty()) {
                        Destiny des = realm.where(Destiny.class).equalTo(Destiny.FIELD_NAME, key).findFirst();
                        try {
                            if (NumberUtils.toInt(des.getdesJoinEffect().get(0).substring(0, 1)) <= map_destiny.get(key)) {
                                lambdaResult.append(SEPARATOR).append(des.getDesName()).append(CRLF);
                                for(String joinEffect : des.getdesJoinEffect()) {
                                    String[] joinEffectSplit = joinEffect.split(":");
                                    lambdaResult.append(NumberUtils.isDigits(joinEffectSplit[0]) ? EMPTY : joinEffectSplit[0] + " 시 ");
                                    lambdaResult.append(joinEffectSplit[1]).append(BLANK).append(joinEffectSplit[2]).append(CRLF);
                                }
                            }
                        } catch( NullPointerException ignored) { }
                    }
                }

        } else if(insert_hero_num > 1) {
                    RealmResults<Heroes> whoHaveSecondName =
                            realm.where(Heroes.class).isNotNull(Heroes.FIELD_NAME2)
                                    .and().notEqualTo(Heroes.FIELD_ID,0)
                                    .sort(Heroes.FIELD_NAME).findAll();
                    lambdaResult.append(",동명 장수는 따로 입력 하라옹\r\n");

                    for(Heroes tmpHero : whoHaveSecondName ) {

                        String line = tmpHero.getHeroBranch();
                        String name = tmpHero.getHeroName();

                        String rep = String.format("%-4s", line).replace(' ','　')
                                + BLANK + String.format("%-4s", name).replace(' ','　')
                                + " ▷ " + tmpHero.getHeroName2();
                        lambdaResult.append(rep).append(CRLF);
                    }

        }


        return lambdaResult.toString();
    } );

            // 장수 특성으로 이름 검색 : 연환공격 냥
            SearchLocalDB heroesBySpec = ( q -> {

                Log.d(TAG,"heroesBySpec Activated: " + q);

                if(q.replace(BLANK,EMPTY).isEmpty())
                    return null;

                int cost_more = 0;
                int cost_below = 0;
                int cost_equal = 0;
                int level_pivot = 0;

                if(q.contains("특성")) {
                // 30, 50, 70, 90 : 30 ~ 49, 50 ~ 69, 70 ~ 89, 90 ~ 99
                    // 0 1 2 3  0 / 20 = 0, 20 / 20 = 1, 40 / 20 = 2, 60 / 20 = 3
                    level_pivot = NumberUtils.toInt(q.replaceAll("[^0-9]",EMPTY),0);
                    level_pivot = level_pivot < 30 ? 0 : level_pivot;
                    level_pivot = level_pivot < 50 &&  level_pivot >= 30 ? 30 : level_pivot;
                    level_pivot = level_pivot < 70 &&  level_pivot >= 50 ? 50 : level_pivot;
                    level_pivot = level_pivot < 90 &&  level_pivot >= 70 ? 70 : level_pivot;
                    level_pivot = level_pivot >= 90 ? 90 : level_pivot;
                    q = q.replace("특성",EMPTY).replaceAll("[0-9]",EMPTY).trim();
                    if(q.replace(BLANK,EMPTY).isEmpty())
                        return null;
                    //Log.d(TAG,level_pivot + "특성");
                } else if(q.contains("~")) {
                    String beforePivot = q.substring(0,q.indexOf("~"));
                    String afterPivot = q.substring(q.indexOf("~"));
                    cost_more = NumberUtils.toInt(beforePivot.replaceAll("[^0-9]", EMPTY),10) -10;
                    cost_below = NumberUtils.toInt(afterPivot.replaceAll("[^0-9]", EMPTY),10) - 10;
                    q = q.replace("~",EMPTY).replaceAll("[0-9]",EMPTY).trim();
                } else {
                    cost_equal = NumberUtils.toInt(q.replaceAll("[^0-9]",EMPTY),10)-10;
                    q = q.replaceAll("[0-9]",EMPTY).trim();
                }



                LinkedList<String> rQueue = new LinkedList<>();
                rQueue.addAll(Arrays.asList(q.split(BLANK)));

                if( rQueue.isEmpty() ) return null;
                String qBranch = null;
                String qLineage = null;
                ArrayList<String> specList = new ArrayList<>();
                while( !rQueue.isEmpty()) {
                    String each = rQueue.remove();
                    String validBranchName = findBranchName( each, realm );
                    if( validBranchName != null ) {
                        qBranch = validBranchName;
                        //Log.d(TAG,"Branch: " + qBranch);
                    } else {
                        String validLineageName = findLineageName( each, realm );
                        if( validLineageName != null ) {
                            qLineage = validLineageName;
                            //Log.d(TAG,"Lineage: " + qLineage);
                        } else if( !each.equals("코스트") && !each.equals("코") && !each.equals("장수")){
                            //Log.d(TAG,"spec: " + each);
                            specList.add(each);
                        }
                    }
                }


                if( qBranch == null && qLineage == null
                        && cost_below == 0 && cost_equal == 0 && cost_more == 0
                        && specList.isEmpty() )
                    return null;


                int rowSize = 1; // 1 ~ query 갯수
                int colSize = 0; // 0 ~ 입력효과갯수
                String[][] validSpecs = new String[specList.size()][];
                StringBuilder oneSpec = new StringBuilder();
                for( String specEach : specList )
                    oneSpec.append(specEach);
                if( !oneSpec.toString().replace(BLANK,EMPTY).isEmpty()) {

                RealmResults<Spec> specs = realm.where(Spec.class)
                        .equalTo(Spec.FIELD_NAME_NO_BLANK, oneSpec.toString(), Case.INSENSITIVE)
                        .or().equalTo(Spec.FIELD_NAME2,oneSpec.toString()).findAll();
                if( specs.isEmpty() )
                    specs = realm.where(Spec.class)
                            .contains(Spec.FIELD_NAME_NO_BLANK, oneSpec.toString(), Case.INSENSITIVE)
                            .or().contains(Spec.FIELD_NAME2, oneSpec.toString()).findAll();

                if(!specs.isEmpty()) {
                    specList.clear();
                    specList.add(oneSpec.toString());
                }

                for( String probSpec : specList ) {
                    if( probSpec.length() < 2 ) return null;
                    specs = specList.size() == 1 ? specs : realm.where(Spec.class)
                            .equalTo(Spec.FIELD_NAME_NO_BLANK, probSpec, Case.INSENSITIVE)
                            .or().equalTo(Spec.FIELD_NAME2,probSpec).findAll();
                    if (specs.isEmpty()) {
                        specs = specList.size() == 1 ? specs : realm.where(Spec.class)
                                .contains(Spec.FIELD_NAME_NO_BLANK, probSpec, Case.INSENSITIVE)
                                .or().contains(Spec.FIELD_NAME2, probSpec).findAll();
                        if( specs.isEmpty()) return null;
                    }
                    validSpecs[colSize] = new String[specs.size()];
                    for( int i = 0; i < specs.size(); i++)
                        validSpecs[colSize][i] = specs.get(i).getSpecName();
                    rowSize *= specs.size();
                    colSize++;
                }
                Log.d( TAG,"rowSize:" + rowSize + " colSize:" + colSize );

                    if( rowSize > 500)
                        return rowSize + context.getResources().getString(R.string.desc_tooManyComb);
                }
                StringBuilder lambdaResult = new StringBuilder();

                int valid_spec_count = 0;
                int valid_hero_count_total = 0;
                StringBuilder descForTooManyResults = new StringBuilder();
                for( int  i = 0; i < rowSize; i++ ) {
                    ArrayList<String> keyList = new ArrayList<>();
                    for( int j = 0; j < colSize; j++ ) {
                        int base = 1;
                        int underBase = 1;
                        for( int k = j; k < colSize; k++) {
                            try {
                                base *= validSpecs[k].length;
                                underBase *= validSpecs[k+1].length;
                            } catch( ArrayIndexOutOfBoundsException | NullPointerException ignore) {
                            }
                        }
                        int specNo = i % base / underBase;
                        keyList.add(validSpecs[j][specNo]);
                    }
                    RealmQuery<Heroes> query = realm.where(Heroes.class);
                    query = (qBranch != null)? query.equalTo(Heroes.FIELD_BRANCH,qBranch) : query;
                    query = (qLineage != null)? query.equalTo(Heroes.FIELD_LINEAGE,qLineage) : query;

                    for( String key : keyList )
                        query.equalTo(Heroes.FIELD_SPECS+"."+ RealmString.VALUE, key);
                    query = (cost_equal>0)? query.equalTo(Heroes.FIELD_COST,cost_equal) : query;
                    RealmResults<Heroes> heroes = query.greaterThanOrEqualTo(Heroes.FIELD_COST,cost_more>0? cost_more: 4)
                            .and().lessThanOrEqualTo(Heroes.FIELD_COST,cost_below>0? cost_below: 15)
                            .and().notEqualTo(Heroes.FIELD_ID,0)
                            .findAll()
                            .sort(Heroes.FIELD_COST, Sort.ASCENDING)
                            .sort(Heroes.FIELD_BRANCH);

                    if(!heroes.isEmpty()) {


                        boolean searchOneSpec = keyList.size() == 1;
                        descForTooManyResults.append(++valid_spec_count).append(".");

                        StringBuilder lambdaEachResult = new StringBuilder();
                        lambdaEachResult.append(level_pivot>0 ? "*"+ level_pivot + "특성: " : EMPTY);
                        for( String key : keyList ) {
                            descForTooManyResults.append(key.replace(BLANK, EMPTY)).append(BLANK);
                            lambdaEachResult.append(level_pivot == 0 ? "*" : EMPTY ).append(key).append(CRLF);
                        }
                        descForTooManyResults.append(CRLF);
                        lambdaEachResult.append(qBranch!=null ? "*계열: " + qBranch  + CRLF : EMPTY);
                        lambdaEachResult.append(qLineage!=null ? "*계보: " + qLineage + CRLF : EMPTY);
                        lambdaEachResult.append(cost_below>0 ? "*COST: "+ (cost_below+10) + "이하" + CRLF : EMPTY);
                        lambdaEachResult.append(cost_more>0 ? "*COST: "+ (cost_more+10)  + "이상" + CRLF : EMPTY);
                        lambdaEachResult.append(cost_equal>0 ? "*COST: "+ (cost_equal+10)  + CRLF : EMPTY);
                        lambdaEachResult.append(level_pivot == 0 ? "검색 결과: " + heroes.size() + "개" + CRLF: BLANK);

                        StringBuilder heroListBuilder = new StringBuilder();
                        heroListBuilder.append(SEPARATOR).append("병종계열 장수이름 COST")
                                .append(level_pivot > 0? " 수치" : EMPTY ).append(CRLF).append(SEPARATOR);

                        int count_each = 0;
                        for( Heroes hero : heroes ) {

                            // 30 50 70 90 => 0 1 2 3
                            if( level_pivot == 0 ) {
                                heroListBuilder.append(String.format("%-4s", hero.getHeroBranch()).replace(' ', '　'))
                                        .append(BLANK).append(String.format("%-4s", hero.getHeroName()).replace(' ', '　'))
                                        .append(BLANK).append(hero.getHeroCost() + 10).append(CRLF);
                                count_each ++;
                            } else if ( hero.getHeroSpecs().get((level_pivot-30)/20).toString().equals(keyList.get(0)) ) {

                                //Log.d(TAG,((level_pivot-30)/20)+"번째 특성: "+ hero.getHeroSpecs().get((level_pivot-30)/20) + " = " + keyList.get(0));
                                heroListBuilder.append(String.format("%-4s", hero.getHeroBranch()).replace(' ', '　'))
                                        .append(BLANK).append(String.format("%-4s", hero.getHeroName()).replace(' ', '　'))
                                        .append(BLANK).append(String.format("%-4s", (hero.getHeroCost() + 10) + BLANK).replace(' ', '　'))
                                        .append(searchOneSpec? hero.getHeroSpecValues().get((level_pivot-30)/20):EMPTY).append(CRLF);
                                count_each ++;
                            }
                        }

                        lambdaEachResult.append(level_pivot > 0 ? "검색 결과: " + count_each + "개" + CRLF: BLANK);
                        lambdaEachResult.append(heroListBuilder);
                        valid_hero_count_total += count_each;
                        lambdaResult.append(count_each > 0 ? lambdaEachResult.toString() + COMMA + CRLF : EMPTY);
                    }
                }

                String result;
                if(valid_spec_count == 0 || valid_hero_count_total == 0)
                    result = null;
                else if(valid_spec_count > 6)
                    result = "검색 결과가 너무 많다옹.. (" + valid_spec_count +"개)" + CRLF
                            +"다음 조합 중에 하나를 입력하라옹!" + CRLF
                            + SEPARATOR + descForTooManyResults;
                else
                    result = lambdaResult.toString();


                return result;
            });

            // 특성으로 설명 검색 : 연속책략 설명냥
            SearchLocalDB descBySpec = ( q -> {

                Log.d(TAG,"descBySepc Activated! : " + q);
                if(q.replace(BLANK,EMPTY).isEmpty())
                    return context.getResources().getString(R.string.description);

                ArrayList<String> specList = new ArrayList<>(Arrays.asList(q.split(BLANK)));

                StringBuilder lambdaResult = new StringBuilder();
                StringBuilder oneSpec = new StringBuilder();
                for(String spec : specList)
                    oneSpec.append(spec);

                RealmResults<Spec> realmSpec = realm.where(Spec.class)
                        .equalTo(Spec.FIELD_NAME_NO_BLANK,oneSpec.toString(), Case.INSENSITIVE)
                        .or().equalTo(Spec.FIELD_NAME2,oneSpec.toString()).findAll();
                if( realmSpec.isEmpty() )
                    realmSpec = realm.where(Spec.class)
                            .contains(Spec.FIELD_NAME_NO_BLANK,oneSpec.toString(), Case.INSENSITIVE)
                            .or().contains(Spec.FIELD_NAME2,oneSpec.toString()).findAll();

                if(!realmSpec.isEmpty()) {
                    specList.clear();
                    specList.add(oneSpec.toString());
                }

                for(String probSpec : specList) {
                    if(!probSpec.isEmpty()) {
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
                        descForTooManyResults.append(probSpec).append(": 검색 결과 ")
                                .append(realmSpec.size()).append("개")
                                .append(CRLF).append(SEPARATOR);
                        for (Spec spec : realmSpec)
                            descForTooManyResults.append(spec.getSpecName()).append(CRLF);

                        lambdaResult.append(descForTooManyResults).append(COMMA);
                    } else {
                        for (Spec spec : realmSpec)
                            lambdaResult.append(spec.getSpecName()).append(CRLF).append(spec.getSpecDescription()).append(COMMA);
                    }
                    }
                }
                return lambdaResult.toString();
            } );

            // 병종 정보 검색 : 수군 설명냥
            SearchLocalDB descByBranch = ( q -> {

                if(q.replace(BLANK,EMPTY).isEmpty())
                    return null;

                LinkedList<String> rQueue = new LinkedList<>();
                rQueue.addAll(Arrays.asList(q.split(" ")));

                StringBuilder lambdaResult = new StringBuilder();
                while(!rQueue.isEmpty()) {
                    String probBranch = rQueue.remove();
                    if (!probBranch.isEmpty()) {
                        RealmResults<Branch> branchResult = realm.where(Branch.class).contains(Branch.FIELD_NAME, probBranch).or()
                                .contains(Branch.FIELD_NAME2, probBranch).findAll();
                    if (branchResult.isEmpty()) return null;
                    for (Branch branch : branchResult) {
                        lambdaResult.append(branch.getBranchName()).append(CRLF);
                        for (int i = 0; i < Branch.INIT_STATS.length; i++)
                            lambdaResult.append(Branch.INIT_STATS[i])
                                    .append(branch.getBranchStatGGs()
                                            .get(i)).append(BLANK);

                        lambdaResult.append(CRLF).append(branch.getBranchOtherStats());

                        lambdaResult.append("*부대 효과").append(CRLF);
                        for (int i = 0; i < Branch.INIT_PASVS.length; i++) {
                            String val = branch.getBranchPasvSpecValues().get(i);
                            lambdaResult.append(Branch.INIT_PASVS[i]).append(": ")
                                    .append(branch.getBranchPasvSpecs()
                                            .get(i)).append(BLANK)
                                    .append(val == null ? EMPTY : val).append(CRLF);
                        }

                        lambdaResult.append("*장수 효과").append(CRLF);
                        for (Branch.INIT_SPECS spec : Branch.INIT_SPECS.values()) {
                            String val = branch.getBranchSpecValues().get(spec.ordinal());
                            lambdaResult.append(spec.name()).append(": ")
                                    .append(branch.getBranchSpecs()
                                            .get(spec.ordinal())).append(BLANK)
                                    .append(val == null ? EMPTY : val).append(CRLF);
                        }
                        lambdaResult.append(COMMA).append(CRLF);
                    }
                }

                }
                return lambdaResult.toString();} );

            // 지형 정보 검색 : 노전차 지형냥
            COMMAND_CERTAIN_ENUM finalCertainCMD = certainCMD;
            SearchLocalDB terrainInfoByKey = (q -> {

                if(q.replace(BLANK,EMPTY).isEmpty())
                    return "병종 지형을 입력하라옹!";

                LinkedList<String> rQueue = new LinkedList<>();
                rQueue.addAll(Arrays.asList(q.split(" ")));

                boolean isTerCMD = (finalCertainCMD == COMMAND_CERTAIN_ENUM.COMMAND_TER);
                StringBuilder lambdaResult = new StringBuilder();
                if( rQueue.size() > 2 || rQueue.isEmpty() )
                    return null;

                String key1 = rQueue.remove();

                String validBranchName = findBranchName(key1, realm);
                boolean firstKeyIsBranch = (validBranchName != null);
                key1 = firstKeyIsBranch? validBranchName : key1;

                if( rQueue.isEmpty() ) {  // key가 하나뿐
                    if( firstKeyIsBranch ) {
                        Terrain terrain = realm.where(Terrain.class).equalTo(Terrain.FIELD_BRANCH_NAME,key1).findFirst();
                        if( terrain == null ) return null;
                        lambdaResult.append(terrain.getBranchName()).append(BLANK)
                                .append(PRFX_COMMAND[finalCertainCMD.ordinal()])
                                .append(COMMAND_CERTAIN[finalCertainCMD.ordinal()])
                                .append(SFX_COMMAND[finalCertainCMD.ordinal()]).append(CRLF)
                                .append(SEPARATOR);
                        int iForCRLF = 0;


                        for(TVpair pair : isTerCMD ? terrain.getTerrainSyns(): terrain.getMovingCost()) {

                            lambdaResult.append(pair.getPaddTvTerrainName(3)).append(":")
                                    .append(pair.getPaddTvValue(isTerCMD?3:1));
                            lambdaResult.append((iForCRLF++)%2==0? BLANK+BLANK:CRLF);
                        }
                    } else { // keyIsTerrain
                        RealmResults<Terrain> terrains = realm.where(Terrain.class).findAll();

                        int iForCRLF = 0;
                        lambdaResult.append(key1).append(BLANK)
                                .append(PRFX_COMMAND[finalCertainCMD.ordinal()])
                                .append(COMMAND_CERTAIN[finalCertainCMD.ordinal()])
                                .append(SFX_COMMAND[finalCertainCMD.ordinal()]).append(CRLF)
                                .append(SEPARATOR);
                        for(Terrain terrain : terrains ) {
                            RealmList<TVpair> tvs = isTerCMD? terrain.getTerrainSyns() : terrain.getMovingCost();
                            boolean terrainIsValid = false;
                            for(TVpair pair : tvs) {
                                if( pair.getTvTerrainName().contains(key1) ) {
                                    lambdaResult.append(terrain.getPaddBranchName(4))
                                            .append(":").append(pair.getPaddTvValue(isTerCMD? 3:1));
                                    terrainIsValid = true;
                                    break;
                                }
                            }
                            if( !terrainIsValid ) return null;
                            lambdaResult.append((iForCRLF++)%2==0? BLANK+BLANK:CRLF);
                        }

                    }
                } else { // key가 2개
                    String key2 = rQueue.remove();
                    String branchKey = firstKeyIsBranch? key1 : key2;
                    String terrainKey = firstKeyIsBranch? key2 : key1;
                    Terrain terrain = realm.where(Terrain.class).equalTo(Terrain.FIELD_BRANCH_NAME,branchKey).findFirst();
                    if(terrain == null) return null;

                    lambdaResult.append(branchKey).append(BLANK).append(terrainKey)
                            .append(PRFX_COMMAND[finalCertainCMD.ordinal()])
                            .append(COMMAND_CERTAIN[finalCertainCMD.ordinal()])
                            .append(SFX_COMMAND[finalCertainCMD.ordinal()]).append(": ");
                    boolean terrainIsValid = false;
                    for(TVpair pair : isTerCMD ? terrain.getTerrainSyns(): terrain.getMovingCost()) {
                        if( pair.getTvTerrainName().contains(terrainKey) ) {
                            lambdaResult.append("[").append(pair.getTvValue()).append("]");
                            terrainIsValid = true;
                            break;
                        }
                    }
                    if( !terrainIsValid ) return null;
                }
                return lambdaResult.toString();
            });

            // 몽매 시련 시너지 검색 : 조조 시너지냥
            @SuppressLint("SimpleDateFormat") SearchLocalDB synergyByKey = (q -> {
                Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyMM");
                StringBuilder lambdaResult = new StringBuilder();

                RealmResults<TermSynergy> synergies;

                if(q.replace(BLANK,EMPTY).isEmpty()) {
                    synergies = realm.where(TermSynergy.class)
                            .equalTo(TermSynergy.FIELD_TERM, format.format(now)).findAll();
                } else {
                    RealmQuery<TermSynergy> synQuery = realm.where(TermSynergy.class);
                    for(String probHero : q.split(BLANK)) {
                        RealmResults<Heroes> heroes = realm.where(Heroes.class)
                                .equalTo(Heroes.FIELD_NAME, probHero).findAll();
                        if( heroes.isEmpty() )
                            heroes = realm.where(Heroes.class)
                                    .contains(Heroes.FIELD_NAME2, probHero).findAll();
                        if( heroes.isEmpty() )
                            return probHero + ": 없다냥";

                        synQuery.beginGroup();
                        if(heroes.size() > 1) {// 조조, 제갈량...
                            for (Heroes hero : heroes) {
                                synQuery.equalTo(TermSynergy.FIELD_MEMBERS + "." + RealmString.VALUE, hero.getHeroName2());
                                synQuery = (heroes.indexOf(hero) == heroes.size()-1)? synQuery : synQuery.or();
                            }
                        } else // 아만, 주준...
                            synQuery.beginGroup().equalTo(TermSynergy.FIELD_MEMBERS+"."+RealmString.VALUE,heroes.first().getHeroName())
                                    .or().equalTo(TermSynergy.FIELD_MEMBERS+"."+RealmString.VALUE,heroes.first().getHeroName2()).endGroup();

                        synQuery.endGroup();
                        synQuery = (q.indexOf(probHero) == q.length()-1)? synQuery : synQuery.and();
                    }
                    synergies = synQuery.findAll();
                    if(synergies.isEmpty())
                        return q + ": 시너지 없다냥";

                }

                lambdaResult.append(new SimpleDateFormat("yy").format(now)).append("년 ")
                        .append(new SimpleDateFormat("MM").format(now)).append("월")
                        .append(CRLF).append("몽매의 시련 시너지");
                for( TermSynergy syn : synergies) {
                    lambdaResult.append(CRLF).append(SEPARATOR);
                    for(RealmString member : syn.getSynMembers())
                        lambdaResult.append(member.isEmpty()? "":
                                StringUtils.rightPad(member.toString(),3,'　'))
                                .append(member.isEmpty()?EMPTY:BLANK);
                    lambdaResult.append(syn.getSynEnemies().get(0).isEmpty()?EMPTY : "vs ");
                    for(RealmString enemy : syn.getSynEnemies())
                        lambdaResult.append(enemy).append(enemy.isEmpty()?EMPTY:BLANK);
                    for(int i = 0; i < syn.getSynSpecs().size(); i++ )
                        lambdaResult.append(CRLF).append("→ ").append(syn.getSynSpecs().get(i)).append(BLANK)
                                .append(syn.getSynSpecValues().get(i));
                }


                return lambdaResult.toString();
            });

            // 인연 검색
            SearchLocalDB destinyByKey = (q->{
                if(q.replace(BLANK,EMPTY).isEmpty()) {
                    String nameResult = "인연 전체 목록" + CRLF + SEPARATOR;
                    for (Destiny destiny : realm.where(Destiny.class).findAll())
                        nameResult += destiny.getDesName() + CRLF;
                    return nameResult;
                }


                StringBuilder lambdaResult = new StringBuilder();
                RealmResults<Destiny> destinies = realm.where(Destiny.class).contains(Destiny.FIELD_NAME_NO_BLANK,q.replace(BLANK,EMPTY)).findAll();
                if(destinies.isEmpty()) {
                    HashMap<String,Integer> map_destiny = new HashMap<>();
                    for( String probHero : q.split(BLANK) ) {
                        RealmResults<Heroes> heroes = realm.where(Heroes.class).equalTo(Heroes.FIELD_NAME,probHero)
                                .or().contains(Heroes.FIELD_NAME2,probHero).findAll();
                        for( Heroes hero : heroes ) {
                            String desName = hero.getHeroDestiny();
                            if( desName != null )
                                for( String desNameEach : desName.split(":"))
                                    map_destiny.put(desNameEach, 1);
                        }
                    }
                    RealmQuery<Destiny> destinyQuery = realm.where(Destiny.class);

                    for(String des : map_destiny.keySet())
                        destinyQuery.equalTo(Destiny.FIELD_NAME,des).or();
                    destinyQuery.alwaysFalse();
                    destinies = destinyQuery.findAll();
                }


                if(!destinies.isEmpty()) {
                    for(Destiny destiny : destinies) {
                        lambdaResult.append(destiny.getDesName()).append(CRLF);
                        try {
                            String desCord = destiny.getDesCord();
                            if( desCord != null )
                                lambdaResult.append(Destiny.INIT_CORD).append(desCord).append(CRLF);
                            for( int i = 0; i < destiny.getdesCondition().size(); i++ ) {
                                String condition = destiny.getdesCondition().get(i);
                                if( condition != null)
                                    lambdaResult.append(Destiny.INIT_CONDITIONS[i]).append(condition).append(CRLF);
                            }
                        } catch (NullPointerException ignored) {
                        }
                        try {
                            lambdaResult.append(Destiny.INIT_LASTING_EFFECT).append(destiny.getDesLastingEffect()).append(CRLF);
                            for( String effect :  destiny.getdesJoinEffect()) {
                                    String[] effectSplit = effect.split(":");
                                    lambdaResult.append(effectSplit[0]).append(NumberUtils.isDigits(effectSplit[0]) ? "명 이상 출진: "
                                            : "시 발동: ").append(effectSplit[1]).append(BLANK).append(effectSplit[2]).append(CRLF);
                            }
                        } catch (NullPointerException ignored) {
                        }
                        lambdaResult.append(COMMA);
                    }
                } else {
                    return "그럼 인연 없다옹";
                }

                return lambdaResult.toString();

            });

            // 보물 이름, 특성 검색 : 7 보인갑 냥 / 전용 무기 냥
            SearchLocalDB  itemByKey = (q->{

                Log.d(TAG,"itemByKey Activated: " + q);
                if(q.replace(BLANK,EMPTY).isEmpty())
                    return "보물 이름 종류 특수효과를 입력하라옹";

                String reinfOrGRD = q.replaceAll("[^0-9]",EMPTY);
                q = q.replaceAll("[0-9]",EMPTY);

                String[] ignores= {"강","등급","+","성"};
                ArrayList<String> ignoreList = new ArrayList<>(Arrays.asList(ignores));
                LinkedList<String> qList = new LinkedList<>(Arrays.asList(q.split(BLANK)));
                ArrayList<String> nsList = new ArrayList<>();
                RealmResults<ItemCate> cates = null;
                String insertCate = null;
                while( !qList.isEmpty()) {
                    String qEach = qList.remove();
                    if(qEach.equals("전용")||qEach.equals("연의")) {
                        reinfOrGRD = qEach;
                    } else if(!ignoreList.contains(qEach)){
                        RealmResults<ItemCate> mainCate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_MAIN_CATE,qEach).findAll();
                        cates = (cates==null && !mainCate.isEmpty())? mainCate : cates;
                        if(mainCate.isEmpty()) {
                            RealmResults<ItemCate> subCate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_SUB_CATE,qEach).findAll();
                            cates = (cates==null && !subCate.isEmpty())? subCate : cates;
                            if(subCate.isEmpty())
                                nsList.add(qEach);
                            else
                                insertCate = qEach;
                        } else {
                            insertCate = qEach;
                        }
                    }
                }

                StringBuilder nameOrSpecBuilder = new StringBuilder();
                for(String name : nsList)
                    nameOrSpecBuilder.append(name);
                String nameOrSpec = nameOrSpecBuilder.toString();
                //Log.d(TAG,"nameOrSpec = " + nameOrSpec);

                StringBuilder lambdaResult = new StringBuilder();

                RealmQuery<Item> itemQuery = realm.where(Item.class);
                if(cates != null) {
                    itemQuery.beginGroup();
                    for(ItemCate cate : cates) {
                        itemQuery.equalTo(Item.FIELD_SUB_CATE,cate.getItemSubCate()).or();
                    }
                    itemQuery.alwaysFalse().endGroup();
                }

                RealmResults<Item> itemsByName = nameOrSpec.length() < 2? itemQuery.and().alwaysFalse().findAll() :
                        itemQuery.and().contains(Item.FIELD_NAME_NO_BLANK,nameOrSpec).findAll();
                if(!itemsByName.isEmpty()) {

                    Log.d(TAG,"Item Search type A");
                    for (Item item : itemsByName) {
                        lambdaResult.append("[").append(item.getItemSubCate()).append("]").append(BLANK)
                                .append(item.getItemName()).append(BLANK).append("(").
                                append(item.getItemGrade()).append(")").append(CRLF);
                        for (int i = 0; i < Item.INIT_STATS.length; i++) {
                            String stat = item.getItemStats().get(i).toString();
                            if(!stat.isEmpty() && !stat.equals(DASH))
                                lambdaResult.append(Item.INIT_STATS[i]).append("> ").append(stat).append(CRLF);
                        }
                        String restriction = item.getItemRestriction();
                        if(restriction == null )  {
                            ItemCate sub_cate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_SUB_CATE,item.getItemSubCate()).findFirst();
                            restriction = sub_cate.getItemRestriction();
                        }

                        lambdaResult.append("제한　> ").append(restriction).append(CRLF);

                        for (int i = 0; i < item.getItemSpecValues().size(); i++) {
                            String itemSpec = item.getItemSpecs().get(i).toString();
                            if(!itemSpec.isEmpty())
                                lambdaResult.append("*").append(itemSpec).append(BLANK)
                                        .append(item.getItemSpecValues().get(i).toString().replace(DASH,EMPTY)).append(CRLF);
                        }

                        lambdaResult.append(COMMA);

                    }
                } else {

                    RealmResults<Spec> specs = nameOrSpec.isEmpty()? null :
                            realm.where(Spec.class).contains(Spec.FIELD_NAME_NO_BLANK,nameOrSpec, Case.INSENSITIVE)
                            .or().contains(Spec.FIELD_NAME2,nameOrSpec).findAll();

                    if( specs == null) {
                        Log.d(TAG,"Item Search type B");
                        itemQuery = realm.where(Item.class);
                        if (cates != null) {
                            itemQuery.beginGroup();
                            for (ItemCate cate : cates)
                                itemQuery.equalTo(Item.FIELD_SUB_CATE, cate.getItemSubCate()).or();
                            itemQuery.alwaysFalse().endGroup();
                        }
                        itemQuery = reinfOrGRD.isEmpty() ? itemQuery : itemQuery.and().equalTo(Item.FIELD_GRD, reinfOrGRD);
                        RealmResults<Item> itemsByCATE = itemQuery.findAll().sort(Item.FIELD_SUB_CATE);

                        if(itemsByCATE.isEmpty()) return null;

                        lambdaResult.append(insertCate != null ? "*" + insertCate + CRLF : EMPTY);
                        lambdaResult.append(reinfOrGRD.isEmpty() ? EMPTY : "*" + reinfOrGRD + CRLF);
                        lambdaResult.append("검색 결과: ").append(itemsByCATE.size()).append("개")
                                .append(CRLF).append(SEPARATOR);
                        for (Item item : itemsByCATE) {
                            lambdaResult.append("[").append(item.getItemSubCate()).append("]").append(BLANK)
                                    .append(item.getItemName()).append(BLANK);
                            if( reinfOrGRD.isEmpty() )
                                lambdaResult.append("(").append(item.getItemGrade()).append(")");
                            lambdaResult.append(CRLF);
                        }

                    } else if( !specs.isEmpty() ) {
                        Log.d(TAG,"Item Search type C");
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
                                lambdaResult.append(reinfOrGRD.isEmpty() ? EMPTY : "*" + reinfOrGRD + CRLF);
                                lambdaResult.append("검색 결과: ").append(itemsBySpec.size()).append("개")
                                        .append(CRLF).append(SEPARATOR);
                                for (Item item : itemsBySpec) {
                                    lambdaResult.append("[").append(item.getItemSubCate()).append("]").append(BLANK)
                                            .append(item.getItemName()).append(BLANK);
                                    if( reinfOrGRD.isEmpty() )
                                        lambdaResult.append("(").append(item.getItemGrade()).append(")");
                                    lambdaResult.append(CRLF);
                                }
                                lambdaResult.append(COMMA);
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
            SearchLocalDB relationByKey = (q->{
                if( q.replace(BLANK,EMPTY).isEmpty())
                    return "병종을 입력하라옹!";

                ArrayList<String> branchList = new ArrayList<>();
                for(String probBranch : q.split(BLANK)) {
                    if(!probBranch.isEmpty() && probBranch.length() > 1) {
                        RealmResults<Branch> branches= realm.where(Branch.class).equalTo(Branch.FIELD_NAME,probBranch)
                                .or().contains(Branch.FIELD_NAME2,probBranch).findAll();
                        branches = branches.isEmpty() ? realm.where(Branch.class)
                                .contains(Branch.FIELD_NAME,probBranch).findAll() : branches;
                        if(branches.isEmpty())
                            return "그런 병종 없다옹";
                        else
                            branchList.add(branches.first().getBranchName());
                    }
                }

                StringBuilder lambdaResult = new StringBuilder();

                if(branchList.size() > 2)
                    return "공격 / 피격 2개 병종만 입력하라옹";
                else if( branchList.size() == 2) {
                    String branchName1 = branchList.get(0);
                    String branchName2 = branchList.get(1);
                    Relation attackRelation = realm.where(Relation.class)
                            .equalTo(Relation.FIELD_ATTACKER, branchName1).and()
                            .equalTo(Relation.FIELD_DEFENDER, branchName2).findFirst();
                    Relation defenseRelation = realm.where(Relation.class)
                            .equalTo(Relation.FIELD_ATTACKER, branchName2).and()
                            .equalTo(Relation.FIELD_DEFENDER, branchName1).findFirst();

                    lambdaResult.append(String.format("%-4s", attackRelation.getBranchAttacker()).replace(' ', '　')).append(" > ")
                            .append(String.format("%-4s", attackRelation.getBranchDefender()).replace(' ', '　')).append(": ")
                            .append(attackRelation.getRelationValue()).append(CRLF);
                    lambdaResult.append(String.format("%-4s", defenseRelation.getBranchAttacker()).replace(' ', '　')).append(" > ")
                            .append(String.format("%-4s", defenseRelation.getBranchDefender()).replace(' ', '　')).append(": ")
                            .append(defenseRelation.getRelationValue()).append(CRLF);

                } else if (branchList.size() == 1) {
                    String branchName = branchList.get(0);
                    RealmResults<Relation> attackRelations = realm.where(Relation.class)
                            .equalTo(Relation.FIELD_ATTACKER, branchName).findAll();
                    RealmResults<Relation> defenseRelations = realm.where(Relation.class)
                            .equalTo(Relation.FIELD_DEFENDER, branchName).findAll();

                    lambdaResult.append(branchName).append(CRLF).append("vs　　　 공격　　피격")
                            .append(CRLF).append(SEPARATOR);
                    for( int i = 0; i < attackRelations.size(); i++ ) {
                        Relation attack = attackRelations.get(i);
                        Relation defense = defenseRelations.get(i);

                        if( attack.getBranchDefender().equals(defense.getBranchAttacker()))
                            lambdaResult.append(String.format("%-4s", attack.getBranchDefender())
                                    .replace(' ', '　')).append(BLANK)
                                    .append(attack.getRelationValue()).append("　　")
                                    .append(defense.getRelationValue()).append(CRLF);
                    }

                } else {
                    return "병종을 입력하라옹!";
                }

                return lambdaResult.toString();
            });

            // 보패 정보 검색
            SearchLocalDB magicItemByKey = (q->{
                //각 보패냥, 단단한 보패냥, 근접피해감소 보패냥

                int statValue = NumberUtils.toInt(q.replaceAll("[^0-9]",EMPTY),200);
                statValue = (statValue<0 || statValue > 200) ? 200 : statValue;
                q = q.replaceAll("[0-9]",EMPTY).replace(BLANK,EMPTY);

                RealmResults<MagicItemSFX> sfxes = realm.where(MagicItemSFX.class).distinct(MagicItemSFX.FIELD_NAME).findAll();
                RealmResults<MagicItemPRFX> prfxes = realm.where(MagicItemPRFX.class).findAll();


                if(q.isEmpty()) {
                    StringBuilder descEmptyCommand = new StringBuilder("접두사 또는 접미사를 입력하게냥" + CRLF + "*접미사 : ");
                    int i = 0;
                    for( MagicItemSFX sfx : sfxes ) {
                        descEmptyCommand.append((i++)%7 == 0 ? CRLF : EMPTY).append(BLANK).append(sfx.getMagicSuffixName());
                    }
                    descEmptyCommand.append(CRLF + "*접두사 : ");
                    i = 0;
                    for( MagicItemPRFX prfx : prfxes ) {
                        descEmptyCommand.append((i++)%3 == 0 ? CRLF : EMPTY).append(BLANK).append(prfx.getPrefixName());
                    }
                    return descEmptyCommand.toString();
                }

                ArrayList<Boolean> qIsSfxList = new ArrayList<>();

                for(char probSFX : q.toCharArray()) {
                    boolean qIsSFX = false;
                    for( MagicItemSFX sfx : sfxes ) {
                        if(sfx.getMagicSuffixName().equals(probSFX+EMPTY)) {
                            qIsSFX = true;
                            //Log.d(TAG,probSFX+"is suffix");
                            break;
                        }
                    }
                    qIsSfxList.add(qIsSFX);
                }

                boolean qIsSFXes = true;
                for(boolean qIsSFX : qIsSfxList)
                    qIsSFXes &= qIsSFX;

                StringBuilder lambdaResult = new StringBuilder();


                if( qIsSFXes ) {
                    // 각 보패냥   ,,   각 항 심 보패냥
                    for(char probSFX : q.toCharArray()) {
                        lambdaResult.append("접미사 ").append(probSFX).append(" 스탯 정보").append(CRLF).append(SEPARATOR);
                        lambdaResult.append("등급 공 정 방 순 사 HP MP").append(CRLF).append(SEPARATOR);
                        RealmResults<MagicItemSFX> sfxGrades = realm.where(MagicItemSFX.class).equalTo(MagicItemSFX.FIELD_NAME, probSFX+EMPTY).findAll();
                        for (MagicItemSFX sfxGrade : sfxGrades) {

                            lambdaResult.append(sfxGrade.getMagicSuffixGRD()).append(":　");
                            // 공 정 방 순 사 HP MP
                            try {
                                for (int stat : sfxGrade.getMagicSuffixStats()) {
                                    lambdaResult.append(StringUtils.leftPad(stat+EMPTY,2,'0')).append(BLANK);
                                }
                            } catch( NullPointerException ignore) {}

                            lambdaResult.append(CRLF);
                        }


                        lambdaResult.append(COMMA);
                    }
                } else {
                    if(q.length()<2)
                        return "접두사 또는 효과를 2글자 이상 입력하게냥";
                    RealmResults<MagicItemPRFX> prfxNames =  realm.where(MagicItemPRFX.class).contains(MagicItemPRFX.FIELD_NAME,q).findAll();

                    if(!prfxNames.isEmpty()) {
                        for (MagicItemPRFX prfxName : prfxNames) {
                            lambdaResult.append(prfxName.getPrefixName()).append(BLANK).append("(")
                                    .append(prfxName.getPrefixSpec()).append(")").append(CRLF)
                                    .append(prfxName.getPrefixStat()).append(BLANK).append(statValue);
                            try {
                                for (int i = 0; i < 5; i++)
                                    lambdaResult.append(CRLF).append("Lv.").append((i + 1)).append(":  ")
                                            .append((prfxName.getPrefixValue().get(i) * (double) statValue / 200.0));
                            } catch( NullPointerException ignore ) {}
                            lambdaResult.append(COMMA);
                        }
                    } else {
                        RealmResults<Spec> specs = realm.where(Spec.class).contains(Spec.FIELD_NAME,"보패:").and().
                                beginGroup().contains(Spec.FIELD_NAME_NO_BLANK,q).or().contains(Spec.FIELD_NAME2,q).endGroup().findAll();
                        if( specs.isEmpty() )
                            return "그런 보패 없다냥..";
                        for( Spec spec : specs ) {
                            MagicItemPRFX prfxName = realm.where(MagicItemPRFX.class).equalTo(MagicItemPRFX.FIELD_SPEC,spec.getSpecName().replace("보패: ",EMPTY)).findFirst();
                            try {
                                lambdaResult.append(prfxName.getPrefixName()).append(BLANK).append("(")
                                        .append(prfxName.getPrefixSpec()).append(")").append(CRLF)
                                        .append(prfxName.getPrefixStat()).append(BLANK).append(statValue);
                            } catch( NullPointerException ignore ) {
                                //return "["+spec.getSpecName()+"] 효과를 가진 접두사는 없다옹. "
                                 //       + CRLF + "'"+ q + " 조합냥'으로 검색해보게냥!";
                            }
                            try {
                                for (int i = 0; i < 5; i++)
                                    lambdaResult.append(CRLF).append("Lv.").append((i + 1)).append(":  ")
                                            .append((prfxName.getPrefixValue().get(i) * (double) statValue / 200.0));
                            } catch( NullPointerException ignore) {}
                            lambdaResult.append(COMMA);
                        }
                    }

                }

                return lambdaResult.toString();

            });


            // 보패 조합 검색
            SearchLocalDB magicCombByKey = (q->{
                //각 조합냥
                q = q.replace(BLANK,EMPTY);

                RealmResults<MagicItemSFX> sfxes = realm.where(MagicItemSFX.class).distinct(MagicItemSFX.FIELD_NAME).findAll();

                if(q.isEmpty()) {
                    StringBuilder descEmptyCommand = new StringBuilder("접미사 조합 또는 조합효과를 입력하게냥" + CRLF + "접미사 : ");
                    int i = 0;
                    for( MagicItemSFX sfx : sfxes ) {
                        descEmptyCommand.append((i++)%7 == 0 ? CRLF : EMPTY).append(BLANK).append(sfx.getMagicSuffixName());
                    }
                    return descEmptyCommand.toString();
                }

                ArrayList<Boolean> qIsSfxList = new ArrayList<>();

                for(char probSFX : q.toCharArray()) {
                    boolean qIsSFX = false;
                    for( MagicItemSFX sfx : sfxes ) {
                        if(sfx.getMagicSuffixName().equals(probSFX+EMPTY)) {
                            qIsSFX = true;
                            //Log.d(TAG,probSFX+"is suffix");
                            break;
                        }
                    }
                    qIsSfxList.add(qIsSFX);
                }

                boolean qIsSFXes = true;
                for(boolean qIsSFX : qIsSfxList)
                    qIsSFXes &= qIsSFX;

                StringBuilder lambdaResult = new StringBuilder();

                RealmResults<MagicItemCombination> combinations;
                if( qIsSFXes ) {
                    // 각 조합냥
                    RealmQuery<MagicItemCombination> combQuery = realm.where(MagicItemCombination.class).alwaysTrue();

                    for(char probSFX : q.toCharArray())
                        combQuery.and().equalTo(MagicItemCombination.FIELD_SFXES+"."+RealmString.VALUE, probSFX+EMPTY);

                    combinations = combQuery.findAll();



                } else {
                    if(q.length()<2)
                        return "조합 효과를 2글자 이상 입력하게냥";
                    combinations = realm.where(MagicItemCombination.class).contains(MagicItemCombination.FIELD_COMB,q).findAll();
                }

                if(combinations.isEmpty()) {
                    return "그런 보패 조합 없다냥...";
                }

                lambdaResult.append("보패 조합 검색 결과: " + combinations.size() + "개").append(CRLF)
                        .append(SEPARATOR);

                for( MagicItemCombination combination : combinations ) {
                    for(int i = 0; i < 4; i++ )
                        lambdaResult.append(combination.getMagicCombSFXes().get(i));
                    lambdaResult.append(BLANK).append("[").append(combination.getMagicCombGRD())
                            .append("등급]").append(BLANK).append(combination.getMagicCombination()).append(CRLF);
                }




                return lambdaResult.toString();

            });

            // 섬멸, 경쟁 일정 검색




            String result = null;

            switch (certainCMD) {
                case COMMAND_TER:
                case COMMAND_MOV:
                    result = terrainInfoByKey.search(req);
                    result = (result==null)? "잘못된 입력이라옹" : result;
                    break;
                case COMMAND_DESC :
                    result = descByBranch.search(req);
                    result = (result==null)? descBySpec.search(req) : result;
                    break;
                case COMMAND_DEST :
                    result = destinyByKey.search(req);
                    break;
                case COMMAND_SYN:
                    result = synergyByKey.search(req);
                    break;
                case COMMAND_ITEM:
                    result = itemByKey.search(req);
                    result = (result==null)? "그런 보물 없다냥..." : result;
                    break;
                case COMMAND_AGENDA:
                    result = null;
                    break;
                case COMMAND_RELATION:
                    result = relationByKey.search(req);
                    break;
                case COMMAND_MAGIC:
                    result = magicItemByKey.search(req);
                    break;
                case COMMAND_EXTERMINATE:
                    result = null;
                    break;
                case COMMAND_COMB:
                    result = magicCombByKey.search(req);
                    break;
                case COMMAND_DOT:
                    result = dotByPoints.search(req);
                    break;
                    default:
                        result = heroesByName.search(req);
                        result = (result==null)? heroesBySpec.search(req) : result;
                        result = (result==null)? itemByKey.search(req) : result;
                        result = (result==null)? descBySpec.search(req) : result;
                        result = (result==null)? "장수->특성->보물->설명 검색결과: 없음" : result;
            }


            if(result == null) {
                return false;
            } else {
                KakaoReplier replier = new KakaoReplier(context,sendCat,sbn);
                replier.execute(result,"[L]");
                return true;
            }
        }
        return false;
    }

    private interface SearchLocalDB { String search(String req); }

    private String findBranchName( String proBranch, Realm realm ) {
        if(proBranch.isEmpty()||proBranch.length()<2) return null;
        String branchName = null;
        RealmResults<Branch> branches = realm.where(Branch.class).contains(Branch.FIELD_NAME,proBranch)
                .or().contains(Branch.FIELD_NAME2,proBranch).findAll();

        if( !branches.isEmpty() ) {
            Branch branch = branches.size() > 1 ? realm.where(Branch.class).equalTo(Branch.FIELD_NAME, proBranch)
                    .or().contains(Branch.FIELD_NAME2, proBranch).findFirst() : branches.first();
            try {
                branchName = branch.getBranchName();
            } catch( NullPointerException ignore) {

            }
        }
        return branchName;
    }

    private String findLineageName( String probLineage, Realm realm ) {
        String lineageName = null;
        if( probLineage.length() > 3 ) {
            Heroes lineage =  realm.where(Heroes.class).
                    contains(Heroes.FIELD_LINEAGE,probLineage).findFirst();
            if( lineage !=  null )
                lineageName = lineage.getHeroLineage();
        }
        return lineageName;
    }

    private String drawRangeView( int rowSize, int colSize, String... points) {

        boolean[][] sparseMatrix = new boolean[rowSize][colSize];

        for( String point : points ) {
            String[] value = point.split(BLANK);
            int row = NumberUtils.toInt(value[0]);
            int col = NumberUtils.toInt(value[1]);
            sparseMatrix[row][col] = true;
        }

        StringBuilder drawResult = new StringBuilder();
        for(boolean[] row : sparseMatrix) {
            for(boolean bool: row)
                drawResult.append(bool?RANGE_FULL:RANGE_EMPTY);
            drawResult.append(CRLF);
        }

        return drawResult.toString();
    }
}
