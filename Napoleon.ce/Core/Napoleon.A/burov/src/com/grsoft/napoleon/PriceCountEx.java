package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DocItemEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	int priceCost;
	int discount;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean canChangeCost() {
		return (document != null && document instanceof OrderImplBase<?>);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		priceCost = priceVal;

		TextView tv;
		tv = (TextView)findViewById(R.id.tvCost);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public int getValue() { return -discount; }
					@Override
					public void applayInput(int value, Object... params) {
						discount = -value;
						priceVal = priceCost - (int)(((long)priceCost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
						updateCost();
						updateDicsount();
						updateSumTextView();
					}});
			}
		});
		
		if(document != null && document instanceof OrderImplBase<?> && 
				document.getRowid() != ExtrasConst.INVALID_ID ) {
			OrgImpl org = new OrgImpl(); 
			OrderImplBase<?> o = (OrderImplBase<?>) document;
			DataObject dobj = o.findItem(price.getData().id);
			if( dobj != null && dobj instanceof DocItemEx) {
				DocItemEx item = (DocItemEx)dobj;
				discount = item.getDiscount();
				int cost = item.getCost();
				if( priceVal != cost ) {
					priceVal = cost;
					updateCost();
					updateSumTextView();
				}
			}
			
			org.close();
		}
		updateDicsount();
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		int checkDiscount = (int)((long)(priceCost - newCost) * 10000 / priceCost);
		discount = checkDiscount;
		updateDicsount();
		super.onChangeCost(newCost);
	}
	
	private void updateDicsount() {
		int val = discount;
		String label = "скидка,%";
		if( val < 0 ) {
			label = "наценка,%";
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
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		
		DataObject item = ((OrderImplBase<?>)document).findItem(price.getData().id);	
		
		if( item != null && item instanceof DocItemEx) {
			((DocItemEx)item).setDiscount(discount);
			document.write();
		}

		return ret;
	}
}
