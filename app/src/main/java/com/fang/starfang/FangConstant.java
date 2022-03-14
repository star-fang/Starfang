package com.fang.starfang;

public class FangConstant {
    public static final int REQ_CODE_PICK_ITEM_DIALOG_FRAGMENT = 0;
    public static final int REQ_CODE_REINFORCE_ITEM_DIALOG_FRAGMENT = 1;

    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_FAIL = 1;

    public static final int ITEM_MAIN_CATEGORY_CODE_WEAPON = 0;
    public static final int ITEM_MAIN_CATEGORY_CODE_ARMOR = 1;
    public static final int ITEM_MAIN_CATEGORY_CODE_AID = 2;

    public static final int GUARDIAN_INIT_VALUE = 0;
    public static final int GRADE_INIT_VALUE = 3;

    public static final String INTENT_KEY_ITEM_NAME = "itemName";
    public static final String INTENT_KEY_ITEM_ID = "itemID";
    public static final String INTENT_KEY_ITEM_REINFORCE = "itemReinforce";
    public static final String INTENT_KEY_ITEM_CATE_SUB = "itemSubCate";
    public static final String INTENT_KEY_ITEM_CATE_MAIN = "itemMainCate";
    public static final String INTENT_KEY_SUFFIX_ID = "suffixID";

    //public static final String READ_SHEET_MODE_INFO = "readSheetInfo";
    // public static final String READ_SHEET_MODE_DOWN = "readSheetDown";
    //public static final String READ_SHEET_MODE_UP = "readSheetUp";

    public static final String INTENT_KEY_HERO_ID = "heroID";

    public static final String INTENT_KEY_RELIC_POSITION = "relicPosition";
    public static final String INTENT_KEY_RELIC_SLOT = "relicSlot";

    public static final String CONSTRAINT_SEPARATOR = ",";

    public static final String PACKAGE_KAKAO = "com.kakao.talk";
    public static final String PACKAGE_DISCORD = "com.discord";
    public static final String PACKAGE_STARFANG = "com.fang.starfang";

    public static final String REPLY_KEY_LOCAL = "key_local_reply";

    public static final int BOT_STATUS_STOP = 0;
    public static final int BOT_STATUS_START = 1;
    public static final int BOT_STATUS_RESTART = 2;
    public static final String BOT_STATUS_KEY = "bot_status";
    public static final String BOT_NAME_KEY = "bot_name";
    public static final String BOT_RECORD_KEY = "bot_record";
    public static final String BOT_START_COUNT_KEY = "start_count";
    public static final String BOT_RESTART_COUNT_KEY = "restart_count";
    public static final String SHARED_PREF_STORE = "sp_store";

    public static final String EXTRA_INFORMATION = "information";
}
