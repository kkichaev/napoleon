package com.grsoft.util;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;

public interface TreeNodeFactory {
	PriceTreeNode createPriceTreeNode(TreeNode parent, long priceRowId, String name, String id);
	String getWhereStr();
	boolean isValid(TreeNode node);
}
