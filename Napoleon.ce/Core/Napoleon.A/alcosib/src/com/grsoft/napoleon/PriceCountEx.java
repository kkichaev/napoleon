package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	int priceCost;
	int discount;
	int minCost;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void refreshData() {
		super.refreshData();
		PriceEx pe = (PriceEx)price.getData(); 
		minCost = pe.minCost;
		
		if( document instanceof OrderImplEx ) {
			int whIndex = ((OrderImplEx)document).getWhIndex();
			if( whIndex > 0 && whIndex <= pe.whQty.size() ) {
				int tminCost = pe.whQty.get(whIndex-1).minCost;
				if( tminCost > 0 )
					minCost = tminCost;
			}
		}
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvMinCost);
		tv.setText(Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
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
					@Override public int getValue() { return discount; }
					@Override
					public void applayInput(int value, Object... params) {
						//int svDiscount = discount;
						discount = value;
						int newCost = countPriceVal(); 
						if( newCost < minCost ) {
							Toast.makeText(PriceCountEx.this, R.string.cost_below_min, Toast.LENGTH_LONG).show();
							//discount = svDiscount;
							//return;
						}
						priceVal = newCost;
						updateCost();
						updateDiscount();
						updateSumTextView();
					}}, Consts.DISCOUNT_SCALE, false, "Введите скидку");
			}
		});
		
		findViewById(R.id.cbMinCost).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				priceVal = minCost;
				discount = (int)(((long)(priceVal  - priceCost) * Consts.DISCOUNT_SCALE * 100 - Consts.DISCOUNT_SCALE * 50)/ priceCost);
				updateCost();
				updateDiscount();
				updateSumTextView();
			}
		});
				
		if(document != null && document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ID ) {
			OrderImpl o = (OrderImpl)document;
			OrderEx ord = (OrderEx)o.getData();
			OrderItemEx oe = (OrderItemEx) o.findItem(price.getData().id);
			discount = (oe != null) ? oe.discount : ord.discount;

			priceVal = countPriceVal();
			updateCost();
			updateSumTextView();
			
		}
		updateDiscount();
	}

	private int countPriceVal() {
		return priceCost + (int)(((long)priceCost * discount + Consts.SUM_SCALE * Consts.DISCOUNT_SCALE / 2) / (Consts.SUM_SCALE * Consts.DISCOUNT_SCALE));
	}
	
	private void updateDiscount() {
		String label = (discount <= 0 ) ? "скидка,%" : "наценка,%";
		((TextView)findViewById(R.id.tvDiscountLabel)).setText(label);
		
		String value = Util.IntToScaleStr(Math.abs(discount), Consts.DISCOUNT_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		
		if( document instanceof OrderImpl ) {
			OrderItemEx oi = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);
			if( oi != null ) {
				oi.discount = discount;
				document.write();
			}
		}

		return ret;
	}
}
