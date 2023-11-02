package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseAdapter;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class WarehouseEx extends WarehouseNew implements OnClickListener {
	private static DocType lastDoc;
	private View btnNextPrice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnNextPrice = findViewById(R.id.ibNextPrice);
		
		if (usePromoMatrix()) {
			btnNextPrice.setOnClickListener(this);
			matrixName = getString(R.string.promoMatrixName);
		}else
			btnNextPrice.setVisibility(View.GONE);
		
	}
	
	private PromoMatrix promoMatrix = new PromoMatrix(this);
	
	protected void postAdapterInit() {
		if (lastDoc != null && lastDoc != DocType.getCurDoc())
			FoldersAdapter.resetCache();
		
		lastDoc = DocType.getCurDoc();
		
		if (usePromoMatrix())
			openPromoMatrix();
		else
			super.postAdapterInit();
	}

	private void openPromoMatrix() {
		FoldersAdapter.resetCache();
		applayAdapter(promoMatrix);
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if (usePromoMatrix())
			items.add(0, getString(R.string.promoMatrixName));
		
		return super.prepareMatrixList(items);
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ibNextPrice) {
			FoldersAdapter.resetCache();
			
			matrixName = PRICE_WITHOUT_MATRIX;
			applyAdapter((WarehouseAdapter) createListAdapter(), adapter.isExpanded());
		}
	}
	
	@Override
	protected boolean inheritedApplayMatrix(String matrixName) {
		boolean result = super.inheritedApplayMatrix(matrixName);
		
		if(usePromoMatrix() && matrixName.equals(getString(R.string.promoMatrixName))) {
			openPromoMatrix();
			result = true;
		}
		
		return result;
	}
	
	private boolean usePromoMatrix() {
		return DocType.getCurDoc() == OrderDoc.instance() && document.getRowid() != ExtrasConst.INVALID_ROWID && PromoMatrix.havePromo();
	}
	
	@Override
	protected void applyAdapter(WarehouseAdapter newadapter, boolean expanded) {
		super.applyAdapter(newadapter, expanded);
		
		if (btnNextPrice != null)
			btnNextPrice.setEnabled(!matrixName.equals(PRICE_WITHOUT_MATRIX));
	}
}


