package com.grsoft.util;

import java.util.ArrayList;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Categories;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategAdapter extends FoldersAdapterEx{
	public static final String CATEG_ADAPTER = "CategAdapter";  
	
	public CategAdapter(WarehouseManager warehouse) {
		super(warehouse);
	}
	
	@Override public String getName() { return CATEG_ADAPTER; }
	
	@Override
	protected void fillPriceIds(SQLiteDatabase database) {
		try{
			fprice.clear();
			
			String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
			String folderTable = getFolderTableName();
			
			if(DbWriter.isTableExists(priceTable)){
				String sql = "SELECT f.id, p.rowid, p.name, p.id FROM " + priceTable + 
						" p INNER JOIN " + folderTable + " f ON p.catid = f.fid ";
				
				String where = getWhereStr();
				
				if( where.length() > 0 ) {
					sql += " WHERE " + where;
				}
				
				Cursor cursor = database.rawQuery(sql, null);
				
				if (cursor.moveToFirst()) {
					try{
						do{
							long rowid = cursor.getLong(1);
							String id = cursor.getString(3);
							
							if( !inset( rowid, id ) )
								continue;
							
							int folderid = cursor.getInt(0);
							
							if(!fprice.containsKey(folderid))
								fprice.put(folderid, new ArrayList<PriceInfo>());
							
							PriceInfo pi = new PriceInfo(rowid, cursor.getString(2), id);
							fprice.get(folderid).add(pi);
						} while(cursor.moveToNext());
					} finally { 
						cursor.close();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override protected String getFolderTableName() { return DataObjectInfo.getInstance().getTableName(Categories.class); }
}
