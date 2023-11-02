package com.grsoft.napoleon;

import com.grsoft.dataobjects.CostData;
import com.grsoft.napoleon.modules.CostManagerImpl;

public class CostManagerImplEx extends CostManagerImpl {
	CostData curData = new CostData();
	public CostManagerImplEx() {
		super(8);
	}
	
	@Override
	protected int readData(byte[] buf) {
		curData.cost = (buf[3]) << 24 | (buf[2]&0xff) << 16 | (buf[1]&0xff) <<  8 | (buf[0]&0xff);
		curData.minCost = (buf[7]) << 24 | (buf[6]&0xff) << 16 | (buf[5]&0xff) <<  8 | (buf[4]&0xff);
		return curData.cost;
	}
	
	public CostData getCurCost() { return curData; }
}
