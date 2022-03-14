package com.fang.starfang.ui.conversation.adapter;

import android.util.Log;
import android.widget.Filter;

import com.fang.starfang.local.model.realm.Conversation;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ConversationFilter extends Filter {

    private static final String TAG = "FANG_CONV_FILTER";
    private static ConversationFilter instance = null;
    private final ConversationRealmAdapter adapter;

    public static ConversationFilter getInstance() {
        return instance;
    }

    ConversationFilter(ConversationRealmAdapter adapter) {
        super();
        this.adapter = adapter;
        instance = this;
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

        try (Realm realm = Realm.getDefaultInstance()) {

            ConversationFilterObject conversationFilterObject = ConversationFilterObject.getInstance();
            RealmQuery<Conversation> query = realm.where(Conversation.class).alwaysTrue();
            if (conversationFilterObject.isCatChecked()) {
                makeQueryGroup(query, conversationFilterObject.getSendCats(), Conversation.FIELD_SENDCAT, true, true);
            }

            if (conversationFilterObject.isRoomChecked()) {
                makeQueryGroup(query, conversationFilterObject.getRooms(), Conversation.FIELD_ROOM, true, false);
            }
            makeQueryGroup(query, conversationFilterObject.getPackages(), Conversation.FIELD_PACKAGE, true, false);

            if (conversationFilterObject.isConvChecked()) {
                makeQueryGroup(query, conversationFilterObject.getConversations(), Conversation.FIELD_CONVERSATION, false, false);
            }

            if (conversationFilterObject.isTimeChecked()) {
                long time_before = conversationFilterObject.getTime_before();
                try {
                    long time_before_day_after = addAndCheck(time_before, (long) 24 * 60 * 60 * 1000 - 1, "");

                    query = (time_before < 0) ? query :
                            query.and().lessThanOrEqualTo(Conversation.FIELD_TIME_VALUE, time_before_day_after);
                    //Log.d(TAG, "time_before: " + time_before_day_after);
                } catch (ArithmeticException e) {
                    Log.e(TAG, Log.getStackTraceString( e ));
                }


                long time_after = conversationFilterObject.getTime_after();
                query = (time_after < 0) ? query :
                        query.and().greaterThanOrEqualTo(Conversation.FIELD_TIME_VALUE, time_after);
            }

            RealmResults<Conversation> realmResults = query.findAll();
            adapter.updateData(realmResults);
        } catch( RuntimeException e ) {
            Log.e(TAG, Log.getStackTraceString( e ));
        }
    }

    private long addAndCheck(long a, long b, String msg) {
        long ret;
        if (a > b) {
            // use symmetry to reduce boundary cases
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
                    // opposite sign addition is always safe
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

    private void makeQueryGroup(RealmQuery<Conversation> query, String[] cs_group, String column,boolean match, boolean isCatRequest) {

        if(cs_group == null) {
            //Log.d(TAG,column + " filter : null");
            return;
        }

        if(cs_group.length == 0) {
            return;
        }
        query.and().beginGroup();
        for(String cs : cs_group) {
            query = match ? query.equalTo(column,cs) : query.contains(column,cs, Case.INSENSITIVE);
            query.or();
        }
        query = isCatRequest? query.isNull(column).or() : query;
        query.alwaysFalse().endGroup();
    }

}