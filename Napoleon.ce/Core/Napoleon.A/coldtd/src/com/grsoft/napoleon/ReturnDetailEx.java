package com.grsoft.napoleon;

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.utl.PackShowHelper;
import com.grsoft.napoleon.utl.ScannerHelper;


public class ReturnDetailEx extends ReturnDetail {
	AtomicBoolean isScanning=new AtomicBoolean(false);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isScanning.set(false);
	}
	
	@Override
	protected void updateTotalSum() {

		ReturnImplEx odoc = (ReturnImplEx)doc;
		TextView tvTotalSum = (TextView)findViewById(R.id.tvTotalSum);

		int count = odoc.count(), countPack = odoc.countPack();
		long sum = odoc.sum();
		
		PackShowHelper.updateTotalSum(tvTotalSum, sum, count, countPack);
	}
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		if( item.inPack() ) {
			PackShowHelper.drawItemQty(color, item, tvQty, (PriceEx) price.getData());
		} else
			super.drawItemQty(color, item, tvQty);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 132:
			NapoleonEx.moveTo(this);
			break;
		case 212:
		case 221:
			if( isScanning.compareAndSet(false, true) ) {
				scan();
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	void scan() {
		Thread scanThread = new Thread() {
			public void run() {
				ScannerHelper.doScan(ReturnDetailEx.this, doc);
				isScanning.set(false);
			}
		};
		scanThread.start();
	}
}
