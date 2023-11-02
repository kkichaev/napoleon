package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import android.content.Context;
import android.graphics.Color;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Offer;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.OfferDetail;
import com.grsoft.napoleon.OfferEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.modules.print.util.DocNumberStrategy.ISupplyer;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;


public class OfferImpl extends OrderImplBase<Offer> implements Itemsable, ISupplyer{

	@Override
	public void open(Context context) { OfferDetail.open(context, getRowid()); }
	
	@Override
	public void editProperties(Context context,  boolean isOldOrder){ OfferEdit.open(context, getRowid(), isOldOrder);};
	
	@Override
	public void postInit() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(Util.getDate());
		data.start = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 7);
		data.finish = cal.getTime();
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		if( initSilent(orgId, coord) )
			OfferEdit.open(context, getRowid(), false);
		return false;
	}
	
	@Override
	public boolean initSilent(String orgId, GpsCoord coord) {
		data.number = DocHelper.makeDocNumber(this);
		return super.initSilent(orgId, coord);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceImpl price = new PriceImpl();
		price.read(itemRowid);
		price.close();
		String id = price.getData().id;
		
		DataObject item = findItem(id);
		
		if(item != null)
			data.items.remove(item);
		else{
			OrderItem oi = new OrderItem();
			oi.id = id;
			data.items.add(oi);
			oi.qty = 1 * Consts.QTY_SCALE;
		}
		
		write();
		close();
		
		((DataSetNotify)context).notifyDataSetChanged();
	}

	@Override
	public DataObject findItem(String itemId) {
		for(OrderItem i : data.items)
			if(i.id.equals(itemId))
				return i;
		return null;
	}

	@Override
	public int getItemColor() {	 return Color.MAGENTA;  }

	@Override
	public int getItemValue(Price item) { return item.qty; }

	@Override
	public int getItemQty(Price item) {
		DataObject obj = findItem(item.id);
		return obj == null ? 0 : 1;
	}

	@Override
	public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) { return false; }

	@Override
	public CreatableDocument<Offer> createInstance() { return new OfferImpl(); }

	@Override
	public String getSupplyer() {return data.firmCode; }

}
