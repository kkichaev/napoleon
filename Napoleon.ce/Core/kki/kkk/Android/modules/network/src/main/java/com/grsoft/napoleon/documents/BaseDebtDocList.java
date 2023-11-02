package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

public abstract class BaseDebtDocList extends DocList {
	protected ArrayList<DebetItem> items = new ArrayList<DebetItem>();

	protected DocList deliveries;
	protected DocList payments;
	
	protected BaseDebtDocList() {}
	public BaseDebtDocList(String where, String order, boolean loadDelivery) {
		init(where, order, loadDelivery);
		orderDocuments();
	}

	protected void init(String where, String order, boolean loadDelivery) {
		ids = new ArrayList<Long>();

		deliveries = (loadDelivery) ? createDeliveryList(where, order) : null;
		payments = new DocList(getPaymentType(), where, order);

		HashSet<String> dlvNumbers = new HashSet<String>();
		HashSet<String> payNumbers = new HashSet<String>();
		if( loadDelivery )
			loadDeliveries(dlvNumbers);
		loadPayments(payNumbers);
	}
	
	protected DocList createDeliveryList(String where, String order) {
		return new DocList(getDeliveryType(), where, order);
	}
	
	protected Date loadDeliveries(HashSet<String> usedNumbers) {
		Date lastDlvDate = null;
		
		for( int i=0; i < deliveries.getCount(); i++ ) {
			Document<?> d = deliveries.get(i); 
			
			Date date = d.getDate();
			
			DebetItem item = new DebetItem();
			item.docs = deliveries;
			item.index = i;
			item.isDelivery = true;
			item.date = date;
			item.number = d.getNumber();
			items.add(item);
			
			if( lastDlvDate == null ) lastDlvDate = date;
			else if( lastDlvDate.compareTo(date) < 0 ) lastDlvDate = date;
			
			usedNumbers.add(d.getNumber());
//			ids.add((long) ids.size());
		}
		return lastDlvDate;
	}

	protected Date loadPayments(HashSet<String> usedNumbers) {
		Date lastPayDate = null;
		
		for( int i=0; i < payments.getCount(); i++ ) {
			Document<?>  d = payments.get(i); 
			
			Date date = d.getDate();
			
			DebetItem item = new DebetItem();
			item.docs = payments;
			item.index = i;
			item.isDelivery = false;
			item.date = date;
			item.number = d.getNumber();
			items.add(item);
			
			if( lastPayDate == null ) lastPayDate = date;
			else if( lastPayDate.compareTo(date) < 0 ) lastPayDate = date;
			
			usedNumbers.add(d.getNumber());
			ids.add((long) ids.size());
		}
		return lastPayDate;
	}	
	
	protected abstract Class<? extends Document<?>> getDeliveryType();
	protected abstract Class<? extends Document<?>> getPaymentType();
	
	
	@Override
	public void close() {
		if( deliveries != null )
			deliveries.close();
		
		if (payments != null)
			payments.close();
		
		super.close();
	}

	protected void orderDocuments() { Collections.sort(items); }
	
	@Override public int getCount() { return items.size(); }
	
	@Override public long getId(int index) { return index; }
	
	@Override
	public Document<?> get(int index) {
		if( index < 0 || index >= items.size() )
			return null;
		
		return items.get(index).getDocument();
	}
}
