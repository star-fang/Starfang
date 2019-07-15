package com.fang.starfang.local.task;

// 190403 To do : realm object 상속으로 dynamic binding 구현하기 (가능?)

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fang.starfang.local.model.realm.Branch;
import com.fang.starfang.local.model.realm.Destiny;
import com.fang.starfang.local.model.realm.Dot;
import com.fang.starfang.local.model.realm.Heroes;
import com.fang.starfang.local.model.realm.Item;
import com.fang.starfang.local.model.realm.ItemCate;
import com.fang.starfang.local.model.realm.ItemReinforcement;
import com.fang.starfang.local.model.realm.MagicItemCombination;
import com.fang.starfang.local.model.realm.MagicItemPRFX;
import com.fang.starfang.local.model.realm.MagicItemSFX;
import com.fang.starfang.local.model.realm.Relation;
import com.fang.starfang.local.model.realm.Spec;
import com.fang.starfang.local.model.realm.TermSynergy;
import com.fang.starfang.local.model.realm.Terrain;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.view.ProgressBarPreference;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmSyncTask  {

    private final static String TAG = "FANG_REALM";
    private final static String REALM_BASE_URL = "http://clavis.dothome.co.kr/fangcat/convertToRealm/";
    private final static String GET_JSON_PHP = "convertToJSON.php";
    private Context context;
    private String pref_table;
    private Realm realm;
    private Gson gson;
    private ProgressBarPreference progressBarPreference;

    public RealmSyncTask(Context context, String pref_table, ProgressBarPreference syncPreference) {
        this.context = context;
        this.pref_table = pref_table;
        this.progressBarPreference = syncPreference;


        GsonBuilder gsonBuilder = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });
        // register the deserializer
        gsonBuilder.registerTypeAdapter(new TypeToken<RealmList<RealmString>>() {
        }.getType(), new RealmStringDeserializer());

        gson = gsonBuilder.create();
    }

    public void getAllData(boolean toast) {
        realm = Realm.getDefaultInstance();

        // Get Data and Delete old data from Realm database....
        String url_get_heroes = REALM_BASE_URL + GET_JSON_PHP + "?pref_t=" + pref_table.replace(" ","%20");
        Log.d(TAG,"get method: " + url_get_heroes );

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                url_get_heroes, jsonArray -> {
                    Log.d(TAG,"JSON received: " + jsonArray.length() + "objects" );

                    if (!jsonArray.isNull(0)) {

                        realm.beginTransaction();
                        switch ( pref_table ) {
                            case Terrain.PREF_TABLE:
                                realm.delete(Terrain.class);
                                realm.createAllFromJson(Terrain.class,jsonArray);
                                Log.d(TAG, "SYNC Terrain REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"지형 정보 Merge, 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case Heroes.PREF_TABLE:
                                realm.delete(Heroes.class);
                                progressBarPreference.setMax(jsonArray.length());
                                for(int i = 0; i < jsonArray.length(); i++ ) {
                                    try {
                                        String json = jsonArray.get(i).toString();
                                        Heroes hero = gson.fromJson(json,Heroes.class);
                                        realm.copyToRealm(hero);
                                        progressBarPreference.setProgress(i);
                                        //progressBarPreference.setLabel(hero.getHeroName());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                progressBarPreference.setLabel("장수 정보 동기화 완료");


                                Log.d(TAG, "SYNC Hero REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"장수 정보 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case Destiny.PREF_TABLE:
                                realm.delete(Destiny.class);
                                realm.createAllFromJson(Destiny.class, jsonArray);
                                for(Destiny des : realm.where(Destiny.class).findAll())
                                    des.setDesNameNoBlank(des.getDesName().replace(" ", ""));
                                Log.d(TAG, "SYNC Destiny REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"인연 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case Spec.PREF_TABLE:
                                realm.delete(Spec.class);


                                for(int i = 0; i < jsonArray.length(); i++ ) {
                                    try {
                                        String json = jsonArray.get(i).toString();
                                        Spec spec = gson.fromJson(json,Spec.class);
                                        realm.copyToRealm(spec);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                //realm.createAllFromJson(Spec.class, jsonArray);
                                for(Spec spec : realm.where(Spec.class).findAll())
                                    spec.setSpecNameNoBlank(spec.getSpecName().replace(" ",""));
                                    // 검색용 공백 제거 column 생성
                                Log.d(TAG, "SYNC Spec REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"설명 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case Branch.PREF_TABLE:
                                realm.delete(Branch.class);
                                realm.createAllFromJson(Branch.class, jsonArray);
                                Log.d(TAG, "SYNC Branch REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"병종 정보 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case TermSynergy.PREF_TABLE:
                                realm.delete(TermSynergy.class);
                                for(int i = 0; i < jsonArray.length(); i++ ) {
                                    try {
                                        String json = jsonArray.get(i).toString();
                                        TermSynergy synergy = gson.fromJson(json,TermSynergy.class);
                                        realm.copyToRealm(synergy);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d(TAG, "SYNC TermSynergy REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"몽매 시너지 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case Item.PREF_TABLE:
                                realm.delete(Item.class);
                                for(int i = 0; i < jsonArray.length(); i++ ) {
                                    try {
                                        String json = jsonArray.get(i).toString();
                                        Item item = gson.fromJson(json,Item.class);
                                        realm.copyToRealm(item);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                for(Item item : realm.where(Item.class).findAll())
                                    item.setItemNameNoBlank(item.getItemName().replace(" ",""));
                                      // 검색용 공백 제거 column 생성
                                Log.d(TAG, "SYNC Item REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"보물 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case ItemCate.PREF_TABLE:
                                realm.delete(ItemCate.class);
                                realm.createAllFromJson(ItemCate.class, jsonArray);
                                Log.d(TAG, "SYNC ItemCate REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"보물 분류 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case ItemReinforcement.PREF_TABLE:
                                realm.delete(ItemReinforcement.class);
                                realm.createAllFromJson(ItemReinforcement.class, jsonArray);
                                Log.d(TAG, "SYNC ItemReinforcement REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"보물 강화 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case Relation.PREF_TABLE:
                                realm.delete((Relation.class));
                                realm.createAllFromJson(Relation.class, jsonArray);
                                Log.d(TAG, "SYNC Relation REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"병종 상성 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case MagicItemCombination.PREF_TABLE:
                                realm.delete(MagicItemCombination.class);
                                for(int i = 0; i < jsonArray.length(); i++ ) {
                                    try {
                                        String json = jsonArray.get(i).toString();
                                        MagicItemCombination comb = gson.fromJson(json,MagicItemCombination.class);
                                        realm.copyToRealm(comb);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d(TAG, "SYNC MagicItemCombination REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"보패 조합 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case MagicItemPRFX.PREF_TABLE:
                                realm.delete(MagicItemPRFX.class);
                                realm.createAllFromJson(MagicItemPRFX.class, jsonArray);
                                Log.d(TAG, "SYNC  MagicItemPRFX REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"보패 접두사 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case MagicItemSFX.PREF_TABLE:
                                realm.delete(MagicItemSFX.class);
                                realm.createAllFromJson(MagicItemSFX.class, jsonArray);
                                Log.d(TAG, "SYNC MagicItemSFX REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"보패 접미사 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            case Dot.PREF_TABLE:
                                realm.delete(Dot.class);
                                realm.createAllFromJson(Dot.class, jsonArray);
                                Log.d(TAG, "SYNC Dot REALM COMPLETE!");
                                if(toast) Toast.makeText(context,"도트 동기화 완료",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.d(TAG, "SYNC REALM failure : empty table name");
                        }
                        realm.commitTransaction();
                        realm.refresh();
                    } else {
                        Log.d(TAG, "SYNC REALM failure : empty JSON");
                        Toast.makeText(context,"데이터 값 없음",Toast.LENGTH_LONG).show();
                    }

                }, error -> { Log.e(TAG,"error:" + error.getMessage());
                              Toast.makeText(context,"데이터 전송 실패",Toast.LENGTH_LONG).show(); } );


        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonArrayRequest);

    }


    /**
     * Created by catalin prata on 29/05/15.
     * <p/>
     * Used to deserialize a list of realm string objects
     */
    public class RealmStringDeserializer implements
            JsonDeserializer<RealmList<RealmString>> {

        @Override
        public RealmList<RealmString> deserialize(JsonElement json, Type typeOfT,
                                                  JsonDeserializationContext context) throws JsonParseException {

            RealmList<RealmString> realmStrings = new RealmList<>();
            JsonArray stringList = json.getAsJsonArray();

            for (JsonElement stringElement : stringList) {
                realmStrings.add(new RealmString(getNullAsEmptyString(stringElement)));
            }

            return realmStrings;
        }
    }

    private String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }

}
