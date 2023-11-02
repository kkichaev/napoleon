package com.grsoft.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.Warehouse;

public class MatrixAdapter extends MatrixBaseAdapter {
	private String name;
	protected MatrixImpl matrix;
	
	public MatrixAdapter(Warehouse warehouse, String matrix) {
		super(warehouse);
		this.name = matrix;
		this.matrix = new MatrixImpl();
		
		if(Features.USE_MATRIX_ORDER)
			FoldersAdapter.resetCache();
	}

	public String getName() { return "MatrixAdapter" + name; }
	
	protected List<MatrixItem> getMatrixItems(){
		if (matrix.getRowid() == ExtrasConst.INVALID_ID){
			matrix.getData().name = name;
			matrix.read();
			matrix.close();
		}
		
		return matrix.getData().items;
	}
	
	public Matrix getMatrix() { return matrix.getData(); }
	
	@Override
	public void onClick(int pos) {
		if( Features.USE_MATRIX_ORDER ) {
			TreeNode node = (TreeNode) getItem(pos);
			if (node != null && node.isFolderNode()) {
				node.open();

				Collections.sort(node.getChilds(), new MatrixOrderComparer(getMatrix()));
				folderTop = (FolderTreeNode) node;
				fireDataSetChanged();
				fireSetSelection(0);
				
				return;
			}
		}
		super.onClick(pos);
	}
	
	@Override
	protected void sortFullTree(TreeNode node) {
		Comparator<TreeNode> sv = TreeNodeComparator;
		if( Features.USE_MATRIX_ORDER )
			TreeNodeComparator = new MatrixOrderComparer(getMatrix());
		
		super.sortFullTree(node);
		
		if( Features.USE_MATRIX_ORDER )
			TreeNodeComparator = sv;
	}
}
