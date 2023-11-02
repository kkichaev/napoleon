/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Маршрут
 *
 * kki   16/02/2011   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgFolder",keyFields="name")
public class OrgFolders extends DataObject
{
	public String name = "";
	public Date dateFrom = new Date();
	
	public List<OrgFolderItem> items = new ArrayList<OrgFolderItem>();
	
	public boolean IsActive(Date date) {
		char sym = name.charAt(0);
		if (!Character.isDigit(sym)) 
			return true;

		int cw = Character.digit(sym, 10);
		int wi = 1;
		long stTime = dateFrom.getTime();
		long cTime = date.getTime();
		if (cTime > stTime) {
			final long week = 1000 * 3600 * 24 * 7;
			long diff = cTime - stTime;
			if (diff >= week)
				wi = (int) ((diff / week) % 4 + 1);
			else
				wi = 1;
		}
		return (cw == wi);
	}
}
	