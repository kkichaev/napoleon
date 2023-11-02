/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.grsoft.database.DlvSumHitching;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPlanNew;
import com.grsoft.dataobjects.Brands;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DaysGoods;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.DivisionPlan;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.IdMtx;
import com.grsoft.dataobjects.IdoMtx;
import com.grsoft.dataobjects.MMLFeatures;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.MerchRouteForAgent;
import com.grsoft.dataobjects.NoOrderReason;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.PlanChanges;
import com.grsoft.dataobjects.PlanGroups;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceTypes;
import com.grsoft.dataobjects.RejectAct;
import com.grsoft.dataobjects.RejectActItem;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.RequestSync;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnLimit;
import com.grsoft.dataobjects.ReturnRequest;
import com.grsoft.dataobjects.ReturnRequestItem;
import com.grsoft.dataobjects.SalesChannel;
import com.grsoft.dataobjects.SalesTypes;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.TradeAction;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OffTakeHistory.OffTakeInflator;
import com.grsoft.napoleon.documents.OrderBundleDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.RejectActDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.ReturnRequestDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.network.ByteStream;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.PriceNodeComparer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	static final String GLOBAL_PREFERENCES = "global_preferences";
	static final String ID_ORG_IN_WORK = "id_org_in_work";
	
	long sheduleStart = 0;
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {

//		Runtime r = Runtime.getRuntime();
//		long maxMem = r.maxMemory();
//		long freeMem = r.freeMemory();
//		long avail = (maxMem - (r.totalMemory() - freeMem)) / 2;
//
//
//		ByteStream.MAX_BUF_LENGTH = (int)avail;

		DebtDocEx.init();
		OrderDocEx.init(OrderImplEx.class);
		VisitDoc.instance(VisitImplEx.class);
		RemnantsDoc.instance(RemnantsImplEx.class);
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replacePrimaryKey(PaymentEx.class, "ido,firm,number");
		doi.replaceListType(ReturnRequest.class, "items", ReturnRequestItem.class);
		doi.replaceListType(Visit.class, "items", VisitItemEx.class);
		doi.replaceListType(DeliveryEx.class, "items", DeliveryItemEx.class);
		doi.replaceListType(OrgFolders.class, "items", OrgFolderItemEx.class);
		doi.replaceListType(Matrix.class, "items", MatrixItemEx.class);
		doi.replaceListType(Remnants.class, "items", RemnantItemEx.class);
		doi.replaceListType(RejectAct.class, "items", RejectActItem.class);
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Payment.class, PaymentEx.class);
		DbObject.regNewDataType(ScriptDef.class, ScriptDefEx.class);
		DbObject.regNewDataType(Script.class, ScriptEx.class);
		DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);

		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
	
		FoldersAdapter.TreeNodeComparator = new PriceNodeComparer();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public List<Hitching> createList() { 
				CostStrategyEx.resetCache();
				
				return Arrays.asList(new Hitching[] {
					new RcvNewHitching(OrgDog.class, "OrgDogovor"),
					new RcvNewHitching(DaysGoods.class, "DaysGoods"),
					new RcvNewHitching(AgentPlanNew.class, "PlanNew"),
					new RcvNewHitching(DivisionPlan.class, "DivisionPlan"),
					new RcvNewHitching(PlanGroups.class, "PlanGroups"),
					new RcvNewHitching(PriceTypes.class, "PriceType"),
					new RcvNewHitching(Firm.class, "Firms"),
					new RcvNewHitching(PlanChanges.class, "PlanChanges"),
					new RcvNewHitching(IdMtx.class),
					new RcvNewHitching(IdoMtx.class),
					new Hitching(OrgMatrix.class),
					new RcvNewHitching(ReturnCause.class),
					new RcvNewHitching(ReturnLimit.class),
					new RcvNewHitching(SalesTypes.class),
					new RcvNewHitching(NoOrderReason.class),
					new RcvNewHitching(SalesChannel.class),
					new RcvNewHitching(MMLFeatures.class),
					new RcvNewHitching(Brands.class),
					new RcvNewHitching(TradeAction.class),
					new RcvNewHitching(MerchRouteForAgent.class),
				});
		}}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new DlvSumHitching(); } 
		}, UpdateDB.DEBET_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new DocumentRestore(ReturnRequestDoc.instance()),
						new DocumentRestore(RejectActDoc.instance()),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);
	
		DebtDoc.LoadDelivery = false;
		OffTakeInflator.OFF_TAKE_COEF = 125;
		OrgFoldersTree.SheduleResolver = new OrgFoldersTree.SheduleStartResolver() {
			
			@Override
			public long getSheduleStart() {
				if(sheduleStart == 0) {
					// начало цикла - начало года
					Calendar c = Calendar.getInstance();
					c.set(Calendar.DAY_OF_MONTH, 1);
					c.set(Calendar.MONTH, Calendar.JANUARY);
					c.set(Calendar.HOUR_OF_DAY, 0);
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);
					while(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
						c.add(Calendar.DAY_OF_YEAR, -1);
					sheduleStart = c.getTime().getTime();
				}
				return sheduleStart;
			}
		};
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Прогресс плана", new Runnable() {
					@Override public void run() { PlanProgressView.open(activity); }
				}));
			}
		});
		
		Hitching h = new RcvNewHitching(RequestSync.class);
		ReadService.recievers.add(h);
		WriteService.recievers.add(h);
	}
	
	@Override protected Class<? extends ScriptImpl> scriptImplType() { return ScriptImplEx.class; }
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		DocList.activity = DocListEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.INPUT_QTY_IN_PACK = true;
		Features.FILTER_DOCUMENTS_BY_ITEM = true;		
		Features.ASSORTMENT_MATRIX = true;
		Features.WEIGHT_SCALE = 10;
		Features.LOAD_FULL_PRICE = true;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;

		Features.SYNC_INFO = true;
		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.UPDATE_PRICE_BACKGROUND = true;
