package com.fang.starfang.ui.main.dialog;

import androidx.appcompat.widget.AppCompatTextView;

import com.fang.starfang.local.model.realm.Conversation;
import com.fang.starfang.ui.main.recycler.filter.ConversationFilterObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ConstraintDocBuilder {
    private static final String SUMMARY_TIME_FORMAT = "yyyy년 MM월 dd일";
    private  Realm realm;
    private AppCompatTextView summaryView;
    private ConversationFilterObject filterObject;

    private static ConstraintDocBuilder builderInstance = null;
    public static ConstraintDocBuilder getInstance() {
        return builderInstance;
    }

    public ConstraintDocBuilder(Realm realm, AppCompatTextView summaryView, ConversationFilterObject filterObject) {
        this.realm = realm;
        this.summaryView = summaryView;
        this.filterObject= filterObject;
        builderInstance = this;
    }

    public void build() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SUMMARY_TIME_FORMAT, Locale.KOREA);
        String[] cs_cats = filterObject.getSendCats();
        String[] cs_rooms = filterObject.getRooms();
        long time_after = filterObject.getTime_after();
        long time_before = filterObject.getTime_before();
        String[] cs_convs = filterObject.getConversations();
        String[] cs_packs = filterObject.getPackages();

        StringBuilder stringBuilder = new StringBuilder();
        if(cs_cats != null) {
            if (cs_cats.length > 0 && filterObject.isCatChecked()) {
                RealmQuery<Conversation> query_cats = realm.where(Conversation.class).alwaysFalse();
                for(String cs_cat : cs_cats) {
                    query_cats.or().contains(Conversation.FIELD_SENDCAT, cs_cat);
                }
                RealmResults<Conversation> realmResults_cats = query_cats.distinct(Conversation.FIELD_SENDCAT).findAll();
                stringBuilder.append(" ★작성자:  ").append(realmResults_cats.size()).append("명").append("\n");
                for(Conversation conversation : realmResults_cats) {
                    stringBuilder.append("  - ").append(conversation.getSendCat()).append("\n");
                }
                stringBuilder.append("\n");
            }
        }

        if(cs_rooms != null) {
            if (cs_rooms.length > 0 && filterObject.isRoomChecked()) {
                RealmQuery<Conversation> query_rooms = realm.where(Conversation.class).alwaysFalse();
                for(String cs_room : cs_rooms) {
                    query_rooms.or().contains(Conversation.FIELD_ROOM, cs_room);
                }
                RealmResults<Conversation> realmResults_rooms = query_rooms.distinct(Conversation.FIELD_ROOM).findAll();
                stringBuilder.append(" ★단톡방:  ").append(realmResults_rooms.size()).append("개").append("\n");
                for(Conversation conversation : realmResults_rooms) {
                    stringBuilder.append("  - ").append(conversation.getCatRoom()).append("\n");
                }
                stringBuilder.append("\n");
            }
        }

        if(( time_after > 0  || time_before > 0) && filterObject.isTimeChecked()) {
            stringBuilder.append("★기간:\n");
            if (time_after > 0) {
                stringBuilder.append("    ").append(simpleDateFormat.format(new Date(time_after))).append("\n");
            } else {
                stringBuilder.append("    2016년 10월 6일\n");
            }

            if (time_before > 0) {
                stringBuilder.append("    ~ ").append(simpleDateFormat.format(new Date(time_before))).append("\n");
            } else {
                stringBuilder.append("    ~ 2026년 10월 6일\n");
            }
            stringBuilder.append("\n");
        }

        if(cs_convs != null) {
            if(cs_convs.length>0 && filterObject.isConvChecked()) {
                stringBuilder.append("★단어 포함:").append("\n");
                for(String conv : cs_convs) {
                    stringBuilder.append("  - ").append(conv).append("\n");
                }
                stringBuilder.append("\n");
            }

        }

        if(cs_packs != null) {
            if(cs_packs.length>0) {
                stringBuilder.append("\n ★메신저:").append("\n");
                for(String pack : cs_packs) {
                    stringBuilder.append("  ").append(pack).append("\n");
                }
                stringBuilder.append("\n");
            }

        }

        String resultStr = stringBuilder.toString();
        resultStr = resultStr.equals("")? " 필터 설정 안됨..." : resultStr.substring(0,resultStr.length()-2);
        summaryView.setText(resultStr);
    }
}
