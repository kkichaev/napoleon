/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.Arrays;
import java.util.List;

import com.grsoft.database.DayDeliveryHitchingClassic;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DogovorClassic;
import com.grsoft.dataobjects.Equip;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.ScriptDefItemEx;
import com.grsoft.dataobjects.Supplier;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitCloudResponse;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplClassic;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.BarcodeDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.FacingDoc;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.InvEquDoc;
import com.grsoft.napoleon.documents.InvFrgDoc;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;

import android.app.Application;
import android.content.Context;

public class NapoleonApp extends Application {
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		Base.init();
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(OrgMatrix.class, "items", OrgMatrixItemEx.class);
		doi.replaceListType(ScriptDefEx.class, "items", ScriptDefItemEx.class);
		doi.replaceListType(Visit.class, "items", VisitItemEx.class);
		
		ScriptDoc.instance(ScriptImplEx.class);
		
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(ScriptDef.class, ScriptDefEx.class);
		DbObject.regNewDataType(Visit.class, VisitEx.class);

		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance(RemnantsImplClassic.class));
		DocType.addType(IncassDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(FacingDoc.instance());
		//2021.07.06 СВД приказал скрыть
		//DocType.addType(InvFrgDoc.instance());
		DocType.addType(InvEquDoc.instance());
		DocType.addType(BarcodeDoc.instance());
		
		DocType.addType(NewClientDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());

		UpdateDB.activity = UpdateDBBase.class;
		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		DocList.activity = DocListEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		PotenzialOrg.activity = NewClientList.class;
		VisitEdit.activity = VisitEditEx.class;
		
		Features.SCRIPT_DOC = true;
		Features.QUESTION = true;
		Features.LOAD_FULL_PRICE = true;
		
		DayDeliveryHitchingClassic ddh = new DayDeliveryHitchingClassic();
		ReadService.recievers.add(ddh);
		WriteService.recievers.add(ddh);
		WriteService.recievers.add(new RcvNewHitching(VisitCloudResponse.class, "VisitCloudResponse"));
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
					new RcvNewHitching(Supplier.class),
					new RcvNewHitching(DogovorClassic.class),
					new RcvNewHitching(Fridge.class),
					new RcvNewHitching(Equip.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		DocFilterOnClickListener.HiddenTypes.add(NewClientDoc.instance());
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
