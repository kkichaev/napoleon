package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void updateTotalSum() {
		((OrderDocEx)OrderDocEx.instance()).updateTotalSum(this, doc.sum(), doc.weight(), 
				((CfgNplW)ConfigManager.getConfig()).isPackView ? doc.countPack() : doc.count(), 
				R.id.tvTotalSum,
				((OrderImplEx)doc).sumDisc());
		
		
	}
}
