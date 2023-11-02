package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.Ret1cImpl;

public class DebtDocListEx extends DebtDocList {
	
	DocList rets;

	public DebtDocListEx(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery);
	}
	
	@Override
	public void close() {
		super.close();
		if( rets != null )
			rets.close();
	}
	
	@Override
	protected void init(String where, String order, boolean loadDelivery) {
		super.init(where, order, loadDelivery);
		
		rets = new DocList(Ret1cImpl.class, where, order);
		for(int i=0; i<rets.getCount(); i++) {
			Document<?> doc = rets.get(i);
			
			DebetItem item = new DebetItem();
			item.docs = rets;
			item.index = i;
			item.isDelivery = false;
			item.date = doc.getDate();
			item.number = doc.getNumber();
			items.add(item);			
		}
	}
}
