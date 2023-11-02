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
import com.grsoft.dataobjects.FaceMatrix;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItemEx;
import com.grsoft.dataobjects.OrgTask;
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
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.BarcodeDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.FacingDoc;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.InvEquDoc;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.ViewInitializer;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class NapoleonApp extends NapoleonAppBase {
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected void defineNewType() {
		DebtDocEx.initialize();

		Features.INCASS_DEBET_DISTRIB = false;
		IncassDoc.instance();

		super.defineNewType();

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(OrgMatrix.class, "items", OrgMatrixItemEx.class);
		doi.replaceListType(ScriptDefEx.class, "items", ScriptDefItemEx.class);
		doi.replaceListType(Visit.class, "items", VisitItemEx.class);

		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(ScriptDef.class, ScriptDefEx.class);
		DbObject.regNewDataType(Visit.class, VisitEx.class);

		DayDeliveryHitchingClassic ddh = new DayDeliveryHitchingClassic();
		ReadService.recievers.add(ddh);
		WriteService.recievers.add(ddh);
		WriteService.recievers.add(new RcvNewHitching(VisitCloudResponse.class, "VisitCloudResponse"));

		CostStrategy.defaultInstance = new CostStrategyEx();

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(Supplier.class),
						new RcvNewHitching(DogovorClassic.class),
						new RcvNewHitching(Fridge.class),
						new RcvNewHitching(Equip.class),
						new RcvNewHitching(OrgMatrix.class, "OrgMatrix"),
						new RcvNewHitching(FaceMatrix.class, "FaceMatrix"),
						new Hitching(OrgTask.class, "OrgTask"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				activity.findViewById(R.id.cbRemains).setVisibility(View.GONE);
			}
		};
	}

	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	@Override protected Class<? extends ScriptImpl> scriptImplType() { return ScriptImplEx.class; }
	@Override protected Class<? extends RemnantsImpl> remnantsImplType() { return RemnantsImplEx.class; }

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		OrderDetail.activity = OrderDetailEx.class;
		FocusItemEditor.activity = FocusedItemEditorEx.class;

		Documents.activity = DocumentsEx.class;
		Warehouse.activity = WarehouseEx.class;
		DocList.activity = DocListEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		PotenzialOrg.activity = NewClientList.class;
		VisitEdit.activity = VisitEditEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.MAX_FOTO_HEIGHT = 4000;
		Features.MAX_FOTO_WIDTH = 4000;
		Features.LOAD_FULL_PRICE = true;
		Features.UNLIMIT_VISIT_ITEMS = false;
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DocType.addType(FacingDoc.instance());
		DocType.addType(InvEquDoc.instance());
		DocType.addType(BarcodeDoc.instance());

		DocType.addType(NewClientDoc.instance());
		DocType.addType(InvEquDoc.instance());
		DocType.addType(BarcodeDoc.instance());

		DocType.addType(NewClientDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(NewClientDoc.instance());
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
