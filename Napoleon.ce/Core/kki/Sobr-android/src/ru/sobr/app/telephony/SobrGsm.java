package ru.sobr.app.telephony;

import ru.sobr.app.R;
import ru.sobr.app.provider.SobrContract;
import ru.sobr.app.utils.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

public class SobrGsm {

    // private static final String TAG = "SobrGsm";
    // private static final boolean DEBUG = false;

    public static final String SOBR_GSM_BALANCE = "100";
    public static final String SOBR_GSM_SMS_INFO = "09";

    public static final String SOBR_GSM_MAP = "300";
    public static final String SOBR_GSM_COORDINATES = "500";

    public static final String SOBR_GSM_ENGINE_START = "123";

    public static final String SOBR_GSM_TIMER_SET = "123 ";

    public static final String SOBR_GSM_EXECUTIONUNIT_ON = "456";
    public static final String SOBR_GSM_EXECUTIONUNIT_OFF = "789";

    public static final String SOBR_GSM_REPORT_ON_MOVE = "007 ";

    public static final String SOBR_GSM_PREHEATER_ON = "456";
    public static final String SOBR_GSM_PREHEATER_OFF = "789";

    public static final String SOBR_GSM_HEATER_ON = "777";
    public static final String SOBR_GSM_HEATER_OFF = "999";

    public static final String SOBR_GSM_SECURITY_ON = "1";
    public static final String SOBR_GSM_SECURITY_WO = "11";
    public static final String SOBR_GSM_SECURITY_OFF = "0";

    public static final String SOBR_GSM_SEARCH_MODE_ON = "666";
    public static final String SOBR_GSM_SEARCH_MODE_OFF = "999";

    public static final String SOBR_GSM_ENGINE_LOCK = "666";

    public static final String SOBR_GSM_DOORS_OPEN = "911";

    private static final String SOBR_GSM_SERVICE_MODE_ON = ":NNNNN*60#0*";
    private static final String SOBR_GSM_SERVICE_MODE_OFF = ":NNNNN*60#1*";

    private static final String SOBR_GSM_CHANGE_PASS = ":NNNNN*01#YYYYY*";

    private static final String SOBR_GSM_GET_SETTINGS = ":NNNNN*40*";
    private static final String SOBR_GSM_GET_SETTINGS_TYPE3 = "ХХХХ*40*";

    private static final String SOBR_GSM_UNLOCK_ENGINE = ":NNNNN*33#ВВВ*";

    private static final String SOBR_GSM_PIN_CODE = ":NNNNN*39#ХХХХ*";
    private static final String SOBR_GSM_PIN_CODE_TYPE3 = "ХХХХ*39#AAAA*";

    private static final String SOBR_GSM_BASE_PHONE_NUMB = ":NNNNN*35#XXXXXXXXXXX*";
    private static final String SOBR_GSM_BASE_PHONE_NUMB_TYPE3 = "PPPP*35#XXXXXXXXXXX*";

    private static final String SOBR_GSM_SECOND_PHONE_NUMB = ":NNNNN*36#XXXXXXXXXXX*";
    private static final String SOBR_GSM_THIRD_PHONE_NUMB = ":NNNNN*37#XXXXXXXXXXX*";

    private static final String SOBR_GSM_MAX_BALANCE = ":NNNNN*58#KKK*";
    private static final String SOBR_GSM_MAX_BALANCE_TYPE3 = "ХХХХ*58#KKK*";

    private static final String SOBR_GSM_GET_BALANCE = ":NNNNN*59#ККККК*";
    private static final String SOBR_GSM_GET_BALANCE_TYPE3 = "ХХХХ*59#KKKKK*";

    private static final String SOBR_GSM_PROG_CAR_REMOTE = ":NNNNN*50*";
    private static final String SOBR_GSM_PROG_KEYS = ":NNNNN*51*";
    private static final String SOBR_GSM_PROG_LABLE_1 = ":NNNNN*64*";
    private static final String SOBR_GSM_PROG_LABLE_2 = ":NNNNN*65*";
    private static final String SOBR_GSM_PROG_LABLE_3 = ":NNNNN*66*";

