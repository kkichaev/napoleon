package com.keeper.db;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.keeper.KeeperApp;
import com.keeper.utils.Crypto;

public class DataBaseManager extends SQLiteOpenHelper {
	private final static String DATA_BASE_NAME = "keeper";
	private static final int DATABASE_VERSION = 23;
	private static final String D_TAG = "DataBaseManager"; 
	 
	public DataBaseManager(Context context) {
		super(context, new File(KeeperApp.getAppDataDir(),DATA_BASE_NAME).getAbsolutePath(),
				null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(D_TAG, "onCreate");
		createTableGroup(db);
		createTableConfig(db);
		createTableUri(db);
		createTableAccounts(db);
	}

	private void createTableAccounts(SQLiteDatabase db) {
		String sql;
		sql = "CREATE TABLE accounts ( " +
			"_id INTEGER PRIMARY KEY, uri_id INTEGER NOT NULL, login TEXT NOT NULL, " +
			"passw TEXT, created INTEGER" +
			", modified INTEGER, deleted INTEGER)";
		db.execSQL(sql);
	}

	private void createTableUri(SQLiteDatabase db) {
		String sql;
		sql = "CREATE TABLE uri (" +
			"_id INTEGER PRIMARY KEY, group_id INTEGER NOT NULL, uri TEXT NOT NULL, "+
			"alias TEXT, created INTEGER" +
			", modified INTEGER, deleted INTEGER)";
		db.execSQL(sql);
	}

	private void createTableConfig(SQLiteDatabase db) {
		String sql;
		sql = "CREATE TABLE config (key TEXT PRIMARY KEY, value TEXT)";
		db.execSQL(sql);
		
		try{
			StringBuilder sbCryptoKey = new StringBuilder();
			sbCryptoKey.append("1").append("2").append("3").append("4");
			ContentValues cv = new ContentValues();
			
			cv.put("key", "password");
			cv.put("value", Crypto.encrypt(sbCryptoKey.toString(), sbCryptoKey.toString()));
			db.insert("config", null, cv);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void createTableGroup(SQLiteDatabase db) {
		String sql = "CREATE TABLE [group] (" +
			"_id INTEGER PRIMARY KEY, name TEXT NOT NULL, created INTEGER" +
			", modified INTEGER, deleted INTEGER)";
		db.execSQL(sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(D_TAG, "onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS [group]");
		db.execSQL("DROP TABLE IF EXISTS config");
		db.execSQL("DROP TABLE IF EXISTS uri");
		db.execSQL("DROP TABLE IF EXISTS accounts");
		onCreate(db);
	}
	
	public void clearTables(){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM [group]");
		db.execSQL("DELETE FROM uri");
		db.execSQL("DELETE FROM accounts");
	}
}
