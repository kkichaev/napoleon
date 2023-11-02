package com.grsoft.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.WarehouseNew;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;

public class AssortmentMatrixAdapterEx extends AssortmentMatrixAdapter {

	public AssortmentMatrixAdapterEx(WarehouseNew warehouse, String id) {
		super(warehouse, id);
	}

	@Override
	protected List<MatrixItem> getMatrixItems() {
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		
		try{
			Set<String> priceIds = new HashSet<String>();
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			Date end = calendar.getTime();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.MONTH, -PERIOD_IN_MONTH);
			Date begin = calendar.getTime();
			DatePeriod dp = new DatePeriod(begin, end);
			dp.periodType = DatePeriod.CREATED;
			
			DocList dl = OrderDoc.instance().docList(id, null, dp);
			
			for(int i = 0; i < dl.getCount(); i++){
				Document<?> d = dl.get(i);
				Order o = ((OrderImpl)d).getData();

				if(o.items != null && o.items.size() > 0)
					for(OrderItem oi : o.items)
						addItem(result, priceIds, oi.id);
			}
			
			dl = RemnantsDoc.instance().docList(id, null, dp);
			
			for(int i = 0; i < dl.getCount(); i++){
				Document<?> d = dl.get(i);
				Remnants r = (Remnants) d.getData();
				
				if(r.items != null && r.items.size() > 0)
					for( RemnantItem ri : r.items )
						addItem(result, priceIds, ri.id);
			}
			
		}catch(Exception e){
			
		}
		
		return result;
	}
}