    // private static final String SOBR_GSM_DAILY_MODE_TYPE_CHIP =
    // "ХХХХ*T1#1440*N#0*";
    private static final String SOBR_GSM_DAILY_MODE_TYPE_CHIP = "ХХХХ*T1#1440*N#0*";
    private static final String SOBR_GSM_DAILY_MODE_TYPE_G0103 = "ХХХХ*N#0*12#1*";

    private static final String SOBR_GSM_WEEKLY_MODE_TYPE_CHIP = "ХХХХ*T1#480*N#21*";
    private static final String SOBR_GSM_WEEKLY_MODE_TYPE_G0103 = "ХХХХ*N#6*12#3*";

    private static final String SOBR_GSM_SET_COMMUNICATION_TIME = "ХХХХ*24#HHmm*";

    // private static final String SOBR_GSM_MIC = "007*";

    public static final String ACTION_SMS_SENT = "ru.sobr.action.SMS_SENT";
    private static final String SOBR_GSM_SET_TIME_ZONE = ":NNNNN*05#nn*";
    private static final String SOBR_GSM_SET_TIME_ZONE_TYPE_3_3 = "XXXX*P#nn*";
    private static final String SOBR_GSM_SET_FREQ_1 = ":NNNNN*07#1*";
    private static final String SOBR_GSM_SET_FREQ_2 = ":NNNNN*07#2*";
    private static final String SOBR_GSM_SET_FREQ_3 = ":NNNNN*07#3*";
    private static final String SOBR_GSM_SET_FREQ_4 = ":NNNNN*07#4*";
    private static final String SOBR_GSM_SET_SCH_0 = ":NNNNN*08#0*";
    private static final String SOBR_GSM_SET_SCH_6 = ":NNNNN*08#6*";
    private static final String SOBR_GSM_SET_SCH_13 = ":NNNNN*08#13*";
    private static final String SOBR_GSM_SET_SCH_30 = ":NNNNN*08#30*";
    private static final String SOBR_GSM_FIFTH_PHONE_NUMB = ":NNNNN*86#XXXXXXXXXXX*";
    private static final String SOBR_GSM_MILAGE = ":PPPPP*88#XXXXXX*";
    private static final String SOBR_GSM_RESEND_INPUT_SMS = ":NNNNN*54#X*";
    private static final String SOBR_GSM_MILAGE_KNOW = ":PPPPP*88*";
    private static final String SOBR_GSM_FIND_ON_PARKING = "02";
	private static final String SOBR_GSM_SEC_BY_MOVE_ON = "*38#1*";
	private static final String SOBR_GSM_SEC_BY_HOUR = "*38#2*";
	private static final String SOBR_GSM_SEC_BY_MOVE_OFF = "*38#0*";
	private static final String SOBR_GSM_SEC_SET_BY_HOUR = "*20#START FINISH*";
	private static final String SOBR_GSM_SEC_RANGE = "*47#MM*";
	private static final String SOBR_GSM_CHANEL = "1";
	private static final String SOBR_GSM_SET_SCH_TYPE_3_0 = "XXXX*N#0*";
    private static final String SOBR_GSM_SET_SCH_TYPE_3_6 = "XXXX*N#6*";
    private static final String SOBR_GSM_SET_SCH_TYPE_3_13 = "XXXX*N#13*";
    private static final String SOBR_GSM_SET_SCH_TYPE_3_30 = "XXXX*N#30*";
    private static final String SOBR_GSM_SET_FREQ_TYPE_3_1 = "XXXX*12#1*";
    private static final String SOBR_GSM_SET_FREQ_TYPE_3_2 = "XXXX*12#2*";
    private static final String SOBR_GSM_SET_FREQ_TYPE_3_3 = "XXXX*12#3*";
    private static final String SOBR_GSM_SET_FREQ_TYPE_3_4 = "XXXX*12#4*";
    private static final String SOBR_GSM_SET_COMMUNICATION_TIME_GSM510 = ":NNNNN*06#HHmm*";
    public static final String SOBR_GSM_REPORT_ON_MOVE_GSM510 = ":NNNNN*47#MM*";
    private static final String SOBR_GSM_STARTPREHEATER = "456";
	private static final String SOBR_GSM_SEC_ALARM = "456 TIME";
    
