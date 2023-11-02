package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Firm;

public class SupplSourceEx extends SupplSource {
	@PrintInfo(name="АгентТел")
	public  String agentPhone;

	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		
		buh = firm.buh;
		chief = firm.chief;
		AgentPrefixEx ap = (AgentPrefixEx) AgentPrefix.get();
		if( ap != null )
			agentPhone = ap.phone;
		else 
			agentPhone = "";
	}
}
