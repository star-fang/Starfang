package com.fang.starfang.view.recycler;

import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConversationFilterObject {
    private final static String TAG = "FANG_FILTER_OBJECT";
    private Set<String> sendCats;
    private String[] rooms = null;
    private long time_before = -1;
    private long time_after = -1;
    private String[] packages = null;
    private String[] conversations = null;
    private static ConversationFilterObject conversationFilterObject = null;

    private ConversationFilterObject() {
        this.sendCats = new HashSet<>();
        Log.d(TAG,"ConversationFilterObject constructed");
    }

    public static ConversationFilterObject getInstance() {
        return conversationFilterObject == null ? conversationFilterObject = new ConversationFilterObject() : conversationFilterObject;
    }

    public String[] getSendCats() {
        return sendCats == null ? null : sendCats.toArray(new String[0]);
    }

    public String[] getRooms() {
        return rooms;
    }

    public long getTime_before() {
        return time_before;
    }

    public long getTime_after() {
        return time_after;
    }

    public String[] getPackages() {
        return packages;
    }

    public String[] getConversations() {
        return conversations;
    }

    public void addSendCat(String sendCat) {
        this.sendCats.add(sendCat);
    }

    public void removeSendCat(String sendCat) {
        this.sendCats.remove(sendCat);
    }

    public boolean catIsChecked(String sendCat) {
        return sendCats.contains(sendCat);
    }

    public int getSendCatCount() {

        return (sendCats == null)? 0 : sendCats.size();
    }

    public String getSendCatJoinString(int limit) {

        if( sendCats == null) {
            return  "";
        }

        int size = sendCats.size();
        if( size > 0 ) {

            StringBuilder resultSetJoin = new StringBuilder();
            if(size > limit) {
                resultSetJoin.append(  sendCats.iterator().next() ).append("님 외 ").append(size - 1).append("명");
            } else {
                for( Iterator<String> iterator = sendCats.iterator(); iterator.hasNext(); ) {
                    resultSetJoin.append(iterator.next());
                    resultSetJoin.append(iterator.hasNext()? ", " : "");
                }
            }

            return resultSetJoin.toString();

        } else {
            return "";
        }


    }

    public void setRooms(String[] rooms) {
        this.rooms = rooms;
    }

    public void setTime_before(long time_before) {
        this.time_before = time_before;
    }

    public void setTime_after(long time_after) {
        this.time_after = time_after;
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
    }

    public void setConversations(String[] conversations) {
        this.conversations = conversations;
    }

}
