package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MatrixItemsAdapter;

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
	
	private List<MatrixItem> uniqueItems(List<OrgMatrixItem> items) {
		HashSet<String> used = new HashSet<String>();
		List<MatrixItem> res = new ArrayList<MatrixItem>();
		for(OrgMatrixItem item : items) {
			if( used.contains(item.id) == false) {
				used.add(item.id);
				MatrixItem add = new MatrixItem();
				add.id = item.id;
				res.add(add);
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