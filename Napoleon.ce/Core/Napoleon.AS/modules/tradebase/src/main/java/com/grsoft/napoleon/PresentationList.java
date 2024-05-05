package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;

import android.database.Cursor;

public class PresentationList extends ArrayList<PresentationData> {
	private static final long serialVersionUID = 1L;
	private String where = "";
	
	public String getImage(long rowid) {
		for(PresentationData pd : this)
			if( pd.rowid == rowid )
				return pd.image;
	
		return null;
	}
	
	public PresentationData getData(long rowid) {
		for(PresentationData pd : this)
			if( pd.rowid == rowid )
				return pd;
	
		return null;
	}
	
	public int indexOf(long rowid) {
		int idx = 0;
		for(PresentationData pd : this) {
			if( pd.rowid == rowid )
				return idx;
			idx++;
		}
	
		return -1;
	}
	
	void sort(final boolean solidPrice) {
		Collections.sort(this, new Comparator<PresentationData>(){
			@Override
			public int compare(PresentationData arg0, PresentationData arg1) {
				if(!solidPrice) {
					int res = arg0.folder - arg1.folder; 
					if( res != 0 )
						return res;
				}
				return arg0.name.compareTo(arg1.name);
			}			
		});
	}
	
	public void fill(boolean solidPrice){
		clear();
		
		String table = DataObjectInfo.getInstance().getTableName(Present.class);
		String ptable = DataObjectInfo.getInstance().getTableName(Price.class); 
		
		if(DbWriter.isTableExists(table)){
			StringBuilder sql = new StringBuilder();
			sql.append("select price.folderid, price.rowid, price.name, ph.photopath, price.id from \"")
				.append(ptable)
				.append("\" inner join \"")
				.append(table)
				.append("\" ph on price.id = ph.id");
			
			if(where != null && where.trim().length() > 0)
				sql.append(" where ").append(where);

			Cursor cursor = null;
			try{
				cursor = DataBaseManager.getDataBase().rawQuery(sql.toString(), null);					
				while (cursor.moveToNext()) {
					long rowid = cursor.getLong(1);
					int folderid = cursor.getInt(0);
					String name = cursor.getString(2);
					String id = cursor.getString(4);
					String image = cursor.getString(3);
					add(new PresentationData(rowid, folderid, name, image, id));
				}
			} finally {
				if( cursor != null )
					cursor.close();
			}
			
			sort(solidPrice);
		}
	}

	public void setWhereStr(String whereStr) { this.where = whereStr; }
	
	public void filter(long rowid) {
		List<PresentationData> result = new ArrayList<PresentationData>();
		
		for(PresentationData pd : this) {
			if( pd.rowid == rowid ) {
				result.add(pd);
				break;
			}
		}
		
		clear();
		addAll(result);
	}
}
