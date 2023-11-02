package ru.sobr.app.utils;

import ru.sobr.app.provider.SobrContract;
import android.content.ContentValues;

public class DefaultProfiles {

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    public static ContentValues sobrGsm(ContentValues values) {

        values.put(SobrContract.Profiles.COMMAND_123, "adz_value");
        values.put(SobrContract.Profiles.COMMAND_456, "ppp_on_value");
        values.put(SobrContract.Profiles.COMMAND_789, "ppp_off_value");
        values.put(SobrContract.Profiles.COMMAND_666, "blocking_value");
        values.put(SobrContract.Profiles.COMMAND_777, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_999, "blocking_off_value");
        values.put(SobrContract.Profiles.COMMAND_09, "call_and_sms_value");
        values.put(SobrContract.Profiles.COMMAND_911, "doors_unlock_value");
        values.put(SobrContract.Profiles.GPS_RECEIVER, TRUE);
        values.put(SobrContract.Profiles.REPORT_ON_MOVE, FALSE);
        values.put(SobrContract.Profiles.SHOCK_SENSOR, TRUE);
        values.put(SobrContract.Profiles.IMMOBILIZER, TRUE);
        values.put(SobrContract.Profiles.PREHEATER, FALSE);
        values.put(SobrContract.Profiles.GSM510_WORK_MODE, "0");
        values.put(SobrContract.Profiles.CMD1, "");
        values.put(SobrContract.Profiles.KEY1, "");
        values.put(SobrContract.Profiles.CMD2, "");
        values.put(SobrContract.Profiles.KEY2, "");
        values.put(SobrContract.Profiles.CMD3, "");
        values.put(SobrContract.Profiles.KEY3, "");
        values.put(SobrContract.Profiles.CMD4, "");
        values.put(SobrContract.Profiles.KEY4, "");
        values.put(SobrContract.Profiles.CHANELS, 0);
        values.put(SobrContract.Profiles.ALARM, FALSE);
        
        return values;
    }

    public static ContentValues sobrDomonline(ContentValues values) {

        values.put(SobrContract.Profiles.COMMAND_123, "siren_value");
        values.put(SobrContract.Profiles.COMMAND_456, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_789, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_666, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_777, "heater_on_value");
        values.put(SobrContract.Profiles.COMMAND_999, "heater_off_value");
        values.put(SobrContract.Profiles.COMMAND_09, "call_only_value");
        values.put(SobrContract.Profiles.COMMAND_911, "disable_value");
        values.put(SobrContract.Profiles.GPS_RECEIVER, FALSE);
        values.put(SobrContract.Profiles.REPORT_ON_MOVE, FALSE);
        values.put(SobrContract.Profiles.SHOCK_SENSOR, FALSE);
        values.put(SobrContract.Profiles.IMMOBILIZER, FALSE);
        values.put(SobrContract.Profiles.PREHEATER, FALSE);
        values.put(SobrContract.Profiles.GSM510_WORK_MODE, "0");
        values.put(SobrContract.Profiles.CMD1, "");
        values.put(SobrContract.Profiles.KEY1, "");
        values.put(SobrContract.Profiles.CMD2, "");
        values.put(SobrContract.Profiles.KEY2, "");
        values.put(SobrContract.Profiles.CMD3, "");
        values.put(SobrContract.Profiles.KEY3, "");
        values.put(SobrContract.Profiles.CMD4, "");
        values.put(SobrContract.Profiles.KEY4, "");
        values.put(SobrContract.Profiles.CHANELS, 0);
        values.put(SobrContract.Profiles.ALARM, FALSE);

        return values;
    }

