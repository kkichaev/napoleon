/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.PresentSdcard;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

import android.app.Application;
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
	protected void initChildActivity() {
		super.initChildActivity();
		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolderEx.class;
		PricePresentation.activity = PricePresentationFolder.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.HAVE_VISIT_CAUSE = true;
		Features.PRESENTATION_ON_SDCARD = true;
	}

	@Override
	protected void defineNewType() {
		super.defineNewType();
		PresentSdcard.initStrategy = new InitPresentStrategy();
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
