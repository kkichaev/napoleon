package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	private static final String COST_FILTER = "cost_filter";
	private static final String COST_POSITION_FILTER_NAME = "CostPositionFilter";
	
	static long lastOrder = ExtrasConst.INVALID_ROWID;
	static int curMatrix = 0;
	MatrixOrder matrixOrder = null;
	boolean hideMatrix = false;
	
	private String LAST_DOC_TYPE = "last_doc_type"; 
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouse_ex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.ibNextPrice).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				curMatrix++;
				resetMatrix();
			}
		});
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		String lastDocType = getPreferences(Context.MODE_PRIVATE).getString(LAST_DOC_TYPE, "");
		String curDocName = DocType.getCurDoc().getName();
		
		if(!curDocName.equals(lastDocType)){
			Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
			ed.putString(LAST_DOC_TYPE, curDocName);
			ed.commit();
			
			FoldersAdapter.resetCache();
		}
		
		hideMatrix = false;
		if( DocType.getCurDoc() == ReturnDoc.instance() ) {
			hideMatrix = true;
			return new ReturnAdapter(this);
		}
		
		if( DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID ) {
			if( lastOrder != document.getRowid() ) {
				lastOrder = document.getRowid();
				curMatrix = 0;
			}
			
			if( matrixOrder == null ) {
				matrixOrder = new MatrixOrder();
				String table = DataObjectInfo.getInstance().getTableName(matrixOrder.getClass());
				DbReader r = new DbReader();
				r.select(matrixOrder, table, null);
				r.close();
			}
			
			if( curMatrix < matrixOrder.items.size() ) {
				hideMatrix = true;
				findViewById(R.id.llMatrixOrder).setVisibility(View.VISIBLE);
				String name = matrixOrder.items.get(curMatrix).name;
				TextView tv = (TextView)findViewById(R.id.tvMatrixName);
				tv.setText(name);
				return new MatrixAdapter(this, name);
			}
		}
		findViewById(R.id.llMatrixOrder).setVisibility(View.GONE);
		return super.createListAdapter();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean ret = super.onPrepareOptionsMenu(menu);
		MenuItem mi = menu.findItem(R.id.itMatrix);
		if( mi != null)
			mi.setVisible(!hideMatrix);
		return ret;
	}
	
	class ReturnAdapter extends FoldersAdapter {

		public ReturnAdapter(WarehouseNew warehouse) {
			super(warehouse);

			FoldersAdapter.resetCache();
		}
		
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			try {
				PriceImpl pi = new PriceImpl();
				Price p = pi.getData();
				
				fprice.clear();

				HashSet<String> items = new HashSet<String>();
				DocList dl = DeliveryDoc.instance().docList(document.getId());
				for(Document<?> d : dl) {
					Delivery dlv = ((DeliveryImpl)d).getData();
					for(DeliveryItem di : dlv.items) {
						if( items.contains(di.id))
							continue;
						
						p.id = di.id;
						if( pi.read() == false )
							continue;
						
						if(!fprice.containsKey(p.folderID))
							fprice.put(p.folderID, new ArrayList<PriceInfo>());
						
						PriceInfo pri = new PriceInfo(pi.getRowid(), p.name, p.id);
						fprice.get(p.folderID).add(pri);
					}
				}
				dl.close();
				pi.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menuex;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itFilters){
			showDialog(R.id.filter_dlg);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.filter_dlg)
			return createFilterDlg();
		else	
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




