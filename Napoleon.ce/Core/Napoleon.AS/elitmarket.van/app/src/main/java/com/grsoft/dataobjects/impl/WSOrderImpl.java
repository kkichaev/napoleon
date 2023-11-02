package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.WSOrderLoadedItem;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.WSOrderDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class WSOrderImpl extends OrderImplBase<WSOrder> {
	//private static final int DAY_RANGE_DLV = 28;

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		//CreateOrder.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<WSOrder> createInstance() {
		return new WSOrderImpl();
	}

	@Override
	public void open(Context context) {
		WSOrderDetail.open(context, this);
	}

//	@Override
//	public int sum() {
//		return 0;
//	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		
		String prcId = priceImpl.getData().id;
		for(WSOrderLoadedItem mi : data.loadedItems)
			if( mi.id.equals(prcId) &&  qty <= mi.qty )
				return true;
		
		Price price = priceImpl.getData();
		boolean ret = true;
		OrderItem item = (OrderItem) findUpdateItem(price);

		int priceUpdate = 0;
		if( checkPriceQty() ) {
			int newQty = checkPriceQty(priceImpl, qty, item);
			if( newQty != qty ) {
				ret = false;			
				qty = newQty;
			}
		}

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty > 0 )
			{
				Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

				try {
					item = (OrderItem) itemClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				item.cost = cost;
				item.id = price.id;
				item.qty = qty;
				
				if(inPack) item.flags |= OrderItem.IN_PACK;
		
				if(updateQtyHandler != null)
					updateQtyHandler.itemUpdated(item, data, true);
				
				data.items.add(item);
				priceUpdate = - qty;
			} else
				needUpdate = false;
		} else
		{
			priceUpdate = item.qty;
		
			priceUpdate -= qty;
			
			if( item.qty != qty ) {
				item.qty = qty;
				item.cost = cost;
				if(inPack) item.flags |= OrderItem.IN_PACK;
				else item.flags &= (~OrderItem.IN_PACK);
			} else if( item.cost != cost ) {
				item.cost = cost;					
			} else
				needUpdate = false;
			
			if(updateQtyHandler != null) {
				updateQtyHandler.itemUpdated(item, data, false);
				needUpdate = true;
			}
		}
		
		if( needUpdate ) {
			if( qty != 0 )
				beforeItemWrite(item, price);
			
			write();
			if( priceUpdate != 0 && checkPriceQty() )
				updatePrice(priceImpl, priceUpdate);
			
			// refresh sum after writing
			getDocumentType().refreshDocSum(data.id);
		}
		
		return ret;
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
		Sales dlv =(Sales) SalesDoc.instance().create().getData();
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
	
	@Override
	protected boolean checkPriceQty() { return false; }
	
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
