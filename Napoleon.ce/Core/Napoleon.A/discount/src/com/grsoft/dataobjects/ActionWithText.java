package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class ActionWithText extends ActionsDiscont {
	private String text = "";
	private long orderDiscountSum = 0;
	private OrderItem affectedItem = null;
	
	public String getActionText() { return text; }
	public long getOrderDiscountSum() { return orderDiscountSum; }
	public OrderItem getAffectedItem() { return affectedItem; }

	public void updateText(Price p) {
		text = String.format("(%s - %s) cкидка %s%% на %s %s",
			Util.simpleDateFormat.format(start),
			Util.simpleDateFormat.format(finish),
			Util.IntToScaleStr(dsc, Consts.SUM_SCALE),
			p.name,
			type == ITEM_TYPE? ("\nсумма товара в заказе " + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false)) :
			type == ORDER_TYPE? ("\nсумма заказа " + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false)) :
			""
		);
	}
	
	public void updateText() {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		p.id = id_i;
		pi.read();
		updateText(p);
		pi.close();
	}
	
	public static String getOrderText(OrderEx doc) {
		Trvl t = new Trvl(doc);
		DataTraveler.travel(ActionWithText.class, t, "id='" + doc.action + "'");
		
		return t.text;
	}
	
	public void updateOrderDiscountSum(OrderEx doc, OrderItem item) {
		long itemSum = ((long)item.qty) * item.cost / Consts.QTY_SCALE;
		long dscSum = itemSum - CostStrategy.costWithDiscount(itemSum, dsc, Consts.SUM_SCALE);
		
		orderDiscountSum = 0;
		if(type == DISCOUNT_TYPE)
			orderDiscountSum = dscSum;
		else if(type == ITEM_TYPE) {
			if(itemSum >= sum)
				orderDiscountSum = dscSum;
		} else if(type == ORDER_TYPE) {
			if(doc.sum() >= sum )
				orderDiscountSum = dscSum;
		}
		
		if(orderDiscountSum != 0)
			affectedItem = item;
	}

	public static List<ActionWithText> getAllActions(OrderEx doc) {
		
		final List<ActionWithText> ret = new ArrayList<ActionWithText>();
		final PriceImpl pi = new PriceImpl();
		final Price p = pi.getData();
		
		long now = Util.getDayStart(doc.date).getTime();
		long next = now + 24 * 3600 * 1000;
		String where = "idWh = '" + doc.whCode + "' and start < " + Long.toString(next) + " and finish >= " + Long.toString(now);
	
		DataTraveler.travel(ActionWithText.class, new DataTraveler.Travel<ActionWithText>(true) {

			@Override
			public boolean travel(DataTraveler<ActionWithText> item) {
				p.id = item.data.id_i;
				if( pi.read() ) {
					item.data.updateText(p);
					ret.add(item.data);
				}
				return true;
			}
		}, where);
		
		pi.close();
		return ret;
	}

	public static List<ActionWithText> getActiveActions(OrderEx doc) {
		List<ActionWithText> ret = getAllActions(doc);
		List<ActionWithText> needDel = new ArrayList<ActionWithText>();
		
		for(ActionWithText a : ret) {
			OrderItem oi = doc.findItem(a.id_i);
			if(oi == null)
				needDel.add(a);
			else
				a.updateOrderDiscountSum(doc, oi);
		}
		
		ret.removeAll(needDel);
		return ret;
	}
}

class Trvl extends DataTraveler.Travel<ActionWithText> {
	String text = "";
	long orderDiscount = 0;
	
	public Trvl(OrderEx doc) {
		orderDiscount = doc.discountSum;
	}

	@Override
	public boolean travel(DataTraveler<ActionWithText> item) {
		item.data.updateText();
		text = item.data.getActionText() + " <b>" + Util.IntToScaleStr(orderDiscount, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " </b>";
		return false;
	}
}

