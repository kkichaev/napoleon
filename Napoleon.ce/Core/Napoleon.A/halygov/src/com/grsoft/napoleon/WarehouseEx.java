package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Filter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	private static final String COST_FILTER = "cost_filter";
	private static final String COST_POSITION_FILTER_NAME = "CostPositionFilter";
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouse_ex;
	}
	
	@Override
	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menuex;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itFilters:
			showDialog(R.id.filter_dlg);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.filter_dlg:
			return createFilterDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Filter[] filters = new Filter[]{createZeroPositionFilter(), new CostPositionFilter()};
	
	private Dialog createFilterDlg() {
		AlertDialog.Builder result = new AlertDialog.Builder(this);
		result.setTitle(R.string.select_filter);
		CharSequence[] items = getResources().getTextArray(R.array.filter_items);
		result.setMultiChoiceItems(items, new boolean[items.length], null);
		result.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlertDialog ade = (AlertDialog)dialog;
				SparseBooleanArray array = ade.getListView().getCheckedItemPositions();
				Editor ed = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
				
				boolean zf = array.get(0);  
				ed.putBoolean(ZERO_FILTER, zf);
				if(zf) 
					adapter.putFilter(filters[0]); 
				else 
					adapter.deleteFilter(ZeroPositionFilter.NAME);
				
				zf = array.get(1);  
				ed.putBoolean(COST_FILTER, zf);
				if(zf) 
					adapter.putFilter(filters[1]); 
				else 
					adapter.deleteFilter(COST_POSITION_FILTER_NAME);
				
				ed.commit();
				
				adapter.buildSet();
			}
		});
		
		result.setNegativeButton(R.string.cancel,null);
		return result.create();
	}
		
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case R.id.filter_dlg:
			prepareFilterDlg(dialog);
		default:
		}
	}

	private void prepareFilterDlg(Dialog dialog) {
		AlertDialog adlg = (AlertDialog) dialog;
		
		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
		adlg.getListView().setItemChecked(0, pref.getBoolean(ZERO_FILTER, false));
		adlg.getListView().setItemChecked(1, pref.getBoolean(COST_FILTER, false));
	}
	
	class CostPositionFilter extends Filter{
		
		public CostPositionFilter() {
			super(COST_POSITION_FILTER_NAME);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean inset(long priceRowID, String id) {
			price.read(priceRowID);
			return CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getItemCost(price.getData(), document) > 0;
		}
	}
	
	@Override
	protected void postAdapterChange() {
		ivFilter.setVisibility(adapter.getFilter(ZeroPositionFilter.NAME) != null || adapter.getFilter(COST_POSITION_FILTER_NAME) != null ? 
				View.VISIBLE : View.GONE);
	}
	
	@Override
	protected void adapterInit() {
		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME,
				Context.MODE_PRIVATE);
		
		if(pref.getBoolean(COST_FILTER, false))
			adapter.putFilter(filters[1]);
		
		super.adapterInit();
	}
}




