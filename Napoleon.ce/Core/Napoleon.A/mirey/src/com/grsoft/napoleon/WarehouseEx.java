package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	private TextView tvOrgOrdSum;
	private int skladIdx = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvOrgOrdSum = (TextView) findViewById(R.id.tvOrgOrdSum);
		
		OrgImpl org = new OrgImpl();
		org.read("id", document.getId());
		tvOrgOrdSum.setText(
				getString(R.string.rec_ord_sum,
				Util.IntToScaleStr(((OrgEx)org.getData()).ordsum, Consts.SUM_SCALE)));
	}
	
	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	
	public static void resetCache() { idStore = ""; }
	
	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
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
			result = (((OrderImplEx)document).getItemValue(p) > 0);			
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
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
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
		((OrderEx)document.getData()).whIndex = skladIdx;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}

}
