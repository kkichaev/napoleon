/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

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
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PKO1cDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.ScanLocationDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.napoleon.modules.print.DebtDoc;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DocFilterOnClickListener;


public class NapoleonApp extends NapoleonAppBase{
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected void defineNewType() {
		Print.init(false);
		VisitDocEx.init();

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);

		DocFilterOnClickListener.HiddenTypes.add(PKO1cDoc.instance());
		CostStrategy.defaultInstance = new CostStrategyEx();

		com.grsoft.napoleon.modules.print.DebtDoc.DebtDocType = DebtDocEx.class;
		ServerCommand.Category = "pda";
		ScanLocationEdit.BEST_ACC = 50;

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new LoadOrdersHitching(); }
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new PKO1cHitching(); }
		}, UpdateDB.DEBET_DATA_HITCHING);
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
	}

	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		DocList.activity = DocListEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		QuestionWebView.activity = QuestEdit.class;
		PriceCount.activity = PriceCountEx.class;
		PotenzialOrg.activity = PotenzialOrgEx.class;
		Documents.activity = DocumentsEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

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
		Features.RECIEVE_REMNANTS_IN_MAIN_MENU = false;
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
