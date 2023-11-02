package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.CreateOrder.CostTypeEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.modules.CostManager;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	private static final String COST_FILTER = "cost_filter";
	private static final String COST_POSITION_FILTER_NAME = "CostPositionFilter";

	private HashSet<String> actionItems = new HashSet<String>();
	private ArrayList<CostTypeEx> costTypes = new ArrayList<CostTypeEx>();
	private Filter[] filters = new Filter[]{createZeroPositionFilter(), new CostPositionFilter()};
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document instanceof OrderImplEx ) {
			actionItems.clear();
			BonusDefImpl.loadBonus(document.getDate(), new BonusDefImpl.BonusAction() {

				@Override
				public boolean doAction(BonusDef item) {
					actionItems.add(item.iditem);
					return true;
				}
			});
		}
		
		if(DocType.getCurDoc() == OrderDoc.instance()){
			CostManager.CostType[] ctypes = Features.COST_MANAGER.getCostTypes();
			if( ctypes != null ) {
				int index = 0;
				for( CostManager.CostType ct : ctypes ) {
					CostTypeEx ctx = new CostTypeEx(index++, ct);
					costTypes.add(ctx);
				}
			}
	
			Collections.sort(costTypes, new Comparator<CostTypeEx>() {
				@Override public int compare(CostTypeEx object1, CostTypeEx object2) { return object1.name.compareTo(object2.name); }
			});
		}
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);

		TextView tv = (TextView)v.findViewById(R.id.tvPriceItemName);
		tv.setCompoundDrawablesWithIntrinsicBounds(actionItems.contains(price.getData().id) ? R.drawable.bonus : 0, 0, 0, 0);

		return v;
	}
	
	@Override
	protected int getOptionsMenuId() { return R.menu.warehouse_opt_menuex; }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itFilters:
			showDialog(R.id.filter_dlg);
			return true;
		case R.id.itPriceType:
			showDialog(R.id.pricetypes_dlg);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.filter_dlg:
			return createFilterDlg();
		case R.id.pricetypes_dlg:
			return createPriceTypeDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createPriceTypeDlg() {
		AlertDialog.Builder result = new AlertDialog.Builder(this);
		result.setTitle(R.string.select_price_type);
		
		CharSequence[] item = new CharSequence[costTypes.size()];
		
		for(int i = 0; i < costTypes.size(); i++)
			item[i] = costTypes.get(i).name;
		
		result.setSingleChoiceItems(item, -1,  new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CostTypeEx ct = costTypes.get(which);
				
				if(ct != null){
					dialog.dismiss();
					OrderEx order = ((OrderEx)document.getData()); 
					order.sumType = ct.costIndex;
					order.costType = ct.id;
					document.write();
					adapter.buildSet();
				}
			}
		});
		
		return result.create();
	}

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
				
				FoldersAdapter.resetCache();
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
			break;
		case R.id.pricetypes_dlg:
			preparePriceTypesDlg(dialog);
			break;
		default:
		}
	}

	private void preparePriceTypesDlg(Dialog dialog) {
		AlertDialog adlg = (AlertDialog) dialog;
		final int NOT_SELECTED = -1;
		
		int sel = NOT_SELECTED;
		for(int i = 0; i < costTypes.size(); i++)
			if(costTypes.get(i).costIndex == document.getSumType()){
				sel = i;
				break;
			}
		
		if(sel != NOT_SELECTED)
			adlg.getListView().setItemChecked(sel, true);
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
		
		FoldersAdapter.resetCache();
		super.adapterInit();
	}

}
