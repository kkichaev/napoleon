package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.CommonMatrixImpl;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMtxImpl;
import com.grsoft.util.MatrixBaseAdapter;

public class Warehouse2Ex extends WarehouseEx {
	final static String MATRIX_NAME = "<Матрица контрагента>";
	
	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if(type == COLUMN_COST)
			textView.setText("");
		else{
			if( type == COLUMN_QTY_WH || type == COLUMN_QTY_WH_ORD )
				type = COLUMN_QTY_ORD;
			super.setTextColumnValue(textView, type, price);
		}
	}
	
	@Override
	protected void adapterInit(){}

	@Override
	protected void onResume() {
		super.onResume();
		
		if( document != null && !matrixInited) {
			OrgMtxImpl orgMtx = new OrgMtxImpl();
			orgMtx.getData().id = document.getId();
			orgMtx.read();
			orgMtx.close();
			
			CommonMatrixImpl matrix = new CommonMatrixImpl();
			matrix.getData().name = orgMtx.getData().matrix;
			matrix.read();
			matrix.close();
			matrixInited = true;
			orgMatrix = matrix.getData().items;
			
			if(orgMatrix != null && orgMatrix.size() > 0)
				applayAdapter(new OrgMatrixAdapter(this, matrix.getData().items));
			else
				adapter.buildSet();
		}
	}
	
	@Override
	protected void resetMatrix() {
		matrixName = MATRIX_NAME;
		applayAdapter(new OrgMatrixAdapter(this, orgMatrix));
	}

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null && orgMatrix.size() > 0) {
			items.add(pos++, MATRIX_NAME);
		}
		
		items.remove(PRICE_WITHOUT_MATRIX);
		
		return items;
	}
}

class OrgMatrixAdapter extends MatrixBaseAdapter {
	List<MatrixItem> matrix;
	public OrgMatrixAdapter(WarehouseNew warehouse, List<MatrixItem> matrix) {
		super(warehouse);
		this.matrix = matrix;
	}

	public String getName() { return Warehouse2Ex.MATRIX_NAME; }

	@Override
	protected List<MatrixItem> getMatrixItems() {
		return matrix;
	}
}
