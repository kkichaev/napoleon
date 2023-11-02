/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobject.AgentPrefixEx;
import com.grsoft.dataobject.OrgEx;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;

public class NapoleonAppMTBase extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void initBase(boolean vanProgram) {
		initFeatures();
		initAcivity();

		Print.init();

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);
		
		CreateSales.activity = CreateSalesEx.class;
		
		DocHelper.makeDocNumberStrategy = new MakeDocNumberEx();
		
		DocType.addType(OrderDoc.instance(orderImplType()));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		if( vanProgram ) {
			DocType.addType(SalesDoc.instance());
			DocType.addType(PkoDoc.instance());
			DocType.setCurDoc(SalesDoc.instance());
		} else {
			DocType.setCurDoc(OrderDoc.instance());
			ServerCommand.Category = "pda";
		}
		
		DocType.addType(RemnantsDoc.instance(remantsImplType()));
		DocType.addType(QuestionDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(ScriptDoc.instance());
		DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
