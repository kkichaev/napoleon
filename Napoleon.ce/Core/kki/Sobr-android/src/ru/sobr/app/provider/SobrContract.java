package ru.sobr.app.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class SobrContract {
    private SobrContract() {
    }

    public static final String AUTHORITY = "ru.sobr.app.provider";

    public static final class Profiles implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/Profiles");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.sobr.app.Profiles";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.sobr.app.Profiles";

        public static final String NAME = "name";
        public static final String SYSTEM_TYPE = "sobr_system_type";
        // public static final String PHONE_NUMBER = "phone_number";
        public static final String SYSTEM_PHONE_NUMBER = "system_phone_number";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String PASSWORD = "password";
        public static final String UNLOCK_CODE = "unlock_code";

        public static final String SOBR_ASSIST_LOGIN = "sobr_assist_login";
        public static final String SOBR_ASSIST_PASSWORD = "sobr_assist_password";

        public static final String PIN_CODE = "pin_code";
        public static final String PIN_CODE_ON_BOOT = "pin_code_on_boot";
        public static final String BASE_PHONE_NUMBER = "main_phone_number";
        public static final String SECOND_PHONE_NUMBER = "second_phone_number";
        public static final String THIRD_PHONE_NUMBER = "third_phone_number";
        public static final String BALANCE_THRESHOLD = "balance_threshold";
        public static final String BALANCE_QUERY_CODE = "balance_query_code";

        public static final String PHONE_STATUS = "phone_status";
        public static final String COMMAND_123 = "command_132";
        public static final String COMMAND_456 = "command_456";
        public static final String COMMAND_789 = "command_789";
        public static final String COMMAND_666 = "command_666";
        public static final String COMMAND_777 = "command_777";
        public static final String COMMAND_999 = "command_999";
        public static final String COMMAND_09 = "command_09";
        public static final String COMMAND_911 = "command_911";
        public static final String COMMAND_123_TITLE = "command_132_title";
        public static final String COMMAND_456_TITLE = "command_456_title";
        public static final String COMMAND_789_TITLE = "command_789_title";
        public static final String COMMAND_777_TITLE = "command_777_title";
        public static final String COMMAND_999_TITLE = "command_999_title";
        public static final String COMMAND_911_TITLE = "command_911_title";
        public static final String GPS_RECEIVER = "gps_receiver";
        public static final String REPORT_ON_MOVE = "report_on_move";
        public static final String SHOCK_SENSOR = "shock_sensor";
        public static final String IMMOBILIZER = "immobilizer";
        public static final String FIFTH_PHONE_NUMBER = "fifth_phone_number";
        public static final String GSM510_WORK_MODE = "gsm510_work_mode";
        public static final String PREHEATER = "preheater";
        public static final String CHANELS = "chanels";
        public static final String CMD1 = "cmd1";
        public static final String KEY1 = "key1";
        public static final String CMD2 = "cmd2";
        public static final String KEY2 = "key2";
        public static final String CMD3 = "cmd3";
        public static final String KEY3 = "key3";
        public static final String CMD4 = "cmd4";
        public static final String KEY4 = "key4";
        public static final String ALARM = "alarm";
        public static final String ALARM_TIME = "alarmtime";
        
        public static final String PROJECTION[] = {_ID, SYSTEM_PHONE_NUMBER, PHONE_STATUS, PIN_CODE,
                SYSTEM_TYPE};

        public static final String PROJECTION_LIST[] = {_ID, NAME, SYSTEM_PHONE_NUMBER};

        public static final String DEFAULT_SORT_ORDER = null;
    }

}

