package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FolderItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.WarehouseNew;

public class OrgMatrixAdapter extends MatrixBaseAdapter {
	List<OrgMatrixItem> orgMatrix;
	List<FolderItem> folders;
	String orgTag;
	public OrgMatrixAdapter(WarehouseNew warehouse, List<FolderItem> folders, List<OrgMatrixItem> orgMatrix, String orgTag) {
		super(warehouse);
		this.folders = folders;
		this.orgMatrix = orgMatrix;
		this.orgTag = orgTag;
	}
	
	@Override
	public String getName() {
		return "OrgMatrixAdapter" + orgTag;
	}

	@Override
	protected List<MatrixItem> getMatrixItems() {
		ArrayList<MatrixItem> result = new ArrayList<MatrixItem>();
		
		if(folders == null) {
			for(OrgMatrixItem i : orgMatrix) {
				MatrixItem mi = new MatrixItem();
				mi.id = i.id;
				result.add(mi);
			}
			
			return result;
		}
		
		String[] selection = { "" };
		String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
		StringBuilder sql = new StringBuilder(); 
		sql.append("SELECT id FROM ").append(priceTable).append(" WHERE folderid=?");
		SQLiteDatabase database = DataBaseManager.getDataBase();
		SQLiteCursor cursor = null;
		
		try {
			cursor = (SQLiteCursor) database.rawQuery(sql.toString(), selection);
			
			for(FolderItem fi : folders){
				selection[0] = Integer.toString(fi.folderID);
				
				if (cursor.requery() && cursor.moveToFirst()){
					do{
						String id = cursor.getString(0);
						if( orgMatrix != null ) {
							boolean contains = false;
							for(OrgMatrixItem i : orgMatrix)
								if(i.id.equals(id)) {
									contains = true;
									break;
								}
							if( !contains )
								continue;
						}
						
						MatrixItem item = new MatrixItem();
						item.id = id;
						result.add(item);
					}while(cursor.moveToNext());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally{
			if ( cursor != null)
				cursor.close();
		}
		return result;
	}
}
