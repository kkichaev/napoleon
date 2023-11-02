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
import com.grsoft.dataobjects.impl.OrderImpl;
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
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		initDemo();
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
	
	private void initDemo() {
		final String GLOBAL_PREF_NAME = "main_pref";
		final String INITED_KEY = "demo_inited";
		boolean inited = false;
		
//		Features.IS_MARKET_VERSION = true;
		Features.CREATED_ORG_SHOW_ALL_DOCS = true;
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
		DataBaseManager.getDataBase().close();

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

		DataBaseManager.init();
	}
	
	private void unzipPhotos(){
		
		if (!Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED))
			return;
		
		try{
			InputStream is = getResources().openRawResource(R.raw.photo);
			ZipInputStream zis = new ZipInputStream(is);
			
			BufferedInputStream bis = new BufferedInputStream(zis);
			File outputDir = PricePhotoHitching.getPhotoDir(this);

			File p = getExternalFilesDir(null);

			Log.d("getExternalFilesDir", p.getAbsolutePath());
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
