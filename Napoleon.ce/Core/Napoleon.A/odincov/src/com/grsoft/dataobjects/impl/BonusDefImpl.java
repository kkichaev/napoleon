package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.DataObjectInfo;

public class BonusDefImpl extends DbObject<BonusDef> {
	public interface BonusAction {
		boolean doAction(BonusDef bonus);
	}
	
	@SuppressLint("DefaultLocale")
	public static void loadBonus(Date date, BonusAction action) {
		if( date == null )
			date = new Date();
		date = roundDate(date);
		BonusDef data = new BonusDef();
		String table = DataObjectInfo.getInstance().getTableName(data.getClass());
		DbReader r = new DbReader();

		String where = String.format("start <= %d and till >= %d", date.getTime(), date.getTime());
		boolean bdo = r.select(data, table, where);
		while( bdo ) {
			
			if( action.doAction(data) == false )
				break;
			data = new BonusDef();
			bdo = r.selectNext(data);
		}
		r.close();
	}
	
	private static Date roundDate(Date date){
		Calendar cal = Calendar.getInstance();       
	    cal.setTime(date);      
	    cal.set(Calendar.HOUR_OF_DAY, 0);            
	    cal.set(Calendar.MINUTE, 0);                 
	    cal.set(Calendar.SECOND, 0);                 
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}
}
