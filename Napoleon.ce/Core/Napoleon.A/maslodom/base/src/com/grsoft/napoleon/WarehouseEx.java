package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class WarehouseEx extends WarehouseNew {
	private static final int SELECT_COST = 0x240;
	private static final int SELECT_SKLAD = 0x241;
	
	PriceImpl price = new PriceImpl();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		if( document instanceof OrderImplEx && document.getRowid() == ExtrasConst.INVALID_ROWID ){
			menu.add(Menu.NONE, SELECT_COST, Menu.NONE, "“ип цен");
			menu.add(Menu.NONE, SELECT_SKLAD, Menu.NONE, "—клад");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == SELECT_COST){
			showDialog(SELECT_COST);
			return true;
		} else if(item.getItemId() == SELECT_SKLAD){
			showDialog(SELECT_SKLAD);
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == SELECT_COST)
			return createCostDialog();
		else if(id == SELECT_SKLAD)
			return createSkadDialog();
		
		return super.onCreateDialog(id);
	}
	

	private Dialog createCostDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("÷ены");
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		
		c.key = "¬ид÷ены";
		if( ci.read() ) {
			List<CharSequence> list = new ArrayList<CharSequence>();
			DialogHelper.makeList(c.value, list);
			if( list.size() > 0 ) {
				CharSequence[] csa = new CharSequence[list.size()];
				list.toArray(csa);
				
				b.setItems(csa, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { selectCost(which); }
				});
			}
		}
		
		ci.close();
		return b.create();
	}
	
	
	private Dialog createSkadDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("—клады");
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		
		c.key = "—клады";
		if( ci.read() ) {
			final List<KeyValue> list = new ArrayList<KeyValue>();
			DialogHelper.makeListWithKey(c.value, list, null);
			if( list.size() > 0 ) {
				CharSequence[] csa = new CharSequence[list.size()];
				
				for(int i = 0; i <csa.length; i++)
					csa[i] = list.get(i).value;
				
				b.setItems(csa, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {	selectSkald(which); }
				});
			}
		}
		
		ci.close();
		return b.create();
	}
	

	protected void selectSkald(int which) {
		((OrderEx)document.getData()).whIndex = which;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}

	protected void selectCost(int which) {
		((OrderImplEx)document).getData().sumType = which;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		return super.createListAdapter();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		price.close();
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilter();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 
			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
}
