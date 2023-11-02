package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	int maxPrcDD = 5;
	int priceCost;
	int discount;
	int limit;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		priceCost = CostStrategy.getInstance(null).getItemCost(price.getData(), null);

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
						if( value < 0) {
							MessageBox.show(PriceCountEx.this, "Ошибка", "Скидка выше максимальной");
							return;
						}
						discount = -value;
						priceVal = priceCost - (int)(((long)priceCost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
						updateCost();
						updateDicsount();
						updateSumTextView();
					}});
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

	void makePrcAlert(String message, final Runnable run ) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Вопрос");
		b.setMessage(message);
		b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { run.run(); }
		});
		b.setNegativeButton("Нет", null);
		b.create().show();		
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
