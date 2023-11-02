package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DailyPlan;
import com.grsoft.dataobjects.DailyPlanItem;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceMap;
import com.grsoft.napoleon.DailyPlanEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

import android.content.Context;

public class DailyPlanImpl extends CreatableDocument<DailyPlan> {

	@Override public void open(Context context) { DailyPlanEdit.open(context, this); }
	
	@Override
	public void postInit() {
		int ch = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); 
		if(ch > 10) 
			data.date = new Date(data.date.getTime() + 24 * 3600 * 1000);
		
		String stmt = "select max(date) from " + data.getTableName() + " where id='" + data.id + "' and date > " + Long.toString(data.date.getTime()); 
		try {
			android.database.Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
			if(c.moveToNext()) {
				long maxDate = c.getLong(0);
				if(maxDate > data.date.getTime())
					data.date = Util.getDayStart(new Date(maxDate + 24 * 3600 * 1000));
			}
			c.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public long countTotal() {
		long ret = 0;
		for(DailyPlanItem i : data.items)
			ret += i.weight;
		return ret;
	}
	
	@Override
	public boolean isEditable() {
		Date d = Util.getDate();
		return data.date.compareTo(d) >= 0;
	}

	public static long getPlan(String orgId, Date date) {
		long ret = ExtrasConst.INVALID_ROWID;
		String stmt = "select created from " + (new DailyPlan()).getTableName() + " where id='" + orgId + "' and date = " + 
				Long.toString(Util.getDayStart(date).getTime());
		
		try {
			android.database.Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
			if(c.moveToNext()) {
				ret = c.getLong(0);
			}
			c.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Map<Folder, Integer> checkDoc(OrderImpl doc) {
		Map<Folder, Integer> ret = new HashMap<Folder, Integer>();
		FolderTree folders = new FolderTree();
		PriceMap price = new PriceMap();
		folders.load();
		
		for(DailyPlanItem pi: data.items) {
			Folder f = folders.getFolder(pi.id);
			if(f == null)
				continue;
			ret.put(f, pi.weight);
		}
		
		for(OrderItem oi : doc.getData().items) {
			Price p = price.get(oi.id);
			Folder f = folders.getFolder(p.folderID);
			if(f == null)
				continue;
			
			Integer w = ret.get(f);
			if(w != null) {
				w -= oi.qty * p.weight / Consts.QTY_SCALE;
				if(w <= 0)
					ret.remove(f);
				else
					ret.put(f, w);
			}
		}
		
		return ret;
	}
}
