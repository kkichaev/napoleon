package com.grsoft.util;

import com.grsoft.database.PriceTreeNodeEx;
import com.grsoft.database.TreeNode;

public class TreeNodeCmpEx extends TreeNodeCmp {
	@Override
	public int compare(TreeNode object1, TreeNode object2) {
		if(object1 instanceof PriceTreeNodeEx && object2 instanceof PriceTreeNodeEx) {
			long cmp = ((PriceTreeNodeEx)object1).rang = ((PriceTreeNodeEx)object2).rang;
			if(cmp != 0)
				return cmp < 0 ? -1 : 1;
		}
		return super.compare(object1, object2);
	}
}
