package com.grsoft.dataobjects.impl;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Quality;
import com.grsoft.dataobjects.QualityItem;

public class QualityImpl extends DbObject<Quality>{
	public static QualityItem getItem(String id){
		QualityItem result = null;
		SQLiteDatabase db = DataBaseManager.getDataBase();
		android.database.Cursor cursor = db.query(DataObjectInfo.getInstance().getTableName(
				DbObject.getDataType(Quality.class)), new String[]{"name"}, 
				null, null, null, null, null);
		
		while(cursor.moveToNext()){
			QualityImpl qualityImpl = new QualityImpl();
			qualityImpl.getData().name = cursor.getString(cursor.getColumnIndex("name"));
			qualityImpl.read();
			qualityImpl.close();
			
			for(QualityItem item: qualityImpl.getData().items){
				if (item.id.equals(id)){
					result = item;
					break;
				}
			}
		}
		
		cursor.close();
		
		return result;
	}
}
