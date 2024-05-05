package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {

	Integer maxDiscount = null;
	long priceCost;
	long minCost = 0;
	int dsc = 0;

	@Override protected int getContentViewId() { return R.layout.pricecount_new_ex; }

	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).boxed > 0;
	}

	@Override
	protected boolean canChangeCost() {
		if(maxDiscount != null && maxDiscount > 0)
			return true;
		return super.canChangeCost();
	}

	//	@Override
//	protected int getStartValue() {
//		return Consts.QTY_SCALE;
//	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(document instanceof OrderImplEx)
			((OrderImplEx) document).setUpdateQtyHandler(this);
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		if(document instanceof OrderImplEx && maxDiscount == null) {
			maxDiscount = 0;
			StringBuilder sb = new StringBuilder();
			ConfigImpl ci = new ConfigImpl();
			if(ci.getValue(sb, "MaxDiscount")) {
				maxDiscount = (int)Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
			}

			if(maxDiscount > 0) {
				findViewById(R.id.trMinCost).setVisibility(View.VISIBLE);
				findViewById(R.id.trDiscount).setVisibility(View.VISIBLE);
				findViewById(R.id.tvDiscount).setOnClickListener(v -> {
					changeDiscount();
				});
				findViewById(R.id.tvPrice).setOnClickListener(v-> doCostChange());
			}
		}
		
		PriceEx pe = (PriceEx)price.getData();
		String unit = getString(pe.boxed > 0 ? R.string.box_lbl : R.string.qty_lbl);

		((TextView)findViewById(R.id.tvMinCost)).setText(Util.IntToScaleStr(pe.minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		minCost = pe.minCost;
		
		View v = findViewById(R.id.trPacket);
		v.setVisibility((pe.boxed > 0 || pe.qtyInPack == Consts.QTY_SCALE ) ? View.GONE : View.VISIBLE); 
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvQtyOrder);
		String str = getString(R.string.orderQty) + "\n" + unit;
		tv.setText(str);

		tv = (TextView)findViewById(R.id.tvOnWh);
		str = getString(R.string.onWh) + ", " + unit;
		tv.setText(str);
		
		if( pe.boxed > 0) {
			TextView tvQty = (TextView) findViewById(R.id.tvQty);
			int whQty = (int)((long)pe.qty * Consts.QTY_SCALE / pe.qtyInPack);
			tvQty.setText(Util.IntToScaleStr(whQty, Consts.QTY_SCALE));
		}

		if(document instanceof OrderImplEx) {
			OrderItemEx oie = (OrderItemEx) ((OrderImpl) document).findItem(pe.id);
			if(oie == null) {
				priceCost = priceVal;
				dsc = 0;
				updateCost();
			} else {
				priceCost = oie.costWOD;
				dsc = oie.discount;
				chgFromDiscount = true;
				onChangeCost(oie.cost);
			}
			updateDiscount();
		}
	}

	@Override
	protected long getSum(int count) {
		if(cbPackets != null && cbPackets.isChecked())
			count = (int)((long)count * qtyInPack / Consts.QTY_SCALE);
		return countSum(count);
	}

	long countSum(int qty) {
		long sum = (long) priceVal * qty / Consts.QTY_SCALE;
//		if(dsc != 0)
//			sum = CostStrategy.costWithDiscount(sum, dsc, Consts.SUM_SCALE);
		return sum;
	}

	boolean chgFromDiscount = false;
	void onDiscountChange(int newDsc) {
		int cost = (int) CostStrategy.costWithDiscount(priceCost, newDsc, Consts.SUM_SCALE);
		if(cost < minCost)
		{
			Toast.makeText(this, "Цена товара меньше минимальной", Toast.LENGTH_SHORT).show();
			return;
		}
		dsc = newDsc;
		chgFromDiscount = true;
		onChangeCost(cost);
		updateDiscount();
	}

	boolean checkCost(long newCost) {
		if(newCost < minCost) {
			Toast.makeText(this, "Цена товара меньше минимальной", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	boolean checkDiscount(int newDsc) {
		if(newDsc > maxDiscount) {
			Toast.makeText(PriceCountEx.this, "Скидка больше максимальной", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onChangeCost(int newCost) {
		if(!checkCost(newCost))
			return;
		if(!chgFromDiscount) {
			int newDsc = (int)((1.0 - (double)newCost / priceCost) * 10000);
			if(!checkDiscount(newDsc)) {
				return;
			}
			dsc = newDsc;
			updateDiscount();
		}
		chgFromDiscount = false;

		super.onChangeCost(newCost);
	}

	void changeDiscount() {
		InputNumberDlg.open(this, new InputNumber() {
			@Override
			public void applayInput(int value, Object... params) {
				if(!checkDiscount(value))
					return;
				onDiscountChange(value);
			}

			@Override public long getValue() {return dsc;}
		}, Consts.SUM_SCALE, false, "Введите скидку");
	}

	void updateDiscount() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx) item;
		oie.discount = dsc;
		oie.sum = countSum(item.qty);
		oie.costWOD = priceCost;
	}
}
