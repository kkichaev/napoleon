package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.calculator2.Calculator;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	int bonusVisible;
	static BroadcastReceiver calcResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		calcResult = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null)
					onChangeCost((int) Util.StrToScale(intent.getStringExtra(Calculator.CALCULATOR_RESULT_VALUE), Consts.SUM_SCALE));
			}
		};
		
		registerReceiver(calcResult, new IntentFilter(Calculator.CALCULATOR_RESULT_ACTION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing())
			unregisterReceiver(calcResult);
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void refreshData() {
		super.refreshData();

		View trBonus = findViewById(R.id.trBonus);
		bonusVisible = View.GONE;
		if( document instanceof OrderImplEx ) {
			BonusDefImpl.loadBonus(document.getDate(), new BonusDefImpl.BonusAction() {
				
				@Override
				public boolean doAction(BonusDef item) {
					if( price.getData().id.equals(item.iditem) ) {
						TextView tvBonus = (TextView)findViewById(R.id.tvBonus);
						String text = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
						tvBonus.setText(text);
						bonusVisible = View.VISIBLE;
						return false;
					}
					return true;
				}
			});
		}
		trBonus.setVisibility(bonusVisible);
	}
	
	@Override
	protected int getStartValue(){ return 0;}

	@Override
	protected void doCostChange() {
		Intent data = new Intent(this, Calculator.class);
		String val = Util.IntToScaleStr((int) priceVal, Consts.SUM_SCALE, ".");
		data.putExtra(Calculator.START_CALC_VAL, val);
		data.putExtra(Calculator.BROADCAST_RESULT, true);
		startActivity(data);
	}
	
}
