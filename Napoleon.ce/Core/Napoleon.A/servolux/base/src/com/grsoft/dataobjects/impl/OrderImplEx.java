package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PlanQtyData;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.OrderDocEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class OrderImplEx extends OrderImpl {

	Map<String, PlanQtyData> qtyData;
	Map<String, Integer> saled = new HashMap<String, Integer>();
	
//	static HashMap<String, String> firms = null;
//	public static void clearCache() { firms = null; }
	FirmEx firm = null;
	
	@Override public void open(Context context) {
		OrderDetail.open(context, this);
	}
	
	@Override public CreatableDocument<Order> createInstance() { return new OrderImplEx(); }
	
	@Override public void close() { super.close(); }
	
	@Override public CreatableDocument<Order> copy() { return null; }
	
	@Override
	public boolean read() {
		if( !super.read() )
			return false;

		qtyData = null;
		return true;
	}
	
	@Override
	public void postInit() {
		final OrderEx o = (OrderEx)data;
		
		o.dlvDate = Util.getNextDay(o.date);
		o.modify = o.created;
		o.editPostSend = o.created;
		o.linked = o.created.getTime();

		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx)oi.getData();
		org.id = o.id;
		oi.read();
		oi.close();
		
		DataTraveler.travel(OrgDog.class, new DataTraveler.Travel<OrgDog>() {

			@Override
			public boolean travel(DataTraveler<OrgDog> item) {
				o.firmCode = item.data.firm;
				return false;
			}
		}, "ido='" + org.ido + "'");
	}
		
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		if(initSilent(orgId, coord)) {
			OrderDocEdit.open(context, ((OrderEx)data).linked);
		}
		return false;
	}
	
	public void copyFrom(OrderImplEx src, long created) {
		DataObject.makeCopy(data, src.getData());
		data.created = new Date(created);
		data.items = new ArrayList<OrderItem>();
		data.remark = "";
		((OrderEx)data).modify = data.created;
		((OrderEx)data).editPostSend = data.created;
	}
	
	public int getQty(String id) {
		OrderItem oe = (OrderItem) findItem(id);
		return oe == null ? 0 : oe.qty;
	}
	
	@Override
	public int getItemQty(Price item) {
		return getQty(item.id);
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		super.postCopyProcess(copy);

		OrderEx dest = (OrderEx) copy.getData();
		
		Calendar c = Calendar.getInstance();
		c.setTime(dest.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		dest.dlvDate = c.getTime();
		dest.modify = dest.created;
		dest.editPostSend = dest.created;
	}
	
//	static void loadFirms() {
//		firms = new HashMap<String, String>();  
//				
//		ConfigImpl ci = new ConfigImpl();
//		Config c = ci.getData();
//		c.key = "Организация";
//		if( ci.read() ) {
//			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
//			DialogHelper.makeListWithKey(c.value, values, null);
//			for(KeyValue kv : values)
//				firms.put(kv.key.toString(), kv.value.toString());
//		}
//		ci.close();
//	}
	
	public FirmEx getFirm() {
		if( firm == null ) {
			FirmImpl fi = new FirmImpl();
			firm = (FirmEx) fi.getData();
			firm.id = ((OrderEx)data).firmCode;
			fi.read();
			fi.close();
		}
		return firm;
	}
	
	public String getFirmName() {
		return getFirm().name;

//		if( firms == null )
//			loadFirms();
//		return firms.get(((OrderEx)data).firmCode);
	}
	
	@Override
	public String getDescription(Context context) {
		String res = super.getDescription(context);

		String name = getFirmName();
		if( name != null ) {
			res += "&nbsp;&nbsp;&nbsp;<i>" + name + "</i>";
		}
		return res;
	}
	
	@Override
	public boolean read(long rowid, boolean useCache) {
		if( !super.read(rowid, useCache) )
			return false;

		qtyData = null;
		firm = null;
		return true;
	}
	
	@Override
	public boolean isEditable() {
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTime(data.created);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return (c.getTime().getTime() == Util.getDate().getTime());
//		return super.isEditable();
	}
	
	@Override
	public int getItemValue(Price item) { 
		return getRestPlanQty(item.id);
	}
	
	public void loadQtyData() {
		if( qtyData == null ) {
			qtyData = AgentPlanNewImpl.getPlans(((OrderEx)data).firmCode, data.date);
			loadSaled();
		}
	}
	
	public Map<String, PlanQtyData> getPlanData() { 
		loadQtyData();
		return qtyData; 
	}
	
	public PlanQtyData getPlanQty(String id) {
		loadQtyData();
		
		PlanQtyData pq = qtyData.get(id);
		if( pq == null ) {
			PriceImpl pi = new PriceImpl();
			PriceEx pe = (PriceEx)pi.getData();
			pe.id = id;
			pi.read();
			pi.close();

			pq = new PlanQtyData(0, 0, "", pe.qtyInPack == 0 ? Consts.QTY_SCALE : pe.qtyInPack);
			qtyData.put(id, pq);
		}		
		return pq;
	}
	
	public int getRestPlanQty(String id) {
		PlanQtyData pq = getPlanQty(id);		
		int val = pq.qty + pq.changes;
		
		Integer sal = saled.get(id);
		if( sal != null )
			val -= (int)((long)sal * Consts.QTY_SCALE / pq.inPack);

		if( pq.group != null && pq.group.length() > 0 ) {
			for(Entry<String, PlanQtyData> e : qtyData.entrySet()) {
				PlanQtyData v = e.getValue();
				if( v != pq && pq.group.equals(v.group) ) {
					sal = saled.get(e.getKey());
					if( sal != null )
						val -= (int)((long)sal * Consts.QTY_SCALE / pq.inPack);
				}
			}
		}
		
		return val;
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		if( isEditable() && isExported()) {
			((OrderEx)data).editPostSend = new Date();
			setExported(false);
		}
		
		return super.updateQty(priceImpl, qty, cost, inPack);
	}

	private void loadSaled() {
		saled.clear();
		
		String where = null;
		
		if( data.date != null ) {
			Calendar c = Calendar.getInstance(Locale.getDefault());
			c.setTime(data.date);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			Date begin = c.getTime();
			
			c.add(Calendar.DAY_OF_MONTH, 1);
			
			where = String.format("date >= %s and date < %s and firmCode='%s'", 
					Long.toString(begin.getTime()), Long.toString(c.getTime().getTime()), ((OrderEx)data).firmCode);
		}
		
		DocList dl = OrderDoc.instance().docList(null, null, where);
		
		for(Document<?> doc : dl) {
			for(OrderItem oi : ((OrderImpl)doc).getData().items) {
				Integer qty = saled.get(oi.id);
				if( qty == null )
					qty = 0;
				saled.put(oi.id, qty + oi.qty);
			}
		}
		dl.close();
	}
	
	@Override
	public long write() {
		long ret = super.write();
		if( qtyData != null )
			loadSaled();
		return ret;
	}

	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		return qty;
		
//		int pq = getItemValue(p.getData());
//		pq = (int)((long)pq * p.getData().qtyInPack / Consts.QTY_SCALE);
//		if( item != null )
//			pq += item.qty;
//		
//		return (qty < pq) ? qty : (pq >= 0) ? pq : 0;
	}
	
	@Override protected void updatePrice(PriceImpl price, int qty) { }
	
	@Override
	public int count() {
    	int qty = 0;
    	
    	if( data.items != null ) {
    		PriceImpl pi = new PriceImpl();
    		Price pe = (Price) pi.getData();
	    	for(OrderItem item : data.items ) {
	    		pe.id = item.id;
	    		int inPack = Consts.QTY_SCALE;
	    		if( pi.read() && pe.qtyInPack != 0 )
	    			inPack = pe.qtyInPack;
	    		
	    		qty += (int)((long)item.qty * Consts.QTY_SCALE / inPack);
	    	}
	    	pi.close();
    	}
    	
    	return qty / Consts.QTY_SCALE;
	}

	public void addItem(PriceEx pe, int qty, int itemCost, boolean inPack) {
		OrderItem oi = new OrderItem();
		oi.cost = itemCost;
		oi.id = pe.id;
		oi.qty = qty;
		if(inPack)
			oi.flags |= OrderItem.IN_PACK;
		
		data.items.add(oi);
	}
}

