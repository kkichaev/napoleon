package com.grsoft.napoleon;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.BaseAdapter;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;

public class OrderDetailEx extends OrderDetail implements ScannerHelper.DocUpdated {
	private final static String TAG = "OrderDetailEx"; 
	ScannerHelper helper;
	String selid = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( doc instanceof OrderImpl )
			helper = new ScannerHelper((OrderImpl)doc, this);
		
		helper.registerReciver(this);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(TAG, "Activity key down: " + event.getNumber());
		
		if( event.getKeyCode() != KeyEvent.KEYCODE_BACK && helper != null)
			return helper.onKeyDown(event);
		
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( helper != null )
			helper.close();
	}

	@Override public void updated(OrderImpl doc, PriceImpl p) { 
		selid = p.getData().id;
		((BaseAdapter)lvItems.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing())
			helper.unregisterReciver(this);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override
			protected void drawInternal(View view, String name, int color, OrderItem item) {
				super.drawInternal(view, name, color, item);
				
				if(item.id.equals(selid))
					view.setBackgroundResource(R.drawable.list_grey_selector);
				else
					view.setBackgroundResource(R.drawable.list_selector);
			}
			
			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
				
				for(int i = 0; i < getCount(); i++){
					OrderItem it = (OrderItem)getItem(i);
					if(it.id.equals(selid)){
						final int pos = i;
						lvItems.post(new Runnable() { @Override public void run() {	lvItems.setSelection(pos);}});
						break;
					}
				}
				
			}
		});
	}
}
