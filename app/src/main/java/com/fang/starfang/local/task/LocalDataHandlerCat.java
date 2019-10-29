package com.fang.starfang.local.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Agenda;
import com.fang.starfang.local.model.realm.Branch;
import com.fang.starfang.local.model.realm.Destiny;
import com.fang.starfang.local.model.realm.Dot;
import com.fang.starfang.local.model.realm.Heroes;
import com.fang.starfang.local.model.realm.Item;
import com.fang.starfang.local.model.realm.ItemCate;
import com.fang.starfang.local.model.realm.Magic;
import com.fang.starfang.local.model.realm.MagicItemCombination;
import com.fang.starfang.local.model.realm.MagicItemPRFX;
import com.fang.starfang.local.model.realm.MagicItemSFX;
import com.fang.starfang.local.model.realm.Memo;
import com.fang.starfang.local.model.realm.Relation;
import com.fang.starfang.local.model.realm.Spec;
import com.fang.starfang.local.model.realm.TVpair;
import com.fang.starfang.local.model.realm.TermSynergy;
import com.fang.starfang.local.model.realm.Terrain;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.util.KakaoReplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mozilla.javascript.Scriptable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;



public class LocalDataHandlerCat extends AsyncTask<String, Integer, String> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String sendCat;
    private String catRoom;
    private StatusBarNotification sbn;

    private static final String TAG = "LOCAL_HANDLER";
    private enum COMMAND_CERTAIN_ENUM {
        COMMAND_DEST,COMMAND_TER,COMMAND_MOV,COMMAND_DESC,
        COMMAND_SYN,COMMAND_ITEM,COMMAND_AGENDA,COMMAND_MAGIC_ITEM,
        COMMAND_RELATION,COMMAND_EXTERMINATE,COMMAND_DOT,COMMAND_COMB,
        COMMAND_CALC, COMMAND_MAGIC, COMMAND_MEMO, COMMAND_DEL_MEMO, COMMAND_DEFAULT }
    private static final String[] COMMAND_CERTAIN = {
            "인연","지형","소모","설명",
            "시너지","보물","일정","보패",
            "상성","퇴치","도트","조합","계산","책략","메모","삭제"};


    private static final String[] PRFX_COMMAND = {"","","이동력","","몽매","","","","병종","","","보패","","","","메모"};
    private static final String[] SFX_COMMAND = {"","상성","","","","","","","","","","","","","",""};

    private static final String CRLF = "\r\n";
    private static final String BLANK = " ";
    private static final String EMPTY = "";
    private static final String DASH = "-";
    private static final String COMMA = ",";
    private static final String SEPARATOR = "-------------------------------\n";
    private static final String RANGE_EMPTY = "□";
    private static final String RANGE_FULL = "■";

    public LocalDataHandlerCat(Context c, String sender, String room, StatusBarNotification _sbn ) {
        context = c;
        sendCat = sender;
        catRoom = room;
        sbn = _sbn;
    }


    @Override
    protected String doInBackground(String... strings) {
        try(Realm realm = Realm.getDefaultInstance()) {
            handleRequest( strings[0], realm);
            //if()
             //   new LambdaFunctionHandler(context, sendCat, sbn).execute(strings[0]);
        } catch ( RuntimeException ignore ) {

        }
        return null;
    }


    private boolean handleRequest( String req, Realm realm ) {

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
                            reqWithoutSFX = (req.substring(req.length() - suffix.length()).equals(suffix)) ?
                                    req.substring(0, req.length() - suffix.length() ): req;
                    } catch( StringIndexOutOfBoundsException  ignore ) { }

                    try {
                        if (reqWithoutSFX.substring(reqWithoutSFX.length() - probKey.length()).equals(probKey)) {
                            certainCMD = certain;
                            req = req.replace(prefix, EMPTY);
                            req = req.replace(suffix, EMPTY).trim();
                            req = req.substring(0, req.length() - probKey.length()).trim();
                            break;
                        }
                    } catch( StringIndexOutOfBoundsException  ignore ) { }

                } catch (ArrayIndexOutOfBoundsException ignore) { }
            }


            HandleLocalDB dotByPoints = ( q-> {

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
            HandleLocalDB heroesByName = ( q -> {

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
                                .append(insert_hero_num == 1 ? (tmpHero.getHeroName2() != null ? "  a.k.a. "  + tmpHero.getHeroName2() : EMPTY)
                                        + CRLF + Heroes.INIT_COST + ": "
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
            HandleLocalDB heroesBySpec = ( q -> {

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
                        lambdaEachResult.append(level_pivot == 0 ? "검색 결과: " + heroes.size() + "개" + CRLF : BLANK);

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

                        lambdaEachResult.append(level_pivot > 0 ? "검색 결과: " + count_each + "개" + CRLF: EMPTY );
                        lambdaEachResult.append(heroListBuilder);
                        valid_hero_count_total += count_each;
                        lambdaResult.append(count_each > 0 ? lambdaEachResult.toString() + COMMA + CRLF : EMPTY );
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
            HandleLocalDB descBySpec = ( q -> {

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
                        for (Spec spec : realmSpec) {
                            //int numAKA = spec.getSpecName2().replaceAll("[^:]",EMPTY).length();
                            lambdaResult.append(spec.getSpecName())
                                    .append(spec.getSpecName2() != null? "  a.k.a. " + spec.getSpecName2().replace(":","，") + CRLF : CRLF)
                                    .append(spec.getSpecDescription()).append(COMMA);
                        }
                    }
                    }
                }
                return lambdaResult.toString();
            } );


            // 아이템 설명 : 번우 설명냥
            HandleLocalDB descByItem = ( q -> {
                if(q.replace(BLANK,EMPTY).isEmpty())
                    return null;

                LinkedList<String> rQueue = new LinkedList<>();
                rQueue.addAll(Arrays.asList(q.split(" ")));

                StringBuilder lambdaResult = new StringBuilder();
                while(!rQueue.isEmpty()) {
                    String probItem = rQueue.remove();
                    RealmResults<Item> itemResult = realm.where(Item.class).contains(Item.FIELD_NAME_NO_BLANK, probItem).or()
                            .contains(Item.FIELD_NAME,probItem).findAll();
                    if( itemResult.isEmpty() ) return null;
                    for( Item item : itemResult ) {

                        String itemSpecOne;
                        String itemSpecTwo;

                        lambdaResult.append("[").append(item.getItemSubCate()).append("] ").append(item.getItemName()).append(" (")
                                .append(item.getItemGrade()).append(")").append(CRLF).append(item.getitemDescription());

                        try {
                            itemSpecOne = item.getItemSpecs().get(0).getStringValue();

                            if( !itemSpecOne.isEmpty() ) {
                                try {
                                    lambdaResult.append(CRLF).append(CRLF).append("*").append(itemSpecOne).append(": ")
                                            .append(realm.where(Spec.class).equalTo(Spec.FIELD_NAME, itemSpecOne).findFirst()
                                                    .getSpecDescription().replace("n(%)","n%")
                                                    .replace("n%", item.getItemSpecValues().get(0).getStringValue()));
                                } catch (NullPointerException e) {
                                    lambdaResult.append("*").append(itemSpecOne);
                                }
                            }

                        } catch (NullPointerException ignore ) {

                        }


                        try {
                            itemSpecTwo = item.getItemSpecs().get(1).getStringValue();

                            if( !itemSpecTwo.isEmpty() ) {

                                try {
                                    lambdaResult.append(CRLF).append(CRLF).append("*").append(itemSpecTwo).append(": ")
                                            .append(realm.where(Spec.class).equalTo(Spec.FIELD_NAME, itemSpecTwo).findFirst()
                                                    .getSpecDescription().replace("n(%)","n%")
                                                    .replace("n%", item.getItemSpecValues().get(1).getStringValue()));
                                } catch (NullPointerException e) {
                                    lambdaResult.append("*").append(itemSpecTwo);
                                }
                            }

                        } catch (NullPointerException ignore ) {

                        }

                        lambdaResult.append(",");


                    }
                }

                return  lambdaResult.toString();
            } );


            // 병종 정보 검색 : 수군 설명냥
            HandleLocalDB descByBranch = ( q -> {

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
            HandleLocalDB terrainInfoByKey = (q -> {

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

                            lambdaResult.append(pair.getPaddTvTerrainName(4)).append(":")
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
            @SuppressLint("SimpleDateFormat") HandleLocalDB synergyByKey = (q -> {
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
            HandleLocalDB destinyByKey = (q->{
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
            HandleLocalDB  itemByKey = (q->{

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
                    if(qEach.equals("전용")||qEach.equals("연의")||qEach.equals("제작")) {
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

                            String stat = reinfOrGRD.isEmpty() ? item.getItemStats().get(i).toString() :
                                    Reinforcement.getInstance().reinforce(item,i,reinfOrGRD);
                            if(!stat.isEmpty() && !stat.equals(DASH))
                                lambdaResult.append(Item.INIT_STATS[i]).append(": ").append(stat).append(CRLF);
                        }
                        String restriction = item.getItemRestriction();
                        if(restriction == null )  {
                            ItemCate sub_cate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_SUB_CATE,item.getItemSubCate()).findFirst();
                            restriction = sub_cate.getItemRestriction();
                        }

                        lambdaResult.append("제한　: ").append(restriction).append(CRLF);

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
            HandleLocalDB relationByKey = (q->{
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

                    lambdaResult.append(String.format("%-4s", attackRelation.getBranchAttacker()).replace(' ', '　')).append(" → ")
                            .append(String.format("%-4s", attackRelation.getBranchDefender()).replace(' ', '　')).append(": ")
                            .append(attackRelation.getRelationValue()).append(CRLF);
                    lambdaResult.append(String.format("%-4s", defenseRelation.getBranchAttacker()).replace(' ', '　')).append(" → ")
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
            HandleLocalDB magicItemByKey = (q->{
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
                    String [] initStat = {"공","정","방","순","사","HP","MP"};
                    for(char probSFX : q.toCharArray()) {
                        lambdaResult.append("접미사 ").append(probSFX).append(" 스탯 정보").append(CRLF).append(SEPARATOR);
                        //lambdaResult.append("등급 공 정 방 순 사 HP MP").append(CRLF).append(SEPARATOR);
                        RealmResults<MagicItemSFX> sfxGrades = realm.where(MagicItemSFX.class).equalTo(MagicItemSFX.FIELD_NAME, probSFX+EMPTY).findAll();

                        for (MagicItemSFX sfxGrade : sfxGrades) {

                            lambdaResult.append("[").append(sfxGrade.getMagicSuffixGRD()).append("등급] ");
                            // 공 정 방 순 사 HP MP
                            int i = 0;
                            try {
                                for (int stat : sfxGrade.getMagicSuffixStats()) {
                                    lambdaResult.append(stat!=0? initStat[i]+StringUtils.leftPad(stat+BLANK,3,'0') : EMPTY);
                                    i++;
                                    //lambdaResult.append(StringUtils.leftPad(stat+EMPTY,2,'0')).append(BLANK);
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
                            return null;
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
            HandleLocalDB magicCombByKey = (q->{
                //각 조합냥
                q = q.replace(BLANK,EMPTY);

                RealmResults<MagicItemSFX> sfxes = realm.where(MagicItemSFX.class).distinct(MagicItemSFX.FIELD_NAME).findAll();

                if(q.isEmpty()) {
                    StringBuilder descEmptyCommand = new StringBuilder("접미사 조합 또는 조합효과를 입력하게냥" + CRLF + "*접미사 : ");
                    int i = 0;
                    for( MagicItemSFX sfx : sfxes ) {
                        descEmptyCommand.append((i++)%7 == 0 ? CRLF : EMPTY).append(BLANK).append(sfx.getMagicSuffixName());
                    }
                    RealmResults<MagicItemCombination> combs = realm.where(MagicItemCombination.class).distinct(MagicItemCombination.FIELD_COMB).findAll();
                    descEmptyCommand.append(CRLF).append("*조합효과 : ");
                    i = 0;
                    for( MagicItemCombination comb : combs ) {
                        descEmptyCommand.append((i++)%2 == 0 ? CRLF : EMPTY).append(BLANK).append(StringUtils.rightPad(comb.getMagicCombination().replaceAll("[0-9][0-9]%",EMPTY),6,'　'));
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

            // 보패관리 : Starfang 강인한 각 습득
            HandleLocalDB manageMagicItem = (q-> {

                // 습득, 갈갈, 변경, 착용, 해제, 렙업

                if(q.replace(BLANK,EMPTY).isEmpty())
                    return null;

                LinkedList<String> rQueue = new LinkedList<>();
                rQueue.addAll(Arrays.asList(q.split(BLANK)));

                if( rQueue.isEmpty() ) return null;
                return null;
            });


            // 책략 이름 검색
            HandleLocalDB searchMagicByName = ( q-> {

                Log.d(TAG,"searchMagicByName Activated" );
                q = q.replace(BLANK,EMPTY);

                if( q.isEmpty() )
                    return null;



               RealmResults<Magic> magics = realm.where(Magic.class).contains(Magic.FIELD_NAME, q).findAll();

               if( magics.isEmpty() ) return null;

               StringBuilder lambdaResult = new StringBuilder();
               for( Magic magic : magics ) {
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
                           .append(CRLF + "*기본 명중률: ").append(magic.getMagicAccu())
                           .append(CRLF + "*효과 범위: ").append(magic.getMagicEffectArea())
                           .append(CRLF + "*시전 범위: ").append(magic.getMagicTargetArea())
                           .append(tmpAccuType.equals("Normal") ? "" : CRLF + "*적중 타입: " + tmpAccuType)
                           .append(tmpDamageType.equals("None") ? "" : CRLF + "*데미지 타입: " + tmpDamageType)
                           .append(tmpHealType.equals("-") ? "" : CRLF + "*치료 타입: " + tmpHealType)
                           .append(tmpStreak == 0 ? "" : CRLF + "*연속 발동 가능")
                           .append(tmpObstructive == 0 ? "" : CRLF + "*방해계 책략")
                           .append(CRLF).append(magic.getMagicDesc()).append(",");
               }
                return lambdaResult.toString();
            });


            HandleLocalDB searchMagicByType = ( q-> {

                Log.d(TAG,"searchMagicByType Activated" );
                q = q.replace(BLANK, EMPTY);

                if (q.isEmpty())
                    return null;

                StringBuilder lambdaResult = new StringBuilder();
                RealmResults<Magic> magicTypes = realm.where(Magic.class).distinct(Magic.FIELD_TYPE).findAll();

                int count = 0;
                for( Magic type : magicTypes ) {


                    if( type.getMagicSkillType().contains(q)) {

                        Log.d(TAG,type.getMagicSkillType());
                        RealmResults<Magic> magics = realm.where(Magic.class).equalTo(Magic.FIELD_TYPE, type.getMagicSkillType()).findAll();
                        lambdaResult.append("*[" + type.getMagicSkillType() + "]속성 책략" + CRLF);
                        lambdaResult.append("검색 결과: ").append(magics.size()).append("개")
                                .append(CRLF).append(SEPARATOR);
                        for (Magic magic : magics) {
                            lambdaResult.append(magic.getMagicName()).append(CRLF);
                            count++;
                        }
                        lambdaResult.append(",");
                    }
                 }




                return count == 0 ? null: lambdaResult.toString();
            } );


            HandleLocalDB searchMagicByBranch = ( q-> {

                Log.d(TAG, "searchMagicByBranch Activated");
                q = q.replace(BLANK, EMPTY);

                if (q.isEmpty())
                    return null;


                q = findBranchName(q, realm);

                if( q == null)
                    return null;

                StringBuilder lambdaResult = new StringBuilder();

                lambdaResult.append("*").append(q).append("계 책략").append(CRLF).append(SEPARATOR);



                try {
                    Branch branch = realm.where(Branch.class).equalTo(Branch.FIELD_NAME, q).findFirst();
                    if (branch.getBranchMagic().equals("-")) return "책략 없음";

                    for(String each : branch.getBranchMagic().split(":")) {
                        Magic magic = realm.where(Magic.class).equalTo(Magic.FIELD_NAME, each).findFirst();
                        lambdaResult.append("[").append(magic.getMagicSkillType()).append("] ").append(magic.getMagicName()).append(CRLF);
                    }
                } catch (NullPointerException ignore) {
                }


                return lambdaResult.toString();



            });


            HandleLocalDB getAgenda = q-> {
               StringBuilder lambdaResult = new StringBuilder();
               String division = null;
               if( q.contains("섬멸")) {
                   q = q.replace("섬멸전","");
                   q = q.replace("섬멸","");
                   division = "섬멸";
               } else if( q.contains("경쟁")) {
                   q = q.replace("경쟁전", "");
                   q = q.replace("경쟁", "");
                   division = "경쟁";
               } else {
                   return "[섬멸]혹은[경쟁]을 입력하세요";
               }


                String dateInfo = q.replaceAll("[^0-9]", "");
                Date date = new Date();
                date.setTime(date.getTime());
                SimpleDateFormat df_ymd = new SimpleDateFormat("yyMMdd", Locale.KOREA);
                try {
                    if (dateInfo.length() == 6) {
                        q = q.replace(dateInfo, "");
                        date = df_ymd.parse(dateInfo);
                    } else if (dateInfo.length() == 4) {
                        q = q.replace(dateInfo, "");
                        dateInfo = (new SimpleDateFormat("yy", Locale.KOREA).format(date)) + dateInfo;
                        date = df_ymd.parse(dateInfo);
                    }

                } catch (ParseException ignore) {
                }


                Date date_before = new Date();
                date_before.setTime(date.getTime() - 21 * 24 * 60 * 60 * 1000);
                Date date_after = new Date();
                date_after.setTime(date.getTime() + 22 * 24 * 60 * 60 * 1000);

               int dateBeforeInt = Integer.parseInt(df_ymd.format(date_before));
               int dateAfterInt = Integer.parseInt(df_ymd.format(date_after));
                String map = q.trim();



                lambdaResult.append( new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(date) ).
                        append(BLANK).append(division).append("일정").append(CRLF).append(SEPARATOR);

                RealmResults<Agenda> agendaRealmResults = null;
                if(map.equals("")) {
                    agendaRealmResults = realm.where(Agenda.class).between(Agenda.FIELD_START, dateBeforeInt, dateAfterInt).and()
                            .equalTo(Agenda.FIELD_DIV, division).findAll();
                } else {
                    agendaRealmResults = realm.where(Agenda.class).and().contains(Agenda.FIELD_MAP, map)
                            .equalTo(Agenda.FIELD_DIV, division).findAll();
                }


                if(agendaRealmResults.isEmpty()) {
                    return "[날짜4~6자리]or[맵] [경쟁or섬멸] 일정냥 <<< 이렇게 입력하게냥";
                }

                    SimpleDateFormat df_md = new SimpleDateFormat("MM.dd", Locale.KOREA);

                    for (Agenda agenda : agendaRealmResults) {

                        try {
                            Date start_date = df_ymd.parse(agenda.getAgendaStart()+"");
                            Date end_date = new Date();
                            end_date.setTime(start_date.getTime() + 6 * 24 * 60 * 60 * 1000);

                            lambdaResult.append(df_md.format(start_date) + "~" + df_md.format(end_date) + BLANK + agenda.getAgendaMap())
                                    .append(((start_date.getTime() <= date.getTime())
                                            && (end_date.getTime() + 24 * 60 * 60 * 1000 > date.getTime())) ? " V" : "")
                                    .append(CRLF);

                        } catch (ParseException ignore) {
                        }
                    }



               return lambdaResult.toString();
            };

            HandleLocalDB createOrGetMemo = q -> {
                if(catRoom == null) {
                    return "단톡방에서만 사용가능한 기능입니다.";
                }

                q = q.trim();

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

                int id = NumberUtils.toInt(q.replaceAll("[^0-9]",EMPTY),-1);
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





                String result = null;

            switch (certainCMD) {
                case COMMAND_TER:
                case COMMAND_MOV:
                    result = terrainInfoByKey.handle(req);
                    result = (result==null)? "잘못된 입력이라옹" : result;
                    break;
                case COMMAND_DESC :
                    result = descByBranch.handle(req);
                    result = (result==null)? descBySpec.handle(req) : result;
                    result = (result==null)? descByItem.handle(req) : result;
                    result = (result==null)? "?ㅅ?" : result;
                    break;
                case COMMAND_DEST :
                    result = destinyByKey.handle(req);
                    break;
                case COMMAND_SYN:
                    result = synergyByKey.handle(req);
                    break;
                case COMMAND_ITEM:
                    result = itemByKey.handle(req);
                    result = (result==null)? "그런 보물 없다냥..." : result;
                    break;
                case COMMAND_AGENDA:
                    result = getAgenda.handle(req);
                    break;
                case COMMAND_RELATION:
                    result = relationByKey.handle(req);
                    break;
                case COMMAND_MAGIC_ITEM:
                    result = magicItemByKey.handle(req);
                    result = (result==null)? magicCombByKey.handle(req) : result;
                    result = (result==null)? "보패정보 -> 보패조합 검색결과 : 없음" : result;
                    break;
                case COMMAND_COMB:
                    result = magicCombByKey.handle(req);
                    break;
                case COMMAND_EXTERMINATE:
                    result = null;
                    break;
                case COMMAND_DOT:
                    result = dotByPoints.handle(req);
                    break;
                case COMMAND_MEMO:
                    result = createOrGetMemo.handle(req);
                    break;
                case COMMAND_DEL_MEMO:
                    result = deleteMemo.handle(req);
                    break;
                case COMMAND_CALC:
                    try {
                        org.mozilla.javascript.Context mozillaC = org.mozilla.javascript.Context.enter();
                        mozillaC.setOptimizationLevel(-1);
                        Scriptable scope = mozillaC.initSafeStandardObjects();
                        result = mozillaC.evaluateString(scope, req, "<cmd>", 1, null).toString();
                    } catch (Exception ignore) {

                    }
                    break;

                case COMMAND_MAGIC:
                    result = searchMagicByName.handle(req);
                    result = (result==null)? searchMagicByBranch.handle(req) : result;
                    result = (result==null)? searchMagicByType.handle(req) : result;
                    result = (result==null)? "책략 이름 또는 속성을 입력하라옹" : result;
                    break;
                    default:
                        result = heroesByName.handle(req);
                        result = (result==null)? heroesBySpec.handle(req) : result;
                        result = (result==null)? itemByKey.handle(req) : result;
                        result = (result==null)? searchMagicByName.handle(req) : result;
                        result = (result==null)? descBySpec.handle(req) : result;
                        result = (result==null)? "장수->특성->보물->책략->설명 검색결과: 없음" : result;
            }


            if(result != null) {
                KakaoReplier replier = new KakaoReplier(context,sendCat,sbn);
                replier.execute(result,"[L]");
                return true;
            }

        return false;
    }

    private interface HandleLocalDB {
        String handle(String req);
    }

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