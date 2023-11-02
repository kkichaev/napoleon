package com.grsoft.ads.documents;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.grsoft.ads.R;
import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.OrderEx;
import com.grsoft.ads.dataobjects.impl.ClientImpl;
import com.grsoft.ads.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.napoleon.util.LinesCountController;

public class OrderDocEx extends OrderDoc {
	
	protected OrderDocEx() { 
		super(DOC_NAME, OrderImplEx.class);
	}
	
	@Override
	public DataBaseAdapter<? extends DataObject> createAdapter(Context context,
			LinesCountController countController){
		DataBaseAdapter<? extends DataObject> result = null; 
		
		try{
			result = new OrderTreeAdapterEx(context);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new OrderDocEx();
		return instance;
	}
}

class OrderTreeAdapterEx extends OrderTreeAdapter{

	@SuppressWarnings("unused")
	private static final String TAG = "OrderTreeAdapterEx";

	public OrderTreeAdapterEx(Context context) throws IllegalAccessException,
			InstantiationException {
		super(context, "priority ASC");
		instanceType = OrderImplEx.class;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		Object item = getItem(position);
		
		if (item instanceof OrderImplEx){
			if (((!((OrderImplEx)item).isDoing() || ((OrderImplEx)item).isDone())) &&
					((OrderImplEx)item).isMissed())
				view.setBackgroundResource(R.drawable.list_red_selector);
		}
		
		return view;
	}
	
	@Override
	protected String clnm1StrGet(ClientImpl client, Order order) {
		StringBuilder result = new StringBuilder(super.clnm1StrGet(client, order));
		
		if (order instanceof OrderEx){
			OrderEx oe = (OrderEx)order;
			if (oe.delivhour.length() > 0)
				result.append("<br><i>часы доставки: ").append(oe.delivhour).append("</i>");
		}
		return result.toString();
		
		

	}
}