package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixItemsAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";
	public static final String CURRENT_MATRIX = "current_matrix";
	public static final String PREF_NAME = "warehouse_pref";
		
	OrgMatrixAdapter matrixAdapter = null;

	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		super.setTextColumnValue(textView, type, price);
		if(type == COLUMN_COST | type == COLUMN_COST_SUM) {
			textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
			textView.setTextColor(Color.BLUE);
		}
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( document != null ) {
			if( document.getRowid() == ExtrasConst.INVALID_ID )
				document.read(docRowId);
			
			OrgImpl oi = new OrgImpl();
			OrgEx org = (OrgEx) oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();
			
			List<MatrixItem> orgMatrix = null;

			OrgMatrixImpl mi = new OrgMatrixImpl();
			OrgMatrix mtx = mi.getData();
			mtx.id = org.id;
			if( mi.read() ) {
				orgMatrix = uniqueItems(mtx.items);
			} else {
				mtx.id = org.ido;
				if( mi.read() )
					orgMatrix = uniqueItems(mtx.items);
			}
			mi.close();
			
			if( orgMatrix != null ) {
				matrixAdapter = new OrgMatrixAdapter(this, orgMatrix, org.id);
				
//				matrixName = MATRIX_NAME;
//				SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//				sp.edit().putString(CURRENT_MATRIX, matrixName).commit();
//
				return matrixAdapter;
			}
		}
		return super.createListAdapter();
	}
	
	private List<MatrixItem> uniqueItems(List<MatrixItem> items) {
		HashSet<String> used = new HashSet<String>();
		List<MatrixItem> res = new ArrayList<MatrixItem>();
		for(MatrixItem item : items) {
			if( used.contains(item.id) == false) {
				used.add(item.id);
				res.add(item);
			}
		}
		return res;		
	}

	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixAdapter != null && matrixName.equals(MATRIX_NAME)) {
			applayAdapter(matrixAdapter);
		} else
			super.applayMatrix(matrixName);
//		
//		this.matrixName = matrixName;
//		SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//		sp.edit().putString(CURRENT_MATRIX, matrixName).commit();
	}
	
	@Override
	protected void resetMatrix() {
		if(matrixAdapter != null) {
			applayAdapter(matrixAdapter);
			matrixName = MATRIX_NAME;
		} else 
			super.resetMatrix();
	}

	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	
	public static void resetCache() { idStore = ""; }
	
	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
			if(idStore.equals(o.whCode) == false ) {
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
}

class OrgMatrixAdapter extends MatrixItemsAdapter {
	String id;
	public OrgMatrixAdapter(WarehouseNew warehouse, List<MatrixItem> items, String id) {
		super(warehouse, items);
		this.id = id;
	}
	
	@Override
	public String getName() {
		return super.getName() + id;
	}
}