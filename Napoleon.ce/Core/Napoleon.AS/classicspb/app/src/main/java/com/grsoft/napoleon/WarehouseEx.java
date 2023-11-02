package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.MatrixTree;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgBase;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.IMatrix;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.BarcodeDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.MatrixItemsAdapter;

public class WarehouseEx extends Warehouse {
	final String MATRIX_NAME = "<Матрица контрагента>";
	public static final String CURRENT_MATRIX = "current_matrix";
	public static final String PREF_NAME = "warehouse_pref";
		
	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	
	HashSet<String> focusedItems = new HashSet<String>();
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		int pos = 1;
		if( orgMatrix != null ) {
			items.add(pos++, MATRIX_NAME);
		}
		
		return items;
	}

	@Override
	public View  getPriceView(PriceTreeNode node, View convertView) {
		View view = super.getPriceView(node, convertView);

		if (DocType.getCurDoc() == BarcodeDoc.instance())
			view.findViewById(R.id.llQuant).setVisibility(View.GONE);

		return view;
	}

	@Override
	protected int getDefaultColor(Price p) {
		if( focusedItems.contains(p.id) )
			return Color.BLUE;
		return super.getDefaultColor(p);
	}
	
//	@Override
//	protected void applayZeroFilter(boolean goTop) {
//		FolderTreeNode top = (FolderTreeNode) foldersTree.getTop();
//
//		if (matrixName.equals(PRICE_WITHOUT_MATRIX))
//			foldersTree =  createFoldersTree(zeroPozitionFiltered);
//		else
//			applayMatrix(matrixName);
//
//		if (top != null && !goTop)
//			setFolder(top.id);
//		else
//			setPriceForTopLevel();
//	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME)) {
			applayAdapter(new MatrixItemsAdapter(this, orgMatrix, MATRIX_NAME));
		} else
			super.applayMatrix(matrixName);
	}

	@Override
	protected void readDocument() {
		super.readDocument();

		if( document != null && !matrixInited) {
			if( document instanceof IMatrix ) {
				OrgMatrix om = ((IMatrix)document).getMatrix();
				if( om != null ) {
					orgMatrix = new ArrayList<MatrixItem>();
					for(OrgMatrixItem oi : om.items) {
						MatrixItem mi = new MatrixItem();
						mi.id = oi.id;
						orgMatrix.add(mi);
					}
				}

				if(document instanceof OrderImplEx)
					focusedItems = ((OrderImplEx)document).getFocusedItems();
			} else if( document instanceof RemnantsImpl ) {
				OrgMatrixImpl matrix = new OrgMatrixImpl();
				OrgMatrix m = matrix.getData();

				OrgImpl oi = new OrgImpl();
				Org org = oi.getData();
				OrgBase ob = (OrgBase)oi.getData();
				org.id = document.getId();
				oi.read();
				oi.close();

				String matrixName = ob.getMatrix();
				if( matrixName != null && matrixName.length() > 0 ) {
					m.name = matrixName;
					if( matrix.read() ) {
						orgMatrix = new ArrayList<MatrixItem>();
						for(OrgMatrixItem omi : matrix.getData().items) {
							MatrixItem mi = new MatrixItem();
							mi.id = omi.id;
							orgMatrix.add(mi);
						}

					}

					matrix.close();
				}
			}
			matrixInited = true;
		}
	}

	@Override
	protected BaseAdapter createListAdapter() {
		if( document instanceof RemnantsImpl && orgMatrix != null) {
			return new MatrixItemsAdapter(this, orgMatrix, MATRIX_NAME);
		}
		return super.createListAdapter();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if( orgMatrix != null )
			menu.removeItem(R.id.itMatrix);

		if (DocType.getCurDoc() == BarcodeDoc.instance()) {
			for(int i = 0; i < menu.size(); i++)
				menu.getItem(i).setVisible(false);
		}

		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if( orgMatrix != null )
			menu.removeItem(R.id.itMoveToFolder);
	}

	@Override
	protected void updateTotalSum() {
		if (document instanceof OrderImplBase<?>)
			updateTotalSum(document.sum(), ((OrderImplBase<?>)document).weight(),
					((OrderImplBase<?>)document).count());
		else
			super.updateTotalSum();
	}
	
	@Override
	protected void resetMatrix() {
		SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		sp.edit().putString(CURRENT_MATRIX, matrixName).commit();
		super.resetMatrix();
	}
}