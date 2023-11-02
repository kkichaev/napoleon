package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.FoldersAdapter;

import android.database.sqlite.SQLiteDatabase;
import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		if( DocType.getCurDoc() == ReturnDoc.instance() ) {
			return new ReturnAdapter(this);
		}
		return super.createListAdapter();
	}

	class ReturnAdapter extends FoldersAdapter {

		public ReturnAdapter(WarehouseNew warehouse) {
			super(warehouse);
		}
		
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			try {
				PriceImpl pi = new PriceImpl();
				Price p = pi.getData();
				
				fprice.clear();

				HashSet<String> items = new HashSet<String>();
//				Date end = Util.getDateTime();
//				Calendar c = Calendar.getInstance();
//				c.setTime(end);
//				c.add(Calendar.MONTH, -5);
//				
//				DatePeriod dp = new DatePeriod(c.getTime(), end);
//				DocList dl = OrderDoc.instance().docList(document.getId(), "", "");
//				for(Document<?> d : dl) {
//					if( !((OrderImpl)d).isExported() )
//						continue;
//					Order dlv = (Order) d.getData();
//					for(OrderItem di : dlv.items) {
//						if( items.contains(di.id))
//							continue;
//						
//						p.id = di.id;
//						if( pi.read() == false )
//							continue;
//						
//						items.add(di.id);
//						if(!fprice.containsKey(p.folderID))
//							fprice.put(p.folderID, new ArrayList<PriceInfo>());
//						
//						PriceInfo pri = new PriceInfo(pi.getRowid(), p.name, p.id);
//						fprice.get(p.folderID).add(pri);
//					}
//				}
				DocList dl = DeliveryDoc.instance().docList(document.getId(), "", "");
				for(Document<?> d : dl) {
					Delivery dlv = (Delivery) d.getData();
					for(DeliveryItem di : dlv.items) {
						if( items.contains(di.id))
							continue;
						
						p.id = di.id;
						if( pi.read() == false )
							continue;
						
						items.add(di.id);
						if(!fprice.containsKey(p.folderID))
							fprice.put(p.folderID, new ArrayList<PriceInfo>());
						
						PriceInfo pri = new PriceInfo(pi.getRowid(), p.name, p.id);
						fprice.get(p.folderID).add(pri);
					}
				}
				dl.close();
				pi.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
