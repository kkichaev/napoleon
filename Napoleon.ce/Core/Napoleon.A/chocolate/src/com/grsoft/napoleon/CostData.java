package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceCost;

class CostData {
	public int cost;
	public int itemCost;
	
	public CostData(PriceCost pc) {
		this.cost = pc.cost;
		this.itemCost = pc.itemCost;
	}
}