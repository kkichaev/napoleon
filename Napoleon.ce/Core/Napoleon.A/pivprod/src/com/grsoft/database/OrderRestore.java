package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.documents.OrderDoc;

public class OrderRestore extends DocumentRestore {
	public OrderRestore(){
		super(OrderDoc.instance());
	}
	
	@SuppressLint("SimpleDateFormat")
	protected void makeDocReceiveCondition(String timeField, int months, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -months);
		calendar.add(Calendar.DATE, -days);
		Date begin = calendar.getTime();
		
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		setCondition(String.format(" id in (%s) and \"%s\" >= ToDate('%s 00:00:00')", collectOrgIds(),
				timeField, simpleDateFormat.format(begin)));
	}

	private String collectOrgIds() {
		SQLiteDatabase db = DataBaseManager.getDataBase();
		Cursor c = null;
		StringBuilder sb = new StringBuilder();
		
		try{
			c = db.rawQuery("select id from " + DataObjectInfo.getInstance().getTableName(Org.class), null);
			
			while(c.moveToNext()){
				if(sb.length() > 0)
					sb.append(",");
				
				sb.append("'")
					.append(c.getString(0))
					.append("'");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (c != null)
				c.close();
		}
		
		return sb.toString();
	}
}
