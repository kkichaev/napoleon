package com.grsoft.util;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.TreeNode;

public class FolderNodeCmp extends TreeNodeCmp {
	@Override
	public int compare(TreeNode object1, TreeNode object2) {
		if( object1 instanceof FolderTreeNode && object2 instanceof FolderTreeNode )
			return object1.toString().compareTo(object2.toString());
		return super.compare(object1, object2);
	}
}
