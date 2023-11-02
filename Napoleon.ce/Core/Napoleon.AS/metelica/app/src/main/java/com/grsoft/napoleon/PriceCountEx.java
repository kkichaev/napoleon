package com.grsoft.napoleon;

import com.grsoft.dataobjects.MetelicaPrices;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PricesItem;
import com.grsoft.dataobjects.impl.MetelicaPricesImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	int minCost = 0;
	int mgrCost = 0;

	private boolean hasPriceItem = false;

	@Override
	protected void postOnCreate() {
		super.postOnCreate();

		OrgImpl org = new OrgImpl();
		org.read("id", document.getId());

		MetelicaPricesImpl mpi = new MetelicaPricesImpl();
		MetelicaPrices mp = mpi.getData();
		mp.price = ((OrgEx)org.getData()).price;
		mpi.read();

		for(PricesItem pi : mp.items) {
			if (pi.id.equals(price.getData().id)) {
				minCost = pi.minCost;
				mgrCost = pi.mgrCost;
				hasPriceItem = true;
			}
		}

		if (!hasPriceItem)
			minCost = ((PriceEx)price.getData()).minCost;
	}

	@Override
	protected boolean canChangeCost() {
		return minCost > 0;
	}

	@Override
	protected void doCostChange() {
		InputNumberDlg.open(this, new InputNumber() {
			@Override public long getValue() { return priceVal; }
			@Override public void applayInput(int value, Object... params) { 
				if(value < minCost) {
					Toast.makeText(PriceCountEx.this, "Цена меньше минимальной", Toast.LENGTH_LONG).show();
					return;
				}
				onChangeCost(value); 
			}
		}, Consts.SUM_SCALE, false, getString(R.string.cost), false, new InputNumberDlg.Decorator(){

			@Override public int getContentView() { return R.layout.inputnumberdlg; }

			@Override
			public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {
				TextView tv = (TextView) view.findViewById(R.id.tvDiscountInfo);
				String text = "Мин.цена: " + Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				tv.setText(text);
				tv.setVisibility(View.VISIBLE);
			}
		}); 
	}

	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		if( document instanceof OrderImpl) {
			OrderItemEx oi = (OrderItemEx) ((OrderImpl)document).findItem(price.getData().id);

			if( oi != null) {

				if (hasPriceItem)
					oi.mgrCost = mgrCost;
				else
					oi.mgrCost = ((PriceEx)price.getData()).mgrCost;

				document.write();
			}
		}

		return ret;
	}

	@Override
	protected boolean isInputValid(Runnable r) {
		int limit = ((PriceEx)price.getData()).limit;
		if(DocType.getCurDoc() == OrderDoc.instance() && limit > 0) {
			int qty = fixOrderQty(cbPackets.isChecked(), qtyItems, price.getData());
			if(qty < limit) {
				showDialog(R.id.limit_qty_dlg);
				return false;
			}
		}

		return super.isInputValid(r);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.limit_qty_dlg) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка");
			b.setMessage(getString(R.string.limit_message,
					Util.IntToScaleStr(((PriceEx)price.getData()).limit, Consts.QTY_SCALE)));
			return b.create();
		}
		return super.onCreateDialog(id);
	}
}
