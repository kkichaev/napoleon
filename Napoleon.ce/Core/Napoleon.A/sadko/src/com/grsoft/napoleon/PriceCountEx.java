package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	int dsc;
	int suplDsc;
	int priceCost;
	int maxDsc, minCost;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean canChangeCost() {
		if(!(document != null && document instanceof OrderImplEx) )
			return false;
		
		PriceEx pe = (PriceEx)price.getData(); 
		return pe.action == 0 && maxDsc != 0;
	}
	
	// Меняем только скиду - цену изменить не можем
	@Override
	protected void doCostChange() {}
	
	@Override
	protected void updateCost() {
		String value = Util.IntToScaleStr(getInputCost(price.getData()), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		tv.setText(value);
		tv.setTextColor(Color.BLACK);
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
					@Override public int getValue() { return -dsc; }		
				}, Consts.SUM_SCALE, false, "Скидка", DiscountInputDlg.Type.OnlyDiscount); 
			}
		});
		
		if( document instanceof OrderImpl){
			((OrderImpl)document).setUpdateQtyHandler(this);
		}
	}
	
	@Override
	protected void refreshData() {
		dsc = 0;
		
		PriceEx p = (PriceEx) price.getData();
		
		@SuppressWarnings("unchecked")
		CostStrategyEx cs = (CostStrategyEx) CostStrategy .getInstance((Class<? extends Document<?>>)((document == null) ? null : document.getClass())); 
		
		priceCost = cs.getPriceCose(p, document);
		suplDsc = cs.getSuplDiscount(p, document);
		maxDsc = suplDsc != 0 || p.action != 0 ? 0 : p.discount;
		
		TextView tv = (TextView)findViewById(R.id.tvTruePrice);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		tv = (TextView)findViewById(R.id.tvSuplDsc);
		tv.setText(Util.IntToScaleStr(suplDsc, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		super.refreshData();
		
		tv = (TextView)findViewById(R.id.tvMaxDsc);
		tv.setText(Util.IntToScaleStr(maxDsc, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " %");
		if( canChangeCost() ) {
			minCost = getCost(priceCost, maxDsc);
			
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
		if( newCost < minCost){
			Toast.makeText(this, String.format("Цена ниже минимальной %s",
					Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false)),
					Toast.LENGTH_SHORT).show();
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
		if( newNac > maxDsc ) {
			Toast.makeText(this, String.format("Скидка больше максимальной %s%%", 
					Util.IntToScaleStr(maxDsc, Consts.SUM_SCALE, Util.DEC_DELIM, false)), 
					Toast.LENGTH_SHORT).show();
			return;
		}
		int newCost = getCost(priceCost, newNac);
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
}
