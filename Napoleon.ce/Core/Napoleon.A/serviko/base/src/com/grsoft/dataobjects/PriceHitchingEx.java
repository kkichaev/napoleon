package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.PriceHitching;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.database.sqlite.SQLiteStatement;

public class PriceHitchingEx extends PriceHitching {

	SQLiteStatement stmt;
	List<KeyValue> sklads = new ArrayList<KeyValue>();
	
	@Override
	public void onStart() {
		super.onStart();
		
		WhData wd = new WhData();
		String tableName = wd.getTableName();
		DbWriter.dropTable(tableName);
		DbWriter.checkDBTable(wd.getClass());
		
		String sql = "insert or replace into \"" + tableName + "\" (qty, whCode, id) values (?, ?, ?)";
		try {
			stmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		ci.read();
		ci.close();
		
		DialogHelper.makeListWithKey(c.value, sklads, "");
	}
	
	@Override
	protected void beforeInsert(Price dobj) {
		super.beforeInsert(dobj);
		
		if( stmt == null )
			return;
		
		PriceEx pe = (PriceEx)dobj;
		for( int i=0; i<pe.whQty.size() && i<(sklads.size() - 1); i++) {
			PriceWhData whd = pe.whQty.get(i);
			if( whd.qty != 0) {
				stmt.clearBindings();
				stmt.bindLong(1, whd.qty);
				stmt.bindString(2, sklads.get(i+1).key.toString());
				stmt.bindString(3, pe.id);
				stmt.execute();
			}
		}
		pe.whQty.clear();
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if(stmt != null) {
			stmt.close();
			stmt = null;
		}
	}
}
