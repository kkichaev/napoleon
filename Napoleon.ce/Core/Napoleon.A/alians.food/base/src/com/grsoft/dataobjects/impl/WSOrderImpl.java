package com.grsoft.dataobjects.impl;

import java.util.HashMap;
import java.util.Map;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.StockQty;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.WSOrderLoadedItem;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.PriceCountEx;
import com.grsoft.napoleon.WSOrderDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.content.Context;

public class WSOrderImpl extends OrderImplBase<WSOrder> {
	//private static final int DAY_RANGE_DLV = 28;

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, (DbObject<WSOrder>) this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		open(ctx);
	}

	@Override
	public CreatableDocument<WSOrder> createInstance() {
		return new WSOrderImpl();
	}

	@Override
	public void open(Context context) {
		WSOrderDetail.open(context, this);
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
//2017.06.23 Убрали: ошбика - 1906 		
//		String prcId = priceImpl.getData().id;
//		for(WSOrderLoadedItem mi : data.loadedItems)
//			if( mi.id.equals(prcId) &&  qty <= mi.qty )
//				return true;
		
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
	public void postInit() {
		super.postInit();
		fill();
	}
	
	private void fill() {
		PriceImpl p = new PriceImpl();
		HashMap<String, Integer> stock = loadStock();
		for (Map.Entry<String, Integer> e : stock.entrySet()) {
			int qty = e.getValue();
			
			p.getData().id = e.getKey();
			if (p.read()) {
				int qty1 = qty - getItemValue(p.getData());
				qty = Math.min(qty1, p.getData().qty);

				if (qty > Consts.QTY_SCALE / 2) {
					OrderItem item = new OrderItem();
					// округлим до целых
					item.qty = ((qty + Consts.QTY_SCALE /2) / Consts.QTY_SCALE) * Consts.QTY_SCALE;
					item.id = e.getKey();

					data.items.add(item);
					data.loadedItems.add(new WSOrderLoadedItem(item.id, item.qty));
				}
			}
		}

		p.close();
	}
	
	private HashMap<String, Integer> loadStock() {
		StockQty data = new StockQty();
		String table = DataObjectInfo.getInstance().getTableName(data.getClass());
		DbReader r = new DbReader();
		
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		boolean bdo = r.select(data, table, null);
		while( bdo ) {
			ret.put(data.id, data.qty);
			bdo = r.selectNext(data);
		}
		r.close();
		return ret;
	}

	@Override
	protected boolean checkPriceQty() { return false; }
	
	@Override
	public int getItemValue(final Price price) {
		class ValCont{
			public int val = 0;
		};
		
		final ValCont result = new ValCont();
		result.val = ((PricePrint)price).vanQty;
		
		StringBuilder sb = new StringBuilder();
		sb.append("created > ").append(Util.getDate().getTime());
		sb.append(" and (params & 1) = 1");
		
		DataTraveler.travel(data.getClass(), new DataTraveler.Travel<WSOrder>() {

			@Override
			public boolean travel(DataTraveler<WSOrder> item) {
				for(OrderItem i : item.data.items)
					if (i.id.equals(price.id))
						result.val += i.qty;
				
				return true;
			}}, sb.toString());
		
		return result.val;
	}
}
