package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	private static final String QTY_COUNT = "qty_count";

	int actionCost = 0;
	int priceCost = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(document instanceof OrderImpl) {
			((OrderImpl)document).setUpdateQtyHandler(this);
		}
	}

	@Override
	protected void refreshData() {
		super.refreshData();

		TextView tvQuant = (TextView) findViewById(R.id.tvQuant);
		tvQuant.setText(Util.IntToScaleStr(((PriceEx)price.getData()).quant, Consts.QTY_SCALE));

		if(document instanceof OrderImpl) {
			int vsbl = View.GONE;

			OrderItemEx oie = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);

			priceCost = ((CostStrategyEx)CostStrategy.defaultInstance).getItemCost(price.getData(), document);
			actionCost = ((CostStrategyEx)CostStrategy.defaultInstance).getActionCost(price.getData(), document.getSumType());
			if(actionCost != 0) {
				vsbl = View.VISIBLE;
			}

			CheckBox cb = findViewById(R.id.cbAction);
			if(oie == null || oie.action != 0) {
				cb.setChecked(true);
				if(oie != null) {
					actionCost = oie.cost;
					priceCost = oie.prcCost;
				}
			} else {
				cb.setChecked(false);
			}

			((TextView)findViewById(R.id.tvActPrice)).setText(Util.IntToScaleStr(actionCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			findViewById(R.id.trActCostApply).setVisibility(vsbl);
			findViewById(R.id.trActCost).setVisibility(vsbl);

			onChangeCost(priceCost);
		}
	}

	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();
		long qty = getCountValue();
		if(cbPackets.isChecked()) {
			qty = qty * qtyInPack / Consts.QTY_SCALE;
		}
		long actSum = qty * actionCost / Consts.QTY_SCALE;

		((TextView)findViewById(R.id.tvActSum)).setText(Util.IntToScaleStr(actSum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}

	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		
		int qty = qtyItems;

		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
		int quant = ((PriceEx)price.getData()).quant;// * Consts.QTY_SCALE;
		if(quant != 0 && (qty % quant != 0)) {
			Toast.makeText(this, "Необходимо сделать заказ кратно " + Util.IntToScaleStr(quant, Consts.QTY_SCALE), Toast.LENGTH_SHORT).show();
			int val = qty / quant;

			edCount.setText(Util.IntToScaleStr((int) (quant * (val+1)), Consts.QTY_SCALE));
			edCount.selectAll();
			return false;
		}

		
		return result;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx) item;
		oie.action = ((CheckBox)findViewById(R.id.cbAction)).isChecked() ? 1 : 0;
		oie.prcCost = priceCost;
		oie.cost = oie.action > 0 && actionCost != 0 ? actionCost : priceCost;
	}
}





