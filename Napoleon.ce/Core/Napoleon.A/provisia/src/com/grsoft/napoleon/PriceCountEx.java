package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class PriceCountEx extends PriceCount {

	private static final int COST_BELOW = 100;

	protected int getContentViewId() { return R.layout.pricecountex; }

	private int dscCost = 0;
	int minCost;
	int curInputCost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	protected void refreshData() {
		super.refreshData();

		PriceEx p = (PriceEx)price.getData();
		
		int qp = (p.qtyInPack == 0) ? 1 : p.qtyInPack;

		TextView tv = (TextView) findViewById(R.id.tvPlace);
		tv.setText(Integer.toString(p.qty / qp));
		
		tv = (TextView) findViewById(R.id.tvPlaceRest);
		tv.setText(Integer.toString((p.qty % qp) / Consts.QTY_SCALE));

		tv = (TextView) findViewById(R.id.tvWeight);
		tv.setText(Util.IntToScaleStr(p.weight, Consts.WEIGHT_SCALE, Util.DEC_DELIM, false));
		
		minCost = p.cost.get(1).cost;
		if( document != null && document instanceof OrderImpl ) {
			OrderEx o = (OrderEx)document.getData();
			dscCost = ((o.params & OrderEx.ofNetCost) != 0) ? p.cost.get(2).cost : p.cost.get(0).cost;
		}
		tv = (TextView) findViewById(R.id.tvDiscountPrice);
		tv.setText(Util.IntToScaleStr(dscCost, Consts.SUM_SCALE));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == COST_BELOW ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подтверждение");
			b.setMessage("Цена ниже мимнимальной. Изменить цену?");
			b.setNegativeButton(R.string.no, null);
			b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PriceCountEx.super.onChangeCost(curInputCost);
					dialog.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		if( newCost > minCost )
			super.onChangeCost(newCost);
		else {
			curInputCost = newCost;
			showDialog(COST_BELOW);
		}
	}
	
	@Override
	protected boolean isComplexSalesHistory() {
		return (document instanceof OrderImplEx && !((CfgNplEx)ConfigManager.getConfig()).hideRestQTY);
	}
	

}
