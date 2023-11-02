package com.grsoft.dataobjects.impl;

import java.util.List;
import android.content.Context;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.BonusDefItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.napoleon.BonusDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

public class BonusImpl extends OrderImplBase<Bonus> {

	public static BonusImpl fromOrder(OrderImplBase<? extends Order> src, BonusDef def) {
		BonusImpl bi = new BonusImpl();
		Bonus b = bi.getData();
		
		Order o = src.getData();
		DbWriter.checkDBTable(b.getClass());
		String table = DataObjectInfo.getInstance().getTableName(Bonus.class);
		String where = "\"order\"=" + Long.toString(o.created.getTime()) + " and def='" + def.id + "'";
		List<Long> ids = DbReader.readIds(table, where, null);
		
		if( ids.size() > 0 )
			bi.read(ids.get(0));
		else {
			DataBaseManager.getDataBase().delete(bi.getTableName(), where, null);
			
			b.order = o.created;
			b.def = def.id;
			
			if(def.items != null)
				for(BonusDefItem i : def.items){
					OrderItem oi = new OrderItem();
					oi.id = i.id;
					oi.qty = i.qty;
					
					b.items.add(oi);
				}
			
			if( b.items.size() != 0 ) {
				b.order = o.created;
				bi.initSilent(src.getId(), new GpsCoord(o.latitude, o.longitude, o.stltime));
				bi.close();
			}
		}
		
		return bi;
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {

	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
	}

	@Override public CreatableDocument<Bonus> createInstance() { return new BonusImpl(); }

	@Override
	public void open(Context context) {
		BonusDetail.open(context, this);
	}
}
