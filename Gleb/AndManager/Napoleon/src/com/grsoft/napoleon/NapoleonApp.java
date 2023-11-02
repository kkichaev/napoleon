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

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.grsoft.database.PricePhotoHitching;
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
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.WarehouseActivity = WarehouseNew.class;
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

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void initDemo() {
		final String GLOBAL_PREF_NAME = "main_pref";
		final String INITED_KEY = "demo_inited";
		boolean inited = false;
		
		Features.IS_MARKET_VERSION = true;
//		Features.CAN_CHANGE_COST = true;

		SharedPreferences preferences = getSharedPreferences(
				GLOBAL_PREF_NAME, Context.MODE_PRIVATE);
		inited = preferences.getBoolean(INITED_KEY, false);
		
		if(!inited){
			createDemoDb();
			unzipPhotos();
			
			preferences.edit().putBoolean(INITED_KEY, true).commit();
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
	
	private void unzipPhotos(){
		
		if (!Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED))
			return;
		
		try{
			InputStream is = getResources().openRawResource(R.raw.photo);
			ZipInputStream zis = new ZipInputStream(is);
			
			BufferedInputStream bis = new BufferedInputStream(zis);
			File outputDir = new File(PricePhotoHitching.PHOTO_DIRECTORY);
			
			if(!outputDir.exists()){
				outputDir.mkdirs();
				
				ZipEntry entry = null;
				while((entry = zis.getNextEntry()) != null){
					
					BufferedOutputStream bos = new BufferedOutputStream(
							new FileOutputStream(new File(outputDir, entry.getName())));
					
					final int BUFF_SZ = 1024 * 8; 
					byte buffer[] = new byte[BUFF_SZ];
					int len;
					
					while((len = bis.read(buffer)) > 0)
						bos.write(buffer, 0 , len);
					
					bos.close();
				}
					
				bis.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
