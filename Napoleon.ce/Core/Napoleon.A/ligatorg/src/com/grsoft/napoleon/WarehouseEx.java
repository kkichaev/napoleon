package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.Util;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";

	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;

	@Override
	protected void adapterInit() {
		if (document != null && !matrixInited) {
			OrgImpl oi = new OrgImpl();
			Org org = oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();

			OrgEx oe = (OrgEx) org;

			if (oe.matrix.size() > 0)
				orgMatrix = oe.matrix;

			matrixInited = true;

			if (orgMatrix != null){
				applyAdapter(new OrgMatrixAdapter(this, orgMatrix), true);
				matrixName = MATRIX_NAME;
			}else{
				if(folderID != -1)
					adapter.buildSet(folderID);
				else
					adapter.buildSet();
			}
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
	protected void updateTotalSum() {
		if (document instanceof OrderImplBase<?>)
			updateTotalSum(document.sum(),
					((OrderImplBase<?>) document).weight(),
					((OrderImplBase<?>) document).count());
		else
			super.updateTotalSum();
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null && orgMatrix.size() > 0) {
			items.add(pos++, MATRIX_NAME);
		}
		
		return items;
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}
	
	@Override
	protected void updateChildPriceView(View view, Price p) {
		TextView tv = (TextView) view.findViewById(R.id.tvRezQty);
		tv.setText(Util.IntToScaleStr(((PriceEx)p).rezQty, Consts.QTY_SCALE));
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
