package com.grsoft.napoleon.documents;

import java.util.Date;

import com.grsoft.dataobjects.impl.DlvReturnImpl;

public class DebtDocListEx extends DebtDocList {
	DocList returns;
	
	public DebtDocListEx(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery);
	}
	
	@Override
	protected void init(String where, String order, boolean loadDelivery) {
		super.init(where, order, loadDelivery);
		
		returns = new DocList(DlvReturnImpl.class, where, order);
		for( int i=0; i < returns.getCount(); i++ ) {
			Document<?> d = returns.get(i); 
			
			Date date = d.getDate();
			
			DebetItem item = new DebetItem();
			item.docs = returns;
			item.index = i;
			item.isDelivery = true;
			item.date = date;
			item.number = d.getNumber();
			items.add(item);
		}
	}
	
	@Override
	public void close() {
		super.close();
		
		if(returns != null)
			returns.close();
	}
}
