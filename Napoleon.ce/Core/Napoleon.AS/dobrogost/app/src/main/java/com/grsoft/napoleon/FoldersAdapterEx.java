package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceFolderItem;
import com.grsoft.dataobjects.impl.PriceFolderImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class FoldersAdapterEx extends FoldersAdapter {
	
	Map<String, Integer> folderids = new HashMap<String, Integer>();
	public FoldersAdapterEx(WarehouseManager warehouse) {
		super(warehouse);
		
		DataTraveler.travel(Folder.class, new DataTraveler.Travel<Folder>() {

			@Override
			public boolean travel(DataTraveler<Folder> item) {
				if (!folderids.containsKey(item.data.fid))
					folderids.put(item.data.fid, item.data.id);
				return true;
			}
		}, null);
	}

	@Override
	protected void fillPriceIds(SQLiteDatabase database) {
		PriceFolderImpl priceFolder = new PriceFolderImpl();
		
		try{
			fprice.clear();
			String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
			
			if(DbWriter.isTableExists(priceTable)){
				SQLiteQueryBuilder fPriceQuery = new SQLiteQueryBuilder();
				fPriceQuery.setDistinct(true);
				fPriceQuery.setTables(priceTable);
				
				Cursor cursor = fPriceQuery.query(database, new String[] {"folderid", 
					"rowid", "name", "id"}, getWhereStr(), null, null, null, null);
				
				if (cursor.moveToFirst()) {
					try{
						do{
							long rowid = cursor.getLong(1);
							String id = cursor.getString(3);
							int folderid = cursor.getInt(0);
							String name = cursor.getString(2);
							
							if( !inset( rowid, id, folderid ) )
								continue;
							
							priceFolder.getData().id = id;
							
							if (priceFolder.read()) {
								for (PriceFolderItem i : priceFolder.getData().items) {
									if (folderids.containsKey(i.fid)) {
										int fid = folderids.get(i.fid); 
										
										addPriceInfo(rowid, id, fid, name);
									}
								}
							}
							
							addPriceInfo(rowid, id, folderid, name);
						} while(cursor.moveToNext());
					} finally { 
						cursor.close();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally { 
			priceFolder.close();
		}
	}

	private void addPriceInfo(long rowid, String id, int folderid, String name) {
		if(!fprice.containsKey(folderid))
			fprice.put(folderid, new ArrayList<PriceInfo>());
		
		PriceInfo pi = new PriceInfo(rowid, name, id);
		fprice.get(folderid).add(pi);
	}
}
