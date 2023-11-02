/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Менеджер баз данных хранит ассоциации имя - база данных
 *
 * kki   07/10/2010   creating
 */
package com.grsoft.database;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.util.debug.Path;

public class DataBaseManager
{
	private static SQLiteDatabase database;
	
	public static List<String> dontDropTableNames = new ArrayList<String>();
	
	public static void init() {
		database = SQLiteDatabase.openOrCreateDatabase(Path.getDataBasePath(), null);
		database.setLocale(Locale.getDefault());
	}

	public static void closeAndInit(){
		database.close();
		init();
	}

	public static void clearBase() {
		ArrayList<String> tables = new ArrayList<String>();
		TableName tn = new TableName();

		DbReader r = new DbReader();
		boolean bdo = r.select(tn, "sqlite_master", "type='table'");
		while( bdo ) {
			tables.add(tn.name);
			bdo = r.selectNext(tn);
		}
		r.close();
		
		for( String name: tables) {
			if( dontDropTableNames.contains(name))
				continue;
			database.execSQL("DROP TABLE '" + name + "'");
		}
	}
	
	public static SQLiteDatabase getDataBase(){
		if (database == null)
			init();
		
		return database;
	}
}

class TableName extends DataObject {
	public String name;
}
