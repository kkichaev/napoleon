package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	int dsc;
	int priceCost;
	int maxNac, maxDsc, minCost, maxCost;
	int minQty;
	
	Boolean canChangeCost = null;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean canChangeCost() {
		if(!(document != null && document instanceof OrderImplBase<?>) )
			return false;
		
		if( canChangeCost == null ) {
			canChangeCost = false;
	        ConfigImpl config = new ConfigImpl();
	        Config c = config.getData();
			c.key = "МожноИзменятьЦену";
			try {
				if (config.read() && Integer.parseInt(c.value) == 1)
					canChangeCost = true;
				c.key = "МаксимальнаяСкидка";
				if( config.read() )
					maxDsc = Integer.parseInt(c.value) * Consts.SUM_SCALE;
				c.key = "МаксимальнаяНаценка";
				if( config.read() )
					maxNac = -Integer.parseInt(c.value) * Consts.SUM_SCALE;
			} catch (Exception e) {
				e.printStackTrace();
			}
			config.close();
		}

		return canChangeCost ? (((PriceEx)price.getData()).canChangeCost > 0) : false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv;
		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public void applayInput(int value, Object... params) { onNacChange(-value); }
					@Override public long getValue() { return -dsc; }
				}, Consts.SUM_SCALE, false, "Скидка/Наценка"); 
			}
		});
		
		if( document instanceof OrderImpl){
			((OrderImpl)document).setUpdateQtyHandler(this);
		}
		
		cbPackets.setEnabled(((PriceEx)price.getData()).packOnly == 0);
	}
	
	@Override
	protected void postOnCreate() {
		super.postOnCreate();
		minQty = ((PriceEx)price.getData()).minQty;
	}

	@Override
	protected long getInputCost(Price p) {
		return priceCost;
	}

	@Override
	protected void refreshData() {
		dsc = 0;
		
		PriceEx p = (PriceEx) price.getData();
		int sumType = document != null ? document.getSumType() : WarehouseEx.costype;
		priceCost = (p.cost.size() > sumType && sumType >= 0) ?  p.cost.get(sumType).cost : 0;			

		TextView tv;
		tv = (TextView)findViewById(R.id.tvInfo);
		tv.setText(Html.fromHtml(p.info));

		super.refreshData();
			
		if( canChangeCost() ) {
			minCost = getCost(priceCost, maxDsc);
			maxCost = getCost(priceCost, maxNac);
			
			findViewById(R.id.trDiscount).setVisibility(View.VISIBLE);
			updateDsc();
		} else {
			findViewById(R.id.trDiscount).setVisibility(View.GONE);
		}
		updateCost();
	}
	
	@Override
	protected DataObject getDocItem(Price p) {
		DataObject res = super.getDocItem(p);
		if( res != null && res instanceof OrderItemEx) {
			OrderItemEx oie = (OrderItemEx)res; 
			dsc = oie.discount;
			priceVal = oie.cost;
		}
		return res;
	}

	void updateDsc() {
		TextView tv;
		String text = dsc < 0 ? "наценка, %" : "скидка, %";
		tv = (TextView)findViewById(R.id.tvDscLabel);
		tv.setText(text);
		
		text = Util.IntToScaleStr(Math.abs(dsc), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(text);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
		tv.setTextColor(Color.BLUE);
	}
	
	@Override
	protected void onChangeCost( int newCost ) {
		if( newCost < minCost || newCost > maxCost){
			Toast.makeText(this, "Цена выходит за пределы изменения", Toast.LENGTH_SHORT).show();
			return;
		}
		
		priceVal = newCost;
		Price p = price.getData();

		if( p.cost.get(0).cost != 0 )
			dsc = (int)(10000 - (long)priceVal * 10000 / priceCost);

		updateCost();
		updateSumTextView();
		updateDsc();
	}
	
	int getCost(int prcCost, int discount) {
		return (int)(((long)prcCost * (10000 - discount)) / 10000);
	}
	
	void onNacChange( int newNac ) {
		int newCost = getCost(priceCost, newNac);
		if( newCost < minCost || newCost > maxCost){
			Toast.makeText(this, "Цена выходит за пределы изменения", Toast.LENGTH_SHORT).show();
			return;
		}
		dsc = newNac;
		priceVal = newCost;
		
		updateCost();
		updateSumTextView();
		updateDsc();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((OrderItemEx)item).discount = dsc;
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		int qty = qtyItems;
		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
		
		if(minQty > 0)
			result = minQty <= qty;
			
		return result;
	}
	
	@Override
	protected void invalidInputValueHandler() {
		Toast.makeText(this, getString(R.string.order_min_qty, Util.IntToScaleStr(minQty, Consts.QTY_SCALE)), Toast.LENGTH_SHORT).show();
		edCount.setText(Util.IntToScaleStr((int) minQty, Consts.QTY_SCALE));
		edCount.selectAll();
	}
	
	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).packOnly == 1;
	}

	public void openPricePresent(View v, String path) {
		if (document == null)
			super.openPricePresent(v, path);
		else {
			updateOrder();
			PricePresentationFolder.open(v.getContext(), price.getRowid(), document.getRowid(), null);
		}

		finish();
	}

	@Override
	public void onBackPressed() {
		PresentationFolderEx.PriceFocus = price.getRowid();
		super.onBackPressed();
	}

	@Override
	protected void postOKProcess() {
		PresentationFolderEx.PriceFocus = price.getRowid();
		super.postOKProcess();
	}
}
