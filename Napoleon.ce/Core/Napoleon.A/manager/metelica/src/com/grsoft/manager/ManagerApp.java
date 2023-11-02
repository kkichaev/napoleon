package com.grsoft.manager;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.manager.documents.MOrderDoc;
import com.grsoft.manager.documents.MRemnantsDoc;
import com.grsoft.manager.documents.MVisitDoc;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;


public class ManagerApp extends ManagerApplicationBase {
	@SuppressWarnings("unused")
	private static final String TAG = "ManagerApp";
	
	protected void init() {
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		ConfigManager.tryInitConfig(new CfgMgr());
		Path.SHARED_FOLDER = "Manager";
		Path.init(this);
			
		DataBaseManager.init();
		DocTypeBase.checkTables();
		DocTypeBase.addType(MOrderDoc.instance());
		DocTypeBase.addType(MVisitDoc.instance());
		DocTypeBase.addType(MRemnantsDoc.instance());

		ConfigManager.load(this);
		
		DocDetail.decorator = new DocDetailDecoratorNew();
		VisitDetail.activity = VisitDetailNew.class;
		QuestionDetail.activity = QuestionDetailNew.class;
	}
}
