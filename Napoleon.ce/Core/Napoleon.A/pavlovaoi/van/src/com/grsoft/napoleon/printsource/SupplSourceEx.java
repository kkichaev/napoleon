package com.grsoft.napoleon.printsource;

import com.grsoft.dataobjects.Firm;
import com.grsoft.napoleon.printsources.SupplSource;

public class SupplSourceEx extends SupplSource {
	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		if(agentOrder.length() > 0) {
			buh = agentName;
			chief = agentName;
		}
	}
}
