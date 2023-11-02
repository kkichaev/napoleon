package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Stock;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.StockImpl;
import com.grsoft.aceteam.R;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

/**
 * Ассортимент.
 * "Псевдо документ" отображает весь товар проданный в точке
 * @author 1111
 *
 */
public class StockDoc extends DocType {
	
	public final static String DOC_NAME = "Ассортимент";
	static protected StockDoc instance = null;
	
	protected StockDoc() { super(DOC_NAME, StockImpl.class); }

	static public DocType instance() 
	{
		if( instance == null )
			instance = new StockDoc();
		return instance;
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
		
		if(tv != null)
			tv.setVisibility(View.GONE);

		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		
		if(tv != null)
			tv.setText(R.string.qty);
	
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		
		if(tv != null)
			tv.setText(R.string.caption);
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
		tv.setVisibility(View.VISIBLE);

		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		tv.setText(R.string.sum);

		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		tv.setText(R.string.date);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		Stock data = (Stock)doc.getData();
		if( data == null ) {
			super.setView(adapter, view, doc);
			return;
		}
		
		TextView tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setText(data.name);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(data.qty, Consts.QTY_SCALE));
		
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setVisibility(View.GONE);
	}

	@Override
	public int getResurceId() { return R.drawable.stock_doc; }
	
	@Override
	public DocList docList(String orgId, String order) { return new StockList(orgId, null); }
	
	@Override
	public DocList docList(String orgId, String order, DatePeriod selection) { return new StockList(orgId, selection); }

	
	class StockList extends DocList {
		protected ArrayList<StockImpl> docs = new ArrayList<StockImpl>();
		
		public StockList(String orgId, DatePeriod period) {
			DocType dt = DeliveryDoc.instance();
			ids = new ArrayList<Long>();
			
			Hashtable<String, StockImpl> data =  loadItems(orgId, dt);
			for( StockImpl ei : data.values() ) {
				docs.add(ei);
				ids.add(new Long(docs.size()));
			}
			
			Collections.sort(docs, new Comparator<StockImpl>() {
				@Override
				public int compare(StockImpl arg0, StockImpl arg1) {
					return arg0.getData().name.compareTo(arg1.getData().name);
				}
			});
		}
		
		@Override
		public Document<?> get(int index) { return ((index < docs.size()) ? docs.get(index) : null); }

		private Hashtable<String, StockImpl> loadItems(String orgId, DocType dt) {
			
			Hashtable<String, StockImpl> data = new Hashtable<String, StockImpl>();
			DocList dl = dt.docList(orgId);
			
			if( dl != null ) {
				PriceImpl pi = new PriceImpl();
				Hashtable<String, String> priceNames = new Hashtable<String, String>();

				for(Document<?> d : dl) {
					List<DeliveryItem> items = ((DeliveryImpl)d).getData().items;
					
					if( items == null )
						continue;
					
					for(DeliveryItem item : items) {
						StockImpl si = data.get(item.id);
						if( si == null ) {
							String name = priceNames.get(item.id);
							
							if( name == null ) {
								pi.getData().id = item.id;
								pi.read();
								name = pi.getData().name;
								priceNames.put(item.id, name);
							}
							
							data.put(item.id, new StockImpl(item, name));
						} else {
							si.getData().qty += item.qty;
						}
					}
				}
				
				pi.close();
				dl.close();
			}
			
			return data;
		}
	}
	
	@Override
	public int getDocTitle() {
		return R.string.stock_doc_title;
	}
}
