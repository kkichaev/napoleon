package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

public class MainEx extends Main {
	@Override
	protected void onResume() {
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		if(cfg.simpleMode > 0 && cfg.simpleModeOrg.length() > 0) {
			OrgImpl oi = new OrgImpl();
			Org o = oi.getData();
			o.id = cfg.simpleModeOrg;
			boolean canDo = oi.read();
			oi.close();
			
			if(canDo) {
				Documents.open(this, o);
				finish();
			}
		}
		super.onResume();
	}
}
