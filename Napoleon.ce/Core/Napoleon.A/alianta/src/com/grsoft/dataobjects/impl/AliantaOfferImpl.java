package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.AliantaOffer;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OfferItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.AliantaOfferEdit;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.DiscountInputDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

import android.content.Context;

public class AliantaOfferImpl extends CreatableDocument<AliantaOffer> implements Itemsable {

	@Override
	public void open(Context context) {
		AliantaOfferEdit.open(context, this);
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		
		open(context);
		Warehouse.open(context, this, true);
		return false;
	}
	
	@Override public int getSumType() { return data.costType; }
	
	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		oi.read("id", data.id);
		data.costType = oi.getData().costype;
		
		super.postInit();
	}
	
	public boolean empty() {
		return (data.items.size() == 0);
	}
	
	public void addItem(Price item) {
		OfferItem oi = new OfferItem();
		oi.id = item.id;
		oi.priceCost = CostStrategy.defaultInstance.getItemCost(item, this);
		oi.cost = CostStrategy.costWithDiscount(oi.priceCost, data.discount, Consts.SUM_SCALE);
		data.items.add(oi);
	}

	
	public void applyDiscount(int newDiscount) {
		if(isEditable()) {
			for(OfferItem oi : data.items) {
				oi.cost = CostStrategy.costWithDiscount(oi.priceCost, newDiscount, Consts.SUM_SCALE);
				oi.discount = newDiscount;
			}
			data.discount = newDiscount;
			write();
		}
	}

	@Override
	public void editItem(long itemRowid, final Context context) {
		final PriceImpl pi = new PriceImpl();
		pi.read(itemRowid);
		pi.close();
		
		int prcCost = CostStrategy.defaultInstance.getItemCost(pi.getData(), this); 
		final OfferItem oi = (OfferItem) findItem(pi.getData().id);
		final int cost = oi == null ? prcCost : oi.priceCost;
		
		DiscountInputDlg.open(context, new InputNumber(cost) {
			
			@Override
			public int getValue() { return oi == null ? data.discount : oi.discount; }
			
			@Override
			public void applayInput(int value, Object... params) {
				value = -value;
				int newCost = CostStrategy.costWithDiscount(cost, value, Consts.SUM_SCALE);
				if(oi == null) {
					OfferItem ti = new OfferItem();
					ti.id = pi.getData().id;
					ti.cost = newCost;
					ti.discount = value;
					ti.priceCost = cost;
					data.items.add(ti);
				} else {
					oi.discount = value;
					oi.cost = newCost;
				}
				write();
				if(context instanceof DataSetNotify)
					((DataSetNotify) context).notifyDataSetChanged();
			}
		}, Consts.SUM_SCALE, false, "¬ведите скидку", DiscountInputDlg.Type.OnlyDiscount);
		
	}
	
	@Override
	public boolean isEditable() {
		return data.emailSended != 0 ? false : super.isEditable();
	}

	@Override
	public DataObject findItem(String itemId) {
		for(OfferItem oi : data.items)
			if(oi.id.equals(itemId))
				return oi;
		return null;
	}
	
	public void remove(OfferItem i) {
		if(isEditable()) {
			data.items.remove(i);
			write();
		}
	}

	@Override public int getItemColor() { return R.color.item_highlight; }
	@Override public int getItemValue(Price item) { return item.qty; }
	@Override public int getItemQty(Price item) { return 0; }

	@Override
	public long getItemSum(Price item) {
		OfferItem oi = (OfferItem) findItem(item.id);
		return oi == null ? 0 : oi.cost;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		return false;
	}

	public void markSendEmail() {
		data.emailSended = 1;
		write();		
	}
}
