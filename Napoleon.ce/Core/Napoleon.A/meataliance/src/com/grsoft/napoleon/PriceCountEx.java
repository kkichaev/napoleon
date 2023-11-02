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
	final int MAX_RUB_DISCOUNT = 9;
	int maxDiscount = MAX_RUB_DISCOUNT * Consts.SUM_SCALE;
	static BroadcastReceiver calcResult;
	private TextView tvMaxDiscount;

	@Override protected int getStartValue() { return 0; }
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvMaxDiscount = (TextView) findViewById(R.id.tvMaxDiscount);
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
		
		calcResult = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null){
					edCount.setText(intent.getStringExtra(Calculator.CALCULATOR_RESULT_VALUE));
					updateSumTextView();
				}
			}
		};
		
		registerReceiver(calcResult, new IntentFilter(Calculator.CALCULATOR_RESULT_ACTION));

		tvBaseCost = (TextView) findViewById(R.id.tvBaseCost);
		if (document instanceof OrderImpl)
			((OrderImpl) document).setUpdateQtyHandler(this);

		ConfigImpl cfgImpl = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		tvMaxDiscount.setText("");
		if(cfgImpl.getValue(sb, "МаксСкидка"))
			try{
				maxDiscount = Integer.parseInt(sb.toString())  * Consts.SUM_SCALE;
				tvMaxDiscount.setText(Util.IntToScaleStr(maxDiscount, Consts.SUM_SCALE));
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
							priceVal = priceCost - discount;
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
		
		cbPackets.setVisibility(View.GONE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(calcResult);
		calcResult = null;
	}
	
	private void updateDicsount() {
		((TextView) findViewById(R.id.tvDiscountLabel)).setText(R.string.disclbl);
		
		int val = discount;
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

	protected void minPriceExceedToast() {
		Toast.makeText(this, R.string.min_price_exceed, Toast.LENGTH_SHORT)
				.show();
	}

}
