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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.grsoft.database.Hitching;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.ReadServiceEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.WriteServiceEx;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
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


	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		RWServiceFactory.instance=new RWServiceFactoryNapoleon(){
			@Override
			public ReadServiceBase createReadService(List<Hitching> hitchings) {

				return new ReadServiceEx(hitchings);
			}
			@Override
			public WriteServiceBase createWriteService(
					List<? extends ObjectListener> objectsToSend,
					boolean rcvRemnants) {

				return new WriteServiceEx(objectsToSend, rcvRemnants);
			}
		};
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.SHOW_EXIT_WARNING = false;
	}

}
