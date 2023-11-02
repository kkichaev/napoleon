package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.AgentTaskDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

public class OrderDetailEx extends OrderDetail {
	@Override
	public void onBackPressed() {
		if( ((CfgNplEx)ConfigManager.getConfig()).showAgentTask && !doc.isExported()) {
			
			DocType.setCurDoc(AgentTaskDoc.instance());
			AgentTaskList.open(this, doc.getId(), true);
		}
		super.onBackPressed();
	}
}
