package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

public class WarehouseEx extends WarehouseNew {
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
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		
		c.key = "Склады";
		if( ci.read() ) {
			final List<KeyValue> list = new ArrayList<KeyValue>();
			DialogHelper.makeListWithKey(c.value, list, null);
			if( list.size() > 0 ) {
				CharSequence[] csa = new CharSequence[list.size()];
				
				for(int i = 0; i <csa.length; i++)
					csa[i] = list.get(i).value;
				
				b.setSingleChoiceItems(csa, skladIdx, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {	
						selectSkald(which);
						dialog.dismiss();
					}
				});
			}
		}
		
		ci.close();
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
			PriceWhData whd = pe.whQty.get(idx);
			result = whd.qty;
		}
		
		return result;
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroPositionFilter() {
			@Override
			public boolean inset(long priceRowID, String id) {
				boolean result = price.read("id", id);
				
				if (result)
					result = getWhQty((Itemsable) document, price.getData()) > 0;
					
				return result;
			}
		};
	}
}
