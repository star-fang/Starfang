package com.fang.starfang.view.recycler;

import android.util.Log;
import android.widget.Filter;

import com.fang.starfang.local.model.realm.Conversation;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;

public class ConversationFilter extends Filter {

    private static final String TAG = "FANG_FILTER";
    private static ConversationFilterObject conversationFilterObject = null;
    private final ConversationRecyclerAdapter adapter;
    private Realm realm;



    public ConversationFilter(ConversationRecyclerAdapter adapter, Realm realm) {
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
        try {
            query = (conversationFilterObject.getTime_before() < 0) ? query :
                    query.and().lessThanOrEqualTo(Conversation.FIELD_TIME, conversationFilterObject.getTime_before());
        } catch( IllegalArgumentException ignored) {

        }

        try {
            query = (conversationFilterObject.getTime_after() < 0) ? query :
                    query.and().greaterThanOrEqualTo(Conversation.FIELD_TIME, conversationFilterObject.getTime_after());
        } catch( IllegalArgumentException ignored) {

    }


        adapter.updateData(query.findAll());
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
        private String[] sendCats;
        private String[] rooms;
        private long time_before;
        private long time_after;
        private String[] packages;
        private String[] conversations;

        private String[] getSendCats() {
            return sendCats;
        }

        private String[] getRooms() {
            return rooms;
        }

        private long getTime_before() {
            return time_before;
        }

        private long getTime_after() {
            return time_after;
        }

        private String[] getPackages() {
            return packages;
        }

        private String[] getConversations() {
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