package com.grsoft.util;
import com.grsoft.aceteam.R;

import java.util.Comparator;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;

public class MatrixOrderComparer implements Comparator<TreeNode>
{
	Matrix matrix;
	public MatrixOrderComparer(Matrix matrix) {
		this.matrix = matrix;
	}
	
	int getOrder(TreeNode obj) {
		if( obj instanceof PriceTreeNode ) {
			String id = ((PriceTreeNode)obj).getId();
			for(MatrixItem mi : matrix.items)
				if(mi.id.equals(id))
					return mi.order;
		}
		return -1;
	}

	@Override
	public int compare(TreeNode object1, TreeNode object2){
		int ord1 = getOrder(object1);
		int ord2 = getOrder(object2);
		if( ord1 != ord2 )
			return ord1 - ord2;
		return object1.compareTo(object2);
	}
}