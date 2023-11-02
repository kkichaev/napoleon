package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.PriceSeries;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.napoleon.NapoleonApp;
import com.grsoft.napoleon.SalesPriceCount;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class SalesImplEx extends SalesImpl {
	
	private static final String SALES_IMPL_PREFERENCE = "SalesImplPreference";
	private static final String DATE_KEY = "date";
	private static final String COUNT_KEY = "count";

	@Override
	public void markPrinted() {
		super.markPrinted();
		putLastNumber();
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		SalesPriceCount.open(context, itemRowid, this);
	}
	
	public List<SalesItemEx> findItems(Price p) {
		List<SalesItemEx> ret = new ArrayList<SalesItemEx>();
		
		for(OrderItem oi : data.items) {
			if(oi.id.equals(p.id))
				ret.add((SalesItemEx)oi);
		}
		
		return ret;
	}
	
	@Override
	public void setExported(boolean value) {
		super.setExported(value);
		if( value )
			putLastNumber();
	}
	
	void putLastNumber() {
		SharedPreferences sp = NapoleonApp.getAppContext().getSharedPreferences(SALES_IMPL_PREFERENCE, Context.MODE_PRIVATE);
		int curCount = sp.getInt(COUNT_KEY, 0);
		
		long nowDate = Util.getDate().getTime();
		long diffDate = data.created.getTime() - nowDate;		
		int count = data.number.length() > 2 ? Integer.parseInt(data.number.substring(data.number.length() - 2)) : 0;
		
		if( diffDate > 0 && diffDate < (3600* 24 * 1000) && count > curCount) {
			SharedPreferences.Editor ed = sp.edit();
			ed.putLong(DATE_KEY, nowDate);
			ed.putInt(COUNT_KEY, count);
			ed.commit();
		}
	}
	
	public static int getLastNumber() {
		long nowDate = Util.getDate().getTime();
		
		SharedPreferences sp = NapoleonApp.getAppContext().getSharedPreferences(SALES_IMPL_PREFERENCE, Context.MODE_PRIVATE);
		long curDate = sp.getLong(DATE_KEY, 0);
		int curCount = sp.getInt(COUNT_KEY, 0);
	
		return curDate == nowDate ? curCount : 0;
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		DataTraveler.travel(Firm.class, new DataTraveler.Travel<Firm> () {

			@Override
			public boolean travel(DataTraveler<Firm> item) {
				data.supplyer = 0;
				data.supplyercode = item.data.id;
				return false;
			}
			
		}, "");
	}
		
	@Override
	protected boolean checkPriceQty() {return !noCheckQty();}
	
	public boolean noCheckQty() {
		boolean ret = false;
		
		ConfigImpl config = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		try {
			if (config.getValue(sb, "МожноПродаватьВМинус") && Integer.parseInt(sb.toString()) == 1)
				ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public int countPack() {
    	int qty = 0;
    	
    	if( data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("qtyInPack");
			
			PricePrint pd = (PricePrint) p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( p.read() ) {
					int qip = (pd.qtyInPack == 0) ? Consts.QTY_SCALE : pd.qtyInPack;
					qty += (int)((long)item.qty * Consts.QTY_SCALE / qip);
				}
			}
			p.close();
    	}
    	
    	return qty / Consts.QTY_SCALE;
	}
	
	@Override
	protected void updateQtyPrice(PriceImpl priceImpl, int priceUpdate) {
		PricePrint price = (PricePrint) priceImpl.getData();
		if( priceUpdate != 0 ) {
			price.vanQty += priceUpdate;
			priceImpl.write();
		}
		
		// refresh sum after writing
		getDocumentType().refreshDocSum(data.id);
		DebtDoc.instance().refreshDocSum(data.id);
	}
	
	@Override
	public boolean delete() {
		boolean res = false;
		DataBaseManager.getDataBase().beginTransaction();
		try {
			if( (res = super.delete()) ) {
				if( isEditable() && data.items != null ) {
					final HashMap<String, HashMap<Date, PriceSeries>> tempMap = new HashMap<String, HashMap<Date, PriceSeries>>();
					DataTraveler.travel(PriceSeries.class, new DataTraveler.Travel<PriceSeries>(true) {

						@Override
						public boolean travel(DataTraveler<PriceSeries> item) {
							HashMap<Date, PriceSeries> ts = tempMap.get(item.data.id);
							if(ts == null) {
								ts = new HashMap<Date, PriceSeries>();
								tempMap.put(item.data.id, ts);
							}
							ts.put(item.data.prdDate, item.data);
							return true;
						}
					}, "");
					
					List<PriceSeries> updated = new ArrayList<PriceSeries>();
					PriceImpl pi = new PriceImpl();
					Price p = pi.getData();
					for(OrderItem item : data.items) {
						HashMap<Date, PriceSeries> ts = tempMap.get(item.id);
						if(ts != null)  {
							SalesItemEx si = (SalesItemEx)item;
							PriceSeries ps = ts.get(si.prdDate);
							if(ps != null) {
								updated.add(ps);
								ps.qty += si.qty;
								continue;
							}
						}
						p.id = item.id;
						pi.read();
						updatePrice(pi, item.qty);
					}
					pi.close();		
				}
		
				getDocumentType().refreshDocSum(data.id);
			}
			DataBaseManager.getDataBase().setTransactionSuccessful();			
		} 
		catch(Exception e) { }
		finally { DataBaseManager.getDataBase().endTransaction(); }
		
		return res;
	}
}
