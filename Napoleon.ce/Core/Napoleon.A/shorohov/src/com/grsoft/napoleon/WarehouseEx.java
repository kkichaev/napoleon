package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.MatrixTree;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Itemsable;

public class WarehouseEx extends Warehouse {
	final String MATRIX_NAME = "<Матрица контрагента>";
	
	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if( orgMatrix != null ) {
			items.add(1, MATRIX_NAME);
			setCurrMatrixName(MATRIX_NAME);
		}
		return items;
	}
	
	@Override
	protected void applayZeroFilter(boolean goTop) {
		FolderTreeNode top = (FolderTreeNode) foldersTree.getTop();
		
		if (matrixName.equals(PRICE_WITHOUT_MATRIX))
			foldersTree =  createFoldersTree(zeroPozitionFiltered);
		else
			applayMatrix(matrixName);
		
		if (top != null && !goTop)
			setFolder(top.id);
		else
			setPriceForTopLevel();
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if( matrixName.equals(MATRIX_NAME)) {
			foldersTree = new MatrixTree(orgMatrix, "", zeroPozitionFiltered, (Itemsable)document);
			notifyDataSetChanged();
		} else
			super.applayMatrix(matrixName);
		
		this.matrixName = matrixName;
		setAsTopLevelGoUp();
	}

	@Override
	protected void onResume() {
		super.onResume();
				
		if( document != null && !matrixInited) {
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = document.getId();
			oi.read();
			oi.close();
			
			if( oe.matrix.size() > 0 ) {
				orgMatrix = oe.matrix;
			}
			matrixInited = true;
		}
		if( orgMatrix != null ) {
			applayMatrix(MATRIX_NAME);
		}
	}
	
	@Override protected ItemSelectAdapter createItemAdapter() { return new ItemsAdapterEx(); }
	
	class ItemsAdapterEx extends ItemSelectAdapter {
		@Override
		public void applyFilter(String value) {
			if( value.compareTo(MATRIX_NAME) == 0 && value.length() > 0 ) {
				foldersTree = new MatrixTree(orgMatrix, value, zeroPozitionFiltered, (Itemsable) document);				
				notifyDataSetChanged();
			} else
				super.applyFilter(value);
		}
	}
}