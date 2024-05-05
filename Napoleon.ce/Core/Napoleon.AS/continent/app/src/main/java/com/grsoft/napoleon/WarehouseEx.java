package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.ViewUtil;
import com.grsoft.util.view.dialog_helper.DialogHelper;


public class WarehouseEx extends Warehouse {
	public static int costype = -1;
	private static final String COST_FILTER = "cost_filter";
	private static final String COST_POSITION_FILTER_NAME = "CostPositionFilter";

	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		costype = document.getSumType();
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;
		if(item.getItemId() == R.id.itFilters)
			showDialog(R.id.filter_dlg);
		else if(item.getItemId() == R.id.itCostype){
			showDialog(R.id.costype_dlg);
			result = true;
		}else
			result = super.onOptionsItemSelected(item);
		
		return result;
	}
	
	final int PERIOD_FOR_DELIVERY = 3;
	final int PERIOD_FOR_ORDER = 1;
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = null;
		
		if( DocType.getCurDoc() == ReturnDoc.instance())
			ret = createAssortementMatrixAdapter();
		else 
			ret = (FoldersAdapter) super.createListAdapter();
		
		return ret;
	}

	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (DocType.getCurDoc() == ReturnDoc.instance()) {
			AssortmentMatrixAdapter.MATRIX_DOC = DeliveryDoc.instance();
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_DELIVERY;
		}else {
			AssortmentMatrixAdapter.MATRIX_DOC = OrderDoc.instance();
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_ORDER;
		}
		
		return super.createAssortementMatrixAdapter();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
	    if (id == R.id.filter_dlg)
	    	return createFilterDlg();
	    else if (id == R.id.costype_dlg)
			return createCostypeDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createCostypeDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<CharSequence> values = new ArrayList<CharSequence>();
		DialogHelper.makeList(ConfigHelper.getCostType(), values);
		builder.setSingleChoiceItems(values.toArray(new CharSequence[values.size()]), costype, onCosTypeClick());
		builder.setTitle(R.string.costype);
		return builder.create();
	}
	
	private DialogInterface.OnClickListener onCosTypeClick() {
		return new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				costype = which;
				FoldersAdapter.resetCache();
				adapter.buildSet();
				dismissDialog(R.id.costype_dlg);
			}
		};
	}

	@Override
	protected int getOptionsMenuId() { return R.menu.warehouse_opt_menuex; }
	
	@SuppressWarnings("unchecked")
	protected long getCost(Price price) {
		return CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getCostInt(price, (Document<?>) document, costype);
	}

	class ZeroQtyFilter extends ZeroPositionFilter {
		@Override
		public String getWhereStr() {
			if(document instanceof OrderImplEx) {
				if(((OrderEx)document.getData()).isVan())
					return "vanQty > 0";
			}
			return super.getWhereStr();
		}
	}

	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroQtyFilter();
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
			return CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getCostInt(price.getData(), document, costype) > 0;
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
	
	@Override
	protected void updateChildPriceView(View view, Price p) {
		super.updateChildPriceView(view, p);
		
		if (DocType.getCurDoc() == WSOrderDoc.instance()) {
			WSOrderImpl wso = (WSOrderImpl) document;
			TextView tvClmn1 = (TextView) view.findViewById(R.id.tvClmn1);
			TextView tvClmn2 = (TextView) view.findViewById(R.id.tvClmn2);
			
			tvClmn1.setText(Util.IntToScaleStr(wso.getItemValue(p), Consts.QTY_SCALE));
			
			StringBuilder sb = new StringBuilder();
			sb.append(Util.IntToScaleStr(wso.getItemCentrValue(p), Consts.QTY_SCALE));
			sb.append("<br>");
			sb.append(Util.IntToScaleStr(getCost(p), Consts.SUM_SCALE));
			
			tvClmn2.setText(Html.fromHtml(sb.toString()));
		}
	}
}
