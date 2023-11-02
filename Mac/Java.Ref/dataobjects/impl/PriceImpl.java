/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Price(список доступных товаров)
 * для работы с базой
 *
 * kki   19/10/2010   creating
 */
package com.grsoft.dataobjects.impl;


import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Price;

public class PriceImpl extends DbObject<Price>
{
	public void updateQty(int qty) {
		data.qty += qty;
		write();		
	}

	public static int getPhotoCount() {
		SQLiteDatabase db = DataBaseManager.getDataBase();
		android.database.Cursor c = db.rawQuery(
				"SELECT COUNT(*) FROM price WHERE photoPath NOT NULL", null);
		
		int result = 0;
		
		if (c.moveToFirst())
			result = c.getInt(0);
		
		c.close();
		
		return result;
	}
}
