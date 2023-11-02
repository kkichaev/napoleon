package com.grsoft.util;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.R;

public class PriceNodeComparer extends TreeNodeCmp {
	public static final int CMP_NAME = R.id.tvSortName; 
	public static final int CMP_THERMAL = R.id.tvSortTherm; 
	public static final int CMP_PACK = R.id.tvSortPack; 
	
	int cmpMethod = CMP_NAME;
	boolean reverse = false;
	
	PriceImpl pi = new PriceImpl();
	
	public int getCompareMethod() { return cmpMethod; }
	public boolean isReverse() { return reverse; }
	
	public void setCompareMethod(int newMethod, boolean reverse) { cmpMethod = newMethod; this.reverse = reverse; }

	String readValue(PriceTreeNode node) {
		PriceEx p = (PriceEx) pi.getData();
		pi.read(node.getRowid());
		return cmpMethod == CMP_NAME ? p.name : cmpMethod == CMP_THERMAL ? p.thermalState : p.packName;
	}
	
	@Override
	public int compare(TreeNode object1, TreeNode object2) {
		if( object1 instanceof PriceTreeNode && object2 instanceof PriceTreeNode ) {
			
			String v1 = readValue((PriceTreeNode)object1);
			String v2 = readValue((PriceTreeNode)object2);
			int val = (reverse) ? v2.compareTo(v1) : v1.compareTo(v2);
			if( val != 0 || cmpMethod == CMP_NAME)
				return val;
		}
		return super.compare(object1, object2);
	}
}
