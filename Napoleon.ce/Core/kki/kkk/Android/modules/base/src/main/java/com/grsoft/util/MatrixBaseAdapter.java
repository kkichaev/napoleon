package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.Warehouse;

public abstract class MatrixBaseAdapter extends FoldersAdapter {
	public MatrixBaseAdapter(Warehouse warehouse) {
		super(warehouse);
	}
	
	protected abstract List<? extends MatrixItem> getMatrixItems();
	
	/**
	 * к названию талицы добавлен псевдоним price
	 * @param sql
	 * @param priceTable
	 */
	protected void makeStmt(StringBuilder sql, String priceTable ) {
		sql.append("SELECT price.folderid, price.rowid, price.name, price.id FROM ").append(priceTable).append(" WHERE price.id=?");
		
		String selection = getWhereStr();		
		if(selection.length() > 0) {
			sql.append(" AND ");		
			sql.append(selection);
		}
	}
	
	protected void addPriceInfo(long rowid, int folderid, String name, String id) {
		if(!fprice.containsKey(folderid))
			fprice.put(folderid, new ArrayList<PriceInfo>());
			
		PriceInfo pi = new PriceInfo(rowid, name, id);
		fprice.get(folderid).add(pi);
	}
	
	@Override
	protected void fillPriceIds(SQLiteDatabase database) {
		try{
			fprice.clear();
			String priceTable = getPriceTableName();
			
			if(DbWriter.isTableExists(priceTable)){
				String[] keys = { "" };

				StringBuilder sql = new StringBuilder();
				makeStmt(sql, priceTable + " price");
				
				SQLiteCursor cursor = null;
				
				try {
					cursor = (SQLiteCursor) database.rawQuery(sql.toString(), keys);
					for(MatrixItem mi : getMatrixItems()){
						keys[0] = mi.id;
						cursor.setSelectionArguments(keys);
						cursor.requery();
						
						if(cursor.moveToNext()){
							long rowid = cursor.getLong(1);
							String id = cursor.getString(3);
							if( !inset(rowid, id) )
								continue;
							
							addPriceInfo(rowid, cursor.getInt(0), cursor.getString(2), mi.id);
						}
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
}
