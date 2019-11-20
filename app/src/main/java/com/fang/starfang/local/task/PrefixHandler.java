package com.fang.starfang.local.task;

import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.NotificationListener;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.model.realm.RoomCommand;
import com.fang.starfang.util.KakaoReplier;

import java.lang.ref.WeakReference;
import java.util.StringTokenizer;

import io.realm.Realm;

public class PrefixHandler extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> context;
    private String sendCat;
    private String catRoom;
    private StatusBarNotification sbn;
    private boolean isLocalRequest;
    private String botName;

    private static final String TAG = "FANG_PRFX_HANDLER";
    private static final String COMMAND_START = "start";
    private static final String COMMAND_STOP = "stop";
    private static final String COMMAND_START_KOR = "시작";
    private static final String COMMAND_STOP_KOR = "중지";


    public PrefixHandler(Context context, String sendCat, String catRoom, StatusBarNotification sbn, boolean isLocalRequest, String botName) {
        this.context = new WeakReference<>(context);
        this.sendCat = sendCat;
        this.catRoom = catRoom;
        this.sbn = sbn;
        this.isLocalRequest = isLocalRequest;
        this.botName = botName;
    }

    @Override
    protected String doInBackground(String... strings) {
        try(Realm realm = Realm.getDefaultInstance()) {
            selectRequest(strings[0], realm);
        } catch ( RuntimeException ignore ) {

        }
        return null;
    }

    private void selectRequest(String request, Realm realm ) {

        String result = null;

            if (request != null) {

                // 멍멍시작냥
                if (request.substring(request.length() - 1).equals(NotificationListener.getCommandCat()) && request.length() > 2) {

                    request = request.substring(0, request.length() - 1).trim(); // 냥 제거> 멍멍시작


                    if(request.length() > botName.length() + 1) {
                        // 멍멍시작
                        if(request.substring(0,botName.length()).equals(botName)) {
                            String command = request.substring(request.length()-2);

                            switch (command) {
                                case COMMAND_START_KOR:
                                    result = handleByRoomCommandStart(realm);
                                    break;
                                case COMMAND_STOP_KOR:
                                    result = handleByRoomCommandStop(realm);
                                    break;
                                default:
                            }

                        }
                    }

                    if( result == null ) {
                        if(checkStop(realm)) {
                            return;
                        }
                        result = new LocalDataHandlerCat(context.get(), sendCat, catRoom).handleRequest(request,realm);

                    }

                } else if (request.substring(request.length() - 1).equals(NotificationListener.getCommandDog()) && request.length() > 2) {

                    if(checkStop(realm)) {
                        return;
                    }
                    result = new LocalDataHandlerDog().handleRequest(request, realm);
                }
            }

            commitResult(result, realm);


    }

    private String handleByRoomCommandStart( Realm realm) {
        if(catRoom == null) {
            return "시작: 단톡방에서만 사용가능한 명령입니다.";
        }
        RoomCommand roomCommand = realm.where(RoomCommand.class).equalTo(RoomCommand.FIELD_ROOM, catRoom).findFirst();
        if(roomCommand == null) {
            roomCommand = new RoomCommand();
            roomCommand.setRoomName(catRoom);
            roomCommand.setStatus(COMMAND_START);
            realm.beginTransaction();
            realm.copyToRealm(roomCommand);
            realm.commitTransaction();
        } else {
            String status = roomCommand.getStatus();
            if(!status.equals(COMMAND_START)) {
                realm.beginTransaction();
                roomCommand.setStatus(COMMAND_START);
                realm.commitTransaction();
            }
        }


        return catRoom+ " 냥봇 시작";
    }

    private String handleByRoomCommandStop(Realm realm) {
        if(catRoom == null) {
            return "중지: 단톡방에서만 사용가능한 명령입니다.";
        }

            RoomCommand roomCommand = realm.where(RoomCommand.class).equalTo(RoomCommand.FIELD_ROOM, catRoom).findFirst();
            if(roomCommand == null) {
                roomCommand = new RoomCommand();
                roomCommand.setRoomName(catRoom);
                roomCommand.setStatus(COMMAND_STOP);
                realm.beginTransaction();
                realm.copyToRealm(roomCommand);
                realm.commitTransaction();

            } else {
                String status = roomCommand.getStatus();
                if(!status.equals(COMMAND_STOP)) {
                    realm.beginTransaction();
                    roomCommand.setStatus(COMMAND_STOP);
                    realm.commitTransaction();
                }
            }

        return catRoom+ " 냥봇 중지";




    }

    private void commitResult(String result, Realm realm) {
        if (isLocalRequest) {
            result = result == null ? "검색결과가 없다옹" : result;
            StringTokenizer st = new StringTokenizer(result,"," );
            realm.beginTransaction();
            while( st.hasMoreTokens()) {
                String tmpRes = st.nextToken();
                if( tmpRes.substring(0,2).equals("\r\n")) {
                    tmpRes = tmpRes.substring(2);
                }
                try {
                    if( tmpRes.substring(tmpRes.length()-2).equals("\r\n")) {
                        tmpRes = tmpRes.substring(0,tmpRes.length()-2);
                    }
                    Conversation conversationRep = new Conversation( null, null,null,NotificationListener.PACKAGE_STARFANG,tmpRes);
                    realm.copyToRealm(conversationRep);

                } catch (NullPointerException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException ignored) {
                }
            }
            realm.commitTransaction();

        } else {
            if (result != null) {
                KakaoReplier replier = new KakaoReplier(context.get(), sendCat, sbn);
                replier.execute(result, botName);
            }
        }
        realm.close();
    }

    private boolean checkStop(Realm realm) {
        if(catRoom!=null) {
            RoomCommand roomCommand = realm.where(RoomCommand.class).equalTo(RoomCommand.FIELD_ROOM, catRoom).findFirst();
            if (roomCommand != null) {
                if (roomCommand.getStatus().equals("stop")) {
                    Log.d(TAG,catRoom + "중지됨...");
                    return true;
                }
            }
        }
        return false;
    }




}
