package com.fang.starfang.local.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.local.model.realm.RoomCommand;
import com.fang.starfang.util.NotificationReplier;

import java.lang.ref.WeakReference;

import io.realm.Realm;

public class PrefixHandler extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> context;
    private String sendCat;
    private String catRoom;
    private StatusBarNotification sbn;
    private boolean isLocalRequest;
    private String botName;
    private boolean record;

    private static final String TAG = "FANG_PRFX_HANDLER";
    private static final String COMMAND_START = "start";
    private static final String COMMAND_STOP = "stop";
    private static final String COMMAND_START_KOR = "시작";
    private static final String COMMAND_STOP_KOR = "정지";
    private static final String COMMAND_CHECK_COUNT = "살았";
    private static final String COMMAND_RESTART_COUNT = "죽었";
    private static final String COMMAND_NAME = "이름";


    public PrefixHandler(Context context,
                         String sendCat, String catRoom,
                         StatusBarNotification sbn, boolean isLocalRequest,
                         String botName, boolean record ) {
        this.context = new WeakReference<>(context);
        this.sendCat = sendCat;
        this.catRoom = catRoom;
        this.sbn = sbn;
        this.isLocalRequest = isLocalRequest;
        this.botName = botName;
        this.record = record;
    }

    @Override
    protected String doInBackground(String... strings) {
        String request = strings[0];
        try(Realm realm = Realm.getDefaultInstance()) {
            if( record || isLocalRequest ) {
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
            selectRequest(request, realm);


        } catch ( RuntimeException e ) {
            e.printStackTrace();
        }

        return null;
    }


    private void selectRequest(String request, Realm realm ) throws RuntimeException {

        String result = null;

            if (request != null) {

                // 멍멍시작냥
                if (request.substring(request.length() - 1).equals("냥") && request.length() > 2) {

                    request = request.substring(0, request.length() - 1).trim(); // 냥 제거> 멍멍시작


                    if(request.length() > botName.length() + 1) {
                        // 멍멍시작
                        if(request.substring(0,botName.length()).equals(botName)) {
                            String command = request.substring(request.length()-2);

                            switch (command) {
                                case COMMAND_START_KOR:
                                    handleByRoomCommandStart(realm);
                                    return;
                                case COMMAND_STOP_KOR:
                                    handleByRoomCommandStop(realm);
                                    return;
                                case COMMAND_CHECK_COUNT:
                                case COMMAND_RESTART_COUNT:
                                    handleByCommandCount( command );
                                    return;
                                default:
                            }

                        }
                    } else if( request.length() == 2) {
                        switch (request) {
                            case COMMAND_CHECK_COUNT:
                            case COMMAND_RESTART_COUNT:
                                handleByCommandCount( request );
                                return;
                            case COMMAND_NAME:
                                commitResult(botName);
                                return;
                                default:
                        }

                    }

                        if(checkStop(realm)) {
                            return;
                        }
                        result = new LocalDataHandlerCat(context.get(), catRoom).handleRequest(request,realm);


                } else if (request.substring(request.length() - 1).equals("멍") && request.length() > 2) {

                    if(checkStop(realm)) {
                        return;
                    }
                    result = new LocalDataHandlerDog().handleRequest(request);
                }
            }

            commitResult(result);


    }

    private void handleByCommandCount( String command ) {

        Resources resources = context.get().getResources();
        SharedPreferences sharedPref = context.get().getSharedPreferences(
                resources.getString(R.string.shared_preference_store),
                Context.MODE_PRIVATE);

        int restartCount = sharedPref.getInt(
                resources.getString(R.string.restart_count)
                , 0 );

        int startCount = sharedPref.getInt(
                resources.getString(R.string.start_count)
                , 0 );

        if( command.equals( COMMAND_CHECK_COUNT ) ) {
            commitResult("그렇다냥...");
        } else {
            commitResult("살아 있다냥 ㅎㅅㅎ \r\n => 시작: " + startCount + "번 // 죽음: " + restartCount + "번");
        }
    }

    private void handleByRoomCommandStart( Realm realm ) throws RuntimeException {
        if(catRoom == null) {
            commitResult( COMMAND_START_KOR + ": 단톡방에서만 사용가능한 명령입니다." );
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
        if(catRoom == null) {
            commitResult( COMMAND_STOP_KOR + ": 단톡방에서만 사용가능한 명령입니다." );
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
            if (result != null) {
                NotificationReplier replier = new NotificationReplier(context.get(), sendCat, catRoom, sbn, isLocalRequest, record);
                replier.execute(result, botName);
            }

    }

    private boolean checkStop(Realm realm) {
        if(catRoom!=null) {
            RoomCommand roomCommand = realm.where(RoomCommand.class).equalTo(RoomCommand.FIELD_ROOM, catRoom).findFirst();
            if (roomCommand != null) {
                if (roomCommand.getStatus().equals(COMMAND_STOP)) {
                    Log.d(TAG,catRoom + " stopped");
                    return true;
                }
            }
        }
        return false;
    }




}
