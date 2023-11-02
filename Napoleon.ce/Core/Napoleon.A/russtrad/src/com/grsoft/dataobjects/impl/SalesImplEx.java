package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Party;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;

public class SalesImplEx extends SalesImpl implements Itemsable{

	static final int RET_PARTY = 0x100;
	
	@Override
	public DataObject findItem(String itemId) {
		SalesItem result = null;
		
		for(OrderItem item : getData().items){
			if (item.id.equals(itemId)){
				if(result == null)
					result = ((SalesItem)item).copy();
				else
					result.qty += item.qty;
			}
		}
		
		return result;
	}

	@Override
	public int getItemValue(Price item) {
		int result = 0;
		for(Party party : ((PriceEx)item).party)
			result += party.qty;
		
		return result;
	}

	class PartyComparator implements Comparator<Party> {

		String code;
		
		public PartyComparator(String orgCode) { code = orgCode; }
		
		@Override
		public int compare(Party lhs, Party rhs) {
			if( !lhs.owner.equals(rhs.owner) ) {
				if( lhs.owner.equals(code) )
					return -1;				
				if( rhs.owner.equals(code))
					return 1;
			}
			return lhs.date.compareTo(rhs.date);
		}
		
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack){
		PriceEx price = (PriceEx) priceImpl.getData();
		
		int availQty = qty;
		List<OrderItem> toRemove = new ArrayList<OrderItem>();
		
		for(OrderItem item : getData().items){
			if (!item.id.equals(price.id))
				continue;
			
			Party p = price.getParty(((SalesItemEx)item).date, ((SalesItemEx)item).owner);
			if (p != null)
				p.qty += item.qty;
			
			toRemove.add(item);
		}
		
		data.items.removeAll(toRemove);
		List<Party> parties = price.party;
		
		if (parties != null){
			Collections.sort(parties, new PartyComparator(data.supplyercode));
			
			Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");
			for(Party p : parties){
				if(p != null && p.qty > 0){
					int cqty = availQty <= 0 ? 0 : (p.qty > availQty) ? availQty : p.qty;
					
					if (cqty <= 0){
						break;
					}else{
						try {
							SalesItemEx item = (SalesItemEx) itemClass.newInstance();
							item.cost = cost;
							item.id = price.id;
							item.qty = cqty;
							item.date = p.date;
							item.sum = FPOperation.itemMul(item.cost, item.qty, Consts.QTY_SCALE);
							item.costWOtax = item.cost * 100 / (100 + price.tax1);
							item.taxSum = item.sum - FPOperation.itemMul(item.costWOtax, item.qty, Consts.QTY_SCALE);
							item.ntd = p.ntd;
							item.owner = p.owner;
							
							if(p.owner.equals(data.supplyercode))
								item.flags |= RET_PARTY;
							
							p.qty -= cqty;
							availQty -= cqty;
							
							data.items.add(item);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			write();
			priceImpl.write();
			SalesDoc.instance().refreshDocSum(data.id);
		}
		
		return (availQty == 0);
	}
	
	@Override
	public boolean delete() {
		boolean res = false;
		try {
			if( (res = super.delete()) )
			{
				if( !isExported() && data.items != null )
				{
					PriceImpl priceImpl = new PriceImpl();
					
					for(OrderItem item : getData().items){
						priceImpl.getData().id = item.id;
						
						if (priceImpl.read()){
							Party p = ((PriceEx)priceImpl.getData()).getParty(((SalesItem)item).date, ((SalesItemEx)item).owner);
							
							if (p != null){
								p.qty += item.qty;
								priceImpl.write();
							}
						}
						priceImpl.close();
					}
				}
		
				SalesDoc.instance().refreshDocSum(data.id);
			}
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public CreatableDocument<Sales> createInstance() {
		return new SalesImplEx();
	}
}
