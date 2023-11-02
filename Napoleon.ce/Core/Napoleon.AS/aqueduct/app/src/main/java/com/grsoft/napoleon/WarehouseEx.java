package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Manufactor;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.util.Filter;
import com.grsoft.util.MatrixBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WarehouseEx extends Warehouse {

	final String MATRIX_NAME = "<Матрица контрагента>";
	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	static final int DLG_MFR = 0x324;

	List<String> mfrs = new ArrayList<>();
	List<String> mfrFilter = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linesController.setMinLines(3);
		findViewById(R.id.btnMfrFilter).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DLG_MFR);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == DLG_MFR) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Выберите производителей");

			final List<String> names = new ArrayList<>();
			DataTraveler.travel(Manufactor.class, new DataTraveler.Travel<Manufactor>() {
				@Override
				public boolean travel(DataTraveler<Manufactor> item) {
					mfrs.add(item.data.id);
					names.add(item.data.name);
					return true;
				}
			}, "id in (select distinct manufactor from " + new PriceEx().getTableName() + ")");

			String[] choices = names.toArray(new String[] {});
			boolean[] checked = new boolean[choices.length];
			for(String s : mfrFilter) {
				checked[mfrs.indexOf(s)] = true;
			}
			b.setMultiChoiceItems(choices, checked, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					String code = mfrs.get(which);
					if(isChecked) mfrFilter.add(code);
					else  mfrFilter.remove(code);
				}
			});
			b.setPositiveButton("Применить", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					adapter.deleteFilter(MfrFilter.NAME);
					if(mfrFilter.size() > 0) {
						adapter.putFilter(new MfrFilter(mfrFilter));
					}
					adapter.buildSet();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}

	@Override protected int getLayoutId() { return R.layout.warehouseex; }

	@Override
	protected String getItemName(Price p) {
		String name = super.getItemName(p);
		name += "\n" + ((PriceEx)p).manufactor;
		return name;
	}

	@Override
	protected void adapterInit() {
		if (((document instanceof RemnantsImpl) || (document instanceof OrderImpl)) && !matrixInited) {
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
	
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		super.onPrepareOptionsMenu(menu);
//
//		if(orgMatrix != null && orgMatrix.size() > 0){
//			MenuItem i = menu.findItem(R.id.itMatrix);
//
//			if(i != null)
//				i.setVisible(false);
//		}
//
//		return true;
//	}

	class MfrFilter extends Filter {
		public static final String NAME = "MfrFilter";
		public String sql;

		public MfrFilter(List<String> mfrs) {
			super(NAME);

			sql = "";
			for(String s : mfrs) {
				sql +="'" + s + "',";
			}
			if(sql.length() > 0) {
				sql = "manufactor in (" + sql.substring(0, sql.length() - 1) + ")";
			}
		}

		@Override public String getWhereStr() { return sql; }
	}
}


class OrgMatrixAdapter extends MatrixBaseAdapter {
	List<MatrixItem> matrix;

	public OrgMatrixAdapter(Warehouse warehouse, List<MatrixItem> matrix) {
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
