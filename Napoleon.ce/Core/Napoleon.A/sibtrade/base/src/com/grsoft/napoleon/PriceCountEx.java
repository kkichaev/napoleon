package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.android.calculator2.Calculator;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesHistory;
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
	SimpleDateFormat sfd =  new SimpleDateFormat("dd.MM", Locale.getDefault());

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

	private static class HistoryItem
	{
		public HistoryItem(long date) {
			this.date = date;
		}
		
		public long date = 0;
		public int order = 0;
		public int returns = 0;
	}

	@SuppressLint("UseSparseArrays")
	@Override
	protected void createSimpleHistory(Price p, LinearLayout ll) {
		Map<Long, HistoryItem> data = new HashMap<Long, HistoryItem>();
		
		SalesHistory history = new SalesHistory();
		history.create(document.getId(), p.id, Features.SALES_FROM_ORDERS) ;

		for(Entry<Long, Integer> e : history.entrySet()) {
			HistoryItem h = new HistoryItem(e.getKey());
			h.date = e.getKey();
			h.order = e.getValue();
			
			data.put(h.date, h);
		}
			
		for(Document<?> d : ReturnDoc.instance().docList(document.getId())) {
			ReturnImpl r = (ReturnImpl)d;
			
			for(OrderItem i : r.getData().items) {
				if (i.id.equals(p.id)) {
					long key = d.getDate().getTime();
					
					if (!data.containsKey(key)) {
						data.put(key, new HistoryItem(key));
					}
					
					HistoryItem h = data.get(key);
					h.returns += i.qty;
				}
			}
		}
		
		List<HistoryItem> values = new ArrayList<PriceCountEx.HistoryItem>();
		values.addAll(data.values());
		Collections.sort(values, new Comparator<HistoryItem>() {

			@Override
			public int compare(HistoryItem lhs, HistoryItem rhs) {
				if (lhs.date - rhs.date < 0)
					 return 1;
				 if (lhs.date - rhs.date > 0)
					 return -1;
				 return 0;
			}
		});
		
		
		for (HistoryItem h : values)
		{
			TextView tvSaleItem = new TextView(this);
			tvSaleItem.setText(Html.fromHtml(
					String.format("%s<br>%s&nbsp;&nbsp;&nbsp;<font color='red'>%s</font>", sfd.format(new Date(h.date)), 
							 Util.IntToScaleStr(h.order, Consts.QTY_SCALE),
							 Util.IntToScaleStr(h.returns, Consts.QTY_SCALE)
							 )));
			
			tvSaleItem.setLines(2);
			tvSaleItem.setTextColor(getResources().getColor(R.color.black));
			tvSaleItem.setPadding(5, 3, 5, 3);
			ll.addView(tvSaleItem);
			
			Log.d("makeSaleHistory", tvSaleItem.getText().toString());
		}
		
	}
}
