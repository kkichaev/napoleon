package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.FocusedGroupImpl;
import com.grsoft.dataobjects.impl.FocusedItemsImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.network.DocExportListener;

public class OrderDocEx extends OrderDoc {
	public static final int LAST_ITEM_WEEKS = -3;
	
	public static void initialize () {
		instance = new OrderDocEx();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		DocExportListener dl = super.getDirtyDocuments();
		
		ArrayList<Long> needRemove = new ArrayList<Long>();
		DocList docs = dl.getDocuments();
		for(Document<?> d : docs) {
			OrderImplBase<? extends Order> doc = (OrderImplBase<? extends Order>) d;
			if( FocusedGroupImpl.getUnsettedGroups(doc).size() > 0 || FocusedItemsImpl.getUnsettedItems(doc).size() > 0 ) {
				needRemove.add(doc.getRowid());
			}
		}
		docs.removeDocuments(needRemove);
		docs.close();
		return dl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void getItemsFromLastDoc(String id, List<String> itemIds, int period)
	{
		DocList list = docList(id, "created DESC");
		if( list.getCount() > 1 ) {
			int index = 1;
			if( period > 0 ) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.WEEK_OF_MONTH, LAST_ITEM_WEEKS);
				c.set(Calendar.HOUR_OF_DAY, 0);
				Date checkDate = c.getTime();
				
				for( ; index < list.getCount(); index++ ) {
					OrderImplBase<? extends Order> doc = (OrderImplBase<? extends Order>)list.get(index);
					if( doc == null || doc.getData().created.before(checkDate) )
						break;
					
					addItemsId(itemIds, doc);
				}
			} else {
				OrderImplBase<? extends Order> doc = (OrderImplBase<? extends Order>)list.get(1);
				addItemsId(itemIds, doc);
			}
		}
		list.close();
	}
}
