package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgRemnants;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.Pair;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";

	List<MatrixItem> orgMatrix = null;
	HashMap<String, Pair<String, Date>> rmnVal = new HashMap<String, Pair<String,Date>>();
	private boolean matrixInited = false;

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		ret.putFilter(new ZeroPositionFilter());
		return ret;
	}

	@Override
	protected void adapterInit() {
		if (document != null && !matrixInited) {
			OrgImpl oi = new OrgImpl();
			Org org = oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();

			OrgEx oe = (OrgEx) org;

			if(oe.remnants != null)
				for(OrgRemnants o : oe.remnants){
					if(orgMatrix == null)
						orgMatrix = new ArrayList<MatrixItem>();
					MatrixItem mi = new MatrixItem();
					mi.id = o.id;
					
					if(!rmnVal.containsKey(mi.id))
						rmnVal.put(mi.id, Pair.create(o.qty, o.date));
					
					orgMatrix.add(mi);
				}

			matrixInited = true;

//			if (orgMatrix != null){
//				applayAdapter(new OrgMatrixAdapter(this, orgMatrix));
//				matrixName = MATRIX_NAME;
//			}else
				adapter.buildSet();
		}
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME)) {
			applayAdapter(new OrgMatrixAdapter(this, orgMatrix));
		} else
			super.applayMatrix(matrixName);
		
		this.matrixName = matrixName;
	}
	

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null && orgMatrix.size() > 0) {
			items.add(pos++, MATRIX_NAME);
		}
		
		return items;
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
