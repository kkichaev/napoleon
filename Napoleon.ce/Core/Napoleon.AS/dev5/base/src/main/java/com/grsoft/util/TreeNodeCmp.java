package com.grsoft.util;
import com.grsoft.aceteam.R;

import java.util.Comparator;

import com.grsoft.database.TreeNode;

public class TreeNodeCmp implements Comparator<TreeNode>
{

	@Override
	public int compare(TreeNode object1, TreeNode object2){
		return object1.compareTo(object2);
	}
}
