package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order2Ex;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl2Ex;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
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
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Warehouse2Ex extends WarehouseEx {
	private TextView tvOrgOrdSum;
	private int skladIdx = 0;
	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	
	public static void resetCache() { idStore = ""; }
	
	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImpl2Ex) {
			Order2Ex o = (Order2Ex) document.getData();
			if(idStore == null || idStore.equals(o.whCode) == false ) {
				FoldersAdapter.resetCache();
				idStore = o.whCode;
			}
			return new ZeroFilter();
		}
		return super.createZeroPositionFilter();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false; 
			
			Price p = pi.getData();
			p.id = id;
			pi.read();
			result = (((OrderImpl2Ex)document).getItemValue(p) > 0);			
			return result;
		}
	}
	
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
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.select_sklad_dlg)
			showDialog(R.id.select_sklad_dlg);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.select_sklad_dlg)
			return createSkadDialog();
		return super.onCreateDialog(id);
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
		((Order2Ex)document.getData()).whIndex = skladIdx;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}

	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		super.setTextColumnValue(textView, type, price);
		
		if (textView.getId() == R.id.tvClmn2 && DocType.getCurDoc() == OrderDoc.instance()) {
			long ren = ((OrderImpl2Ex)document).getRentability(price.id);
			
			if (ren != 0) {
				String text = textView.getText().toString();
				text += "<br>" + Util.IntToScaleStr(ren, Consts.SUM_SCALE);
				textView.setText(Html.fromHtml(text));
			}
		}
	}
}
