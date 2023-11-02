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

import android.app.Activity;
import android.app.Application;

import com.grsoft.database.DefectsRestore;
import com.grsoft.database.DocumentRestoreEx;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.OdometrHitching;
import com.grsoft.database.StartAuditHitching;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.CellsAuditDoc;
import com.grsoft.napoleon.documents.DefectReportDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VandReloadDoc;
import com.grsoft.napoleon.documents.VandSellDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class NapoleonApp extends Application {
	
	public final static int MONEY_COLOR = 0xF7F796; 
	
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	private void initDocTypes() {
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
//		DocType.addType(IncassDoc.instance());
		DocType.addType(VandSellDoc.instance());
		DocType.addType(CellsAuditDoc.instance());
		DocType.addType(VandReloadDoc.instance());
		DocType.addType(DefectReportDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(TaskDoneDoc.instance());
		
		DocType.setCurDoc(VandSellDoc.instance());		

		Warehouse.activity = WarehouseNew.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		
		Features.POTENZIAL_ORG = false;
		Features.UPDATE_DB_CHECK_VISITS = true;
		Features.LOAD_FULL_PRICE = true;
		Features.SCRIPT_DOC = true;
		Features.DOC_SUM_BY_PERIOD = true;
		
		DocFilterOnClickListener.HiddenTypes.add(WSOrderDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(OrderDoc.instance());
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] data = new Hitching[] {
//						new DocumentRestoreEx(IncassDoc.instance()),
//						new DocumentRestoreEx(VandSellDoc.instance()),
//						new DocumentRestoreEx(CellsAuditDoc.instance()),
//						new DocumentRestoreEx(VandReloadDoc.instance()),
//						new DocumentRestoreEx(WSOrderDoc.instance()),
						new DefectsRestore(),
				};
				return Arrays.asList(data);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new StartAuditHitching(); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getApplicationContext().getString(R.string.wsorder_title), new Runnable (){
					
				@Override public void run() { WSOrderList.open(activity); } }));
			}
		});
		UpdateDB.addHitchingCtor(new HitchingCtor() {	
			@Override public Hitching create() { return new OdometrHitching();	}
		}, UpdateDB.EXPORT_DATA_HITCHING);
	}
	
	@Override
	public void onCreate() {
		ServerCommand.Category = "vend";
		
		super.onCreate();
		FirstRunInit.init(this);
		
		initDocTypes();
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
