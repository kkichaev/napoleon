package com.grsoft.manager;

import com.grsoft.database.DataBaseManager;
import com.grsoft.manager.documents.MAnswerDoc;
import com.grsoft.manager.documents.MIncassDoc;
import com.grsoft.manager.documents.MOrderDoc;
import com.grsoft.manager.documents.MPkoDoc;
import com.grsoft.manager.documents.MRemnantsDoc;
import com.grsoft.manager.documents.MReturnDoc;
import com.grsoft.manager.documents.MSalesDoc;
import com.grsoft.manager.documents.MScriptDoc;
import com.grsoft.manager.documents.MVisitDoc;
import com.grsoft.manager.documents.NotVisitedDoc;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;


public class ManagerApp extends ManagerApplicationBase {
	@SuppressWarnings("unused")
	private static final String TAG = "ManagerApp";
	
	protected void init() {
		ConfigManager.tryInitConfig(new CfgMgr());
		Path.SHARED_FOLDER = "Manager";
		Path.init(this);
		
		ManagerNew.activity = ManagerEx.class;
		DrawerHelper.instanceType = DrawerHelperEx.class;
		
		SyncDetail.SYNC_DETAIL_MODULE_NAME = "route_monitor";
		DataBaseManager.init();
		DocTypeBase.checkTables();
		DocTypeBase.addType(MOrderDoc.instance());
		DocTypeBase.addType(MVisitDoc.instance());
		DocTypeBase.addType(MRemnantsDoc.instance());
		DocTypeBase.addType(MIncassDoc.instance());
		DocTypeBase.addType(MAnswerDoc.instance());
		DocTypeBase.addType(MSalesDoc.instance());
		DocTypeBase.addType(MPkoDoc.instance());
		DocTypeBase.addType(MScriptDoc.instance());
		DocTypeBase.addType(MReturnDoc.instance());
		DocTypeBase.addType(NotVisitedDoc.instance());

		ConfigManager.load(this);
		
		DocDetail.decorator = new DocDetailDecoratorNew();
		VisitDetail.activity = VisitDetailNew.class;
		QuestionDetail.activity = QuestionDetailNew.class;
	}
	
	protected void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
			ServerCommand.Category = "monitor";
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
