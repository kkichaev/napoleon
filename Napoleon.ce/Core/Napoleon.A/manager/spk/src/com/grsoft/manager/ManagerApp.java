package com.grsoft.manager;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.AnswerItemEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.QuestionEx;
import com.grsoft.dataobjects.impl.DbObject;
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
import com.grsoft.napoleon.util.CfgMgrEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;


public class ManagerApp extends ManagerApplicationBase {
	@SuppressWarnings("unused")
	private static final String TAG = "ManagerApp";
	
	protected void init() {
		ConfigManager.tryInitConfig(new CfgMgrEx());
		Path.SHARED_FOLDER = "Manager";
		Path.init(this);
			
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
		DrawerHelper.instanceType = DrawerHelperEx.class;
		AuditEdit.activity = AuditEditEx.class;
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Answer.class, "items", AnswerItemEx.class);
		DbObject.regNewDataType(Question.class, QuestionEx.class);
	}
}
