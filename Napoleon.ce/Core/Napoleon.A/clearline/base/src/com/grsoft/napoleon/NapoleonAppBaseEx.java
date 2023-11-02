/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LoadOrdersHitching;
import com.grsoft.database.PKO1cHitching;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.GPSGatherDoc;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PKO1cDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DocFilterOnClickListener;

import android.content.Context;

public class NapoleonAppBaseEx extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	public static boolean IS_PRESELLING_PROGRAM = true;
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void initDocTypes() {
		GPSGatherEdit.BEST_ACC = 100;
		VisitDocEx.init();
				
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		
//		DataObjectInfo.getInstance().replacePrimaryKey(DeliveryEx.class, "ido,number");
//		DataObjectInfo.getInstance().replacePrimaryKey(PaymentEx.class, "ido,number");

		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(IncassDoc.instance());
		DocType.addType(GPSGatherDoc.instance());
		
		DocType.addType(QuestionDoc.instance());
		DocType.addType(ScriptDoc.instance(scriptImplType()));
		DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));
		
		DocType.addType(ScriptDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		DocList.activity = DocListEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		QuestionWebView.activity = QuestEdit.class;
		
		DocFilterOnClickListener.HiddenTypes.add(PKO1cDoc.instance());
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		Features.ORG_STOP_TABLE = true;
		Features.BLOCK_IN_STOP_LIST = true;
		Features.INPUT_QTY_IN_PACK = true;
		Features.SHOW_ORG_ADDRESS = true;
		Features.QTY_IN_PACK_IN_DOCS = true;
		Features.CANT_CHANGE_SEND_FLAG = true;
		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.MAX_FOTO_HEIGHT = 5000;
		Features.MAX_FOTO_WIDTH = 5000;
		
		Features.CANT_SEND_SCRIPT_PART = true;
		Features.UPDATE_DB_CHECK_VISITS = true;
	
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new LoadOrdersHitching(); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new PKO1cHitching(); }
		}, UpdateDB.DEBET_DATA_HITCHING);
		
		com.grsoft.napoleon.modules.print.DebtDoc.DebtDocType = DebtDocEx.class;
	}
	
	@Override
	protected CfgNpl createConfig() {
		return new CfgNplEx();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
