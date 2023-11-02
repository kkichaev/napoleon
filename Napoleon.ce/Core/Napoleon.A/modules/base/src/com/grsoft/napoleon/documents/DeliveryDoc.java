package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.R;

public class DeliveryDoc extends DocType {

	static private DeliveryDoc instance = null;
	
	private DeliveryDoc() { super("Отгрузки", DeliveryImpl.class); }
	
	static public DocType instance() 
	{
		if( instance == null )
			instance = new DeliveryDoc();
		return instance;
	}

	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";
		if( where != null && where.length() > 0 ) {
			if( whereStr.length() > 0 )
				whereStr += " AND ";
			whereStr += where;
		}
		return new DocList(DeliveryImpl.class, whereStr, order);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.dlv_doc;
	}

	public void getItemsFromLastDoc(String id, List<String> itemIds, int period)
	{
		DocList list = docList(id, "date DESC");
		if( list.getCount() > 0 ) {
			int index = 0;
			if( period > 0 ) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, -period);
				c.set(Calendar.HOUR_OF_DAY, 0);
				Date checkDate = c.getTime();
				
				for( ; index < list.getCount(); index++ ) {
					DeliveryImpl doc = (DeliveryImpl)list.get(index);
					if( doc == null || doc.getData().created.before(checkDate) )
						break;
					
					addItemsId(itemIds, doc);
				}
			} else {
				DeliveryImpl doc = (DeliveryImpl)list.get(0);
				addItemsId(itemIds, doc);
			}
		}
		list.close();
	}

	protected void addItemsId(List<String> itemIds, DeliveryImpl doc) {
		if( doc != null ) {
			for(DeliveryItem item: doc.getData().items) {
				if( !itemIds.contains(item.id))
					itemIds.add(item.id);
			}				
		}
	}}
