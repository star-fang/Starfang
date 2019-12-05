package com.fang.starfang.local.task;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.Agenda;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Destiny;
import com.fang.starfang.local.model.realm.source.Dot;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.local.model.realm.source.ItemReinforcement;
import com.fang.starfang.local.model.realm.source.Magic;
import com.fang.starfang.local.model.realm.source.RelicCombination;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.local.model.realm.source.NormalItem;
import com.fang.starfang.local.model.realm.source.Relation;
import com.fang.starfang.local.model.realm.source.Spec;
import com.fang.starfang.local.model.realm.source.Terrain;
import com.fang.starfang.local.model.realm.union.UnionBranch;
import com.fang.starfang.local.model.realm.union.UnionSkill;
import com.fang.starfang.local.model.realm.union.UnionSpec;
import com.fang.starfang.local.model.realm.UpdateTime;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFloatingRealmAdapter;
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

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class RealmSyncTask  extends AsyncTask<String,String, String> {

    private final static String TAG = "FANG_SYNC";
    private final static String REALM_BASE_URL = "/fangcat/convertToRealm/";
    private final static String GET_JSON_PHP = "convertToJSON.php";
    private WeakReference<Context> context;
    private Gson gson;
    private String address;
    private AlertDialog.Builder builder;
    private WeakReference<ScrollView> scroll_progress_list;
    private WeakReference<LinearLayout> progress_list;
    private WeakReference<View> currentProgressView;
    private WeakReference<TextView> progress_result;
    private WeakReference<Button> button_close;

    public RealmSyncTask(String address, Context context)
    {

        this.context = new WeakReference<>(context);
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

        gsonBuilder.registerTypeAdapter(new TypeToken<RealmList<RealmString>>() {
        }.getType(), new RealmStringDeserializer());

        gsonBuilder.registerTypeAdapter(new TypeToken<RealmList<RealmInteger>>() {
        }.getType(), new RealmIntegerDeserializer());

        gson = gsonBuilder.create();

        AlertDialog progressDialog = getDialogProgressBar().create();
        progressDialog.setCancelable(false);

        button_close.get().setOnClickListener(v-> progressDialog.dismiss());
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... tasks) {
        int count = tasks.length;
        int count_latest = 0;
        int count_fail = 0;


        for (String currentTask : tasks) {
            publishProgress(currentTask, "", "동기화 중 입니다.");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String[] jsonResult = getAllData(currentTask);
            publishProgress(currentTask, jsonResult[0], jsonResult[1]);
            if (jsonResult[0].equals("fail")) {
                count_fail++;
            } else if (jsonResult[0].equals("latest")) {
                count_latest++;
            }


            if (isCancelled()) {
                break;
            }
        }

        return "동기화 완료: 전체:" + count + "개, 갱신: " +
                (count - count_fail - count_latest) + "개, 최신:" + count_latest +
                "개, 실패:" + count_fail + "개";
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
                +"&lut="+utStr.replace(" ","%20");
        Log.d(TAG,"get method: " + mJSONURLString );

        final String[] jsonResult = {"", ""};

        RequestQueue requestQueue = Volley.newRequestQueue(context.get());

        RequestFuture<JSONObject> requestFuture= RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, mJSONURLString,null,
                requestFuture, requestFuture ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue.add(jsonObjectRequest);


        try {
            JSONObject jsonObject = requestFuture.get(5, TimeUnit.SECONDS);

                Realm realm = Realm.getDefaultInstance();
                    jsonResult[0] = jsonObject.get("status").toString();
                    jsonResult[1] = jsonObject.get("message").toString();

                    String lut = jsonObject.get("time").toString();
                    realm.beginTransaction();
                    UpdateTime updateTime = realm.where(UpdateTime.class).equalTo(UpdateTime.FIELD_TABLE,pref_table).findFirst();
                    if (updateTime != null) {
                        updateTime.setLatestUpadateTime(lut);
                    }
                    realm.commitTransaction();

                if(jsonResult[0].equals("latest") || jsonResult[0].equals("fail") ) {

                    return jsonResult;
                }


               JSONArray jsonArray = jsonObject.getJSONArray("data");

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
                                    String json = jsonArray.get(i).toString();
                                    Heroes hero = gson.fromJson(json,Heroes.class);
                                    //realm.copyToRealm(hero);
                                    HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID,hero.getHeroNo()).findFirst();
                                    if(heroSim == null) {
                                        heroSim = new HeroSim(hero);
                                        realm.copyToRealm(heroSim);
                                    } else {
                                        realm.copyToRealm(hero);
                                        heroSim.updateHero(realm);
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
                                    String json = jsonArray.get(i).toString();
                                    Spec spec = gson.fromJson(json,Spec.class);
                                    spec.setSpecNameNoBlank(spec.getSpecName().replace(" ",""));
                                    realm.copyToRealm(spec);
                            }
                            Log.d(TAG, "SYNC Spec REALM COMPLETE!");
                            break;
                        case Branch.PREF_TABLE:
                            realm.delete(Branch.class);

                            for(int i = 0; i < jsonArray.length(); i++ ) {
                                    String json = jsonArray.get(i).toString();
                                    Branch branch = gson.fromJson(json,Branch.class);
                                    realm.copyToRealm(branch);
                            }

                            for(HeroSim heroSim : realm.where(HeroSim.class).findAll()) {
                                heroSim.updateBranch(realm);
                                heroSim.updateBasePower();
                            }


                            Log.d(TAG, "SYNC Branch REALM COMPLETE!");
                            break;
                        case Item.PREF_TABLE:
                            realm.delete(Item.class);
                            for(int i = 0; i < jsonArray.length(); i++ ) {
                                    String json = jsonArray.get(i).toString();
                                    Item item = gson.fromJson(json,Item.class);
                                    item.setItemNameNoBlank(item.getItemName().replace(" ",""));
                                    realm.copyToRealm(item);
                            }

                            for(ItemSim itemSim : realm.where(ItemSim.class).findAll()) {
                                itemSim.updateItem( realm );
                            }
                            Log.d(TAG, "SYNC Item REALM COMPLETE!");
                            break;
                        case ItemCate.PREF_TABLE:
                            realm.delete(ItemCate.class);
                            for(int i = 0; i < jsonArray.length(); i++ ) {
                                String json = jsonArray.get(i).toString();
                                ItemCate itemCate = gson.fromJson(json,ItemCate.class);
                                realm.copyToRealm(itemCate);
                            }
                            Log.d(TAG, "SYNC ItemCate REALM COMPLETE!");
                            break;
                        case ItemReinforcement.PREF_TABLE:
                            realm.delete(ItemReinforcement.class);
                            for(int i = 0; i < jsonArray.length(); i++ ) {
                                    String json = jsonArray.get(i).toString();
                                    ItemReinforcement itemReinforcement = gson.fromJson(json,ItemReinforcement.class);
                                    realm.copyToRealm(itemReinforcement);
                            }
                            Log.d(TAG, "SYNC ItemReinforcement REALM COMPLETE!");
                            break;
                        case NormalItem.PREF_TABLE:
                            realm.delete(NormalItem.class);
                            for(int i = 0; i < jsonArray.length(); i++) {
                                String json = jsonArray.get(i).toString();
                                NormalItem normalItem = gson.fromJson(json, NormalItem.class);
                                realm.copyToRealm(normalItem);
                            }
                            Log.d(TAG, "SYNC normalItem REALM COMPLETE!");
                            break;
                        case Relation.PREF_TABLE:
                            realm.delete((Relation.class));
                            realm.createAllFromJson(Relation.class, jsonArray);
                            Log.d(TAG, "SYNC Relation REALM COMPLETE!");
                            break;
                        case RelicCombination.PREF_TABLE:
                            realm.delete(RelicCombination.class);
                            for(int i = 0; i < jsonArray.length(); i++ ) {
                                    String json = jsonArray.get(i).toString();
                                    RelicCombination comb = gson.fromJson(json, RelicCombination.class);
                                    realm.copyToRealm(comb);
                            }
                            Log.d(TAG, "SYNC RelicCombination REALM COMPLETE!");
                            break;
                        case RelicPRFX.PREF_TABLE:
                            realm.delete(RelicPRFX.class);
                            for(int i = 0; i < jsonArray.length(); i++ ) {
                                String json = jsonArray.get(i).toString();
                                RelicPRFX relicPRFX = gson.fromJson(json, RelicPRFX.class);
                                realm.copyToRealm(relicPRFX);
                            }

                            for( RelicSim relicSim : realm.where(RelicSim.class).findAll()) {
                                relicSim.updatePrefix( realm );
                            }
                            Log.d(TAG, "SYNC  RelicPRFX REALM COMPLETE!");
                            break;
                        case RelicSFX.PREF_TABLE:
                            realm.delete(RelicSFX.class);
                            for(int i = 0; i < jsonArray.length(); i++ ) {
                                String json = jsonArray.get(i).toString();
                                RelicSFX relicSFX = gson.fromJson(json, RelicSFX.class);
                                realm.copyToRealm(relicSFX);
                            }

                            for( RelicSim relicSim : realm.where(RelicSim.class).findAll()) {
                                relicSim.updateSuffix( realm );
                            }
                            Log.d(TAG, "SYNC RelicSFX REALM COMPLETE!");
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



        } catch (InterruptedException | ExecutionException e) {
            Log.d(TAG,e.toString());

            jsonResult[0] = "fail";
            jsonResult[1] = "전송 정보 오류";


        } catch (TimeoutException e) {
            Log.d(TAG,e.toString());
            jsonResult[0] = "fail";
            jsonResult[1] = "시간 초과";

        } catch (JSONException | RealmPrimaryKeyConstraintException e) {
            Log.d(TAG,e.toString());
            jsonResult[0] = "fail";
            jsonResult[1] = "파싱 오류";
        }
        return jsonResult;
    }

    private AlertDialog.Builder getDialogProgressBar() {


        if (builder == null) {
            builder = new AlertDialog.Builder(context.get());
            View progress_dialog = View.inflate(context.get(), R.layout.dialog_progress, null);
            progress_list = new WeakReference<>(progress_dialog.findViewById(R.id.progress_list));
            scroll_progress_list = new WeakReference<>(progress_dialog.findViewById(R.id.scroll_progress_list));
            progress_result = new WeakReference<>(progress_dialog.findViewById(R.id.progress_result));
            button_close = new WeakReference<>(progress_dialog.findViewById(R.id.button_close_progress_dialog));
            button_close.get().setVisibility(View.INVISIBLE);
            builder.setView(progress_dialog);
        }
        return builder;
    }

    // After each task done
    @Override
    protected void onProgressUpdate(String... values){

        LayoutInflater inflater = LayoutInflater.from(context.get());
        View row_progress = inflater.inflate(R.layout.row_progress,progress_list.get(),false);
        ProgressBar progressBar = row_progress.findViewById(R.id.progressBar);
        TextView textView_progress = row_progress.findViewById(R.id.progressBar_text);
        textView_progress.setText(values[1]);
        TextView textView_db = row_progress.findViewById(R.id.progressTextView_table);
        textView_db.setText(values[0]);
        TextView textView_msg = row_progress.findViewById(R.id.progressTextView_message);
        textView_msg.setText(values[2]);

        LinearLayout progress_layout = progress_list.get();
        if(values[1].equals("")) {
            progress_layout.addView(row_progress);
            currentProgressView = new WeakReference<>(row_progress);
            ScrollView scroll_progress = scroll_progress_list.get();
            int innerHeight = progress_layout.getHeight();
            int scrollHeight = scroll_progress.getHeight();
            int scrollY = innerHeight - scrollHeight;
            if( scrollY > 0 ) {
                Log.d(TAG, "scroll to : " + scrollY );
                scroll_progress.smoothScrollTo( 0 , scrollY );
            }
        } else {
            progress_layout.removeView(currentProgressView.get());
            progressBar.setVisibility(View.INVISIBLE);
            progress_layout.addView(row_progress);
        }





    }

    // When all async task done
    @Override
    protected void onPostExecute(String result){
        button_close.get().setVisibility(View.VISIBLE);
        progress_result.get().setText(result);

        HeroesFixedRealmAdapter heroesFixedRealmAdapter = HeroesFixedRealmAdapter.getInstance();
        if(heroesFixedRealmAdapter != null ) {
            heroesFixedRealmAdapter.notifyDataSetChanged();
            Log.d(TAG, "notify to HeroesFixedRealmAdapter");
        }

        HeroesFloatingRealmAdapter heroesFloatingRealmAdapter = HeroesFloatingRealmAdapter.getInstance();
        if(heroesFloatingRealmAdapter != null ) {
            heroesFloatingRealmAdapter.notifyDataSetChanged();
            Log.d(TAG, "notify to HeroesFloatingRealmAdapter");
        }
    }

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

        private String getNullAsEmptyString(JsonElement jsonElement) {
            return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
        }
    }



    public class RealmIntegerDeserializer implements
            JsonDeserializer<RealmList<RealmInteger>> {

        @Override
        public RealmList<RealmInteger> deserialize(JsonElement json, Type typeOfT,
                                                  JsonDeserializationContext context) throws JsonParseException {

            RealmList<RealmInteger> realmIntegers = new RealmList<>();
            JsonArray stringList = json.getAsJsonArray();

            for (JsonElement integerElement : stringList) {
                realmIntegers.add(new RealmInteger(getNullAsZeroInt(integerElement)));
            }

            return realmIntegers;
        }

        private int getNullAsZeroInt(JsonElement jsonElement) {
            String valueStr = jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
            return NumberUtils.toInt(valueStr,0);
        }
    }




}
