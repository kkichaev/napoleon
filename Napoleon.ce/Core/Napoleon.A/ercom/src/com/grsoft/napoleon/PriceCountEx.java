package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	int maxDiscount;
	int maxMargin;
	int priceCost;
	int discount;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		if( cfg.getValue(sb, "МаксимальнаяСкидкаНаКПК") )
			maxDiscount = Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
		sb = new StringBuilder();
		if( cfg.getValue(sb, "МаксимальнаяНаценкаНаКПК") )
			maxMargin = Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
		
		priceCost = priceVal;
		TextView tv;
		tv = (TextView)findViewById(R.id.tvCost);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				CostInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public int getValue() { return discount; }
					@Override
					public void applayInput(int value, Object... params) {
						
						if( value < 0 ) {
							if( -value > maxDiscount ) {
								MessageBox.show(PriceCountEx.this, "Ошибка", "Скидка выше максимальной");
								return;
							}
						} else {
							if( value > maxMargin ) {
								MessageBox.show(PriceCountEx.this, "Ошибка", "Наценка выше максимальной");
								return;
							}
						}
						
						discount = value;
						int sign = (discount < 0) ? -1 : 1;
						priceVal = priceCost + (int)((long)priceCost * value + sign * Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE);
						
						updateCost();
						updateDicsount();
						updateSumTextView();
					}
				});
			}
		});
				
		if(document != null && document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ID ) {
			OrderImpl o = (OrderImpl)document;
			OrderItemEx oe = (OrderItemEx) o.findItem(price.getData().id);
			if( oe != null ) {
				discount = oe.discount;
				if( priceVal != oe.cost ) {
					priceVal = oe.cost;
					updateCost();
					updateSumTextView();
				}
			}
		}
		updateDicsount();
	}
	
	private void updateDicsount() {
		String value;
		value = (discount <=0) ? "скидка,%" : "наценка,%";
		((TextView)findViewById(R.id.tvDiscountLabel)).setText(value);
		
		value = Util.IntToScaleStr(Math.abs(discount), Consts.SUM_SCALE, Util.DEC_DELIM, false);
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
