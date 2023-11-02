package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.TypeOrgMatrixImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.MatrixBaseAdapter;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";

	TypeOrgMatrixImpl matrix = new TypeOrgMatrixImpl();
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		
		if( matrix != null  && matrix.getData().items.size() > 0) 
			items.add(pos++, MATRIX_NAME);
		
		return items;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME)) {
			applayAdapter(new OrgMatrixAdapter(this, matrix.getData().items));
		} else
			super.applayMatrix(matrixName);
		
		this.matrixName = matrixName;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		
	}
	
	@Override
	protected void adapterInit() {
		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
		if (pref.getBoolean(ZERO_FILTER, false))
			adapter.putFilter(createZeroPositionFilter());
		
		if (document != null && DocType.getCurDoc() != ReturnDoc.instance()) {
			OrgImpl oi = new OrgImpl();
			Org org = oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();

			OrgEx oe = (OrgEx) org;

			matrix.getData().id = oe.mid;
			
			if(matrix.read() && matrix.getData().items.size() > 0){
				applayAdapter(new OrgMatrixAdapter(this, matrix.getData().items));
				matrixName = MATRIX_NAME;
			}else
				adapter.buildSet();
		}else
			adapter.buildSet();
	}
}

class OrgMatrixAdapter extends MatrixBaseAdapter {
	List<MatrixItem> matrix;

	public OrgMatrixAdapter(WarehouseNew warehouse, List<MatrixItem> matrix) {
		super(warehouse);
		this.matrix = matrix;
	}

	public String getName() {
		return "OrgMatrixAdapter";
	}

	@Override
	protected List<MatrixItem> getMatrixItems() {
		return matrix;
	}
}
