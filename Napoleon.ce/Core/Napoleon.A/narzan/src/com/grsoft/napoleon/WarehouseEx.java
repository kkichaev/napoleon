package com.grsoft.napoleon;

import java.util.List;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	final String MATRIX_NAME = "<Матрица контрагента>";

	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (orgMatrix != null) {
			menu.removeItem(R.id.itMatrix);
			menu.removeItem(R.id.itColumns);
		}

		menu.removeItem(R.id.itPresentation);
		menu.removeItem(R.id.itZeroFilter);
		return true;
	}

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		ret.putFilter(new ZeroPositionFilter());
		return ret;
	}

	@Override
	protected void adapterInit() {
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (orgMatrix != null)
			menu.removeItem(R.id.itMoveToFolder);
	}

	@Override
	protected void onResume() {
		super.onResume();

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

			if (orgMatrix != null)
				applayAdapter(new OrgMatrixAdapter(this, orgMatrix));
			else
				adapter.buildSet();
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.itMatrix).setVisible(false);
		return result;
	}

	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		int value = 0;
		int scale = Consts.QTY_SCALE;
		RemnantItemEx item = (RemnantItemEx) ((RemnantsImpl) document).findItem(price.id);

		if (item != null) 
			switch (type) {
			case COLUMN_QTY_WH:
				value = item.face;
				break;

			case COLUMN_COST:
				value = item.qty;
				break;
				
			default:
				break;
			}

		textView.setText(Util.IntToScaleStr(value, scale, Util.DEC_DELIM,
				(scale == Consts.QTY_SCALE)));
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