package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.MatrixTree;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.Itemsable;

public class WarehouseEx extends Warehouse {
	final String MATRIX_NAME = "<Ассортимент>";
	
	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	
	ReturnImpl retDoc = null;
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		if( orgMatrix != null ) {
			items.add(1, MATRIX_NAME);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document != null && document instanceof OrderImpl)
			retDoc = ReturnImplEx.getAssociated((OrderImpl)document, false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if( retDoc != null )
			retDoc.read(retDoc.getRowid(), false);
	
		if( document != null && !matrixInited) {
			if((document instanceof ReturnImpl) || (document instanceof OrderImpl)) {
				OrgImpl oi = new OrgImpl();
				OrgEx oe = (OrgEx)oi.getData();
				oe.id = document.getId();
				oi.read();
				oi.close();
				
				if( oe.matrix.size() > 0 ) {
					orgMatrix = oe.matrix;
				}
			}			
			matrixInited = true;
			
			if(orgMatrix != null )
				applayMatrix(MATRIX_NAME);
		}
	}
	
	@Override
	protected int getDefaultColor(Price p) {
		if( retDoc != null && retDoc.findItem(p.id) != null)
			return Color.LTGRAY;
		return super.getDefaultColor(p);
	}
}
