package com.fang.starfang.view.recycler.Filter;

import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConversationFilterObject {
    private final static String TAG = "FANG_FILTER_OBJECT";
    private Set<String> sendCats;
    private Set<String> rooms;
    private long time_before = -1;
    private long time_after = -1;
    private Set<String> conversations;
    private String[] packages = null;
    private static ConversationFilterObject conversationFilterObject = null;
    private boolean catIsChecked = false;
    private boolean roomIsChecked = false;
    private boolean timeIsChecked = false;
    private boolean convIsChecked = false;

    private ConversationFilterObject() {
        this.sendCats = new HashSet<>();
        this.rooms = new HashSet<>();
        this.conversations = new HashSet<>();
        Log.d(TAG,"ConversationFilterObject constructed");
    }

    public static ConversationFilterObject getInstance() {
        return conversationFilterObject == null ? conversationFilterObject = new ConversationFilterObject() : conversationFilterObject;
    }

    public String[] getSendCats() {
        return sendCats == null ? null : sendCats.toArray(new String[0]);
    }

    public String[] getRooms() {
        return rooms == null ? null : rooms.toArray(new String[0]);
    }

    public String[] getConversations(){
        return conversations == null ? null : conversations.toArray(new String[0]);
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



    public void addSendCat(String sendCat) {
        this.sendCats.add(sendCat);
    }

    public void addRoom(String room) {
        this.rooms.add(room);
    }

    public void removeSendCat(String sendCat) {
        this.sendCats.remove(sendCat);
    }

    public void removeRoom(String room) {
        this.rooms.remove(room);
    }

    public boolean catIsAdded(String sendCat) {
        return sendCats.contains(sendCat);
    }

    public boolean roomIsAdded(String room) {
        return rooms.contains(room);
    }

    public int getSendCatCount() {
        return (sendCats == null)? 0 : sendCats.size();
    }

    public int getRoomCount() {
        return (rooms == null)? 0 : rooms.size();
    }

    public int getConvCount() { return (conversations == null)? 0 : conversations.size(); }

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

    public String getRoomJoinString(int limit) {
        if( rooms == null) {
            return  "";
        }
        int size = rooms.size();
        if( size > 0 ) {

            StringBuilder resultSetJoin = new StringBuilder();
            if(size > limit) {
                resultSetJoin.append(  rooms.iterator().next() ).append(" 외 ").append(size - 1).append("개 단톡방");
            } else {
                for( Iterator<String> iterator = rooms.iterator(); iterator.hasNext(); ) {
                    resultSetJoin.append(iterator.next());
                    resultSetJoin.append(iterator.hasNext()? ", " : "");
                }
            }
            return resultSetJoin.toString();
        } else {
            return "";
        }
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
        this.conversations.clear();
        for( String conv : conversations) {
            conv = conv.replace(",","").trim();
            this.conversations.add(conv);
        }
    }

    public void setCheckCat( boolean check ) {
        this.catIsChecked =  check;
    }

    public boolean isCatChecked() {
        return catIsChecked;
    }

    public void setCheckRoom( boolean check ) {
        this.roomIsChecked = check;
    }

    public boolean isRoomChecked() {
        return roomIsChecked;
    }

    public void setCheckTime( boolean check ) {
        this.timeIsChecked =  check;
    }

    public boolean isTimeChecked() {
        return timeIsChecked;
    }

    public void setCheckConv( boolean check ) {
        this.convIsChecked =  check;
    }

    public boolean isConvChecked() {
        return convIsChecked;
    }

}
