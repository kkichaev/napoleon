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

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DistrDocType;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PaDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.network.ServerCommand;
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
	
	class SalesEditor extends SalesPropertiesEditor {
		@Override
		public void edit(Context ctx, SalesImpl doc, boolean isOldOrder) {
			SalesProperties.open(ctx, doc.getRowid(), isOldOrder);
		}
	}
	
	private void initDocTypes() {
		Print.init();
		
		DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);
		
		DbObject.regNewDataType(Firm.class, FirmEx.class);
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(DistrDocType.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(SalesDoc.instance());
		DocType.addType(PkoDoc.instance());
		DocType.addType(PaDoc.instance());
		DocType.setCurDoc(SalesDoc.instance());
		
		SalesDetail.activity = SalesDetailEx.class;
		NPrinter.forms.put("Расх.накл.", "rn");
		NPrinter.forms.put("Акт приема-передачи", "act");
		UpdateDB.activity = UpdateDBEx.class;
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		
//		DocHelper.makeDocNumberStrategy = new BaseDocNumberStrategy(){
//			@Override
//			protected void adjustNumber(DocNumber ri) { 
//				int pos = ri.number.indexOf("-"); 
//				if( pos != -1)
//					ri.number = ri.number.substring(pos + 1, ri.number.length());
//			}
//		};
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);
		initDocTypes();
		
		OrderImpl.OrderEditor = new OrderEditor();
		SalesImpl.Editor = new SalesEditor();
		
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
