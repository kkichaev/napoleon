package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	private static final int ERROR_ORDER = 1234;
	String errMessage= "";
	Runnable runOk;
	
	int minCost;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void refreshData() {
		super.refreshData();
		minCost = ((PriceEx)price.getData()).minCost;
		
		TextView tv = (TextView)findViewById(R.id.tvMinCost);
		tv.setText(Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		if( newCost < minCost ) {
			Toast.makeText(this, R.string.cost_below_min, Toast.LENGTH_SHORT).show();
			return;
		}

		super.onChangeCost(newCost);
	}
	
	@Override
	protected boolean canChangeCost() {
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == ERROR_ORDER ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка в заявке");
			b.setMessage("");
			b.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { runOk.run(); }
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == ERROR_ORDER ) {
			((AlertDialog)dialog).setMessage(errMessage);
		}
		super.onPrepareDialog(id, dialog);
	}
}
