package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	int priceCost;
	int discount;

	Boolean hasDiscount = null;
	int maxDiscount = 0;

	@Override
	protected boolean canChangeCost() {
		
		if (hasDiscount == null) {
			hasDiscount = false;
			ConfigImpl cfg = new ConfigImpl();
			StringBuilder value = new StringBuilder();
			if (cfg.getValue(value, "Discount"))
				hasDiscount = Boolean.parseBoolean(value.toString());
		}

		return hasDiscount;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//cbPackets.setVisibility(View.GONE);
		priceCost = priceVal;

		try {
			ConfigImpl cfg = new ConfigImpl();
			StringBuilder value = new StringBuilder();
			if (cfg.getValue(value, "МаксСкидка"))
				maxDiscount = (int)(Double.parseDouble(value.toString()) * Consts.SUM_SCALE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TextView tv;
		tv = (TextView) findViewById(R.id.tvCost);
		tv.setText(Util.IntToScaleStr(priceCost, Consts.SUM_SCALE,
				Util.DEC_DELIM, false));

		tv = (TextView) findViewById(R.id.tvDiscount);
		if (canChangeCost()) {
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
							int newDiscount = -value;// discount - value;
							int newPreciVal = priceCost
									- (int) (((long) priceCost * newDiscount + Consts.SUM_SCALE
											* Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));

							if (newDiscount <= maxDiscount) {
								discount = newDiscount;
								priceVal = newPreciVal;
								updateCost();
								updateDicsount();
								updateSumTextView();
							} else
								minPriceExceedToast();
						}
					});
				}
			});
		}

		if (document != null && document instanceof OrderImplBase<?>
				&& document.getRowid() != ExtrasConst.INVALID_ID) {
			OrgImpl org = new OrgImpl();
			OrderImplBase<?> o = (OrderImplBase<?>) document;
			DataObject dobj = o.findItem(price.getData().id);
			if (dobj != null && dobj instanceof OrderItemEx) {
				OrderItemEx item = (OrderItemEx) dobj;
				discount = item.discount;
				int cost = item.cost;
				if (priceVal != cost) {
					priceVal = cost;
					updateCost();
					updateSumTextView();
				}
			}

			org.close();
		}
		updateDicsount();
	}

	@Override
	protected void onChangeCost(int newCost) {
		int checkDiscount = (int) ((long) (priceCost - newCost) * 10000 / priceCost);
		discount = checkDiscount;
		
		if (discount <= maxDiscount) {
			updateDicsount();
			super.onChangeCost(newCost);
		} else
			minPriceExceedToast();
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
		if (canChangeCost()) {
			SpannableString ss = new SpannableString(value);
			ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
			tv.setTextColor(Color.BLUE);
			tv.setText(ss);
		} else {
			findViewById(R.id.trDiscount1).setVisibility(View.GONE);
			findViewById(R.id.trDiscount2).setVisibility(View.GONE);
		}
	}

	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();

		DataObject item = ((OrderImplBase<?>) document).findItem(price
				.getData().id);

		if (item != null && item instanceof OrderItemEx) {
			((OrderItemEx) item).discount = discount;
			document.write();
		}

		return ret;
	}

	protected void minPriceExceedToast() {
		Toast.makeText(this, R.string.min_price_exceed, Toast.LENGTH_SHORT)
				.show();
	}
}
