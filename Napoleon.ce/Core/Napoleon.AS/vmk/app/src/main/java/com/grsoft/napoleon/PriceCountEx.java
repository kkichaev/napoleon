package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	int minCost, maxCost;
	private static final int DEFAULT_DISC_RANGE = 10 * Consts.SUM_SCALE;
	private static final String DISC_RANGE_KEY = "ƒиапазон÷ены";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		int q = ((PriceEx)price.getData()).inqty;
		String d = Util.simpleDateFormat.format(((PriceEx)price.getData()).date);

		if (q > 0) {
			TextView tv = findViewById(R.id.tvInqty);
			tv.setText(Util.IntToScaleStr(q, Consts.QTY_SCALE));

			tv = findViewById(R.id.tvInqtyDate);
			tv.setText(d);

			findViewById(R.id.trInqty1).setVisibility(View.VISIBLE);
			findViewById(R.id.trInqty2).setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		boolean sv = Features.CAN_CHANGE_COST;
		Features.CAN_CHANGE_COST = false;
		int cost = (int)CostStrategy.defaultInstance.getItemCost(price.getData(), document);
		Features.CAN_CHANGE_COST = sv;
		
		int discRange = DEFAULT_DISC_RANGE;
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if (cfg.getValue(sb, DISC_RANGE_KEY)) {
			try {
				discRange = Integer.parseInt(sb.toString().trim()) * Consts.SUM_SCALE;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		minCost = (int)CostStrategy.costWithDiscount(cost, discRange, Consts.SUM_SCALE);
		maxCost = (int)CostStrategy.costWithDiscount(cost, -discRange, Consts.SUM_SCALE);
		TextView tv;
		
		tv = (TextView)findViewById(R.id.tvMinPrice);
		tv.setText(Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvMaxPrice);
		tv.setText(Util.IntToScaleStr(maxCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		PriceEx pe = (PriceEx) price.getData();
		tv = (TextView)findViewById(R.id.tvQuant);
		tv.setText(Util.IntToScaleStr(pe.quant, Consts.QTY_SCALE));
	}

	@Override
	protected boolean isInputValid(Runnable r) {
		if(document instanceof OrderImpl) {
			int qty = qtyItems;
			PriceEx pe = (PriceEx) price.getData();
			qty = fixOrderQty(((CheckBox) findViewById(R.id.cbPackets)).isChecked(), qty, pe);
			int quant = pe.quant;

			if(qty > 0 && quant != 0) {
				int whQty = ((OrderImpl) document).getItemValue(pe);
				boolean wrongInput = ((qty % quant) != 0) && (qty != whQty);
				if (wrongInput) {
					Toast.makeText(this, "¬веденное количество не соответствует кратности", Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}
		return super.isInputValid(r);
	}

	@Override
	protected void onChangeCost(int newCost) {
		if(newCost < minCost || newCost > maxCost) {
			Toast.makeText(this, "»зменение цены больше допустимого", Toast.LENGTH_SHORT).show();
			return;
		}
		super.onChangeCost(newCost);
	}

	@Override protected int getContentViewId() { return R.layout.pricecountex; }
}
