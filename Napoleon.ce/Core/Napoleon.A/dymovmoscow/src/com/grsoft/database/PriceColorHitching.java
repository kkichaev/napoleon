package com.grsoft.database;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceColor;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PriceColorHitching extends Hitching {
	SQLiteStatement stmt = null;
	
	public PriceColorHitching() {
		super(PriceColor.class, "PriceColor");
	}
	
	@Override
	public void onStart() {
		try {
			Price p = new Price();
			DataBaseManager.getDataBase().rawQuery("update " + p.getTableName() + " set color=0", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnd() {
		if( stmt != null )
			stmt.close();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		PriceColor pc = (PriceColor)rawObject.createDataObject(PriceColor.class);
		if( stmt == null ) {
			Price p = new Price();
			DbWriter.checkDBTable(p.getClass());
			String sql = "update " + p.getTableName() + " set color=? where id =?";
			stmt = DataBaseManager.getDataBase().compileStatement(sql.toString());
		}
		
		if( stmt != null ) {
			stmt.clearBindings();
			stmt.bindLong(1, pc.color);
			stmt.bindString(2, pc.id);

			stmt.execute();
		}
	}
}
