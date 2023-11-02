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

import com.grsoft.database.OrderDecisionHitching;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RestOutDoc;
import com.grsoft.napoleon.documents.TaskDoneDocM;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
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

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);

		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);

		DocType.addType(RestOutDoc.instance());
		DocType.addType(TaskDoneDocM.instance());

		CostStrategy.defaultInstance = new CostStrategyEx();

		OrderDecisionHitching odh = new OrderDecisionHitching(this);
		ReadService.recievers.add(odh);
		WriteService.recievers.add(odh);
	}

	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
		Warehouse.activity = WarehouseEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.SCRIPT_DOC = true;
		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.SYNC_INFO = true;
	}
}
