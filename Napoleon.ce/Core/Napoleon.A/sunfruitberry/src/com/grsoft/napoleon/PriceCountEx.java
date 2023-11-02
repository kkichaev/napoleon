package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	int dsc;
	int priceCost;
	
	Boolean canChangeCost = null;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		if( document instanceof OrderImpl ) {
			findViewById(R.id.trDiscount).setVisibility(View.VISIBLE);
			
			((OrderImpl)document).setUpdateQtyHandler(this);
			
			TextView tv = (TextView)findViewById(R.id.tvDiscount);
			updateDsc();
			
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public void applayInput(int value, Object... params) { onDscChange(value); }
						@Override public int getValue() { return dsc; }		
					}, Consts.SUM_SCALE, false, "Скидка"); 
				}
			});
		}
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		
		if( document instanceof OrderImpl ) {
			String remark = "";
			
			OrderItemEx oie = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);
			if(oie == null) {
				priceCost = priceVal;
				dsc = 0;
			} else {
				priceCost = oie.costWD;
				if(priceCost == 0) {
					priceCost = oie.cost;
				}
				dsc = oie.discount;
				remark = oie.remark;
			}
			
			((EditText)findViewById(R.id.edRemark)).setText(remark);
			
			onDscChange(dsc);
		} else {
			findViewById(R.id.tvRemark).setVisibility(View.GONE);
			findViewById(R.id.edRemark).setVisibility(View.GONE);
		}
	}

	void updateDsc() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	void onDscChange( int newNac ) {
		dsc = newNac;
		priceVal = CostStrategy.costWithDiscount(priceCost, dsc, Consts.SUM_SCALE);
		
		updateCost();
		updateSumTextView();
		updateDsc();
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx) item;
		oie.costWD = priceCost;
		oie.discount = dsc;
		oie.remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();
	}
}
