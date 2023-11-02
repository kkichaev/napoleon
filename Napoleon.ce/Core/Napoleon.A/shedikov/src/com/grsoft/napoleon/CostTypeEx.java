package com.grsoft.napoleon;

import com.grsoft.napoleon.modules.CostManager;

class CostTypeEx extends CostManager.CostType {
	
	int costIndex;
	
	public CostTypeEx(int index, CostManager.CostType c) { 
		super(c.id, c.name);
		costIndex = index;
	}
	
	@Override public String toString() { return name ; }
}