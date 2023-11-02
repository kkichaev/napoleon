package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDoc extends DocType
	implements DocItemsStock{
	
	public static OrderDoc instance = null;
	
	/**
	 * Для переопределения при инициализации программы
	 */
	protected OrderDoc() { super("Заявки", "Order", OrderImpl.class);} 
	
	protected OrderDoc(String docName, String objName, Class<? extends OrderImplBase<? extends Order>> type) { 
		super(docName, objName, type);
	} 

	static public DocType instance() {
		if( instance == null )
			instance = new OrderDoc();
		return instance;
	}
	
	static public DocType instance(Class<? extends OrderImplBase<? extends Order>> type) {
		if( instance == null )
			instance = new OrderDoc("Заявки", "Order", type);
		return instance;
	}
	
	public String getHistory(String org, String item) {
		StringBuilder result = new StringBuilder();
		ArrayList<Entry<Long, Integer>> saleHistory = new ArrayList<Entry<Long,Integer>>();
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM", Locale.getDefault());
		
		try {
			getSortedSaleHistory(org, item, saleHistory);
			
			for (Entry<Long, Integer> entry: saleHistory) {
				result.append(simpleDateFormat.format(
						new Date(entry.getKey())));
				result.append(" ");
				result.append(Util.IntToScaleStr(entry.getValue(), Consts.QTY_SCALE));
				result.append(" ");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> cd = (CreatableDocument<?>)create();
		DocExportListener dl =  new DocSendListner(getObjectName(), 
				(Class<? extends CreatableDocument<?>>) cd.getClass(), 
				"params", ParamState.ofExported);
		
		if( Features.REMOVE_EMPTY_ORDERS ){
			ArrayList<Long> needRemove = new ArrayList<Long>();
			DocList docs = dl.getDocuments();
			for(Document<?> d : docs) {
				OrderImplBase<? extends Order> doc = (OrderImplBase<? extends Order>) d;
				if( doc.isEmpty() ) {
					needRemove.add(doc.getRowid());
				}
			}
			docs.removeDocuments(needRemove);
			docs.close();
		}
		return dl;
	}
	
	/**
	 * Возвращает отсортированную историю 
	 * 
	 * @param orgId организация
	 * @param itemId прайс
	 * @param result выходной параметр
	 * @throws RuntimeException
	 */
	private void getSortedSaleHistory(String orgId, String itemId, List<Entry<Long, Integer>> result) 
		throws RuntimeException
	{
		SalesHistory history = new SalesHistory();
		createSaleHistory(orgId,itemId, history) ;
		result.addAll(history.entrySet());
		Collections.sort(result, new CmpHistory());
	}
	
	private void createSaleHistory(String orgId, String itemId, SalesHistory history) 
		throws RuntimeException
	{
		int weight = 0;
		if( Features.SHOW_WEIGHT_IN_HISTORY ) {
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			p.id = itemId;
			
			if( pi.read() )
				weight = p.weight;
			pi.close();
		}
		
		DocList list = docList(orgId);
		for( int i=0; i<list.getCount(); i++ ) {
			OrderImplBase<?> doc = (OrderImplBase<?>)list.get(i);
			if( doc != null ) {
				Order o = doc.getData();
				for (DataObject dataObject: o.items) {
					OrderItem orderItem = (OrderItem) dataObject;
					
					if (orderItem.id.equals(itemId)) {
						Date maskDate = new Date(o.date.getYear(), o.date.getMonth(), o.date.getDate());
						int qty = orderItem.qty;
						if( weight != 0 ) {
							qty = (int)(((long)qty * weight + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE);
							qty -= (qty % Consts.WEIGHT_SCALE); // округлим 
						}
						history.put(maskDate.getTime(), qty);
					}
				}
				
			}
		}
		list.close();
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
				c.add(Calendar.MONTH, -period);
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

	protected void addItemsId(List<String> itemIds, OrderImplBase<? extends Order> doc) {
		if( doc != null ) {
			for(OrderItem item: doc.getData().items) {
				if( !itemIds.contains(item.id))
					itemIds.add(item.id);
			}				
		}
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.order_doc;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.order_doc_title;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.order_doc_2;
	}
}

