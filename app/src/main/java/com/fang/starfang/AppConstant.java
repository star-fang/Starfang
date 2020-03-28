package com.fang.starfang;

public class AppConstant {
    public static final int REQ_CODE_PICK_ITEM_DIALOG_FRAGMENT = 0;
    public static final int REQ_CODE_REINFORCE_ITEM_DIALOG_FRAGMENT = 1;

    public static final int RESULT_CODE_SUCCESS_ADD_ITEM = 0;
    public static final int RESULT_CODE_SUCCESS_ADD_HERO = 1;
    public static final int RESULT_CODE_SUCCESS_ADD_RELIC = 2;

    public static final int RESULT_CODE_SUCCESS_MODIFY_ITEM = 3;
    public static final int RESULT_CODE_SUCCESS_MODIFY_HERO = 4;
    public static final int RESULT_CODE_SUCCESS_MODIFY_RELIC = 5;

    public static final int ITEM_MAIN_CATEGORY_CODE_WEAPON = 0;
    public static final int ITEM_MAIN_CATEGORY_CODE_ARMOR = 1;
    public static final int ITEM_MAIN_CATEGORY_CODE_AID = 2;

    public static final int NOTIFY_TYPE_ITEM = 0;
    public static final int NOTIFY_TYPE_HERO = 1;
    public static final int NOTIFY_TYPE_RELIC= 2;
    public static final int NOTIFY_TYPE_ITEM_HERO = 3;
    public static final int NOTIFY_TYPE_HERO_RELIC= 4;

    public static final int NOTIFY_TYPE_CONVERSATION = 6;

    public static final int GUARDIAN_INIT_VALUE = 0;
    public static final int GRADE_INIT_VALUE = 3;

    public static final String INTENT_KEY_ITEM_NAME = "itemName";
    public static final String INTENT_KEY_ITEM_ID = "itemID";
    public static final String INTENT_KEY_ITEM_REINFORCE = "itemReinforce";
    public static final String INTENT_KEY_ITEM_CATE_SUB = "itemSubCate";
    public static final String INTENT_KEY_ITEM_CATE_MAIN = "itemMainCate";
    public static final String INTENT_KEY_SUFFIX_ID = "suffixID";

    public static final String READ_SHEET_MODE_INFO = "readSheetInfo";
    public static final String READ_SHEET_MODE_DOWN = "readSheetDown";
    public static final String READ_SHEET_MODE_UP = "readSheetUp";

    public static final String INTENT_KEY_HERO_ID = "heroID";

    public static final String INTENT_KEY_RELIC_POSITION = "relicPosition";
    public static final String INTENT_KEY_RELIC_SLOT = "relicSlot";

    public static final String WEAPON_KOR = "무기";
    public static final String ARMOR_KOR = "방어구";
    public static final String AID_KOR = "보조구";

    public static final String ITEM_GRADE_NO_REINFORCE = "연의";

    public static final String CONSTRAINT_SEPARATOR = ",";

    static final String INTENT_KEY_SERVICE_STATUS = "status";
    static final String INTENT_KEY_SERVICE_NAME = "name";

    static final String SERVICE_STATUS_START = "start";
    static final String SERVICE_STATUS_STOP = "stop";

    static final String RECORD_STATUS_START = "recording";
    static final String RECORD_STATUS_STOP = "stop";

    static final String FORE_NOTIFY_CHANNEL_ID = "fc01";
    static final String FORE_NOTIFY_CHANNEL_NAME = "channel_fangcat";
}
