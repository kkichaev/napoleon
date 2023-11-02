package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DiscountAction;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.DocList;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.documents.ScriptDoc;

import android.database.sqlite.SQLiteDatabase;

public class OrderImplEx extends OrderImpl {
	int whIndex = -1; 
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		Order o = (Order)copy.data;
		ArrayList<OrderItem> items = new ArrayList<OrderItem>(o.items);
		HashSet<Integer> delParty = new HashSet<Integer>();
		PriceImpl priceImpl = new PriceImpl();
		
		for(OrderItem i : o.items){
			OrderItemEx iex = (OrderItemEx)i;
			priceImpl.getData().id = i.id;
			
			if(priceImpl.read()){
				if(delParty.contains(iex.partid))
					items.remove(i);
				else{
					if(priceImpl.getData().qty < iex.qty){
						if(iex.partid > 0)
							delParty.add(iex.partid);
						
						items.remove(i);
					}
				}
			}
		}
		
		for(OrderItem i : o.items)
			if(delParty.contains(((OrderItemEx)i).partid))
					items.remove(i);
		
		priceImpl.close();
		
		o.items = items;
		super.postCopyProcess(copy);
	}
	
	public int getWhIndex() {
		return ((OrderEx)data).whNumber;
//		int index = -1;
//		ConfigImpl ci = new ConfigImpl();
//		Config c = ci.getData();
//		c.key = "WHouse";
//		
//		String wid = "";
//		try{
//			wid = Integer.toString(((OrderEx)data).whNumber);
//			
//
//			if(ci.read()) {
//				String[] val = c.value.split(";");
//				
//				for(int i = 0; i < val.length; i++)
//					if(wid.equals(val[i])){
//						index = i;
//						break;
//					}
//			}
//			
//			ci.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		if( index < 0 )
//			index = 0;
//		
//		return index;
	}
	

	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

		if( whIndex == 0 )
			return item.qty;
		
		return (whIndex <= ((PriceEx)item).whQty.size()) ? 
				((PriceEx)item).whQty.get(whIndex-1).qty : 
				0;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}
	
	public void checkDiscountAction(String id) {
		OrderEx ex = (OrderEx)data;
		DiscountAction d = getDiscountAction(id);
		
		if( d != null)
			ex.discact.remove(d);
		else {
			d = new DiscountAction();
			d.id = id;
			ex.discact.add(d);
		}
		
		write();
		close();
	}
	
	public DiscountAction getDiscountAction(String id) {
		DiscountAction d = null;
		OrderEx ex = (OrderEx)data;
		
		for(DiscountAction a : ex.discact)
			if(a.id.equals(id)) {
				d = a;
				break;
			}
		
		return d;
	}
	
	@Override
	public boolean delete() {
		boolean result = super.delete();
		
		if(result) {
			DbWriter.checkDBTable(data.getClass());
			
			SQLiteDatabase db = DataBaseManager.getDataBase();
			try {
				db.delete(DataObjectInfo.getInstance().getTableName(Bonus.class), "[order] = ?", new String[] { Long.toString(data.created.getTime())});
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	public List<DocExportListener> getSendedDocuments() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		
		docs.add(new DocSendListner("Order", this));
		docs.add(new DocSendListner(BonusDoc.instance().getObjectName(), BonusImpl.class, String.format("[order] = %d", data.created.getTime())));
		
//
//		List<BonusImpl> cd = getBonus();
//		for( BonusImpl si : cd ) 
//			docs.add(new DocSendListner("Bonus", si));
		
		return docs;
	}

//	private List<BonusImpl> getBonus() {
//		List<BonusImpl> result = new ArrayList<BonusImpl>();
//		
//		com.grsoft.napoleon.documents.DocList dl = new com.grsoft.napoleon.documents.DocList(
//				BonusImpl.class, String.format("[order] = %d", data.created.getTime()), null);
//		
//		for(Document<?> d : dl)
//			if (d instanceof BonusImpl)
//				result.add((BonusImpl)d);
//		
//		return result;
//	}

	
}
