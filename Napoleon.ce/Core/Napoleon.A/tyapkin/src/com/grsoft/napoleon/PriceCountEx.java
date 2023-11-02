package com.grsoft.napoleon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.calculator2.Calculator;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	static BroadcastReceiver calcResult;
	Boolean canChangeCost = null;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean canChangeCost() {
		if( canChangeCost == null) {
			canChangeCost = true;
			ConfigImpl config = new ConfigImpl();
			config.getData().key = "ћожно»змен€ть÷ену";
			try {
				if (config.read() && Integer.parseInt(config.getData().value) == 0)
					canChangeCost = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			config.close();
		}
		return canChangeCost;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		updateCost();

		findViewById(R.id.ivCalc).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				Intent data = new Intent(v.getContext(), Calculator.class);
				String val = Util.IntToScaleStr(priceVal, Consts.SUM_SCALE, ".", false);
				data.putExtra(Calculator.START_CALC_VAL, val);
				data.putExtra(Calculator.BROADCAST_RESULT, true);
				v.getContext().startActivity(data);
			}
		});
		
//		if(document != null && document.isEditable() && canChangeCost() )
//			tv.setOnClickListener(new View.OnClickListener() {
//				@Override public void onClick(View v) { 
//					Intent data = new Intent(v.getContext(), Calculator.class);
//					String val = Util.IntToScaleStr(priceVal, Consts.SUM_SCALE, ".", false);
//					data.putExtra(Calculator.START_CALC_VAL, val);
//					data.putExtra(Calculator.BROADCAST_RESULT, true);
//					v.getContext().startActivity(data);
//				}
//			});
		
		calcResult = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null){
//					int newCost = Util.StrToScale(intent.getStringExtra(Calculator.CALCULATOR_RESULT_VALUE), Consts.SUM_SCALE);
//					onChangeCost(newCost);
				}
			}
		};
		
		registerReceiver(calcResult, new IntentFilter(Calculator.CALCULATOR_RESULT_ACTION));
		
		if( document instanceof WSOrderImpl )
			findViewById(R.id.trVanQty).setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PricePrint p = (PricePrint)price.getData();
		TextView tv = (TextView)findViewById(R.id.tvVanQty);
		tv.setText(Util.IntToScaleStr(p.vanQty, Consts.QTY_SCALE));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
			unregisterReceiver(calcResult);
		}catch(Exception e){}
		
		calcResult = null;
	}
	
	@Override
	protected boolean isComplexSalesHistory() {
		DocType cd = DocType.getCurDoc();
		return (cd != OrderDoc.instance()) ? false : super.isComplexSalesHistory();
	}
}
