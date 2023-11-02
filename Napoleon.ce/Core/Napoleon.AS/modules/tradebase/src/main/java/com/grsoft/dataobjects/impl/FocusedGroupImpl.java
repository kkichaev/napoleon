package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FocusedGroup;
import com.grsoft.dataobjects.FocusedGroupItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderFocusedFolder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;

public class FocusedGroupImpl extends DbObject<FocusedGroup> {
	
	public static String OBJECT_NAME = "FocusedGroup";
	
	/**
	 * Возвращает только папки с товаром
	 * @return папки в которых есть хотя бы одна позиция в прайсе
	 */
	public List<FocusedGroupItem> getItems() {
		List<FocusedGroupItem> items = new ArrayList<FocusedGroupItem>();
		
		if( data.items != null ) {
			// добавляем только папки с товаром, для этого проверим их перед вставкой
			SQLiteDatabase db = DataBaseManager.getDataBase();
			String tableName = DataObjectInfo.getInstance().getTableName(Price.class);
			DbWriter.checkDBTable(DbObject.getDataType(Price.class));
			SQLiteStatement stmt = db.compileStatement("SELECT id from " + tableName + " WHERE folderID = ? LIMIT 1");
	
			for( FocusedGroupItem fgi : data.items ) {
				stmt.bindLong(1, fgi.folderID);
				String id = null;
				try {
					id = stmt.simpleQueryForString();
				} catch(Exception e){
					continue;
				}
				
				if( id != null )
					items.add(fgi);
			}
			
			stmt.close();
		}
		return items;
	}
	
	public static List<FocusedGroupItem> getUnsettedGroups(OrderImplBase<? extends Order> doc) {
		
		FocusedGroupImpl fi = new FocusedGroupImpl();
		fi.data.id = doc.getId();
		if ( !fi.read() ) {
			fi.data.id = "";
			fi.read();
		}
		fi.close();
		
		// заполним
		List<FocusedGroupItem> res = new ArrayList<FocusedGroupItem>();
		for( FocusedGroupItem item : fi.getItems() ) {
			res.add(item);
		}
		
		// удалим что отмечено в комментариях
		for(OrderFocusedFolder ff : doc.getData().focusedFolders) {
			for(int i=0; i<res.size(); i++) {
				FocusedGroupItem item = res.get(i);
				if( item.fid.compareTo(ff.fid) == 0 ) {
					res.remove(i);
					break;
				}
			}
		}
		
		// удалим что есть в товарах 
		PriceImpl p = new PriceImpl();
		for(OrderItem oi : doc.getData().items) {
			p.getData().id = oi.id;
			p.read();
			
			for(int i=0; i<res.size(); i++) {
				FocusedGroupItem item = res.get(i);
				if( item.folderID == p.getData().folderID) {
					res.remove(i);
					break;
				}
			}			
		}
		p.close();
		return res;
	}

}
