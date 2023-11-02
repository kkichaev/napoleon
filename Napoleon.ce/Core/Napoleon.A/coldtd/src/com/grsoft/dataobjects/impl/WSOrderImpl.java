package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.napoleon.CreateOrder;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.WSOrderDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class WSOrderImpl extends OrderImplBase<WSOrder> {
	@Override
	public void editItem(long itemRowid, Context context) {
		DocType.setCurDoc(WSOrderDoc.instance());
		PriceCount.open(context, itemRowid, (DbObject<WSOrder>) this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		DocType.setCurDoc(WSOrderDoc.instance());
		CreateOrder.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<WSOrder> createInstance() {
		return new WSOrderImpl();
	}
	
	@Override public long sum() { return 0; }

	@Override public void open(Context context) { WSOrderDetail.open(context, this); }
	
	@Override public int getItemValue(Price item) { return ((PricePrint)item).vanQty; }
	
	@Override protected boolean checkPriceQty() { return false; }

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		write();
		return true;
//		long r = find(data.created);
//		
//		if(r == ExtrasConst.INVALID_ID){
//			fill();
//			write();
//		}else
//			read(r);
//		
//		close();
//		
//		return true;
	}

//	private void fill() {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(Util.getDate());
//		long begin = cal.getTime().getTime();
//		cal.add(Calendar.DAY_OF_MONTH, 1);
//		long end = cal.getTime().getTime();
//
//		Sales sls = new Sales();
//		DbReader r = new DbReader();
//		String tbl = DataObjectInfo.getInstance().getTableName(Sales.class);
//		boolean bdo = r.select(sls, tbl, "created >= " + begin + " and created < " + end);
//
//		HashMap<String, ArrayList<Integer>> ordItems = new HashMap<String, ArrayList<Integer>>();
//
//		while (bdo) {
//				for (OrderItem i : sls.items) {
//					ArrayList<Integer> qtyList = null;
//
//					if (ordItems.containsKey(i.id))
//						qtyList = ordItems.get(i.id);
//					else {
//						qtyList = new ArrayList<Integer>();
//						ordItems.put(i.id, qtyList);
//					}
//
//					qtyList.add(i.qty);
//				}
//
//			bdo = r.selectNext(sls);
//		}
//
//		for (Map.Entry<String, ArrayList<Integer>> e : ordItems.entrySet()) {
//			int qty = 0;
//			for (Integer i : e.getValue())
//				qty += i;
//
//			if (qty > 0) {
//				OrderItem item = new OrderItem();
//				item.qty = qty;
//				item.id = e.getKey();
//
//				data.items.add(item);
//			}
//		}
//
//		r.close();
//	}
	
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
	
	public int packCount() {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		
		int count = 0;
		for(OrderItem oi : data.items) {
			p.id = oi.id;
			pi.read();
			
			int qip = p.qtyInPack;
			if( qip <= 0 )
				qip = Consts.QTY_SCALE;
			count += (int)((long)oi.qty * Consts.QTY_SCALE / qip);
		}
		
		return count;
	}
}
