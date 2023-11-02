/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.TextPrinter;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.content.Context;

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
		Print.init(true);
	
		NPrinter.setPrintStrategy(NPrinter.TEXT);
		NPrinter.forms.put("Накладная", "nakl");
		TextPrinter.PAGE_ROW_COUNT = 85;
		TextPrinter.STR_DIVIDER = " ,-+%*\\";
		TextPrinter.OUTPUT_FILE_ENCODE = "cp866";
	
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
			}
		});
	}
	
	@Override
	public void setDefDocType() {
		DocType.setCurDoc(SalesDoc.instance());
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(SalesDoc.instance());
//		DocType.addType(PkoDoc.instance());
	}
	
	@Override
	protected void initChildFeature() {
		Features.ASSORTMENT_MATRIX = true;
		Features.INCASS_DEBET_DISTRIB = true;
		Features.UPD = true;
		
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
	}
	
	@Override
	protected void initChildActivity() {
		SalesDetail.activity = SalesDetailEx.class;
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
