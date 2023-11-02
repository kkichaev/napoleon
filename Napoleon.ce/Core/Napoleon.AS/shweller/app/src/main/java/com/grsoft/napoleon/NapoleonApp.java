/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.Tare;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.TareDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected void defineNewType() {
		super.defineNewType();

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Return.class, "items", ReturnItem.class);
		doi.replaceListType(Order.class, "items", OrderItemEx.class);
		doi.replaceListType(Tare.class, "items", OrderItem.class);
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		PriceCount.activity = PriceCountEx.class;
		Warehouse.activity = WarehouseEx.class;
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(TareDoc.instance());
		DocType.addType(ReturnDoc.instance());
	}

	@Override
	protected Class<? extends ReturnImpl> returnsImplType() {
		return ReturnImplEx.class;
	}

	@Override
	public void onCreate() {
		Features.VER_4_1 = true;

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
