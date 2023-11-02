package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.FoldersAdapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;

public class PromoMatrix extends FoldersAdapter{
	public static String name = "PromoMatrix";
	
	public PromoMatrix(WarehouseNewW warehouse) {
		super(warehouse);
	}

	protected void makeStmt(StringBuilder sql, String priceTable ) {
		sql.append("SELECT price.folderid, price.rowid, price.name, price.id FROM ").append(priceTable).append(" WHERE promoCost > 0");
	}
	
	public static boolean havePromo() {
		int count = 0;
		Price p = new Price();
		String sql = "select count(*) from " + p.getTableName() + " where promoCost > 0";
		try {
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
			if(c.moveToNext())
				count = c.getInt(0);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return count > 0;
	}
	
	@Override
	protected void fillPriceIds(SQLiteDatabase database) {
		try{
			fprice.clear();
			String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
			
			if(DbWriter.isTableExists(priceTable)){
				StringBuilder sql = new StringBuilder();
				makeStmt(sql, priceTable + " price");
				
				SQLiteCursor cursor = null;
				
				try {
					cursor = (SQLiteCursor) database.rawQuery(sql.toString(), null);
					while(cursor.moveToNext()){
						long rowid = cursor.getLong(1);
						String id = cursor.getString(3);
						if( !inset(rowid, id) )
							continue;
						
						addPriceInfo(rowid, cursor.getInt(0), cursor.getString(2), id);
					}
				} catch(Exception e) {
					e.printStackTrace();
				} finally{
					if ( cursor!= null)
						cursor.close();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected void addPriceInfo(long rowid, int folderid, String name, String id) {
		if(!fprice.containsKey(folderid))
			fprice.put(folderid, new ArrayList<PriceInfo>());
			
		PriceInfo pi = new PriceInfo(rowid, name, id);
		fprice.get(folderid).add(pi);
	}
}
