package com.grsoft.napoleon;

import android.os.Bundle;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class DocListEx extends DocList {
	@Override
	protected void onResume() {
		super.onResume();
		ScriptDefImpl.setCanScripting(false);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		ScriptDefImpl.setCanScriptingOff();
	}

	@Override
	protected void loadConfig(Bundle b) {
		DocType.setCurDoc(OrderDoc.instance());
		super.loadConfig(b);
	}
}
