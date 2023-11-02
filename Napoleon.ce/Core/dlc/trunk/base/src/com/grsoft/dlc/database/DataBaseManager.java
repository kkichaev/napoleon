package com.grsoft.dlc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseManager extends SQLiteOpenHelper {
	private final static String DATA_BASE_NAME = "dlc";
	private static final int DATABASE_VERSION = 11;
	private static final String TAG = "DataBaseManager"; 

	
	public DataBaseManager(Context context) {
		super(context, DATA_BASE_NAME,null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate");
		createAllowedApp(db);
	}
	
	private void createAllowedApp(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(AllowedApp.TABLE_NAME).append(" (")
		.append(AllowedApp._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
		.append(AllowedApp.CLASSNAME).append(" TEXT NOT NULL UNIQUE")
		.append(");");
		
		db.execSQL(sb.toString());

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", AllowedApp.TABLE_NAME));
		onCreate(db);
	}
}
