package com.fang.starfang.service;

import android.app.Notification;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.fang.starfang.FangConstant;
import com.fang.starfang.local.model.realm.Conversation;

import java.util.Date;

import io.realm.Realm;

public class FangcatReceiver extends BroadcastReceiver {

    private static final String TAG = "FANG_RECEIVE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle information = intent.getBundleExtra(FangConstant.EXTRA_INFORMATION);
        if( information != null ) {
            final String sendCat = information.getString(Notification.EXTRA_TITLE,"");
            final String catRoom = information.getString(Notification.EXTRA_SUB_TEXT, "");

            CharSequence msgChars = getMessageText(intent);
            if (!TextUtils.isEmpty(msgChars)) {

                try (Realm realm = Realm.getDefaultInstance()) {
                    realm.executeTransactionAsync(bgRealm -> {

                        Conversation conversation = bgRealm.createObject(Conversation.class);
                        conversation.setReplyID(null);
                        conversation.setConversation(msgChars.toString());
                        conversation.setPackageName(FangConstant.PACKAGE_STARFANG);
                        conversation.setCatRoom(catRoom);
                        conversation.setSendCat(sendCat);
                        Date date = new Date();
                        conversation.setTimeValue(date.getTime());
                    }, () -> {
                        Log.d(TAG, "receive success");
                    }, error -> {
                        Log.e(TAG,Log.getStackTraceString(error));
                    });
                } catch (RuntimeException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }
            }
        }
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent( intent );
        if( remoteInput != null ) {
            return remoteInput.getCharSequence(FangConstant.REPLY_KEY_LOCAL);

        }
        return null;
    }
}
