package com.fang.starfang.view.recycler;

import android.util.Log;
import android.widget.Filter;


import com.fang.starfang.local.model.realm.Conversation;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ConversationFilter extends Filter {

    private static final String TAG = "FANG_FILTER";
    private static ConversationFilterObject conversationFilterObject = null;
    private final ConversationRecyclerAdapter adapter;
    private Realm realm;


    protected ConversationFilter(ConversationRecyclerAdapter adapter, Realm realm) {
        super();
        this.adapter = adapter;
        this.realm = realm;
        //conversationFilterObject = new ConversationFilterObject();
    }

    public ConversationFilterObject getConversationFilterObject() {
        return conversationFilterObject == null ? conversationFilterObject = new ConversationFilterObject() : conversationFilterObject;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        return new FilterResults();
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        filterResults(constraint.toString());
    }

    private void filterResults( String cs_on_off) {
        if(cs_on_off == null || cs_on_off.equals("off")) {
            return;
        }

        RealmQuery<Conversation> query = realm.where(Conversation.class).alwaysTrue();
        makeQueryGroup(query, conversationFilterObject.getSendCats(), Conversation.FIELD_SENDCAT);
        makeQueryGroup(query, conversationFilterObject.getRooms(),Conversation.FIELD_ROOM);
        makeQueryGroup(query,conversationFilterObject.getPackages(),Conversation.FIELD_PACKAGE);
        makeQueryGroup(query,conversationFilterObject.getConversations(),Conversation.FIELD_CONVERSATION);
        long time_before = conversationFilterObject.getTime_before();
        try {
            long time_before_day_after = addAndCheck(time_before, (long) 24 * 60 * 60 * 1000 - 1, "");

            query = (time_before < 0) ? query :
                    query.and().lessThanOrEqualTo(Conversation.FIELD_TIME_VALUE, time_before_day_after);
            //Log.d(TAG, "time_before: " + time_before_day_after);
        } catch( ArithmeticException ignored ) {

        }


            long time_after = conversationFilterObject.getTime_after();
            query = (time_after < 0) ? query :
                    query.and().greaterThanOrEqualTo(Conversation.FIELD_TIME_VALUE, time_after);
            //Log.d(TAG,"time_after: "+ time_after);

        RealmResults<Conversation> realmResults = query.findAll();
        adapter.updateData(realmResults);
    }

    private long addAndCheck(long a, long b, String msg) {
        long ret;
        if (a > b) {
            // use symmetry to reduce boundry cases
            ret = addAndCheck(b, a, msg);
        } else {
            // assert a <= b
            if (a < 0) {
                if (b < 0) {
                    // check for negative overflow
                    if (Long.MIN_VALUE - b <= a) {
                        ret = a + b;
                    } else {
                        throw new ArithmeticException(msg);
                    }
                } else {
                    // oppisite sign addition is always safe
                    ret = a + b;
                }
            } else {
                // assert a >= 0
                // assert b >= 0

                // check for positive overflow
                if (a <= Long.MAX_VALUE - b) {
                    ret = a + b;
                } else {
                    throw new ArithmeticException(msg);
                }
            }
        }
        return ret;
    }

    private void makeQueryGroup(RealmQuery<Conversation> query, String[] cs_group, String column) {

        if(cs_group == null) {
            //Log.d(TAG,column + " filter : null");
            return;
        }

        if(cs_group.length == 0) {
            return;
        }
        query.and().beginGroup();
        for(String cs : cs_group) {
            query.contains(column,cs,Case.INSENSITIVE).or();
        }
        query.alwaysFalse().endGroup();
    }




    public class ConversationFilterObject {
        private String[] sendCats = null;
        private String[] rooms = null;
        private long time_before = -1;
        private long time_after = -1;
        private String[] packages = null;
        private String[] conversations = null;

        public String[] getSendCats() {
            return sendCats;
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

        public void setSendCats(String[] sendCats) {
            this.sendCats = sendCats;
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




}