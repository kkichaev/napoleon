package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.MatrixBaseAdapter;

import android.widget.BaseAdapter;

public class WarehouseEx extends WarehouseNew {
	public static final String ORG_MATRIX = "<Матрица контрагента>";
	
	List<MatrixItem> orgMatrix = null;
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(ORG_MATRIX);
		return items;
	}
	
	@Override
	protected void postDocInited() {
		if(document == null) {
			orgMatrix = new ArrayList<MatrixItem>();
		} else {
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx) oi.getData();
			oe.id = document.getId();
			oi.read();
			oi.close();
			orgMatrix = oe.matrix;
		}
	} 
	@Override
	protected boolean inheritedApplayMatrix(String matrixName) {
		if(matrixName.equals(ORG_MATRIX)) {
			applayAdapter(new OrgMatrix(this, orgMatrix));
			return true;
		}
		return super.inheritedApplayMatrix(matrixName);
	}
		
	@Override
	protected BaseAdapter createListAdapter() {
		if(!editMode && orgMatrix != null && orgMatrix.size() > 0)
			return new OrgMatrix(this, orgMatrix);
		return super.createListAdapter();
	}
	
	class OrgMatrix extends MatrixBaseAdapter {
		
		List<MatrixItem> items;
		
		public OrgMatrix(WarehouseNewW warehouse, List<MatrixItem> items) {
			super(warehouse);
			this.items = items;
		}
		
		@Override protected List<? extends MatrixItem> getMatrixItems() { return items; }
		
	}
}
