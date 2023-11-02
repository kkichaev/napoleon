package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	int discount;
	int priceCost;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean canChangeCost() {
		return document instanceof OrderImpl;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				DiscountInputDlg.open(PriceCountEx.this, new InputNumber	(priceCost) {
					@Override public long getValue() { return -discount; }
					@Override
					public void applayInput(int value, Object... params) {
						discount = -value;
						priceVal = CostStrategy.costWithDiscount(priceCost, discount, Consts.SUM_SCALE); 
						
						updateCost();
						updateDicsount();
						updateSumTextView();
					}});
			}
		});
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		discount = 0;
		if(document != null && document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ID ) {
			OrderImpl o = (OrderImpl)document;
			OrderItemEx oe = (OrderItemEx) o.findItem(price.getData().id);

			if( oe != null ) {
				discount = oe.discItem;
				if( priceVal != oe.cost ) {
					priceVal = oe.cost;
					updateCost();
					updateSumTextView();
				}
			}

			Price p = price.getData();

			Features.CAN_CHANGE_COST = false;
			priceCost = (int)CostStrategy.defaultInstance.getItemCost(p, document);
			Features.CAN_CHANGE_COST = true;

			((OrderImpl)document).setUpdateQtyHandler(new OrderImplBase.UpdateQtyHandler() {
				@Override
				public void itemUpdated(OrderItem item, Order order, boolean isNew) {
					OrderItemEx ie = (OrderItemEx) item;
					ie.discItem = discount;
				}
			});
		}

		updateDicsount();
	}

	@Override
	protected void doCostChange() {
		InputNumberDlg.open(this, new InputNumber(priceCost) {
			@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
			@Override public long getValue() { return priceVal; }
		}, Consts.SUM_SCALE, false, getString(R.string.cost)); 
	}
	
	private void updateDicsount() {
		int val = discount;
		String label = "������,%";
		if( val < 0 ) {
			label = "�������,%";
			val = -val;
		}
		((TextView)findViewById(R.id.tvDiscountLabel)).setText(label);
		
		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}
	
	
	@Override
	protected void onChangeCost(int newCost) {
		discount = 100 * Consts.SUM_SCALE - (int)(((float)newCost/(float)priceCost) * Consts.SUM_SCALE * 100 );
		updateDicsount();
		super.onChangeCost(newCost);
	}
}
