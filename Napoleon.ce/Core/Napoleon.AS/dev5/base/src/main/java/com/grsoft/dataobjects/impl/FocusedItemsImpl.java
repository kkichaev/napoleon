package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.FocusedItems;
import com.grsoft.dataobjects.FocusedItemsItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderFocusedItem;
import com.grsoft.dataobjects.Price;

public class FocusedItemsImpl extends DbObject<FocusedItems> {

	public static final String OBJECT_NAME = "FocusedItems";

	public static List<FocusedItemsItem> getUnsettedItems(OrderImplBase<? extends Order> doc) {
		List<FocusedItemsItem> ret = new ArrayList<FocusedItemsItem>();
		
		FocusedItemsImpl fi = new FocusedItemsImpl();
		FocusedItems fitems = fi.getData();
		fitems.id = doc.getId();
		if( !fi.read() ) {
			fitems.id = "";
			fi.read();
		}
		fi.close();
		
		if( fitems.items != null ) {		
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();		
			for(FocusedItemsItem i : fitems.items) {
				if(doc.findItem(i.id) == null) {
					boolean unsetted = true;
					
					for(OrderFocusedItem ofi : doc.getData().focusedItems) {
						if( ofi.id.equals(i.id) ) {
							unsetted = false;
							break;
						}
					}
					if( unsetted ) {
						p.id = i.id;
						if( pi.read()  && doc.getItemValue(p) > 0 )
							ret.add(i);
					}
				}
			}
			pi.close();
		}
		
		return ret;
	}

}
