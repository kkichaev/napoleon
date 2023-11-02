package com.grsoft.dataobjects.impl;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FirmRozduhov;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PartsData;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.WhData;
import com.grsoft.napoleon.PricePartySelect;

import android.content.Context;

public class OrderImplEx extends OrderImpl {
	private FirmRozduhov firm = null;
	
	public FirmRozduhov getFirm() {
		if( firm == null ) {
			firm = new FirmRozduhov();
			String table = DataObjectInfo.getInstance().getTableName(FirmRozduhov.class);
			DbReader r = new DbReader();
			r.select(firm, table, "id='" + ((OrderEx)data).firm + "'");
			r.close();
		}
		return firm;
	}
	
	public boolean isLoadedFromKIS() { return ((OrderEx)data).fromKIS != 0;}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceImpl pi = new PriceImpl();
		pi.read(itemRowid);
		pi.close();
		if(!isLoadedFromKIS() && ((PriceEx)pi.getData()).haveParts > 0) {
			PricePartySelect.openPartSelect(context, itemRowid, this);
		} else {
			super.editItem(itemRowid, context);
		}
	}
	
	@Override
	protected void prepareDeleteItem(PriceImpl pi, OrderItem item) {
		PriceEx pe = (PriceEx)pi.getData();
		if(pe.haveParts > 0 ) {
			FirmRozduhov f = getFirm();
			OrderItemEx oie = (OrderItemEx) item;
			PartsData wd = (f.qty < pe.parts.size()) ? pe.parts.get(f.qty) : null;
			if(wd != null) {
				for(WhData wdi : oie.parts)
					wd.items.add(wdi);
				pi.write();
			}
		} else {
			super.prepareDeleteItem(pi, item);
		}
	}
	
	public void updateParts(PriceEx price, List<WhData> items, int cost) {
		OrderItemEx oie = (OrderItemEx) findItem(price.id);
		if(items.size() == 0) {
			if(oie != null)
				data.items.remove(oie);
		} else {
			if(oie == null) {
				oie = new OrderItemEx();
				oie.cost = cost;
				oie.id = price.id;
				data.items.add(oie);
			} else
				oie.parts.clear();
			oie.parts.addAll(items);
		}
		
		if(oie != null)
			oie.updateQty();

		write();

		FirmRozduhov f = getFirm();
		PartsData wd = (f.qty < price.parts.size()) ? price.parts.get(f.qty) : null;
		if(wd != null) {
			for(WhData wi : items)
				wd.remove(wi.weight);
		}
		getDocumentType().refreshDocSum(data.id);
	}
	
	@Override
	public int getItemValue(Price item) {
		FirmRozduhov f = getFirm();
		PriceEx pe = (PriceEx)item;
		if(pe.haveParts > 0) {
			int qty = (f.qty < pe.parts.size()) ? pe.parts.get(f.qty).totalQty() : 0;
			return qty;
		}
		if( f.qty > 0 && f.qty < pe.qtys.size() + 1 )
			return pe.qtys.get(f.qty-1).qty;
		return item.qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		FirmRozduhov f = getFirm();
		PriceEx pe = (PriceEx)price.getData();
		if(pe.haveParts == 0 ) {
			if( f.qty > 0 && f.qty < pe.qtys.size() + 1 )
				pe.qtys.get(f.qty-1).qty += qty;
			else
				pe.qty += qty;
		}
		
		price.write();
	}
}
