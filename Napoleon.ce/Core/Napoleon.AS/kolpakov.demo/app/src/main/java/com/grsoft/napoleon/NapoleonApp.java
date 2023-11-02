/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlan;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PlanRichard;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.QuestionItemEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.MoveDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

		CostStrategy.defaultInstance = new CostStrategyEx();

		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Question.class, "items", QuestionItemEx.class);
		doi.replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		doi.replaceListType(Return.class, "items", ReturnItem.class);
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);

		DebtDocEx.initialize();
		OrderDocEx.initialize();

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(PlanRichard.class),
						new RcvNewHitching(FirmEx.class),
				};
				return Arrays.asList(h);
			}

//			@Override public Hitching create() { return new RcvNewHitching(AgentPlan.class, "AgentPlan"); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(ReturnDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(MoveDoc.instance());

		ScriptDefImpl.docInScript.add(IncassDoc.instance());
		ScriptDefImpl.docInScript.add(ReturnDoc.instance());
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Warehouse.activity = WarehouseEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		DocList.activity = DocListEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.LAST_SALED_ITEMS_PERIOD = 2;
		Features.USE_COST_IN_RETURNS = true;
		Features.LOAD_FULL_PRICE = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.WEIGHT_SCALE = Consts.QTY_SCALE;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
		Features.REMOVE_EMPTY_ORDERS = true;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		initDemo();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void initDemo() {
		final String GLOBAL_PREF_NAME = "main_pref";
		final String INITED_KEY = "demo_inited";
		boolean inited = false;

		SharedPreferences preferences = getSharedPreferences(
				GLOBAL_PREF_NAME, Context.MODE_PRIVATE);
		inited = preferences.getBoolean(INITED_KEY, false);

		if(!inited){
			createDemoDb();

			preferences.edit().putBoolean(INITED_KEY, true).commit();
		}
	}

	private void createDemoDb() {
		DataBaseManager.getDataBase().close();

		try{
			File dataBaseFile = new File(Path.getDataBasePath());
			InputStream dis = getResources().openRawResource(R.raw.napoleon);
			OutputStream dos = new BufferedOutputStream(
					new FileOutputStream(dataBaseFile));

			byte[] buffer = new byte[1024];
			int n = 0;

			while ((n = dis.read(buffer)) != -1)
				dos.write(buffer, 0, n);

			dis.close();
			dos.close();
		}catch (Exception e){
			e.printStackTrace();
		}

		DataBaseManager.init();
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
