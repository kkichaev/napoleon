package com.grsoft.manager;

import com.grsoft.database.DataBaseManager;
import com.grsoft.manager.documents.MOrderDoc;
import com.grsoft.manager.documents.MRemnantsDoc;
import com.grsoft.manager.documents.MVisitDoc;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;


public class ManagerApp extends ManagerApplicationBase {
	@Override
	protected void init() {
		ConfigManager.tryInitConfig(new CfgMgr());
		Path.SHARED_FOLDER = "Manager";
		Path.init(this);
			
		DataBaseManager.init();
		DocTypeBase.checkTables();
		DocTypeBase.addType(MOrderDoc.instance());
		DocTypeBase.addType(MVisitDoc.instance());
		DocTypeBase.addType(MRemnantsDoc.instance());
		

		ConfigManager.load(this);
	}
}
