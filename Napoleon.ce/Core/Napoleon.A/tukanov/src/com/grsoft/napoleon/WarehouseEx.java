package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MatrixItemsAdapter;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";
	List<MatrixItem> orgMatrix = null;
	boolean inited = false;
	
	@Override
	protected BaseAdapter createListAdapter() {
		if( !inited && document != null ) {
			inited = true;
			
			if( document.getRowid() == ExtrasConst.INVALID_ID )
				document.read(docRowId);
			
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx) oi.getData();
			oe.id = document.getId();
			oi.read();
			oi.close();
			
			if( oe.matrix.size() > 0 )
				orgMatrix = oe.matrix;
			
			if ( orgMatrix != null && orgMatrix.size() > 0 ) {
				this.matrixName = MATRIX_NAME;
				return new OrgMatrix(this, orgMatrix, oe.id);
			}
		}
		return super.createListAdapter();
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null ) {
			items.add(pos++, MATRIX_NAME);
		}
		
		return items;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME)) {
			applayAdapter(new OrgMatrix(this, orgMatrix, document == null ? "" : document.getId()));
		} else
			super.applayMatrix(matrixName);
		
		this.matrixName = matrixName;
	}
	
	class OrgMatrix extends MatrixItemsAdapter {
		String id;
		public OrgMatrix(WarehouseNew warehouse, List<MatrixItem> items, String id) {
			super(warehouse, items);
			this.id = id;
		}
		
		@Override
		public String getName() {
			return super.getName() + id;
		}
	}
}
