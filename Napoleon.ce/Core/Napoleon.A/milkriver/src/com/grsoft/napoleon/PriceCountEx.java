package com.grsoft.napoleon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.calculator2.Calculator;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements UpdateQtyHandler {
	int discount;
	int priceCost;
	TextView tvBaseCost;
	int maxDiscount = 10 * Consts.SUM_SCALE;
	static BroadcastReceiver calcResult;

	@Override protected int getStartValue() { return 0; }
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Button button = (Button)findViewById(R.id.bPlus);
		button.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				int scaledQty = qtyItems + 1 * Consts.QTY_SCALE;
				edCount.setText(Util.IntToScaleStr(scaledQty, Consts.QTY_SCALE));
			}
		});
		
		button = (Button)findViewById(R.id.bMinus);
		button.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (qtyItems >= Consts.QTY_SCALE) {
					int scaledQty = qtyItems - Consts.QTY_SCALE;
					edCount.setText(Util.IntToScaleStr(scaledQty, Consts.QTY_SCALE));
				}
			}
		});
		
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		updateCost();

		edCount.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Intent data = new Intent(v.getContext(), Calculator.class);
				String val = edCount.getText().toString();
				data.putExtra(Calculator.START_CALC_VAL, val);
				data.putExtra(Calculator.BROADCAST_RESULT, true);
				v.getContext().startActivity(data);
				return false;
			}
		});
//		tv.setOnClickListener(new View.OnClickListener() {
//			@Override public void onClick(View v) { 
//				Intent data = new Intent(v.getContext(), Calculator.class);
//				String val = Util.IntToScaleStr(priceVal, Consts.SUM_SCALE, ".", false);
//				data.putExtra(Calculator.START_CALC_VAL, val);
//				data.putExtra(Calculator.BROADCAST_RESULT, true);
//				v.getContext().startActivity(data);
//			}
//		});
		
		calcResult = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null){
					edCount.setText(intent.getStringExtra(Calculator.CALCULATOR_RESULT_VALUE));
					updateSumTextView();
//					int newCost = Util.StrToScale(intent.getStringExtra(Calculator.CALCULATOR_RESULT_VALUE), Consts.SUM_SCALE);
//					onChangeCost(newCost);
				}
			}
		};
		
		registerReceiver(calcResult, new IntentFilter(Calculator.CALCULATOR_RESULT_ACTION));

		tvBaseCost = (TextView) findViewById(R.id.tvBaseCost);
		if (document instanceof OrderImpl)
			((OrderImpl) document).setUpdateQtyHandler(this);

		ConfigImpl cfgImpl = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		if(cfgImpl.getValue(sb, "МаксСкидка"))
			try{
				maxDiscount = Integer.parseInt(sb.toString())  * Consts.SUM_SCALE;
			}catch(Exception e){
				e.printStackTrace();
			}
		
		tv = (TextView) findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override
					public int getValue() {
						return -discount;
					}

					@Override
					public void applayInput(int value, Object... params) {
						int newDiscount = -value;
						if (newDiscount <= maxDiscount) {
							discount = -value;
							priceVal = calcDiscount(-discount, priceCost);
							updateCost();
							updateDicsount();
							updateSumTextView();
						} else
							minPriceExceedToast();
					}
				});
			}
		});

		if (document != null && document instanceof OrderImpl
				&& document.getRowid() != ExtrasConst.INVALID_ID) {
			OrderImpl o = (OrderImpl) document;
			OrderItemEx oe = (OrderItemEx) o.findItem(price.getData().id);

			if (oe != null) {
				discount = oe.discount;
				if (priceVal != oe.cost) {
					priceVal = oe.cost;
					updateCost();
					updateSumTextView();
				}
			}

			Price p = price.getData();
			priceCost = CostStrategy.getInstance(
					(Class<? extends Document<?>>) document.getClass())
					.getItemCost(p, document);
		}

		updateDicsount();

		tvBaseCost.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE,
				Util.DEC_DELIM, false));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing()){
			try{
				unregisterReceiver(calcResult);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			calcResult = null;
		}
	}
	
	private void updateDicsount() {
		int val = discount;
		String label = "скидка,%";
		if (val < 0) {
			label = "наценка,%";
			val = -val;
		}
		((TextView) findViewById(R.id.tvDiscountLabel)).setText(label);

		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE,
				Util.DEC_DELIM, false);
		TextView tv = (TextView) findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((OrderItemEx) item).discount = discount;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	public static int calcDiscount(int discount, int cost) {
		int sign = (int) Math.signum(discount);
		cost += ((int) ((long) cost * Math.abs(discount) + Consts.SUM_SCALE
				* Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE))
				* sign;
		return cost;
	}

	protected void minPriceExceedToast() {
		Toast.makeText(this, R.string.min_price_exceed, Toast.LENGTH_SHORT)
				.show();
	}

}
