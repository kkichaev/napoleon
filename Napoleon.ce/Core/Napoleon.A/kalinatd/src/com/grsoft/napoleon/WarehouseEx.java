package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class WarehouseEx extends WarehouseNew {
	private static final String COST_FILTER = "cost_filter";
	private static final String COST_POSITION_FILTER_NAME = "CostPositionFilter";
	private static final int SELECT_COST = 0x241;
	
	@Override protected int getOptionsMenuId() { return R.menu.wh_option_ex; }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		if( document instanceof OrderImpl /*&& document.getRowid() == ExtrasConst.INVALID_ROWID*/ ){
			menu.add(Menu.NONE, SELECT_COST, Menu.NONE, "“ип цен");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == SELECT_COST){
			showDialog(SELECT_COST);
			return true;
		} 
		if (item.getItemId() == R.id.itFilters){
			showDialog(R.id.filter_dlg);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.filter_dlg)
			return createFilterDlg();
		if(id == SELECT_COST)
			return createCostDialog();
		
		return super.onCreateDialog(id);
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
		if(id == R.id.filter_dlg)
			prepareFilterDlg(dialog);
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

	protected void selectCost(int which) {
		((OrderImpl)document).getData().sumType = which;
		FoldersAdapter.resetCache();
		adapter.buildSet();
	}
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		return new FoldersAdapter(this) {
			@Override
			protected void postUpdateView(View view, TreeNode node) {
				if (!node.isFolderNode()) {
					readPriceNode(node.getRowid());
					Price p = price.getData();
					
					int qty = getWhQty((Itemsable)document, p);
					
					if (qty <= 0) {
						view.setBackgroundColor(getResources().getColor(R.color.zero_position_color));
					}
				}
			}
		};
	}
	
	@Override
	protected void postAdapterChange() {
		ivFilter.setVisibility(adapter.getFilter(ZeroPositionFilter.NAME) != null || adapter.getFilter(COST_POSITION_FILTER_NAME) != null ? 
				View.VISIBLE : View.GONE);
	}
	
	@Override
	protected void adapterInit() {
		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
		if(pref.getBoolean(COST_FILTER, false))
			adapter.putFilter(filters[1]);

		super.adapterInit();
	}
}
