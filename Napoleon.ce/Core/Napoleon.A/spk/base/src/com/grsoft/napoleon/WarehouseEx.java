package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixItemsAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";
	final String FOCUS_MATRIX_NAME = "<Фокусный товар>";
	public static final String CURRENT_MATRIX = "current_matrix";
	public static final String PREF_NAME = "warehouse_pref";
		
	List<MatrixItem> orgMatrix = null;
	List<MatrixItem> focusMatrix = null;
	private boolean matrixInited = false;
	private boolean canChangeMatrix = true;
	
	static String orgId = "";
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null ) {
			items.add(pos++, MATRIX_NAME);
			//setCurrMatrixName(MATRIX_NAME);
		}
		
		if( focusMatrix != null ) {
			items.add(pos, FOCUS_MATRIX_NAME);
		}
		return items;
	}

	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME)) {
			applayAdapter(new MatrixItemsAdapter(this, orgMatrix));
		} else if( matrixName.equals(FOCUS_MATRIX_NAME)) {
			applayAdapter(new MatrixItemsAdapter(this, focusMatrix));
		} else
			super.applayMatrix(matrixName);
		
		this.matrixName = matrixName;
		SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		sp.edit().putString(CURRENT_MATRIX, matrixName).commit();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StringBuilder val = new StringBuilder();
		ConfigImpl config = new ConfigImpl();
		
		if(config.getValue(val, "РаботаПоМатрице"))
			canChangeMatrix = val.toString().equals("0");
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		
		if(result){
			if(!canChangeMatrix && 
					DocType.getCurDoc() == OrderDoc.instance())
				menu.findItem(R.id.itMatrix).setVisible(false);
		}
		
		return result;
	}
	
	@Override
	protected void adapterInit() {
		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME,
				Context.MODE_PRIVATE);
		if (pref.getBoolean(ZERO_FILTER, false)
				&& DocType.getCurDoc() != ReturnDoc.instance())
			adapter.putFilter(createZeroPositionFilter());

		String curId = document == null ? "" : document.getId();
		if(orgId.equals(curId) == false) {
			orgId = curId;
			FoldersAdapter.resetCache();
		}
		if( document != null && !matrixInited) {
			OrgImpl oi = new OrgImpl();
			Org org = oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();
			
			OrgEx oe = (OrgEx)org;
			
			SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
			String matrixName = sp.getString(CURRENT_MATRIX, "");
			
			if( oe.matrix.size() > 0 ){
				orgMatrix = oe.matrix;
				
				if(matrixName.trim().length() == 0)
					matrixName = MATRIX_NAME;
			}
			
			if( oe.focusedItems.size() > 0 )
				focusMatrix = oe.focusedItems;
			
			matrixInited = true;

			if (matrixName.equals(PRICE_WITHOUT_MATRIX) || matrixName.trim().length() == 0){
				matrixName = PRICE_WITHOUT_MATRIX;
				super.resetMatrix();
			}else if(orgMatrix != null )
				applayMatrix(matrixName.length() == 0 ? MATRIX_NAME : matrixName);
		}else
			adapter.buildSet();
	}
	
	@Override
	protected void updateTotalSum() {
		if (document instanceof OrderImplBase<?>)
			updateTotalSum(document.sum(), ((OrderImplBase<?>)document).weight(),
					((OrderImplBase<?>)document).count());
		else
			super.updateTotalSum();
	}
	
	@Override
	protected void resetMatrix() {
		SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		sp.edit().putString(CURRENT_MATRIX, matrixName).commit();
		super.resetMatrix();
	}
}