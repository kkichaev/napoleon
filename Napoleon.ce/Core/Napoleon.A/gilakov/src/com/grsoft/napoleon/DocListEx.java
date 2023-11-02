package com.grsoft.napoleon;

import android.os.Bundle;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

public class DocListEx extends DocList {
	boolean oldScriptOff;
	
	@Override
	protected void onResume() {
		super.onResume();
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		oldScriptOff = cfg.scriptOff;
		cfg.scriptOff = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		cfg.scriptOff = oldScriptOff; 
	}

	@Override
	protected void loadConfig(Bundle b) {
		DocType.setCurDoc(OrderDoc.instance());
		super.loadConfig(b);
	}
}
