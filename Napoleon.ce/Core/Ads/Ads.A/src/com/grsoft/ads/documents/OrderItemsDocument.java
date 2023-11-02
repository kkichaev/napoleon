package com.grsoft.ads.documents;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.ads.database.OrderItem;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;

public abstract class OrderItemsDocument<T extends CreateDocDataObject> 
	extends CreatableDocument<T> implements Itemsable{
	public static final int DAY_TO_OLD = 7;
	protected String oldDocField = "";
	
	@Override
	public void open(Context context) {
		
	}

	public abstract List<OrderItem> getOrderItems();
	
	public void removeOldDocuments(){
		if(oldDocField.length() > 0){
			checkDBTable();
			String tableName = DataObjectInfo.getInstance().getTableName(getData().getClass());
			SQLiteDatabase dataBase = DataBaseManager.getDataBase();
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			
			calendar.add(Calendar.DAY_OF_MONTH, -DAY_TO_OLD);
			
			dataBase.delete(tableName, oldDocField + " < ?", new String[]{
					Long.toString(calendar.getTime().getTime())});
		}
	}
	
	public boolean isEditable(){
		return false;
	}
	
	
}
