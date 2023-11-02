/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Application;
import android.content.Context;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.ActionType;
import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.AnswerEx;
import com.grsoft.dataobjects.Cities;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.QuestionItemEx;
import com.grsoft.dataobjects.Retails;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.CommonAuditDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PromoAuditDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		DocType.addType(QuestionDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(CommonAuditDoc.instance());
		DocType.addType(PromoAuditDoc.instance());
		
		DocType.setCurDoc(VisitDoc.instance());		

		DocFilterOnClickListener.HiddenTypes.add(QuestionDoc.instance());
		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		DataObjectInfo.getInstance().replaceListType(ActionType.class, "items", QuestionItemEx.class);
		DbObject.regNewDataType(Answer.class, AnswerEx.class);
		UpdateDB.activity = UpdateDBEx.class;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {			
			@Override public Hitching create() { return new RcvNewHitching(Cities.class, "Cities"); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {			
			@Override public Hitching create() { return new RcvNewHitching(Retails.class, "Retails"); }
		}, UpdateDB.GEN_DATA_HITCHING);
	
		UpdateDB.addHitchingCtor(new HitchingCtor() {			
			@Override public Hitching create() { return new RcvNewHitching(ActionType.class, "ActionType"); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {			
			@Override public Hitching create() { return new RcvNewHitching(Action.class, "Action"); }
		}, UpdateDB.GEN_DATA_HITCHING);

		ServerCommand.Category = "btl";
		
		Features.SCRIPT_DOC = true;
		Features.POTENZIAL_ORG = false;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);

		initDocTypes();
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
