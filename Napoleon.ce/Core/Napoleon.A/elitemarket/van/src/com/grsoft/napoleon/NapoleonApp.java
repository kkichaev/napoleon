/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PkoImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.PaDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

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
		Print.init();
		
		PriceCount.activity = PriceCountEx.class;
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(SalesDoc.instance());
		DocType.addType(PkoDoc.instance(PkoImplEx.class));
		DocType.addType(PaDoc.instance());
		DocType.setCurDoc(SalesDoc.instance());
		
		Features.DISABLE_EDIT_AFTER_PRINT = true;
		Features.CANT_DEL_PRINTED_DOCS = true;
	
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);

		Napoleon.docMenuPrepared.add( new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
			}
		});
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
