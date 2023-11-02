package com.grsoft.napoleon;

import java.util.List;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";
		
	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if( orgMatrix != null )
			menu.removeItem(R.id.itMatrix);
		return true;
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		ret.putFilter(new ZeroPositionFilter());
		return ret;
	}
	
	@Override
	protected void adapterInit(){}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if( orgMatrix != null )
			menu.removeItem(R.id.itMoveToFolder);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if( document != null && !matrixInited) {
			OrgImpl oi = new OrgImpl();
			Org org = (Org) oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();
			
			OrgEx oe = (OrgEx)org;
			
			if( oe.matrix.size() > 0 )
				orgMatrix = oe.matrix;
			
			matrixInited = true;
			
			if(orgMatrix != null )
				applayAdapter(new OrgMatrixAdapter(this, orgMatrix));
			else
				adapter.buildSet();
		}
	}
	
	@Override
	protected void updateTotalSum() {
		if (document instanceof OrderImplBase<?>)
			updateTotalSum(document.sum(), ((OrderImplBase<?>)document).weight(),
					((OrderImplBase<?>)document).count());
		else
			super.updateTotalSum();
	}
}

class OrgMatrixAdapter extends MatrixBaseAdapter {
	List<MatrixItem> matrix;
	public OrgMatrixAdapter(WarehouseNew warehouse, List<MatrixItem> matrix) {
		super(warehouse);
		this.matrix = matrix;
	}

	public String getName() { return "OrgMatrixAdapter"; }

	@Override
	protected List<MatrixItem> getMatrixItems() {
		return matrix;
	}
}