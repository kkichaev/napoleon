package com.grsoft.napoleon;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.DogovorItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DogovorImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements ScannerHelper.DocUpdated {
	public static final String TAG = "PriceCountEx";
	ScannerHelper helper;
	
	Boolean haveInPack = false;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx p = (PriceEx)price.getData();
		TextView tv;
		
		tv = (TextView)findViewById(R.id.tvSpeedSales);
		tv.setText(Util.IntToScaleStr(p.salesSpeed, 10));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(document instanceof OrderImplEx ) {
			((OrderImplEx)document).setUpdateQtyHandler(new OrderImplEx.UpdateQtyHandler() {

				@Override
				public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
					((OrderItemEx)item).qtyPack = (item.inPack()) ? (int)((long)item.qty * Consts.QTY_SCALE / qtyInPack) : 0;
				}
			});
			helper = new ScannerHelper((OrderImplEx)document, this);
			edCount.setOnKeyListener(new View.OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if( event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
						helper.onKeyDown(event);
					return false;
				}
			});
			
			helper.registerReciver(this);
	}
			
		btnOK.setFocusable(false);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(TAG, "Activity key down: " + event.getNumber());
		
		if( event.getKeyCode() != KeyEvent.KEYCODE_BACK && helper != null)
			return helper.onKeyDown(event);
		
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing())
			helper.unregisterReciver(this);
	}


	@Override
	public void updated(OrderImpl doc, PriceImpl p) {
		price.read(p.getRowid(), false);
		refreshData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( helper != null )
			helper.close();
	}	
	
	@Override protected boolean getStartInPack() { return haveInPack; }
	
	@Override
	protected int getQtyInPack(Price p) {
		int inpack = Consts.QTY_SCALE;
		if(document instanceof OrderImplEx ) {
			DogovorImpl di = new DogovorImpl();
			Dogovor d = di.getData();
			
			d.id = ((OrderEx)document.getData()).dgv;
			di.read();
			di.close();
			
			for(DogovorItem item : d.items) {
				if( item.id.equals(p.id)) {
					if( item.qtyInPack > Consts.QTY_SCALE ) {
						inpack = item.qtyInPack;
						haveInPack = true;
					}
					break;
				}
			}
		}
		return inpack;
	}
}
