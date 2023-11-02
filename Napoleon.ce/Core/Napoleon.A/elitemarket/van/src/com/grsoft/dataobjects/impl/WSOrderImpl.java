package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.napoleon.CreateOrder;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.WSOrderDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class WSOrderImpl extends OrderImplBase<WSOrder> {
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, (DbObject<WSOrder>) this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		CreateOrder.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<WSOrder> createInstance() {
		return new WSOrderImpl();
	}
	
	@Override public long sum() { return 0; }

	@Override
	public void open(Context context) {
		WSOrderDetail.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		
		long r = find(data.created);
		
		if(r == ExtrasConst.INVALID_ID){
			fill();
			write();
		}else
			read(r);
		
		close();
		
		return true;
	}

	private void fill() {
		Sales dlv = new Sales();
		DbReader r = new DbReader();
		String tbl = DataObjectInfo.getInstance().getTableName(dlv.getClass());

		HashMap<String, Integer> ordItems = new HashMap<String, Integer>();

		boolean bdo = r.select(dlv, tbl, "created >= " + Util.getDate().getTime());
		while (bdo) {
			for (OrderItem i : dlv.items) {
				int qty = 0;
				if (ordItems.containsKey(i.id))
					qty = ordItems.get(i.id);

				ordItems.put(i.id, qty + i.qty);
			}

			bdo = r.selectNext(dlv);
		}
		r.close();
		
		for (Map.Entry<String, Integer> e : ordItems.entrySet()) {
			int pq = e.getValue() / Consts.QTY_SCALE;
			int qty = (pq / 10) * 10;
			if( (pq % 10) > 3 )
				qty += 10;
			if( qty > 0 ) {
				OrderItem item = new OrderItem();
				// округлим до целых
				item.qty = qty * Consts.QTY_SCALE;
				item.id = e.getKey();

				data.items.add(item);
			}
		}
	}
	
	static public long find(Date d) {
		long ret = ExtrasConst.INVALID_ID;
		long from, to;
		from = d.getTime();
		
		// перейдем на начало дня
		from -= (from % (1000 * 3600 * 24));
		
		// начало следующего дня
		to = from + (1000 * 3600 * 24);
		String tn = DataObjectInfo.getInstance().getTableName(WSOrder.class);
		String condition = "created >= " + Long.toString(from) + " AND created < " + Long.toString(to);
		DbWriter.checkDBTable(getDataType(WSOrder.class));
		List<Long> ids = DbReader.readIds(tn, condition, null);
		
		if( ids.size() > 0 )
			ret = ids.get(0);
		return ret;
	}
}
