/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.ActionPrice;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MTask;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Recommend;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.TaskAnswer;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.ATaskDoc;
import com.grsoft.napoleon.documents.ActGSDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.FacingDoc;
import com.grsoft.napoleon.documents.MerchBeginDoc;
import com.grsoft.napoleon.documents.MerchEndDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskAnswerDoc;
import com.grsoft.napoleon.documents.TaskBeginDoc;
import com.grsoft.napoleon.documents.TaskEndDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DocFilterOnClickListener;

import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
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
		super.defineNewType();
		DataObjectInfo.getInstance().replaceListType(Remnants.class, "items", RemnantItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DbObject.regNewDataType(Visit.class, VisitEx.class);
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		
		DocType.addType(MerchBeginDoc.instance());
		DocType.addType(MerchEndDoc.instance());
		DocType.addType(TaskBeginDoc.instance());
		DocType.addType(TaskEndDoc.instance());
		DocType.addType(ATaskDoc.instance());
		DocType.addType(TaskAnswerDoc.instance());
		DocType.addType(ActGSDoc.instance());
		DocType.addType(FacingDoc.instance());
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}
	
	@Override
	protected Class<? extends RemnantsImpl> remantsImplType() {
		return RemnantsImplEx.class;
	}
	
	@Override
	protected Class<? extends ScriptImpl> scriptImplType() {
		return ScriptImplEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		
		Features.SCRIPT_DOC = true;
		Features.COST_MANAGER = new CostManagerImpl();
		Features.INCASS_DEBET_DISTRIB = true;
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Warehouse.activity = WarehouseEx.class;
		Documents.activity = DocumentsEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		VisitEdit.activity = VisitEditEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}
	
	protected void initDocTypes() {
		super.initDocTypes();
		
		DocFilterOnClickListener.HiddenTypes.add(MerchBeginDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(MerchEndDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(TaskBeginDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(TaskEndDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(ATaskDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(TaskAnswerDoc.instance());
		
		DbWriter.checkDBTable(TaskAnswer.class);
		UpdateDB.addHitchingCtor(new HitchingCtor() { @Override public Hitching create() { return new Hitching(MTask.class){
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				MTask dobj = (MTask) rawObject.createDataObject(dataObject);
				dobj.manager = 1;
				dbProxy.insertRecord(dobj);
			}
		}; }}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(Recommend.class);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new RcvNewHitching(ActionPrice.class);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		RWServiceFactory.instance = new RWServiceFactoryEx();
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
