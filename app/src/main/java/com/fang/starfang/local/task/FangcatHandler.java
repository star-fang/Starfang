package com.fang.starfang.local.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.FangConstant;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.model.realm.RoomCommand;
import com.fang.starfang.util.NotificationReplier;

import java.lang.ref.WeakReference;

import io.realm.Realm;

public class FangcatHandler extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> context;
    private String sendCat;
    private String catRoom;
    private StatusBarNotification sbn;
    private boolean debug;
    private String botName;
    private boolean record;

    private static final String TAG = "FANG_CAT";
    private static final String COMMAND_START = "start";
    private static final String COMMAND_STOP = "stop";
    private static final String COMMAND_START_KOR = "시작";
    private static final String COMMAND_STOP_KOR = "정지";
    private static final String COMMAND_CHECK_COUNT = "살았";
    private static final String COMMAND_RESTART_COUNT = "죽었";
    private static final String COMMAND_NAME = "이름";


    public FangcatHandler(Context context,
                          String sendCat, String catRoom,
                          StatusBarNotification sbn, boolean isLocalRequest,
                          String botName, boolean record) {
        this.context = new WeakReference<>(context);
        this.sendCat = sendCat;
        this.catRoom = catRoom;
        this.sbn = sbn;
        this.debug = isLocalRequest;
        this.botName = botName;
        this.record = record;
    }

    @Override
    protected String doInBackground(String... strings) {
        String request = strings[0];
        if (request != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                if (record || debug) {
                    realm.executeTransaction(bgRealm -> {
                        Conversation conversation = bgRealm.createObject(Conversation.class);
                        conversation.setSendCat(sendCat);
                        conversation.setCatRoom(catRoom);
                        conversation.setPackageName(sbn.getPackageName());
                        conversation.setReplyID(sbn.getTag());
                        conversation.setConversation(request);
                        conversation.setTimeValue(sbn.getNotification().when);
                    });
                }
                processRequest(request, realm);


            } catch (RuntimeException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }
        }

        return null;
    }


    private void processRequest(String request, Realm realm) throws RuntimeException {

        long startTime = System.currentTimeMillis();

        String result = null;

        // 멍멍시작냥
        if (request.substring(request.length() - 1).equals("냥") && request.length() > 2) {

            request = request.substring(0, request.length() - 1).trim(); // 냥 제거> 멍멍시작


            if (request.length() > botName.length() + 1) {
                // 멍멍시작
                if (request.substring(0, botName.length()).equals(botName)) {
                    String command = request.substring(request.length() - 2);

                    switch (command) {
                        case COMMAND_START_KOR:
                            handleByRoomCommandStart(realm);
                            return;
                        case COMMAND_STOP_KOR:
                            handleByRoomCommandStop(realm);
                            return;
                        case COMMAND_CHECK_COUNT:
                        case COMMAND_RESTART_COUNT:
                            handleByCommandCount(command);
                            return;
                        default:
                    }

                }
            } else if (request.length() == 2) {
                switch (request) {
                    case COMMAND_CHECK_COUNT:
                    case COMMAND_RESTART_COUNT:
                        handleByCommandCount(request);
                        return;
                    case COMMAND_NAME:
                        commitResult(botName);
                        return;
                    default:
                }

            }

            if (checkStop(realm)) {
                return;
            }
            result = new CatLambda(context.get(), catRoom).handleRequest(request, realm);


        } else if (request.substring(request.length() - 1).equals("멍") && request.length() > 2) {

            if (checkStop(realm)) {
                return;
            }
            result = new DogLambda().handleRequest(request);
        }

        if (result != null) {
            if( debug ) {
                long executionTime = System.currentTimeMillis() - startTime;
                result += ",요청: " + request + "\r\n처리 시간: " + executionTime + "ms";
            }
            commitResult(result);
        }
    }

    private void handleByCommandCount(String command) {

        SharedPreferences sharedPref = context.get().getSharedPreferences(
                FangConstant.SHARED_PREF_STORE,
                Context.MODE_PRIVATE);

        int restartCount = sharedPref.getInt(
                FangConstant.BOT_RESTART_COUNT_KEY
                , 0);

        int startCount = sharedPref.getInt(
                FangConstant.BOT_START_COUNT_KEY
                , 0);

        if (command.equals(COMMAND_CHECK_COUNT)) {
            commitResult("그렇다냥...");
        } else {
            commitResult("살아 있다냥 ㅎㅅㅎ \r\n => 시작: " + startCount + "번 // 죽음: " + restartCount + "번");
        }
    }

    private void handleByRoomCommandStart(Realm realm) throws RuntimeException {
        if (catRoom == null) {
            commitResult(COMMAND_START_KOR + ": 단톡방에서만 사용가능한 명령입니다.");
        } else {
            realm.executeTransaction(bgRealm -> {
                RoomCommand roomCommand = bgRealm.where(RoomCommand.class).equalTo(RoomCommand.FIELD_ROOM, catRoom).findFirst();
                if (roomCommand == null) {
                    RoomCommand newCommand = bgRealm.createObject(RoomCommand.class);
                    newCommand.setRoomName(catRoom);
                    newCommand.setStatus(COMMAND_START);
                } else {
                    String status = roomCommand.getStatus();
                    if (!status.equals(COMMAND_START)) {
                        roomCommand.setStatus(COMMAND_START);
                    }
                }

            });
            commitResult(catRoom + " 냥봇 시작");
        }

    }

    private void handleByRoomCommandStop(Realm realm) throws RuntimeException {
        if (catRoom == null) {
            commitResult(COMMAND_STOP_KOR + ": 단톡방에서만 사용가능한 명령입니다.");
        } else {
            realm.executeTransaction(bgRealm -> {
                RoomCommand roomCommand = bgRealm.where(RoomCommand.class).equalTo(RoomCommand.FIELD_ROOM, catRoom).findFirst();
                if (roomCommand == null) {
                    RoomCommand newCommand = bgRealm.createObject(RoomCommand.class);
                    newCommand.setRoomName(catRoom);
                    newCommand.setStatus(COMMAND_STOP);
                } else {
                    String status = roomCommand.getStatus();
                    if (!status.equals(COMMAND_STOP)) {
                        roomCommand.setStatus(COMMAND_STOP);
                    }
                }
            });
            commitResult(catRoom + " 냥봇 정지");
        }
    }

    private void commitResult(String result) throws RuntimeException {
        NotificationReplier replier = new NotificationReplier(context.get(), sendCat, catRoom, sbn, debug, record);
        replier.execute(result, botName);
    }

    private boolean checkStop(Realm realm) {
        if (catRoom != null) {
            RoomCommand roomCommand = realm.where(RoomCommand.class).equalTo(RoomCommand.FIELD_ROOM, catRoom).findFirst();
            if (roomCommand != null) {
                if (roomCommand.getStatus().equals(COMMAND_STOP)) {
                    Log.d(TAG, catRoom + " stopped");
                    return true;
                }
            }
        }
        return false;
    }


}
