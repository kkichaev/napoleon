package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import android.database.sqlite.SQLiteStatement;

@TableInfo(name="WhQty", keyFields="id,idStore,year")
@ServerInfo(name="PriceQty")
public class WhData extends DataObject {
	public String idStore = "";
	
	public String id = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;

	@Scale(value=Consts.QTY_SCALE)
	public int rezerv;

	public int year;
	
	public List<WhDataItem> items = new ArrayList<WhDataItem>();

	public static void updateQty(String id, HashMap<String, Integer> chQty) {
		
		String stmt = "Update " + new WhData().getTableName() + " set qty = qty + ? where id='" + id + "' and idStore = ?";  

		try {
			SQLiteStatement s = DataBaseManager.getDataBase().compileStatement(stmt);
			for(Entry<String, Integer> kv : chQty.entrySet()) {
				int qty = kv.getValue();
				if( qty != 0 ) {
					String ids = kv.getKey();
					if(ids.length() == 0) {
						PriceImpl pi = new PriceImpl();
						Price p = pi.getData();
						p.id = id;
						pi.read();
						pi.updateQty(qty);
						pi.close();
					} else {
						s.clearBindings();
						s.bindLong(1, qty);
						s.bindString(2, ids);
						s.execute();
					}
				}
			}
			
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
