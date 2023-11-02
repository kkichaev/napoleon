package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	int minCost = 0;
	int nac = 0;
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.tvDiscount).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public int getValue() { return nac; }
					@Override
					public void applayInput(int value, Object... params) {
						if( value < 0 ) {
							MessageBox.show(PriceCountEx.this, "Ошибка", "Наценка меньше 0");
							return;
						}
						nac = value;
						if( nac >= 10000 )
							nac = 9999;
						priceVal = (int)(((long)10000 * minCost) / (10000 - nac));
						
						updateCost();
						updateDiscount();
						updateSumTextView();
					}}, Consts.SUM_SCALE, false, "Введите наценку", DiscountInputDlg.Type.OnlyNac);
				
			}
		});
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		minCost = (price.getData().cost.size() > 0 ) ? price.getData().cost.get(0).cost : priceVal;
		nac = priceVal == 0 ? 0 : (10000 * (priceVal - minCost)) / priceVal;

		updateDiscount();
	}
	
	private void updateDiscount() {
		String value = Util.IntToScaleStr(nac, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	@Override
	protected void onChangeCost(int newCost) {
		if( price.getData().cost.size() > 0 && newCost < price.getData().cost.get(0).cost )
			MessageBox.show(this, "Ошибка", "Цена ниже минимальной");
		else {
			super.onChangeCost(newCost);
			nac = (10000 * (priceVal - minCost)) / priceVal;
			updateDiscount();
		}
	}
}
