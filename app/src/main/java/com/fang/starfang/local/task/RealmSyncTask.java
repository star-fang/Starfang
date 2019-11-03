package com.fang.starfang.local.task;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.fang.starfang.local.model.realm.Agenda;
import com.fang.starfang.local.model.realm.Branch;
import com.fang.starfang.local.model.realm.Destiny;
import com.fang.starfang.local.model.realm.Dot;
import com.fang.starfang.local.model.realm.Heroes;
import com.fang.starfang.local.model.realm.Item;
import com.fang.starfang.local.model.realm.ItemCate;
import com.fang.starfang.local.model.realm.ItemReinforcement;
import com.fang.starfang.local.model.realm.Magic;
import com.fang.starfang.local.model.realm.MagicItemCombination;
import com.fang.starfang.local.model.realm.MagicItemPRFX;
import com.fang.starfang.local.model.realm.MagicItemSFX;
import com.fang.starfang.local.model.realm.Relation;
import com.fang.starfang.local.model.realm.Spec;
import com.fang.starfang.local.model.realm.Terrain;
import com.fang.starfang.local.model.realm.UnionBranch;
import com.fang.starfang.local.model.realm.UnionSkill;
import com.fang.starfang.local.model.realm.UnionSpec;
import com.fang.starfang.local.model.realm.UpdateTime;
import com.fang.starfang.local.model.realm.primitive.RealmString;
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
import org.json.JSONArray;
import org.json.JSONObject;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmSyncTask  extends AsyncTask<String,String, String> {

    private final static String TAG = "FANG_REALM";
    private final static String REALM_BASE_URL = "/fangcat/convertToRealm/";
    private final static String GET_JSON_PHP = "convertToJSON.php";
    private Context context;
    private Gson gson;
    private String address;
    private AlertDialog.Builder builder;
    private AlertDialog progressDialog;
    private ProgressBar progressBar;
    private TextView mTextView;

    public RealmSyncTask(String address, Context context)
    {

        this.context = context;
        this.address = address;

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

        progressDialog = getDialogProgressBar("Loading...").create();
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... tasks) {
        int count = tasks.length;
        int count_latest = 0;
        int count_fail = 0;


            for (int i = 0; i < tasks.length; i++ ) {
                String currentTask = tasks[i];
                String[] jsonResult = getAllData(currentTask);
                publishProgress(currentTask,jsonResult[0],jsonResult[1]);
                if( jsonResult[0].equals("fail")) {
                    count_fail++;
                } else if( jsonResult[0].equals("latest")) {
                    count_latest++;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    break;
                }
            }


        String resultStr = "동기화 완료: 전체:" + count + "개, 갱신: " + (count-count_fail-count_latest) + "개, 최신:" + count_latest + "개, 실패:" + count_fail + "개";


        return resultStr;
    }

    private String[] getAllData(String pref_table) {

        Realm realmForUT = Realm.getDefaultInstance();
        UpdateTime ut = realmForUT.where(UpdateTime.class).equalTo(UpdateTime.FIELD_TABLE,pref_table).findFirst();
        if(ut == null) {
            UpdateTime newUT = new UpdateTime();
            newUT.setLatestUpadateTime("0");
            newUT.setPrefTable(pref_table);
            realmForUT.beginTransaction();
            realmForUT.copyToRealm(newUT);
            realmForUT.commitTransaction();
            ut = newUT;
        }
        String utStr = ut.getLatestUpadateTime();
        realmForUT.close();
        String mJSONURLString = address + REALM_BASE_URL + GET_JSON_PHP + "?pref_t=" + pref_table.replace(" ","%20")
                +"&lut="+utStr;
        Log.d(TAG,"get method: " + mJSONURLString );

        final String[] jsonResult = {"", ""};

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        RequestFuture<JSONObject> requestFuture= RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, mJSONURLString,null,
                requestFuture, requestFuture ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };


        requestQueue.add(jsonObjectRequest);


        try {
            JSONObject jsonObject = requestFuture.get();
                Realm realm = Realm.getDefaultInstance();
                try {
                    jsonResult[0] = jsonObject.get("status").toString();
                    jsonResult[1] = jsonObject.get("message").toString();
                    String lut = jsonObject.get("time").toString();
                    realm.beginTransaction();
                    UpdateTime updateTime = realm.where(UpdateTime.class).equalTo(UpdateTime.FIELD_TABLE,pref_table).findFirst();
                    if (updateTime != null) {
                        updateTime.setLatestUpadateTime(lut);
                    }
                    realm.commitTransaction();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(jsonResult[0].equals("latest") || jsonResult[0].equals("fail") ) {
                    Log.d(TAG,pref_table + ":" + jsonResult[1]);
                    return jsonResult;
                }


                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonArray!=null) {

                    realm.beginTransaction();

                    switch ( pref_table ) {
                        case Terrain.PREF_TABLE:
                            realm.delete(Terrain.class);
                            realm.createAllFromJson(Terrain.class,jsonArray);
                            Log.d(TAG, "SYNC Terrain REALM COMPLETE!");
                            break;
                        case Heroes.PREF_TABLE:
                            realm.delete(Heroes.class);
                            for(int i = 0; i < jsonArray.length(); i++ ) {
                                try {
                                    String json = jsonArray.get(i).toString();
                                    Heroes hero = gson.fromJson(json,Heroes.class);
                                    realm.copyToRealm(hero);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }



                            Log.d(TAG, "SYNC Hero REALM COMPLETE!");
                            break;
                        case Destiny.PREF_TABLE:
                            realm.delete(Destiny.class);
                            realm.createAllFromJson(Destiny.class, jsonArray);
                            for(Destiny des : realm.where(Destiny.class).findAll())
                                des.setDesNameNoBlank(des.getDesName().replace(" ", ""));
                            Log.d(TAG, "SYNC Destiny REALM COMPLETE!");
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

                            for(Spec spec : realm.where(Spec.class).findAll())
                                spec.setSpecNameNoBlank(spec.getSpecName().replace(" ",""));
                            // 검색용 공백 제거 column 생성
                            Log.d(TAG, "SYNC Spec REALM COMPLETE!");
                            break;
                        case Branch.PREF_TABLE:
                            realm.delete(Branch.class);
                            realm.createAllFromJson(Branch.class, jsonArray);
                            Log.d(TAG, "SYNC Branch REALM COMPLETE!");
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
                            break;
                        case ItemCate.PREF_TABLE:
                            realm.delete(ItemCate.class);
                            realm.createAllFromJson(ItemCate.class, jsonArray);
                            Log.d(TAG, "SYNC ItemCate REALM COMPLETE!");
                            break;
                        case ItemReinforcement.PREF_TABLE:
                            realm.delete(ItemReinforcement.class);
                            realm.createAllFromJson(ItemReinforcement.class, jsonArray);
                            Log.d(TAG, "SYNC ItemReinforcement REALM COMPLETE!");
                            break;
                        case Relation.PREF_TABLE:
                            realm.delete((Relation.class));
                            realm.createAllFromJson(Relation.class, jsonArray);
                            Log.d(TAG, "SYNC Relation REALM COMPLETE!");
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
                            break;
                        case MagicItemPRFX.PREF_TABLE:
                            realm.delete(MagicItemPRFX.class);
                            realm.createAllFromJson(MagicItemPRFX.class, jsonArray);
                            Log.d(TAG, "SYNC  MagicItemPRFX REALM COMPLETE!");
                            break;
                        case MagicItemSFX.PREF_TABLE:
                            realm.delete(MagicItemSFX.class);
                            realm.createAllFromJson(MagicItemSFX.class, jsonArray);
                            Log.d(TAG, "SYNC MagicItemSFX REALM COMPLETE!");
                            break;
                        case Dot.PREF_TABLE:
                            realm.delete(Dot.class);
                            realm.createAllFromJson(Dot.class, jsonArray);
                            Log.d(TAG, "SYNC Dot REALM COMPLETE!");
                            break;
                        case Magic.PREF_TABLE:
                            realm.delete(Magic.class);
                            realm.createAllFromJson(Magic.class, jsonArray);
                            Log.d(TAG, "SYNC Magic REALM COMPLETE!");
                            break;
                        case UnionBranch.PREF_TABLE:
                            realm.delete(UnionBranch.class);
                            realm.createAllFromJson(UnionBranch.class, jsonArray);
                            Log.d(TAG, "SYNC UnionBranch REALM COMPLETE!");
                            break;
                        case UnionSkill.PREF_TABLE:
                            realm.delete(UnionSkill.class);
                            realm.createAllFromJson(UnionSkill.class, jsonArray);
                            Log.d(TAG, "SYNC UnionSkill REALM COMPLETE!");
                            break;
                        case UnionSpec.PREF_TABLE:
                            realm.delete(UnionSpec.class);
                            realm.createAllFromJson(UnionSpec.class, jsonArray);
                            Log.d(TAG, "SYNC UnionSpec REALM COMPLETE!");
                            break;
                        case Agenda.PREF_TABLE:
                            realm.delete(Agenda.class);
                            realm.createAllFromJson(Agenda.class,jsonArray);
                            Log.d(TAG, "SYNC Agenda REALM COMPLETE!");
                            break;
                        default:
                            Log.d(TAG, "SYNC REALM failure : empty table name");
                    }
                    realm.commitTransaction();
                    realm.refresh();
                    realm.close();


                } else {
                    jsonResult[0] = "fail";
                    jsonResult[1] = "데이터 값 없음";
                }



        } catch (InterruptedException | ExecutionException ignored) {

        }
        return jsonResult;
    }

    private AlertDialog.Builder getDialogProgressBar(String title) {

        if (builder == null) {
            builder = new AlertDialog.Builder(context);

            builder.setTitle("Loading...");
            mTextView = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mTextView.setLayoutParams(lp);
            builder.setView(mTextView);
        }
        return builder;
    }

    // After each task done
    @Override
    protected void onProgressUpdate(String... values){
        String beforeValue = mTextView.getText().toString();
        mTextView.setText(beforeValue + "\n" + values[0] + values[1]);
    }

    // When all async task done
    @Override
    protected void onPostExecute(String result){
        String beforeValue = mTextView.getText().toString();
        mTextView.setText(beforeValue + "\n\n" + result);
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
