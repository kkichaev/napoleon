/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.Consts;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void makeSampleDB(Context context) {
		try{
			File dataBaseFile = new File(Path.getDataBasePath());
			if (!dataBaseFile.exists()){
				InputStream dis = context.getResources().openRawResource(R.raw.napoleon);
				OutputStream dos = new FileOutputStream(dataBaseFile);

				byte[] buffer = new byte[1024];
				int n = 0;
				while ((n = dis.read(buffer)) != -1)
				    dos.write(buffer, 0, n);

				dis.close();
				dos.close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}		
	}
	
	public void initDocTypes() {
		OrderDetail.orderDetailActivity = OrderDetailEx.class;
		
		DbObject.regNewDataType(Price.class, PricePrint.class);
		
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(Consts.D_TAG, "NapoleonApp.onCreate");
		FirstRunInit.init(this);
		initDocTypes();
		initProjectTypes();
		makeSampleDB(this);
		OrderImpl.OrderEditor = new OrderEditor();
		
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void initProjectTypes(){
	}
}
