package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Restock;
import com.grsoft.dataobjects.RestockItem;
import com.grsoft.dataobjects.VandReload;
import com.grsoft.dataobjects.VandReloadItem;
import com.grsoft.dataobjects.VandSell;
import com.grsoft.dataobjects.VandSellItem;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.RestockCreateDetail;
import com.grsoft.napoleon.RestockDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.RestockDoc;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

public class RestockImpl extends CreatableDocument<Restock> implements Itemsable {

	@Override
	public void open(Context context) {
		RestockDetail.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		RestockCreateDetail.open(context, this);
		return false;
	}
	
	public void loadItems(Date from, Date end) {
		String where = "created >= " + Long.toString(from.getTime()) + " and ";
		where += "created < " + Long.toString(end.getTime());
		
		data.items = new ArrayList<RestockItem>();
		loadSells(data.items, where);
		loadReloads(data.items, where);
	}
	
	void updateItems(List<RestockItem> items, String id, int qty) {
		if( qty == 0 )
			return;
		
		boolean finded = false;
		for( RestockItem i : items ) {
			if( i.id.equals(id) ) {
				i.sold += qty;
				finded = true;
				break;
			}
		}
		
		if( !finded ) {
			RestockItem i = new RestockItem();
			i.sold = qty;
			i.id = id;
			
			items.add(i);
		}
	}
	
	private void loadReloads(List<RestockItem> items, String where) {
		VandReload doc = new VandReload();

		String table = DataObjectInfo.getInstance().getTableName(doc.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(doc, table, where);
		while( bdo ) {
			for(VandReloadItem vsi : doc.items) {
				int qty = vsi.qty;
				updateItems(items, vsi.id, qty);
			}
			bdo = r.selectNext(doc);
		}
	}

	void loadSells(List<RestockItem> items, String where) {
		VandSell doc = new VandSell();
		
		String table = DataObjectInfo.getInstance().getTableName(doc.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(doc, table, where);
		while( bdo ) {
			for(VandSellItem vsi : doc.items) {
				int qty = vsi.load;
				updateItems(items, vsi.id, qty);
			}
			bdo = r.selectNext(doc);
		}
	}

	@Override
	public void editItem(final long itemRowid, final Context context) {
		InputNumberDlg.open(context, new InputNumber() {
			
		@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
		@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
		
		@Override
		public void applayInput(int value, Object... params) {
			
			if (!isEditable())
				return;
			
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			
			if (updateQty(priceImpl, value, 0, false) && context instanceof DataSetNotify)
				((DataSetNotify)context).notifyDataSetChanged();
			
			priceImpl.close();
			
			RestockDoc.instance().refreshDocSum(data.id);
		}

		@Override
		public int getValue() {
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			priceImpl.close();
			RestockItem ri = (RestockItem)findItem(priceImpl.data.id);
			
			return ri == null ? 0 : ri.qty;
		}
	});}

	@Override
	public DataObject findItem(String itemId) {
		for(RestockItem ri : data.items)
			if( ri.id.equals(itemId))
				return ri;

		return null;
	}

	@Override public int getItemColor() { return Color.GREEN;	}
	@Override public int getItemValue(Price item) { return item.qty; }

	@Override
	public int getItemQty(Price item) {
		RestockItem ri = (RestockItem)findItem(item.id);
		return (ri == null) ? 0 : ri.qty;
	}

	@Override public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		Price price = priceImpl.getData();
		RestockItem item = (RestockItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) {
			if( qty > 0 ) {
				item = new RestockItem();
				item.id = price.id;
				item.qty = qty;
				data.items.add(item);
			}
			else
				needUpdate = false;
		} else {
			if( qty == 0 && item.sold == 0 ) // удаляем только вновь веденные позиции
				data.items.remove(item);
			else {
				if( item.qty != qty )
					item.qty = qty;
				else
					needUpdate = false;
			}
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}
}
