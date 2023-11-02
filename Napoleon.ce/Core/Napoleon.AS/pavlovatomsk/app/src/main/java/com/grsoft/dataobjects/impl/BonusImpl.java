package com.grsoft.dataobjects.impl;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.Order;
import com.grsoft.napoleon.BonusDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

public class BonusImpl extends OrderImplBase<Bonus> {
	
	public static BonusImpl find(OrderImplBase<? extends Order> src) {
		if (src == null || src.getData() == null || src.getData().created == null)
			return null;

		BonusImpl bi = new BonusImpl();
		Bonus b = bi.getData();
		
		DbWriter.checkDBTable(b.getClass());
		String where = "\"order\"=" + Long.toString(src.getData().created.getTime());
		List<Long> ids = DbReader.readIds(b.getTableName(), where, null);
		
		if(ids.size() == 0) {
			bi = null;
		} else {
			bi.read(ids.get(0));
		}
		
		return bi;
	}
	
	public static BonusImpl fromOrder(OrderImplBase<? extends Order> src, HashMap<String, BonusDef> bonuses) {
		Order o = src.getData();
		BonusImpl bi = find(src);
		Bonus b = null;
		if( bi == null ) {
			bi = new BonusImpl();
			b = bi.getData();
			b.order = o.created;
			bi.initSilent(src.getId(), new GpsCoord(o.latitude, o.longitude, o.stltime));
			b.date = o.date;
			bi.write();
		} else {
			b = bi.getData();
		}
		
		if(bi.isEditable()) {
			b.updateItems(o.items, bonuses);		
			if( b.items.size() > 0) {
				bi.write();
				bi.close();
			} else {
				bi.delete();
				bi.close();
				bi = null;
			}
		} else
			bi.close();
		return bi;
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {

	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		delete();
		close();
	}

	@Override public CreatableDocument<Bonus> createInstance() { return new BonusImpl(); }

	@Override
	public void open(Context context) {
		BonusDetail.open(context, this);
	}
}
