package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
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
	CostStrategyEx costStrategy;
	TextView tvBaseCost;
	TextView tvPercent;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tvBaseCost = (TextView) findViewById(R.id.tvBaseCost);
		tvPercent = (TextView) findViewById(R.id.tvPercent);
		
		if(document == null)
			costStrategy = new CostStrategyEx();
		else
			costStrategy = (CostStrategyEx) CostStrategy
					.getInstance((Class<? extends Document<?>>) document.getClass());
		
		if (document instanceof OrderImpl)
			((OrderImpl) document).setUpdateQtyHandler(this);

		TextView tv;
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
						discount = -value;
						priceVal = CostStrategyEx.calcDiscount(
								-discount + costStrategy.getDiscount(
												price.getData(), document),
								priceCost);

						updateCost();
						updateDicsount();
						updateSumTextView();
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
			priceCost = costStrategy.getBaseCost(p, document);
		}

		updateDicsount();
		
		tvPercent.setText(Util.IntToScaleStr(costStrategy.getDiscount(price.getData(), document), 
				Consts.SUM_SCALE, Util.DEC_DELIM, false));
		tvBaseCost.setText(Util.IntToScaleStr(costStrategy.getBaseCost(price.getData(), document), 
				Consts.SUM_SCALE, Util.DEC_DELIM, false));
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
		if (isNewItem)
			((OrderItemEx) item).discount = discount;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

}