    private static String getPinCode(Cursor cursor){
        if(cursor.getCount()==0) return "";
        String systemType = cursor.getString(cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_TYPE));
        boolean needPinCode = false;
        String pinCode = "";
        if (systemType.equals(Constants.SOBR_CHIP0103) || systemType.equals(Constants.SOBR_G0103) 
        		|| systemType.equals(Constants.SOBR_CHIP111213)) {
            needPinCode = true;
        }else{
            int columnPhoneStatus = cursor.getColumnIndexOrThrow(SobrContract.Profiles.PHONE_STATUS);
            if(!cursor.getString(columnPhoneStatus).equals("base_value")){
                needPinCode = true;
            }
        }
        if(needPinCode){
            return cursor.getString(cursor.getColumnIndexOrThrow(SobrContract.Profiles.PIN_CODE));
        }
        return "";
    }

    public static void engineStart(Activity context) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_ENGINE_START, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // if (DEBUG)Log.d(TAG, "operation error");
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void preheaterOn(Activity context, String msgCode) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + msgCode, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void preheaterOff(Activity context) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_PREHEATER_OFF, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void heater(Activity context, String msgCode) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + msgCode, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void setTimer(Activity context, String hours) {
        String phoneNumber = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, SOBR_GSM_TIMER_SET + hours, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void reportOnMove(Activity context, String minutes) {
        String phoneNumber = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles.SYSTEM_PHONE_NUMBER, SobrContract.Profiles.SYSTEM_TYPE,
                            SobrContract.Profiles.PHONE_STATUS, SobrContract.Profiles.PIN_CODE}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, SOBR_GSM_REPORT_ON_MOVE + minutes, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void balance(Activity context) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_BALANCE, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void map(Activity context) {
        String phoneNumber = null;
        String systemType = "";
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    phoneNumber = cursor.getString(cursor
                            .getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER));
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_MAP, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void coordinates(Activity context) {
        String phoneNumber = null;
        String systemType = "";
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    phoneNumber = cursor.getString(cursor
                            .getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER));
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_COORDINATES, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void serviceModeOn(Activity context) {
        String phoneNumber = null;
        String password = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles.SYSTEM_PHONE_NUMBER, SobrContract.Profiles.PASSWORD}, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    int columnPassword = cursor.getColumnIndexOrThrow(SobrContract.Profiles.PASSWORD);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    password = cursor.getString(columnPassword);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null && password != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, SOBR_GSM_SERVICE_MODE_ON.replace("NNNNN", password),
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void serviceModeOff(Activity context) {
        String phoneNumber = null;
        String password = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles.SYSTEM_PHONE_NUMBER, SobrContract.Profiles.PASSWORD}, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    int columnPassword = cursor.getColumnIndexOrThrow(SobrContract.Profiles.PASSWORD);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    password = cursor.getString(columnPassword);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null && password != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, SOBR_GSM_SERVICE_MODE_OFF.replace("NNNNN", password),
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void security(Activity context, String msgCode) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + msgCode, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void securityWo(Activity context) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_SECURITY_WO, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void engineLock(Activity context) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_ENGINE_LOCK, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void engineUnlock(Activity context) {
        String phoneNumber = null;
        String unlockCode = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles._ID, SobrContract.Profiles.SYSTEM_PHONE_NUMBER,
                            SobrContract.Profiles.UNLOCK_CODE, SobrContract.Profiles.PHONE_STATUS,
                            SobrContract.Profiles.PIN_CODE, SobrContract.Profiles.SYSTEM_TYPE
                    }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndex(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    int columnUnlockCode = cursor.getColumnIndex(SobrContract.Profiles.UNLOCK_CODE);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    unlockCode = cursor.getString(columnUnlockCode);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null && unlockCode != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                String command = "";
                if (TextUtils.isEmpty(unlockCode)) command = "999";
                String msgCode = pinCode + unlockCode + command;
//		Log.d("engineUnlock", "msgCode - " + msgCode);
                sendSms(context, phoneNumber, null, msgCode, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void doorsOpen(Activity context) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_DOORS_OPEN, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void searchMode(Activity context, String smsMsg) {
        String phoneNumber = null;
//        String password = null;
//        String phoneStatus = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles._ID, SobrContract.Profiles.SYSTEM_PHONE_NUMBER,
                            SobrContract.Profiles.PASSWORD, SobrContract.Profiles.PIN_CODE,
                            SobrContract.Profiles.PHONE_STATUS, SobrContract.Profiles.SYSTEM_TYPE}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    phoneNumber = cursor.getString(cursor.getColumnIndex(SobrContract.Profiles.SYSTEM_PHONE_NUMBER));

//                    password = cursor.getString(cursor.getColumnIndex(SobrContract.Profiles.PASSWORD));
                    pinCode = getPinCode(cursor);

//                    phoneStatus = cursor.getString(cursor.getColumnIndex(SobrContract.Profiles.PHONE_STATUS));

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
//            if (phoneStatus.equals("base_value"))
//                password = "";
//            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + smsMsg, sentIntent, null);
                // Log.d(TAG, password + smsMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void searchModeForType3(Activity context, String smsMsg) {
        String phoneNumber = null;
        String pinCode = null;
        String phoneStatus = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles._ID, SobrContract.Profiles.SYSTEM_PHONE_NUMBER,
                            SobrContract.Profiles.PIN_CODE, SobrContract.Profiles.PHONE_STATUS, SobrContract.Profiles.SYSTEM_TYPE}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    phoneNumber = cursor.getString(cursor.getColumnIndex(SobrContract.Profiles.SYSTEM_PHONE_NUMBER));

                    pinCode = getPinCode(cursor);

                    phoneStatus = cursor.getString(cursor.getColumnIndex(SobrContract.Profiles.PHONE_STATUS));

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + smsMsg, sentIntent, null);
                // Log.d(TAG, pinCode + smsMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void callInfo(Activity context) {
        String phoneNumber = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles.SYSTEM_PHONE_NUMBER}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void smsInfo(Activity context) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + SOBR_GSM_SMS_INFO, sentIntent, null);
                // if(DEBUG)Log.d(TAG, pinCode + SOBR_GSM_SMS_INFO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void callMic(Activity context) {
        String phoneNumber = null;
        // String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles.SYSTEM_PHONE_NUMBER}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    phoneNumber = cursor.getString(cursor
                            .getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
            // throw new UnsupportedOperationException();
        }
    }

    public static void sirenOn(Activity context, String msgCode) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + msgCode, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void gateOpenClose(Activity context, String msgCode) {
        String phoneNumber = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                // Log.d(TAG, "gateOpenClose"+ msgCode);
                sendSms(context, phoneNumber, null, msgCode, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void userCommand(Activity context, String msgCode) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + msgCode, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void executinUnit(Activity context, String smsMsg) {
        String phoneNumber = null;
        String pinCode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pinCode = getPinCode(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, phoneNumber, null, pinCode + smsMsg, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // if (DEBUG)Log.d(TAG, "operation error");
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileChangePass(Activity context, String systemNumber, String oldPass, String newPass) {
        if (systemNumber != null && oldPass != null && newPass != null) {
            String message = SOBR_GSM_CHANGE_PASS.replace("NNNNN", oldPass).replace("YYYYY", newPass);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileGetSettings(Activity context, String systemNumber, String password) {
        if (systemNumber.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_GET_SETTINGS.replace("NNNNN", password);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileGetSettingsType3(Activity context, String systemNumber, String pinCode) {
        if (systemNumber.length() != 0 && pinCode.length() != 0) {
            String message = SOBR_GSM_GET_SETTINGS_TYPE3.replace("ХХХХ", pinCode);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileUnlockEngine(Activity context, String systemNumber, String password, String unlockCode) {
        if (systemNumber.length() != 0 && unlockCode.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_UNLOCK_ENGINE.replace("NNNNN", password).replace("ВВВ", unlockCode);

            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profilePinCode(Activity context, String systemNumber, String password, String pinCode) {
        if (systemNumber.length() != 0 && pinCode.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_PIN_CODE.replace("NNNNN", password).replace("ХХХХ", pinCode);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profilePinCodeType3(Activity context, String systemNumber, String oldPin, String newPin) {
        if (systemNumber.length() != 0 && oldPin.length() != 0 && newPin.length() != 0) {
            String message = SOBR_GSM_PIN_CODE_TYPE3.replace("ХХХХ", oldPin).replace("AAAA", newPin);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileBasePhoneNumb(Activity context, String systemNumber, String password, String baseNumb) {
        if (systemNumber.length() != 0 && baseNumb.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_BASE_PHONE_NUMB.replace("NNNNN", password).replace("XXXXXXXXXXX", baseNumb);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileBasePhoneNumbType3(Activity context, String systemNumber, String pinCode, String baseNumb) {
        if (systemNumber.length() != 0 && baseNumb.length() != 0 && pinCode.length() != 0) {
            String message = SOBR_GSM_BASE_PHONE_NUMB_TYPE3.replace("PPPP", pinCode).replace("XXXXXXXXXXX", baseNumb);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileSecondPhoneNumb(Activity context, String systemNumber, String password, String secondNumb) {
        if (systemNumber.length() != 0 && password.length() != 0 && secondNumb.length() != 0) {
            String message = SOBR_GSM_SECOND_PHONE_NUMB.replace("NNNNN", password).replace("XXXXXXXXXXX", secondNumb);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileThirdPhoneNumb(Activity context, String systemNumber, String password, String thirdNumb) {
        if (systemNumber.length() != 0 && password.length() != 0 && thirdNumb.length() != 0) {
            String message = SOBR_GSM_THIRD_PHONE_NUMB.replace("NNNNN", password).replace("XXXXXXXXXXX", thirdNumb);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileMaxBalance(Activity context, String systemNumber, String password, String maxBalance) {
        if (systemNumber.length() != 0 && password.length() != 0 && maxBalance.length() != 0) {
            String message = SOBR_GSM_MAX_BALANCE.replace("NNNNN", password).replace("KKK", maxBalance);
            // Log.d(TAG, "profileMaxBalance "+ message);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileMaxBalanceType3(Activity context, String systemNumber, String pinCode, String maxBalance) {
        if (systemNumber.length() != 0 && pinCode.length() != 0 && maxBalance.length() != 0) {
            String message = SOBR_GSM_MAX_BALANCE_TYPE3.replace("ХХХХ", pinCode).replace("KKK", maxBalance);
            // Log.d(TAG, "profileMaxBalanceType3 "+ message);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileGetBalance(Activity context, String systemNumber, String password, String balanceCode) {
        if (systemNumber.length() != 0 && password.length() != 0 && balanceCode.length() != 0) {
            String message = SOBR_GSM_GET_BALANCE.replace("NNNNN", password).replace("ККККК", balanceCode);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileGetBalanceType3(Activity context, String systemNumber, String pinCode, String balanceCode) {
        if (systemNumber.length() != 0 && pinCode.length() != 0 && balanceCode.length() != 0) {
            String message = SOBR_GSM_GET_BALANCE_TYPE3.replace("ХХХХ", pinCode).replace("KKKKK", balanceCode);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileProgCarRemote(Activity context, String systemNumber, String password) {
        if (systemNumber.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_PROG_CAR_REMOTE.replace("NNNNN", password);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileProgKeys(Activity context, String systemNumber, String password) {
        if (systemNumber.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_PROG_KEYS.replace("NNNNN", password);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileProgLable1(Activity context, String systemNumber, String password) {
        if (systemNumber.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_PROG_LABLE_1.replace("NNNNN", password);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileProgLable2(Activity context, String systemNumber, String password) {
        if (systemNumber.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_PROG_LABLE_2.replace("NNNNN", password);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileProgLable3(Activity context, String systemNumber, String password) {
        if (systemNumber.length() != 0 && password.length() != 0) {
            String message = SOBR_GSM_PROG_LABLE_3.replace("NNNNN", password);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileDailyMode(Activity context, String systemNumber, String systemType, String pinCode) {
        if (systemNumber.length() != 0 && systemType.length() != 0 && pinCode.length() != 0) {

            String message = "";
            if (systemType.equals(Constants.SOBR_CHIP0103)) {
                message = SOBR_GSM_DAILY_MODE_TYPE_CHIP.replace("ХХХХ", pinCode);
                // Log.d(TAG, "profisleDailyMode message: "+ message);
            } else {

                message = SOBR_GSM_DAILY_MODE_TYPE_G0103.replace("ХХХХ", pinCode);
                // Log.d(TAG, "profisleDailyMode message: "+ message);
            }
            // if(DEBUG)Log.d(TAG, message);

            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileWeeklyMode(Activity context, String systemNumber, String systenType, String pinCode) {
        if (systemNumber.length() != 0 && systenType.length() != 0 && pinCode.length() != 0) {
//	    Log.d("profileWeeklyMode", " pinCode - " + pinCode);
            String message = "";
            if (systenType.equals(Constants.SOBR_CHIP0103)) {
                message = SOBR_GSM_WEEKLY_MODE_TYPE_CHIP.replace("ХХХХ", pinCode);
            } else {
                message = SOBR_GSM_WEEKLY_MODE_TYPE_G0103.replace("ХХХХ", pinCode);
            }

            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void profileCommunicationTime(Activity context, String systemNumber, String pinCode, String hours,
                                                String minutes) {
        if (systemNumber.length() != 0 && pinCode.length() != 0) {

            String message = SOBR_GSM_SET_COMMUNICATION_TIME.replace("ХХХХ", pinCode).replace("HH", hours)
                    .replace("mm", minutes);
            // Log.d("message", message);
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private static void sendSms(final Activity context, final String destinationAddress, 
    		final String scAddress, final String text, final PendingIntent sentIntent, 
    		final PendingIntent deliveryIntent){
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Phone number: "+destinationAddress+"\ntext: "+text);
//        builder.setNegativeButton("OK", null);
//        builder.create().show();
//
//        context.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent();
//                intent.setAction(SobrGsm.ACTION_SMS_SENT);
//                context.sendBroadcast(intent);
//            }
//        });
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle(R.string.message);
    	builder.setMessage(R.string.commit_to_sms);
    	builder.setPositiveButton(R.string.yes, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SmsManager smsManager = SmsManager.getDefault();
		        smsManager.sendTextMessage(destinationAddress, scAddress, text, 
		        		sentIntent, deliveryIntent);
		        dialog.dismiss();
			}
		});
        
    	builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(SobrGsm.ACTION_SMS_SENT);
				intent.putExtra(SmsStatusReceiver.SUPPRES_TOAST, true);
				context.sendBroadcast(intent);
			}
		});
    	builder.create().show();
    }

	public static void profileTimeZone(Activity context, String password,
			String sel, String phoneNumber) {
	    if (phoneNumber != null) {
	        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
	        try {
	            sendSms(context, phoneNumber, null, 
	            		SOBR_GSM_SET_TIME_ZONE.replace("NNNNN", password).replace("nn", sel),
	                    sentIntent, null);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
		
	}

	public static void profileTimeZone33(Activity context, String sel, String phoneNumber, String pincode) {
	    if (phoneNumber != null) {
	        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
	        try {
	            sendSms(context, phoneNumber, null, 
	            		SOBR_GSM_SET_TIME_ZONE_TYPE_3_3.replace("XXXX", pincode).replace("nn", sel),
	                    sentIntent, null);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
		
	}
	
	public static void profileFreqConn(Activity context, String password,
			final int which, String phoneNumber) {
	    if (phoneNumber != null) {
	        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
	        try {
	        	
	        	String msg = null;
	        	
	        	switch(which){
	        	case 0: msg = SOBR_GSM_SET_FREQ_1;
	        		break;
	        	case 1: msg = SOBR_GSM_SET_FREQ_2;
        			break;
	        	case 2: msg = SOBR_GSM_SET_FREQ_3;
        			break;
	        	case 3: msg = SOBR_GSM_SET_FREQ_4;
        			break;
        		default:
        			msg = null;
	        	}
	        	
	        	
	        	if(msg != null)
		            sendSms(context, phoneNumber, null, msg.replace("NNNNN", password),
		                    sentIntent, null);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
		
	}

	public static void profileWorkSchedule(Activity context,
			String password, int which, String phoneNumber) {
	    if (phoneNumber != null) {
	        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
	        try {
	        	
	        	String msg = null;
	        	
	        	switch(which){
	        	case 0: msg = SOBR_GSM_SET_SCH_0;
	        		break;
	        	case 1: msg = SOBR_GSM_SET_SCH_6;
        			break;
	        	case 2: msg = SOBR_GSM_SET_SCH_13;
        			break;
	        	case 3: msg = SOBR_GSM_SET_SCH_30;
        			break;
        		default:
        			msg = null;
	        	}
	        	
	        	
	        	if(msg != null)
		            sendSms(context, phoneNumber, null, msg.replace("NNNNN", password),
		                    sentIntent, null);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
	}

	public static void fifthPhoneNumb(Activity context, String systemNumber, String password, String num) {
        if (systemNumber.length() != 0 && password.length() != 0 && num.length() != 0) {
        	num = num.replace("+", "");
            String message = SOBR_GSM_FIFTH_PHONE_NUMB.replace("NNNNN", password).replace("XXXXXXXXXXX", num);
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
    }

	public static void profileMillAge(Activity context, String systemNumber, String number,
			String milage) {
		if (systemNumber.length() > 0 && number.length() > 0 && milage.length() > 0) {
            String message = SOBR_GSM_MILAGE.replace("PPPPP", number).replace("XXXXXX", milage);
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
		
	}
	
	public static void resendInputSms(Activity context, String systemNumber, String password,
			int which) {
		if (systemNumber.length() > 0 && password.length() > 0 ) {
            String message = SOBR_GSM_RESEND_INPUT_SMS.replace("NNNNN", password).replace("X", Integer.toString(which));
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
		
	}

	public static boolean millageKnow(Activity context, String fiftPhoneNumber) {
		boolean result = false; 
		String phoneNumber = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles.SYSTEM_PHONE_NUMBER, 
                    	SobrContract.Profiles.FIFTH_PHONE_NUMBER}, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
            }
            
            cursor.close();
        }
        if (phoneNumber != null && fiftPhoneNumber != null && fiftPhoneNumber.length() >= 5) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
                sendSms(context, phoneNumber, null, SOBR_GSM_MILAGE_KNOW.replace("PPPPP", 
                		fiftPhoneNumber.substring(fiftPhoneNumber.length() - 
                				Constants.LEN_FIFTH_NUMBER, fiftPhoneNumber.length())),
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}

	public static boolean findOnParking(Activity context) {
		boolean result = false; 
		String phoneNumber = null;
		String pincode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pincode = getPinCode(cursor);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
                sendSms(context, phoneNumber, null, pincode + SOBR_GSM_FIND_ON_PARKING,
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}

	public static boolean secByMoveOn(Activity context) {
		boolean result = false; 
		String phoneNumber = null;
		String pincode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pincode = getPinCode(cursor);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
                sendSms(context, phoneNumber, null, pincode + SOBR_GSM_SEC_BY_MOVE_ON,
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}

	public static boolean secByHour(Activity context) {
		boolean result = false; 
		String phoneNumber = null;
		String pincode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pincode = getPinCode(cursor);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
                sendSms(context, phoneNumber, null, pincode + SOBR_GSM_SEC_BY_HOUR,
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}

	public static boolean secByMoveOff(Activity context) {
		boolean result = false; 
		String phoneNumber = null;
		String pincode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pincode = getPinCode(cursor);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
                sendSms(context, phoneNumber, null, pincode + SOBR_GSM_SEC_BY_MOVE_OFF,
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}

	public static boolean secSetByHour(Activity context, String start,
			String finish) {
		boolean result = false; 
		String phoneNumber = null;
		String pincode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pincode = getPinCode(cursor);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
            	String text = pincode + SOBR_GSM_SEC_SET_BY_HOUR.replace("START", start)
            			.replace("FINISH", finish);
                sendSms(context, phoneNumber, null, text ,
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}

	public static boolean secSetRange(Activity context, String val) {
		boolean result = false; 
		String phoneNumber = null;
		String pincode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pincode = getPinCode(cursor);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
            	String text = pincode + SOBR_GSM_SEC_RANGE.replace("MM", val);
                sendSms(context, phoneNumber, null, text,
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}
	
	public static boolean channelSms(Activity context, int channel) {
		boolean result = false; 
		String phoneNumber = null;
		String pincode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pincode = getPinCode(cursor);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
                sendSms(context, phoneNumber, null, pincode + SOBR_GSM_CHANEL + Integer.toString(channel),
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}

	public static void profileWorkSchedule33(Activity context, int which, String phoneNumber, String pincode) {
		    if (phoneNumber != null) {
		        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
		        try {
		        	
		        	String msg = null;
		        	
		        	switch(which){
		        	case 0: msg = SOBR_GSM_SET_SCH_TYPE_3_0;
		        		break;
		        	case 1: msg = SOBR_GSM_SET_SCH_TYPE_3_6;
	        			break;
		        	case 2: msg = SOBR_GSM_SET_SCH_TYPE_3_13;
	        			break;
		        	case 3: msg = SOBR_GSM_SET_SCH_TYPE_3_30;
	        			break;
	        		default:
	        			msg = null;
		        	}
		        	
		        	
		        	if(msg != null)
			            sendSms(context, phoneNumber, null, msg.replace("XXXX", pincode),
			                    sentIntent, null);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
	        }
	}
	
	public static void profileFreqConn33(Activity context, final int which,
			String number, String pincode) {
	    if (number != null) {
	        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
	        try {
	        	
	        	String msg = null;
	        	
	        	switch(which){
	        	case 0: msg = SOBR_GSM_SET_FREQ_TYPE_3_1;
	        		break;
	        	case 1: msg = SOBR_GSM_SET_FREQ_TYPE_3_2;
        			break;
	        	case 2: msg = SOBR_GSM_SET_FREQ_TYPE_3_3;
        			break;
	        	case 3: msg = SOBR_GSM_SET_FREQ_TYPE_3_4;
        			break;
        		default:
        			msg = null;
	        	}
	        	
	        	
	        	if(msg != null)
		            sendSms(context, number, null, msg.replace("XXXX", pincode),
		                    sentIntent, null);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
		
	}

	public static void profileCommunicationTimeGSM510(
			Activity context, String systemNumber, String pwd,
			String hours, String minutes) {
		if (systemNumber.length() != 0) {

            String message = SOBR_GSM_SET_COMMUNICATION_TIME_GSM510.replace("NNNNN", pwd)
            		.replace("HH", hours)
                    .replace("mm", minutes);
            // Log.d("message", message);
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
                sendSms(context, systemNumber, null, message, sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
	}

	public static void reportOnMoveGSM510(Activity context, String minutes) {
		String phoneNumber = null;
		String pwd = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    new String[]{SobrContract.Profiles.SYSTEM_PHONE_NUMBER, SobrContract.Profiles.SYSTEM_TYPE,
                            SobrContract.Profiles.PHONE_STATUS, SobrContract.Profiles.PIN_CODE,
                            SobrContract.Profiles.PASSWORD}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pwd = cursor.getString(
                    		cursor.getColumnIndex(SobrContract.Profiles.PASSWORD));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	String msg = SOBR_GSM_REPORT_ON_MOVE_GSM510.replace("NNNNN", pwd).replace("MM",minutes);
                sendSms(context, phoneNumber, null, msg , sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
	}

	public static boolean startPreheater(Activity context) {
		boolean result = false; 
		String phoneNumber = null;
		String pincode = "";
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
                    pincode = getPinCode(cursor);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
                sendSms(context, phoneNumber, null, pincode + SOBR_GSM_STARTPREHEATER,
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}
	
	public static boolean secSetAlarm(Activity context, String time){
		boolean result = false; 
		String phoneNumber = null;
        long profile_id = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(
                "profile_id_preference", -1);
        if (profile_id > 0) {
            Cursor cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(SobrContract.Profiles.CONTENT_URI, profile_id),
                    SobrContract.Profiles.PROJECTION, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                    int columnPhoneNumber = cursor.getColumnIndexOrThrow(SobrContract.Profiles.SYSTEM_PHONE_NUMBER);
                    phoneNumber = cursor.getString(columnPhoneNumber);
            }
            
            cursor.close();
        }
        if (phoneNumber != null) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
            try {
            	result = true;
            	String text = SOBR_GSM_SEC_ALARM.replace("TIME", time);
                sendSms(context, phoneNumber, null, text ,
                        sentIntent, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.operation_error), Toast.LENGTH_LONG)
                    .show();
        }
        
        return result;
	}
}
