package com.grsoft.util;

import java.util.HashMap;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;

public class PriceComparer extends TreeNodeCmp {
	static HashMap<String, Integer> orders = new HashMap<String, Integer>();

	PriceImpl pi = new PriceImpl();
	
	static void clearCache() {
		orders.clear();
	}
	
	int getOrder(String id) {
		Integer ord = orders.get(id);
		if(ord == null) {
			PriceEx pe = (PriceEx)pi.getData();
			pe.id = id;
			pi.read();
			ord = pe.ord;
			orders.put(id, ord);
		}
		return ord;
	}
	
	@Override
	public int compare(TreeNode object1, TreeNode object2) {
		if(object1 instanceof PriceTreeNode && object2 instanceof PriceTreeNode) {
			int ord1 = getOrder(((PriceTreeNode)object1).getId());
			int ord2 = getOrder(((PriceTreeNode)object2).getId());
			if( ord1 >= 0 || ord2 >=0 ) {
				return ord1 < 0 ? 1 : 
					ord2 < 0 ? -1 :
					ord1 - ord2;
			}
		}
		return super.compare(object1, object2);
	}
}
