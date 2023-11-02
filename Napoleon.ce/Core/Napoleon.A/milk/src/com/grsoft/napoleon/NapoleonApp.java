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

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	public void initDocTypes() {
		Warehouse.WarehouseActivity = WarehouseEx.class;

		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);
		initDemo();
		initDocTypes();
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

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void createDemoDb() {
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
	}
}
