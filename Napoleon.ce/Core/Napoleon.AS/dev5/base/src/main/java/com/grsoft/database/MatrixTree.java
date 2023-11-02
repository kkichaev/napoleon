package com.grsoft.database;
import com.grsoft.aceteam.R;

import java.util.List;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.documents.Itemsable;

public class MatrixTree extends FoldersTree {
	public MatrixTree(List<MatrixItem> items, String filter, 
			boolean zeroFilter, Itemsable document) {
		super((MatrixItem)null, document);
		addItems(items, filter, zeroFilter);
	}
	
//	@Override
//	public TreeNode getNextLeaf(TreeNode node, int dir) {
//		return top;
//	}
}
