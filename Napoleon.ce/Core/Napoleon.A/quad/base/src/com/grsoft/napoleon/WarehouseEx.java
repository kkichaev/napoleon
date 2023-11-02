package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DataTraveler.Travel;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Relivery;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew implements ScannerHelper.DocUpdated {
	protected static final String TAG = "WHEx";

	Map<String, Integer> orderQtyCash = new HashMap<String, Integer>();
	
	ScannerHelper helper;
	int orgDiscount;
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View ret = super.getPriceView(node, convertView); 
		String dsc = "";
		TextView tv = (TextView)ret.findViewById(R.id.tvDscountInfo);
		if(document instanceof OrderImplEx) {
			if(orgDiscount == 0 ) {
				PriceEx pe = (PriceEx) price.getData();
				dsc = pe.getDiscountText();
				if( dsc.length() > 0 )
					dsc = pe.getDscTypeText() + " " + dsc;
			}
			tv.setText(dsc);
		} else {
			tv.setVisibility(View.GONE);
		}
		return ret;
	}
	
	@Override
	protected int getCost(Price price) {
		return ((CostStrategyEx)CostStrategy.defaultInstance).getCostWODiscount(price, document);
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( event.getKeyCode() != KeyEvent.KEYCODE_BACK && helper != null )
			return helper.onKeyDown(event);
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document instanceof OrderImpl ) {
			helper = new ScannerHelper((OrderImpl)document, this);
			
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = document.getId();
			oi.read();
			oi.close();
			orgDiscount = oe.discount;			
		}
		
		lvItemSelect.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { 
				Log.d(TAG, String.format("Got %d,  %c", event.getKeyCode(), event.getNumber()));
				if( event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
					helper.onKeyDown(event);
				return false; 
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( helper != null )
			helper.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(document instanceof WSOrderImpl){
			orderQtyCash.clear();
			Date begin = Util.getDate();
			Calendar cal = Calendar.getInstance();
			cal.setTime(begin);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			Date end = cal.getTime();
			
			DatePeriod dp = new DatePeriod(begin, end);
			com.grsoft.napoleon.documents.DocList list = OrderDoc.instance().docList(null, null, dp);
			
			for(Document<?> d : list){
				OrderEx ord = (OrderEx)d.getData();
				for(OrderItem i : ord.items)
					orderQtyCash.put(i.id, getOrderQty(i.id) + i.qty);
			}
		}
	}
	
	public int getOrderQty(String id){
		Integer qty = orderQtyCash.get(id);
		return qty != null ? qty : 0;
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if(document instanceof WSOrderImpl && textView.getId() == R.id.tvClmn2)
			textView.setText(Util.IntToScaleStr(getOrderQty(price.id), Consts.QTY_SCALE, Util.DEC_DELIM, true));
		else
			super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		FoldersAdapter.resetCache();
		return new ZeroFilter();
	}

	@Override
	public void updated(OrderImpl doc, PriceImpl p) {
		PriceCount.open(this, p.getRowid(), doc);
	}
	class ZeroFilter extends ZeroPositionFilter {
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( document instanceof ReturnImplEx)
			return new ReturnAdapter(this, document.getId());
		return super.createListAdapter();
	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);
			
			String where = String.format("id='%s'", orgId);
			DataTraveler.travel(Relivery.class, new Travel<Relivery>() {

				@Override
				public boolean travel(DataTraveler<Relivery> item) {
					for(DeliveryItem di : item.data.items)
						ids.add(di.id);
					return true;
				}}, where);
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}
}