//		Features.SHOW_DAILY_SALES_IN_WAREHOUSE = true;
		Features.SHOW_PRESENT_IMG = true;
		Features.SCRIPT_DOC = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		
		Features.SEND_PROGRAM_SETTINGS = true;
		Features.START_STOP = true;
		
		Features.TRACE_WEEK_INDEX = true;
		Features.DONT_SEND_UNCOMPLETE_SCRIPTS = true;
		
		Features.SHOW_ORG_ADDRESS= true;
		Features.UNLIMIT_VISIT_ITEMS = true;
		Features.CHECK_UNCOMPLETE_SCRIPTS = false;
		
		Features.COST_MANAGER = new CostManagerImpl();
	}
	
	@Override
	protected void initChildDocTypes() {
//		DocType.addType(DistribDoc.instance());
		DocType.addType(ReturnRequestDoc.instance());
		DocType.addType(OrderBundleDoc.instance());
		DocType.addType(RejectActDoc.instance());
		
		DocFilterOnClickListener.HiddenTypes.add(OrderBundleDoc.instance());
		DocType.removeType(ReturnDoc.instance());
	}
	
//	protected void initDocTypes() {
//
//		DocType.addType(OrderDoc.instance());
//		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance(VisitImplEx.class));
//		DocType.addType(DistribDoc.instance());
//		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
//		DocType.addType(ReturnRequestDoc.instance());
//		DocType.addType(QuestionDoc.instance());
//		
//		DocType.addType(ScriptDoc.instance());
//		DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));
//
//		DocType.setCurDoc(OrderDoc.instance());		
//
//		
//
//		
////		RWServiceFactory.instance = new RWServiceFactoryEx();
////		Hitching h = new ReturnAcceptHitching();
////		ReadService.recievers.add(h);
////		WriteService.recievers.add(h);
//	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
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

	public String getInWork(){
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(NapoleonApp.ID_ORG_IN_WORK, "");
	}
	
	public void putInWork(String inWork) {
		SharedPreferences pref = getSharedPreferences(NapoleonApp.GLOBAL_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = pref.edit();
		ed.putString(NapoleonApp.ID_ORG_IN_WORK, inWork);
		ed.commit();
	}
}