    public static ContentValues sobrChip0103(ContentValues values) {

        values.put(SobrContract.Profiles.COMMAND_123, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_456, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_789, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_666, "search_mode_on_value");
        values.put(SobrContract.Profiles.COMMAND_777, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_999, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_09, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_911, "disable_value");
        values.put(SobrContract.Profiles.GPS_RECEIVER, TRUE);
        values.put(SobrContract.Profiles.REPORT_ON_MOVE, FALSE);
        values.put(SobrContract.Profiles.SHOCK_SENSOR, FALSE);
        values.put(SobrContract.Profiles.IMMOBILIZER, FALSE);
        values.put(SobrContract.Profiles.PREHEATER, FALSE);
        values.put(SobrContract.Profiles.GSM510_WORK_MODE, "0");
        values.put(SobrContract.Profiles.CMD1, "");
        values.put(SobrContract.Profiles.KEY1, "");
        values.put(SobrContract.Profiles.CMD2, "");
        values.put(SobrContract.Profiles.KEY2, "");
        values.put(SobrContract.Profiles.CMD3, "");
        values.put(SobrContract.Profiles.KEY3, "");
        values.put(SobrContract.Profiles.CMD4, "");
        values.put(SobrContract.Profiles.KEY4, "");
        values.put(SobrContract.Profiles.CHANELS, 0);
        values.put(SobrContract.Profiles.ALARM, FALSE);
        
        return values;
    }

    public static ContentValues sobrG0103(ContentValues values) {

        values.put(SobrContract.Profiles.COMMAND_123, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_456, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_789, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_666, "search_mode_on_value");
        values.put(SobrContract.Profiles.COMMAND_777, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_999, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_09, "call_and_sms_value");
        values.put(SobrContract.Profiles.COMMAND_911, "disable_value");
        values.put(SobrContract.Profiles.GPS_RECEIVER, TRUE);
        values.put(SobrContract.Profiles.REPORT_ON_MOVE, FALSE);
        values.put(SobrContract.Profiles.SHOCK_SENSOR, FALSE);
        values.put(SobrContract.Profiles.IMMOBILIZER, FALSE);
        values.put(SobrContract.Profiles.PREHEATER, FALSE);
        values.put(SobrContract.Profiles.GSM510_WORK_MODE, "0");
        values.put(SobrContract.Profiles.CMD1, "");
        values.put(SobrContract.Profiles.KEY1, "");
        values.put(SobrContract.Profiles.CMD2, "");
        values.put(SobrContract.Profiles.KEY2, "");
        values.put(SobrContract.Profiles.CMD3, "");
        values.put(SobrContract.Profiles.KEY3, "");
        values.put(SobrContract.Profiles.CMD4, "");
        values.put(SobrContract.Profiles.KEY4, "");
        values.put(SobrContract.Profiles.CHANELS, 0);
        values.put(SobrContract.Profiles.ALARM, FALSE);

        return values;
    }
    
    public static ContentValues sobrGsm510(ContentValues values) {

        values.put(SobrContract.Profiles.COMMAND_123, "adz_value");
        values.put(SobrContract.Profiles.COMMAND_456, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_789, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_666, "disable_values");
        values.put(SobrContract.Profiles.COMMAND_777, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_999, "disable_value");
        values.put(SobrContract.Profiles.COMMAND_09, "call_and_sms_value");
        values.put(SobrContract.Profiles.COMMAND_911, "disable_value");
        values.put(SobrContract.Profiles.GPS_RECEIVER, TRUE);
        values.put(SobrContract.Profiles.REPORT_ON_MOVE, TRUE);
        values.put(SobrContract.Profiles.SHOCK_SENSOR, FALSE);
        values.put(SobrContract.Profiles.IMMOBILIZER, FALSE);
        values.put(SobrContract.Profiles.GSM510_WORK_MODE, "0");
        values.put(SobrContract.Profiles.CHANELS, 0);
        values.put(SobrContract.Profiles.PREHEATER, FALSE);
        values.put(SobrContract.Profiles.CMD1, "");
        values.put(SobrContract.Profiles.KEY1, "");
        values.put(SobrContract.Profiles.CMD2, "");
        values.put(SobrContract.Profiles.KEY2, "");
        values.put(SobrContract.Profiles.CMD3, "");
        values.put(SobrContract.Profiles.KEY3, "");
        values.put(SobrContract.Profiles.CMD4, "");
        values.put(SobrContract.Profiles.KEY4, "");
        values.put(SobrContract.Profiles.ALARM, FALSE);
        
        return values;
    }

}
