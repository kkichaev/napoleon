package com.grsoft.napoleon;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	EditText edPack;
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected long getSumValue() {
		String text = edCount.getText().toString();

		long count = text.length() == 0
			? 0
			: Util.StrToScale(text, Consts.QTY_SCALE);
		
		if( edPack == null ) {
			edPack = (EditText)findViewById(R.id.edPackCount);
			edPack.setInputType(InputType.TYPE_NULL);
			edPack.addTextChangedListener(new TextWatcher() {
				@Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				@Override public void afterTextChanged(Editable s) { updateSumTextView(); }
			});
		
			edCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
				@Override public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus ) {
						keypadHelper.setTargetID(R.id.edCount);
						edCount.selectAll();
						findViewById(R.id.btnComma).setEnabled(true);
					}
				}
			});
			
			edPack.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
				@Override public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus ) {
						keypadHelper.setTargetID(R.id.edPackCount);
						edPack.selectAll();
						findViewById(R.id.btnComma).setEnabled(false);
					}
				}
			});
		}
		
		text = edPack.getText().toString();
		long packCount = text.length() == 0
				? 0 
				: Util.StrToScale(text, Consts.QTY_SCALE);

		count += (int)((long)packCount * qtyInPack / Consts.QTY_SCALE);
		
		long sumItems = getSum((int)count);

		qtyItems = (int)count;
		return sumItems;
	}

	@Override
	protected int getStartValue() {
		return 0;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
	
		PriceEx pe = (PriceEx)price.getData();
		
		int qtyPack = qtyItems / qtyInPack * Consts.QTY_SCALE;
		int qtyItem = qtyItems % qtyInPack;
		
		edCount.setText(Util.IntToScaleStr(qtyItem, Consts.QTY_SCALE));
		edCount.setEnabled(pe.packIn == 0);
		
		edPack.setText(Util.IntToScaleStr(qtyPack, Consts.QTY_SCALE));
		edPack.requestFocus();
		
		TextView tv = (TextView)findViewById(R.id.tvPackPrice);
		tv.setText(Util.IntToScaleStr((int)((long)priceVal * qtyInPack / Consts.QTY_SCALE), Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
}
