package com.grsoft.napoleon.modules.print.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.modules.print.util.MakeDocNumberStartegy;
import com.grsoft.util.Util;

public class DateDocNumberStrategy implements MakeDocNumberStartegy {
	
	static final String PROP_NAME = "DocumentsNumbers"; 
	
	Context context;
	
	public DateDocNumberStrategy(Context context) {
		this.context = context;
	}

	@Override
	public String makeNextDocNumber(DbObject<?> obj) {

		String table = obj.getTableName(); 
				
		AgentPrefix ap = AgentPrefix.get();

		Date now = Util.getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
		String defNumber = String.format(Locale.getDefault(), "%s%02d", sdf.format(now), 1);
		
		SharedPreferences pref = context.getSharedPreferences(PROP_NAME, Context.MODE_PRIVATE);
		String number = pref.getString(table, null);
		if( number == null ) {
			int count = 0;
			String sql = "select count(*) from [" + table + "] where created >= " + Long.toString(now.getTime());
			
			try {
				Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
				if( c.moveToNext() )
					count = c.getInt(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			number = String.format(Locale.getDefault(), "%s%02d", sdf.format(now), count+1);
		} else {
			if( number.length() == ap.prefix.length() + defNumber.length() ) {
				number = number.substring(ap.prefix.length());
				if( number.compareTo(defNumber) < 0 )
					number = defNumber;
				else
					number = Long.toString(Long.parseLong(number) + 1);
			} else
				number = defNumber;
		}
		return ap.prefix + number;
	}

	@Override
	public void saveDocNumber(String table, String number) {
		SharedPreferences pref = context.getSharedPreferences(PROP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		edit.putString(table, number);
		edit.commit();
	}

}
