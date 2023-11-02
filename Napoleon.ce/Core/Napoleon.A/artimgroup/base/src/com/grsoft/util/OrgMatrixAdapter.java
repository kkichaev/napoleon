package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FolderItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.WarehouseNew;

public class OrgMatrixAdapter extends MatrixBaseAdapter {
	List<FolderItem> folders;
	String orgTag;
	public OrgMatrixAdapter(WarehouseNew warehouse, List<FolderItem> folders, String orgTag) {
		super(warehouse);
		this.folders = folders;
		this.orgTag = orgTag;
	}
	
	@Override
	public String getName() {
		return "OrgMatrixAdapter" + orgTag;
	}

	@Override
	protected List<MatrixItem> getMatrixItems() {
		ArrayList<MatrixItem> result = new ArrayList<MatrixItem>();
		
		String[] selection = { "" };
		String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
		StringBuilder sql = new StringBuilder(); 
		sql.append("SELECT id FROM ").append(priceTable).append(" WHERE folderid=?");
		SQLiteDatabase database = DataBaseManager.getDataBase();
		SQLiteCursor cursor = null;
		SQLiteStatement sts = null;
		
		try {
			sts = database.compileStatement("select id from folder where fid = ?");
			
			for(FolderItem fi : folders){
				try{
					sts.bindString(1, fi.fid);
					selection[0] = Long.toString(sts.simpleQueryForLong());
				}catch(Exception e){
					continue;
				}
				
				cursor = (SQLiteCursor) database.rawQuery(sql.toString(), selection);
				
				if (cursor != null){
					while(cursor.moveToNext()){
						String id = cursor.getString(0);
						MatrixItem item = new MatrixItem();
						item.id = id;
						result.add(item);
					};
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally{
			if ( cursor != null)
				cursor.close();
			if(sts != null)
				sts.close();
		}
		
		return result;
	}
}
