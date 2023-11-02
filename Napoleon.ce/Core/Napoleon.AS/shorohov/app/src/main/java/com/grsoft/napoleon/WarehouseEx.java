package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.MatrixTree;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.MatrixItemsAdapter;

public class WarehouseEx extends Warehouse {
	final String MATRIX_NAME = "<Матрица контрагента>";
	
	List<MatrixItem> orgMatrix = new ArrayList<>();

	@Override
	protected void readDocument() {
		super.readDocument();

		OrgImpl oi = new OrgImpl();
		oi.read("id", document.getId());
		orgMatrix = ((OrgEx)oi.getData()).matrix;
	}

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if( orgMatrix.size() > 0 ) {
			items.add(1, MATRIX_NAME);
		}
		return items;
	}

	@Override
	protected boolean inheritedApplayMatrix(String matrixName) {
		if(matrixName.equals(MATRIX_NAME)) {
			applayAdapter(new MatrixItemsAdapter(this, orgMatrix));
			return true;
		}
		return super.inheritedApplayMatrix(matrixName);
	}
}