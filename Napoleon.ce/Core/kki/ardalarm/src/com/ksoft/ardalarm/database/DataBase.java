package com.ksoft.ardalarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
	public final static int DATABASE_VERSION = 3;
	public final static String NAME = "ardalarm.db";

	public DataBase(Context context) {
		super(context, NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ").append(TimeAlarm.TABLE_NAME).append("(")
			.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
			.append(TimeAlarm.NAME).append(" TEXT, ")
			.append(TimeAlarm.PERIOD).append(" INTEGER, ")
			.append(TimeAlarm.HOUR).append(" INTEGER, ")
			.append(TimeAlarm.MINUTE).append(" INTEGER)");
		db.execSQL(sql.toString());	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TimeAlarm.TABLE_NAME);
		onCreate(db);
	}

}
