package ru.sobr.app.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SobrDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sobrapp.db";
    private static final int DATABASE_VERSION = 8;

    static final String PROFILES = "profiles";

    public SobrDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PROFILES + " ("
                + SobrContract.Profiles._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SobrContract.Profiles.NAME + " TEXT NOT NULL,"
                + SobrContract.Profiles.SYSTEM_TYPE + " TEXT NOT NULL,"
                + SobrContract.Profiles.SYSTEM_PHONE_NUMBER + " TEXT NOT NULL,"
                + SobrContract.Profiles.PASSWORD + " TEXT NOT NULL,"
                + SobrContract.Profiles.UNLOCK_CODE + " TEXT NOT NULL,"

                + SobrContract.Profiles.PIN_CODE + " TEXT NOT NULL,"
                + SobrContract.Profiles.PIN_CODE_ON_BOOT + " TEXT NOT NULL,"
                + SobrContract.Profiles.PHONE_STATUS + " TEXT NOT NULL,"
                + SobrContract.Profiles.BASE_PHONE_NUMBER + " TEXT NOT NULL,"
                + SobrContract.Profiles.SECOND_PHONE_NUMBER + " TEXT NOT NULL,"
                + SobrContract.Profiles.THIRD_PHONE_NUMBER + " TEXT NOT NULL,"
                + SobrContract.Profiles.BALANCE_THRESHOLD + " TEXT NOT NULL,"
                + SobrContract.Profiles.BALANCE_QUERY_CODE + " TEXT NOT NULL,"

                + SobrContract.Profiles.SOBR_ASSIST_LOGIN + " TEXT NOT NULL,"
                + SobrContract.Profiles.SOBR_ASSIST_PASSWORD + " TEXT NOT NULL,"

                + SobrContract.Profiles.COMMAND_123 + " TEXT NOT NULL,"
                + SobrContract.Profiles.COMMAND_456 + " TEXT NOT NULL,"
                + SobrContract.Profiles.COMMAND_789 + " TEXT NOT NULL,"
                + SobrContract.Profiles.COMMAND_666 + " TEXT NOT NULL,"
                + SobrContract.Profiles.COMMAND_777 + " TEXT NOT NULL,"
                + SobrContract.Profiles.COMMAND_999 + " TEXT NOT NULL,"
                + SobrContract.Profiles.COMMAND_09 + " TEXT NOT NULL,"
                + SobrContract.Profiles.COMMAND_911 + " TEXT NOT NULL,"
                + SobrContract.Profiles.COMMAND_123_TITLE + " TEXT default NULL,"
                + SobrContract.Profiles.COMMAND_456_TITLE + " TEXT default NULL,"
                + SobrContract.Profiles.COMMAND_789_TITLE + " TEXT default NULL,"
                + SobrContract.Profiles.COMMAND_777_TITLE + " TEXT default NULL,"
                + SobrContract.Profiles.COMMAND_999_TITLE + " TEXT default NULL,"
                + SobrContract.Profiles.COMMAND_911_TITLE + " TEXT default NULL,"
                + SobrContract.Profiles.GPS_RECEIVER + " TEXT NOT NULL,"
                + SobrContract.Profiles.REPORT_ON_MOVE + " TEXT NOT NULL,"
                + SobrContract.Profiles.SHOCK_SENSOR + " TEXT NOT NULL,"
                + SobrContract.Profiles.IMMOBILIZER + " TEXT NOT NULL,"
                + SobrContract.Profiles.FIFTH_PHONE_NUMBER + " TEXT DEFAULT '',"
                + SobrContract.Profiles.GSM510_WORK_MODE + " TEXT DEFAULT '0',"
                + SobrContract.Profiles.PREHEATER + " TEXT DEFAULT 'false',"
                + SobrContract.Profiles.CHANELS + " INTEGER DEFAULT 0,"
                + SobrContract.Profiles.CMD1 + " TEXT DEFAULT '',"
                + SobrContract.Profiles.KEY1 + " TEXT DEFAULT '',"
                + SobrContract.Profiles.CMD2 + " TEXT DEFAULT '',"
                + SobrContract.Profiles.KEY2 + " TEXT DEFAULT '',"
                + SobrContract.Profiles.CMD3 + " TEXT DEFAULT '',"
                + SobrContract.Profiles.KEY3 + " TEXT DEFAULT '',"
                + SobrContract.Profiles.CMD4 + " TEXT DEFAULT '',"
                + SobrContract.Profiles.KEY4 + " TEXT DEFAULT '',"
                + SobrContract.Profiles.ALARM + " TEXT DEFAULT 'false'"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if(oldVersion == 7 && newVersion == 8){
    		StringBuilder sb = new StringBuilder();
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
    			.append(SobrContract.Profiles.FIFTH_PHONE_NUMBER).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
    			.append(SobrContract.Profiles.GSM510_WORK_MODE).append(" TEXT DEFAULT '0'");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.PREHEATER).append(" TEXT DEFAULT 'false'");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.CHANELS).append(" INTEGER DEFAULT 0");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.CMD1).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.KEY1).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.CMD2).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.KEY2).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.CMD3).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.KEY3).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.CMD4).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.KEY4).append(" TEXT DEFAULT ''");
    		db.execSQL(sb.toString());
    		
    		sb.setLength(0);
    		sb.append("ALTER TABLE ").append(PROFILES).append(" ADD COLUMN ")
				.append(SobrContract.Profiles.ALARM).append(" TEXT DEFAULT 'false'");
    		db.execSQL(sb.toString());
    	}else{
	        db.execSQL("DROP TABLE IF EXISTS " + PROFILES);
	        onCreate(db);
    	}
    }
}
