/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;
import com.grsoft.database.OrderDecisionHitching;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.RestOutDoc;
import com.grsoft.napoleon.documents.TaskDoneDocM;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;

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
	protected  void initDocTypes() {
		
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
		DocType.addType(RestOutDoc.instance());
		DocType.addType(TaskDoneDocM.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		PriceCount.activity = PriceCountEx.class;
		CostStrategy.defaultInstance = new CostStrategyEx();
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
		Warehouse.activity = WarehouseEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		
		Features.SCRIPT_DOC = true;
		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.SYNC_INFO = true;
		
		OrderDecisionHitching odh = new OrderDecisionHitching(this);
		ReadService.recievers.add(odh);
		WriteService.recievers.add(odh);
		
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
