/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.FoldersTree;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.LayoutDef;
import com.grsoft.database.PicStoreHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.LayoutFailureCause;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDisablePhoto;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ReportsRequest;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.Unit2Ex;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.LayoutDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.SQLSelector;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void initDocTypes() {
		//Print.init();
		Warehouse.activity = WarehouseEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		DocList.activity = DocListEx.class;
		Documents.activity = DocumentsEx.class;
		VisitEdit.activity = VisitEditEx.class;
		QuestionWebView.activity = QuestEdit.class;
		CreateReturn.activity = CreateReturnEx.class;
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);

		OrderDocEx.init();
		
		DocType.addType(OrderDocEx.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(QuestionDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(LayoutDoc.instance());
		DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));
		
		ScriptDoc.instance(ScriptImplEx.class);
		DocType.setCurDoc(OrderDoc.instance());
		
		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
		doi.replaceListType(Return.class, "items", OrderItemEx.class);
		doi.replaceTableName(ReportsRequest.class, "ReportRequest");
		doi.replaceListType(PriceEx.class, "units", Unit2Ex.class);
		
		FoldersTree.docSelection.put(OrderDocEx.class, new SQLSelector(){
			@Override
		    public String getWhereClause() { 
		    	return "(expired > " + Util.getDate().getTime() + " or expired < date('1990-01-01'))"; };
			}
		);
		
		Features.WEIGHT_SCALE = Consts.WEIGHT_SCALE;
		Features.SCRIPT_DOC = true;
		Features.SHOW_NUMBER_IN_ORDER = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.USER_CAN_SCRIPT_OFF = true;
		Features.QUESTION = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.MAX_FOTO_WIDTH = 5000;
		Features.MAX_FOTO_HEIGHT = 5000;
		Features.UNLIMIT_VISIT_ITEMS = true;
		Features.DONT_SEND_UNCOMPLETE_SCRIPTS = true;
		
		//NPrinter.setPrintStrategy(NPrinter.TEXT);
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new RcvNewHitching(LayoutDef.class);}
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new RcvNewHitching(OrgDisablePhoto.class);}
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new RcvNewHitching(LayoutFailureCause.class);}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new PicStoreHitching();}
		}, UpdateDB.EXPORT_DATA_HITCHING);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.reports), new Runnable() {
					@Override public void run() { ReportListSPK.open(activity); }}));}});
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.route), new Runnable() {
					@Override public void run() { PlanList.open(activity); }}));}});
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		startService(new Intent(this, DocSendWatchService.class));
	}
	
	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
