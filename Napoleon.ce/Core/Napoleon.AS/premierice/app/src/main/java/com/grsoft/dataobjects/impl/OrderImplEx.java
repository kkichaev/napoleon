package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.PriceCountEx;
import com.grsoft.util.Consts;

import java.util.HashMap;
import java.util.Map;

public class OrderImplEx extends OrderImpl {
	boolean isBonus = false;

	Map<String, Bonus> bonus = null;

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCountEx.open(context, itemRowid, this, isBonusMode());
	}

	public Map<String, Bonus> getBonus() {
		if(bonus == null) {
			bonus = new HashMap<>();

			DataTraveler.travel(Bonus.class, new DataTraveler.Travel<Bonus>(true) {
				@Override
				public boolean travel(DataTraveler<Bonus> item) {
					bonus.put(item.data.id, item.data);
					return true;
				}
			}, "ids = '' or ids='" + ((OrderEx)data).whCode + "' and qty > 0");
		}
		return bonus;
	}

	@Override
	public int getItemValue(Price item) {
		if(isBonusMode()) {
			getBonus();
			Bonus b = bonus.get(item.id);
			return b == null ? 0 : b.qty;
		}

		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			return super.getItemValue(item);
		
		return pe.whQty.get(whIndex-1).qty;
	}

	public void setBonusMode(boolean isBonus) { this.isBonus = isBonus; }
	public boolean isBonusMode() { return isBonus | isBonusDoc(); }
	public boolean isBonusDoc() { return ((OrderEx)data).bonus > 0; }

	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		((OrderItemEx)item).bonus = isBonusMode() ? 1 : 0;

		super.beforeItemWrite(item, p);
	}

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if(isBonusMode()) {
			Bonus b = getBonus().get(price.getData().id);
			if(b != null) {
				b.qty += qty;
				DbWriter wr = new DbWriter();
				wr.setUpsert(true);
				wr.insertRecord(b);
				wr.close();
			}
			return;
		}
		int whIndex = ((OrderEx)data).whIndex;
		if(((OrderEx)data).whIndex > 0) {
			PriceEx pe = (PriceEx)price.getData();
			if(whIndex >= pe.whQty.size()) {
				pe.whQty.get(whIndex-1).qty += qty;
				price.write();
			}
			return;
		}
		super.updatePrice(price, qty);
	}
}
