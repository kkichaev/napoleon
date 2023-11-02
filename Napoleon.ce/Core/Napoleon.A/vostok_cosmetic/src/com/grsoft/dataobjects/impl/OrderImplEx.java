package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.ConfigImplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.MessageBox;

public class OrderImplEx extends OrderImpl {
	
	public static OrderImpl autoorder(Date begin, String orgId, GpsCoord coord, long rid) {		
		
		HashMap<String, Integer> items = new HashMap<String, Integer>();

		DatePeriod dp = new DatePeriod(begin, new Date());
		dp.periodType = DatePeriod.CREATED;
		DocList dl = OrderDoc.instance().docList(orgId, null, dp);
		int count = dl.getCount();

		for( int i=0; i<count; i++ ) {
			OrderImplBase<?> doc = (OrderImplBase<?>) dl.get(i);
			for(OrderItem item : doc.data.items) {
				Integer qty = items.get(item.id);
				if( qty == null )
					qty = item.qty;
				else
					qty += item.qty;
				items.put(item.id, qty);
			}
		}
		dl.close();
		
		HashMap<String, Integer> ritems = new HashMap<String, Integer>();
		
		RemnantsImpl ri = new RemnantsImpl();
		if( rid != ExtrasConst.INVALID_ID ) {
			ri.read(rid);
			ri.close();
			for(RemnantItem rmni : ri.getData().items)
				ritems.put(rmni.id, rmni.qty);
		}

		for(Entry<String, Integer> se : items.entrySet()) {
			int qty = se.getValue() / count;
			if((qty % Consts.QTY_SCALE) != 0 )
				qty = (qty / Consts.QTY_SCALE + 1) * Consts.QTY_SCALE;
			
			if(qty == 0)
				qty = Consts.QTY_SCALE;
			Integer rv = ritems.get(se.getKey());
			if( rv != null )
				qty -= rv;
			se.setValue(qty);
		}
		
		OrderImpl ret = (OrderImpl)OrderDoc.instance().create();
		OrgImpl org = new OrgImpl();
		org.getData().id = orgId;
		org.read();
		org.close();
		((OrderEx)ret.getData()).delay = ((OrgEx)org.getData()).delay;
		((OrderEx)ret.getData()).autoorder = 1;
		ret.autoorder(orgId, coord, items);
		return ret;
	}
	
	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		int cost = ((p.cost.size() > data.sumType && data.sumType >= 0) ? p.cost.get(data.sumType).cost : 0);
		if( cost != item.cost ) {
			double dsc = ((double)cost - item.cost) / cost;
			((OrderItemEx)item).discount = -(int)(dsc * Consts.SUM_SCALE * Consts.SUM_SCALE + 0.5);
			// масштабируем и округляем
//			((OrderItemEx)item).discount = -(int)((((long)cost - item.cost) * Consts.SUM_SCALE * Consts.SUM_SCALE + Consts.SUM_SCALE * Consts.SUM_SCALE * 5) / (cost * 10)) * 10;
		} else
			((OrderItemEx)item).discount = 0;
	}
	
	@Override
	public boolean init(final Context context, final String orgId, final GpsCoord coord) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Выберите вариант");
		CharSequence[] items = new CharSequence[] {"Автозаказ", "Обычный заказ"};
		b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( which == 1 ) {
					OrderImplEx.super.init(context, orgId, coord);
					dialog.dismiss();
				} else {
					Date begin = new Date();
					long rid = ExtrasConst.INVALID_ID;
					ConfigImplEx ce = (ConfigImplEx)ConfigManager.getConfig();
					if(ce.useRestInAutoOrder) {
						rid = RemnantsImpl.find(orgId, begin);
						if( rid == ExtrasConst.INVALID_ID ) {
							MessageBox.show(context, "Ошибка", "Нет остатков на текущую дату");
							return;
						}
					}
					//вычтем неделю
					begin = new Date(begin.getTime() - 1000l * 3600 * 24 * ce.daysForAutoorder);
					OrderImpl o = autoorder(begin, orgId, coord, rid);
					if( o != null )
						o.open(context);
					dialog.dismiss();
				}
			}
		});
		b.create().show();
		
		return false;		
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int whIndex = ((OrderEx)data).whIndex;
		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}
	
	@Override
	public int getItemValue(Price item) {
		int whi = ((OrderEx)data).whIndex;
		if( whi == 0 || whi > ((PriceEx)item).whQty.size())
			return item.qty;
		return ((PriceEx)item).whQty.get(whi-1).qty;
	}
	
	@Override
	public String getDescription(Context context) {
		StringBuilder result = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data.created);
		result.append(calendar.get(Calendar.YEAR))
		.append(String.format("%02d", calendar.get(Calendar.MONTH) + 1))
		.append(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)))
		.append(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)))
		.append(String.format("%02d", calendar.get(Calendar.MINUTE)))
		.append(String.format("%02d", calendar.get(Calendar.SECOND)))
		.append("<br>")
		.append(isExported() ? "отправлен" : "не отправлен");
		
		return result.toString();
	}
}
