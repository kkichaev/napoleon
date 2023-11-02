package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements UpdateQtyHandler {
	int prcCost = 0;
	int discount = 0;

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView tv = (TextView) findViewById(R.id.tvDiscount);
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
						if (value < -OrdHelper.getMaxDiscount()) {
							MessageBox.show(PriceCountEx.this, "Ошибка", "Вы превысили допустимое значение");
							return;
						}
						
						discount = -value;
						priceVal = prcCost
								- (int) (((long) prcCost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2)
										/ (Consts.SUM_SCALE * Consts.SUM_SCALE));
						updateCost();
						updateDicsount();
						updateSumTextView();
					}
				});
			}
		});

		
	}

	private void updateDicsount() {
		int val = discount;
		String label = "скидка,%";
		if (val < 0) {
			label = "наценка,%";
			val = -val;
		}
		((TextView) findViewById(R.id.tvDiscountLabel)).setText(label);

		String value = Util.IntToScaleStr(val, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView) findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setTextColor(Color.BLUE);
		tv.setText(ss);
	}

	@Override
	protected boolean canChangeCost() {
		return false;
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		PriceEx pe = (PriceEx) price.getData();

		int index = document == null ? 0 : document.getSumType();
		prcCost = index < pe.cost.size() ? pe.cost.get(index).cost : pe.cost.size() > 0 ? pe.cost.get(0).cost : 0;

		TextView tv;

		tv = (TextView) findViewById(R.id.tvPriceCost);
		tv.setText(Util.IntToScaleStr(prcCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		if (document instanceof OrderImpl && document.getRowid() != ExtrasConst.INVALID_ID) {
			((OrderImpl) document).setUpdateQtyHandler(this);

			OrderImpl o = (OrderImpl) document;
			OrderItemEx oe = (OrderItemEx) o.findItem(price.getData().id);

			if (oe != null)
				discount = oe.discount;
			else
				discount = ((OrderEx)((OrderImpl) document).getData()).discount;
		}
		
		updateDicsount();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.cost_below_min) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка ввода");
			b.setMessage("Цена меньше минимальной");
			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((OrderItemEx) item).discount = discount;

	}
}
