package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OffTakeHistory {
	public class Item {
		public Date date;   // дата
		public int qty = 0;     // кол-во
		public int rest = 0;    // остаток
		public int offTake = 0; // продажа (Off take)
		
		public Item(Date d) { date = d; }
		
		public void copyFrom(SaleItem si) {
			offTake = si.offTake;
			qty = si.qty;
			rest = si.rest;
		}
		
		public String makeText(boolean firstItem) {
			String text = "";
			
			text += Util.IntToScaleStr(rest, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			text += "<br>";
			text += Util.IntToScaleStr(offTake, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			text += "<br>";
			
			if( firstItem ) text+= "<b>";
			text += Util.IntToScaleStr(qty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			if( firstItem ) text+= "</b>";
			
			return text;
		}
	}
	
	public class SaleItem {
		public int qty;     // кол-во
		public int rest;    // остаток
		public int offTake; // продажа (Off take)
		
		public int calcQty(int offTakeCoef) {
			int val = (int)(((long)offTake * offTakeCoef) / Consts.SUM_SCALE) - rest;
			if( val < 0 ) val = 0;
			
			// для дробного числа - добавим до целой части
			if( (val % Consts.QTY_SCALE) != 0 )
				val = (val / Consts.QTY_SCALE + 1) * Consts.QTY_SCALE;
			
			return val;
		}
		
		public boolean empty() { return qty == 0 && rest == 0 && offTake == 0; }
	
		private void fixNegativeValues(){
			if(offTake < 0)
				offTake = 0;
			
			if(rest < 0)
				rest = 0;
			
			if(qty < 0)
				qty = 0;
		}
}

	/***
	 * Контрагент
	 */
	protected String id;
	
	protected SaleItem createSaleItem() { return new SaleItem(); } 
	
	@SuppressWarnings("serial")
	public class ItemsHolder extends Hashtable<String, SaleItem> {
		
		public ItemsHolder(){}
		
		public ItemsHolder(Map<String, SaleItem> map){
			super(map);
		}
		
		@Override
		public synchronized SaleItem get(Object key) {
			SaleItem ret = super.get(key);
			if( ret == null ) {
				ret = createSaleItem();
				put((String) key, ret);
			}
			
			return ret;
		}
	}
	
	public class DateSales implements Comparable<DateSales>, Cloneable{
		public Date date;
		public ItemsHolder items = new ItemsHolder();
		
		public DateSales(Date d) { date = d; }
		
		public void load(Remnants doc) {
			if( doc.items != null )
				for(RemnantItem ri : doc.items) {
					SaleItem si = items.get(ri.id);
					si.rest += ri.qty;
				}
		}
		
		// в обратном порядке
		@Override public int compareTo(DateSales another) { return another.date.compareTo(date); }

		public void load(Order doc) {
			if( doc.items != null )
				for(OrderItem ri : doc.items) {
					SaleItem si = items.get(ri.id);
					si.qty += ri.qty;
				}
		}

		public void load(Delivery doc) {
			if( doc.items != null )
				for(DeliveryItem ri : doc.items) {
					SaleItem si = items.get(ri.id);
					si.qty += ri.qty;
				}
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			DateSales result = new DateSales(date);
			result.items = new ItemsHolder(result.items);
			return super.clone();
		}
	}
	
	@SuppressWarnings("serial")
	public class Sales extends ArrayList<DateSales> {
		
		/**
		 * Возвращает информацию о прадажах за день, время документа не учитывается, все документа за один день объединяются
		 * @param docDate
		 * @return или найденный, или новый элемент
		 */
		DateSales get(Date docDate) {
			Calendar c = Calendar.getInstance();
			c.setTime(docDate);
			Date d = new Date(c.get(Calendar.YEAR) - 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			
			DateSales ret = null;
			for( DateSales ds : this ) {
				if( ds.date.compareTo(d) == 0 ) {
					ret = ds;
					break;
				}
			}
			
			if( ret == null ) {
				ret = getDateSales(d);
				add(ret);
			}
			
			return ret;
		}

		// Возвращает первый документ с датой больше docDate
		DateSales findSaleFromDocDate(Date docDate) {
			Calendar c = Calendar.getInstance();
			c.setTime(docDate);
			Date d = new Date(c.get(Calendar.YEAR) - 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			
			DateSales ret = null;
			for( DateSales ds : this ) {
				if( ds.date.compareTo(d) > 0 ) {
					ret = ds;
					break;
				}
			}
			
			return ret;
		}
	}

	
	/**
	 * загружаем данные о продажах
	 * @param orgId - код контрагента
	 * @param fromOrders - true - загружаем продажи из заявок false - из накладных
	 */
	public OffTakeHistory(String orgId, boolean fromOrders) {
		sales = getSales();
		id = orgId;
		loadRest(orgId);
		
		if( fromOrders )
			loadOrders(orgId);
		else
			loadDeliveries(orgId);
		
		calcOfftake();
		Collections.sort(sales);
	}
	
	protected OffTakeHistory() {}
	
	/**
	 * Не нашел способа получить значения prevItem
	 * @author 1111
	 *
	 */
	public class PrevItemParam {
		public SaleItem prevItem;
	}
	/**
	 * Обновим остаток и первого элемента
	 * @param newRest
	 * @return
	 */
	public Item updateRest(String id, int newRest, PrevItemParam prevItemParam ) {
		Item res = null;
		if( sales.size() > 0 ) {
			DateSales ds = sales.get(0);
			
			SaleItem prevItem = new SaleItem();
			
			if(sales.size() > 1){
				for(int i = 1; i < sales.size(); i++){
					DateSales pds = sales.get(i);
					prevItem = pds.items.get(id);
					
					if( !Features.NOT_ZERO_DATA_FOR_COPLEX_HISTORY )
						break;
					
					if(!prevItem.empty())
						break;
				}
			}
			
			SaleItem si = ds.items.get(id);
			if( si != null ) {
				res = getSaleItem(ds.date);
				si.offTake = prevItem.rest + prevItem.qty - newRest;
				si.rest = newRest;
				si.qty = si.calcQty(inflator.getOffTake(id));
				si.fixNegativeValues();
				res.copyFrom(si);
			}
			
			if( prevItemParam != null )
				prevItemParam.prevItem = prevItem;
		}
		return res;
	}

	private void calcOfftake() {
		Hashtable<String, Integer> prevRest = new Hashtable<String, Integer>();
		DateSales prev = null;
		for( DateSales ds : sales ) {

			for(Entry<String, SaleItem> item : ds.items.entrySet()) {
				String itemID = item.getKey();
				Integer rest = prevRest.get(itemID);
				if( rest == null )
					rest = Integer.valueOf(0);
				
				SaleItem si = item.getValue();
			
				int ot = rest + si.qty - si.rest;
				si.offTake = (ot<0) ? 0 : ot;
				prevRest.put(itemID, si.rest);
				
				if( prev != null ) {
					SaleItem psi = prev.items.get(itemID);
					if(psi != null)
						psi.qty = si.qty;
				}
			}
//				// изменим кол-во проданного на реальное
//			if( prev != null ) {
//				for(Entry<String, SaleItem> pitem : prev.items.entrySet()) {
//					SaleItem csi = ds.items.get(pitem.getKey());
//					SaleItem psi = pitem.getValue();
//					if( csi == null ) psi.qty = 0;
//					else psi.qty = csi.qty;
//				}
//			}
			try{
				prev = (DateSales) ds.clone();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		// calc offtake for last item
		if( sales.size() > 0 )
			for(Entry<String, SaleItem> item : sales.get(sales.size()-1).items.entrySet()) {
				SaleItem si = item.getValue();
				si.qty = si.calcQty(inflator.getOffTake());
			}
	}

	protected void loadOrders(String orgId) {
		OrderDoc rdocs = (OrderDoc) OrderDoc.instance();
		DocList docs = rdocs.docList(orgId, "date");
		for(Document<?> rd : docs) {
			Order doc = (Order)rd.getData();
			DateSales ds = sales.findSaleFromDocDate(doc.created);
			if( ds != null )
				ds.load(doc);		
		}
		docs.close();
	}

	protected void loadDeliveries(String orgId) {
		DocList docs = new DocList(DeliveryImpl.class, "id='" + orgId + "'", "date");
		for(Document<?> rd : docs) {
			Delivery doc = (Delivery)rd.getData();
			DateSales ds = sales.findSaleFromDocDate(doc.date);
			if( ds != null )
				ds.load(doc);		
		}
		docs.close();
	}

	protected void loadRest(String orgId) {
		
		ItemsHolder lastItems = new ItemsHolder();
		
		RemnantsDoc rdocs = (RemnantsDoc) RemnantsDoc.instance();
		DocList docs = rdocs.docList(orgId, "date");
		for(Document<?> rd : docs) {
			Remnants doc = (Remnants)rd.getData();
			DateSales ds = sales.get(doc.date);
			ds.load(doc);
			
			// добавим все встречающиеся элементы в последний день
			for(Entry<String, SaleItem> ei : ds.items.entrySet()) {
				if(lastItems.containsKey(ei.getKey()) == false)
					lastItems.put(ei.getKey(), createSaleItem());
			}
				
		}
		docs.close();
		
		// добавим остатки за текущий день, если их не было
		DateSales ls = sales.get(new Date());
		for(Entry<String, SaleItem> ei : ls.items.entrySet())
			lastItems.remove(ei.getKey());
		
		for(Entry<String, SaleItem> ei : lastItems.entrySet())
			ls.items.put(ei.getKey(), ei.getValue());
	}

	public ArrayList<Item> getHistory(String itemId) {
		ArrayList<Item> res = new ArrayList<Item>();
		
		int weight = 0;
		if( Features.SHOW_WEIGHT_IN_HISTORY ) {
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			p.id = itemId;
			
			if( pi.read() )
				weight = p.weight;
			pi.close();
		}

		for(DateSales item : sales) {
			Item ri = getSaleItem(item.date);
			
			SaleItem s = item.items.get(itemId);			
			if( s != null ) {
				ri.copyFrom(s);
				if( weight != 0) {
					int qty = ri.offTake;
					qty = (int)(((long)qty * weight + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE);
					qty -= (qty % Consts.WEIGHT_SCALE); // округлим
					ri.offTake = qty;
					
					qty = ri.qty;
					qty = (int)(((long)qty * weight + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE);
					qty -= (qty % Consts.WEIGHT_SCALE); // округлим
					ri.qty = qty;
										
					qty = ri.rest;
					qty = (int)(((long)qty * weight + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE);
					qty -= (qty % Consts.WEIGHT_SCALE); // округлим
					ri.rest = qty;
				}
			}
			res.add(ri);
		}
		return res;
	}
	
	public Sales getSalesData() { return sales; }
	
	public ArrayList<Date> getLabels() {
		ArrayList<Date> res = new ArrayList<Date>();
		for( DateSales item : sales )
			res.add(item.date);
		
		return res;
	}
	
	protected Item getSaleItem(Date d) { return new Item(d); };
	protected DateSales getDateSales(Date d) { return new DateSales(d); }
	protected Sales getSales() { return new Sales(); }
	
	protected Sales sales;
	public static OffTakeInflator inflator = new OffTakeInflator();
	
	public static class OffTakeInflator{
		public static int OFF_TAKE_COEF = 150; // SumScale
				
		public int getOffTake(){
			return OFF_TAKE_COEF;
		}
		
		public int getOffTake(String id){
			return getOffTake();
		}
	}
}
