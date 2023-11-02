package com.grsoft.napoleon;

import java.util.Date;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.util.ExtrasConst;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class VisitEditEx extends VisitEditNew {
	@Override
	protected void onResume() {
		update();
		super.onResume();
	}

	private void update() {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		long sr = p.getLong(ScriptImplEx.CURRENT_SCRIPT_ROW_ID, ExtrasConst.INVALID_ROWID);
		visit.read(visit.getRowid(), cache);
		Date ph = ((VisitEx)visit.getData()).photoDate;
		
		if(sr != ExtrasConst.INVALID_ROWID && ph != null && ph.getTime() > 0 && ph.getTime() != visit.getRowid()){
			ScriptImplEx s = new ScriptImplEx();
			s.read(sr);
			s.close();
			
			if(s.getData().items.size() > 0){
				SQLiteDatabase db =  DataBaseManager.getDataBase();
				db.beginTransaction();
				
				try{
					ScriptItem si = s.getData().items.get(0);
					si.date = ph;
					s.write();
					s.close();
					
					ContentValues cv = new ContentValues();
					cv.put("created", ph.getTime());
					int res = db.update(visit.getTableName(), cv, "[created] = ?", new String[]{ Long.toString(visit.getRowid())});
					
					if(res > 0 && visit.read(ph.getTime(), false))
						db.setTransactionSuccessful();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					db.endTransaction();
				}
			}
		}
		
		visit.close();
	}
}
