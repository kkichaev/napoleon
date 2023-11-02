package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.FBTransferImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	private int skladIdx = 0;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		if(document.getRowid() == ExtrasConst.INVALID_ROWID ){
			menu.add(Menu.NONE, R.id.select_sklad_dlg, Menu.NONE, getString(R.string.select_sklad));
		}
		
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.select_sklad_dlg)
			return createSkadDialog();
		return super.onCreateDialog(id);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.select_sklad_dlg)
			showDialog(R.id.select_sklad_dlg);
		return super.onOptionsItemSelected(item);
	}
	
	
	private Dialog createSkadDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Склады");

		List<Sklad> sklads = DbReader.fetch(Sklad.class);
		if(sklads.size() > 0) {
			CharSequence[] csa = new CharSequence[sklads.size()];

			for(int i = 0; i <csa.length; i++)
				csa[i] = sklads.get(i).name;

			b.setSingleChoiceItems(csa, skladIdx, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					selectSkald(which);
					dialog.dismiss();
				}
			});
		}
		
		return b.create();
	}
	
	protected void selectSkald(int which) {
		skladIdx = which;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}

	@Override
	int getWhQty(Itemsable id, Price p) {
		if (skladIdx == 0)
			return super.getWhQty(id, p);
		else 
			return priceQty();
	}

	private int priceQty() {
		int result = 0;
		int idx = skladIdx - 1;
		PriceEx pe = (PriceEx)price.getData();
		
		if(idx >= 0 && idx < pe.whQty.size()) {
			PriceWhData whd = (PriceWhData) pe.whQty.get(idx);
			result = whd.qty;
		}
		
		return result;
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof FBTransferImpl) {
			skladIdx = ((FBTransferImpl)document).getSkladIdx();
		}
		return new ZeroPositionFilter() {
			@Override
			public String getWhereStr() {
				if(skladIdx != 0) {
					where = String.format("((whStates & ( 1 << %d )) != 0)", skladIdx - 1);
				}
				return super.getWhereStr();
			}
		};
	}

	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		super.setTextColumnValue(textView, type, price);

		if (type == COLUMN_QTY_WH && DocType.getCurDoc() == OrderDoc.instance()){
			Itemsable id = (Itemsable) document;
			int value = getWhQty(id, price);
			int res = ((OrderImplEx)document).getItemRes(price, skladIdx);

			if (res > 0) {
				String s = String.format("%s (%s)", Util.IntToScaleStr(value, Consts.QTY_SCALE), Util.IntToScaleStr(res, Consts.QTY_SCALE));
				textView.setText(s);
			}
		}
	}
}
