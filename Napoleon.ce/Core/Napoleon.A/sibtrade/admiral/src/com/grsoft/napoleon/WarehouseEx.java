package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	
	public static void resetCache() { idStore = ""; }

	Set<String> returnItems = new HashSet<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			Date end = calendar.getTime();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.MONTH, -AssortmentMatrixAdapter.PERIOD_IN_MONTH);
			calendar.add(Calendar.DAY_OF_MONTH, -AssortmentMatrixAdapter.PERIOD_IN_DAY);
			Date begin = calendar.getTime();
			DatePeriod dp = new DatePeriod(begin, end);
			dp.periodType = DatePeriod.CREATED;
	
			com.grsoft.napoleon.documents.DocList dl = ReturnDoc.instance().docList(document.getId(), null, dp);
	
			for (int i = 0; i < dl.getCount(); i++) {
				ReturnImpl d = (ReturnImpl) dl.get(i);
				
				for(OrderItem o : d.getData().items) 
					returnItems.add(o.id);
			}
		}
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		if( DocType.getCurDoc() == ReturnDoc.instance() ) {
			return new ReturnAdapter(this);
		}else
			return new FoldersAdapter(this) {
				@Override
				protected void postUpdateView(View view, TreeNode node) {
					super.postUpdateView(view, node);
					
					if (node instanceof PriceTreeNode && returnItems.contains(((PriceTreeNode)node).getId()))
						view.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_selector));
				}
		};
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
				com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(document.getId(), "", "");
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

	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
			if(idStore.equals(o.whCode) == false ) {
				FoldersAdapter.resetCache();
				idStore = o.whCode;
			}
			return new ZeroFilter();
		}
		return super.createZeroPositionFilter();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false; 
			
			Price p = pi.getData();
			p.id = id;
			pi.read();
			result = (((OrderImplEx)document).getItemValue(p) > 0);			
			return result;
		}
	}
}
