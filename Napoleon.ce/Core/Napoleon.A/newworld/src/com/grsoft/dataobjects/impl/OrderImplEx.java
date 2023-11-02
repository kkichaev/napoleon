package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.dataobjects.OrderCheckedItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrice;
import com.grsoft.dataobjects.Price;

public class OrderImplEx extends OrderImpl {
	
	public HashSet<String> getPriceItems() {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = getId();
		oi.read();
		oi.close();
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		HashSet<String> items = new HashSet<String>();
		if( o.price != null ) {
			for(OrgPrice op : o.price) {
				p.id = op.id;
				if( pi.read() && p.qty > 0 ) {
					items.add(op.id);
				}
			}
		}
		pi.close();
		
		return items;
	}
	
	public boolean haveUnsettedItems() {
		HashSet<String> items = getPriceItems();

		if( data.items != null )
			for(OrderItem ori : data.items)
				items.remove(ori.id);
		
		if( ((OrderEx)data).checkedItems != null )
			for(OrderCheckedItem oci : ((OrderEx)data).checkedItems)
				items.remove(oci.id);
		return (items.size() != 0);
	}
	
	public boolean isChecked(String id) {
		if( data.items != null )
			for(OrderItem ori : data.items)
				if( id.equals(ori.id) )
					return true;
		
		if( ((OrderEx)data).checkedItems != null )
			for(OrderCheckedItem oci : ((OrderEx)data).checkedItems)
				if( id.equals(oci.id) )
					return true;
		
		return false;
	}
	
	public void check(String id) {
		if( data.items != null )
			for(OrderItem ori : data.items)
				if( id.equals(ori.id) )
					return ;
		
		List<OrderCheckedItem> checked = ((OrderEx)data).checkedItems; 
		if( checked != null )
			for(OrderCheckedItem oci : checked)
				if( id.equals(oci.id) ) {
					checked.remove(oci);
					return;
				}
		
		OrderCheckedItem oci = new OrderCheckedItem();
		oci.id = id;
		if( checked == null ) {
			checked = new ArrayList<OrderCheckedItem>();
			((OrderEx)data).checkedItems = checked;
		}
		checked.add(oci);
	}
}
