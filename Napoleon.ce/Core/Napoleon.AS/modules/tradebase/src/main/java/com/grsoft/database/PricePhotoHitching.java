/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   15/07/2011   creating
 */
package com.grsoft.database;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PricePhoto;
import com.grsoft.dataobjects.PricePhotoItem;
import com.grsoft.napoleon.Features;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

/***
 * Обработчик приема фотографий для прайса
 * @author kki
 *
 */
public class PricePhotoHitching extends RcvNewHitching {
	Context context;
	@SuppressWarnings("unused")
	private static final String TAG = "PricePhotoHitching";  
//	public static final String PHOTO_DIRECTORY = Environment.getExternalStorageDirectory() +
//		"/Napoleon/files/price_photo/";
	/***
	 * Номер для имени фотографии
	 */
	private boolean enableExternalStore = false;
	SQLiteStatement getFolderStmt;
	boolean appendData;

	public static File getPhotoDir(Context context){
		return  new File(context.getExternalFilesDir(null), "prezentation");
	};

	public PricePhotoHitching(Context context) {
		this(context, false);
	}

	public PricePhotoHitching(Context context, String objName) {
		this(context, false);
		this.objectName = objName;
	}

	public PricePhotoHitching(Context context, boolean appendData) {
		super(PricePhoto.class, "PricePhoto");

		this.context = context;
		this.appendData = appendData;

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			enableExternalStore = true;
			File photoDir = getPhotoDir(context);
		
			if (!photoDir.exists()){
				enableExternalStore = photoDir.mkdirs();
			
				if (enableExternalStore){
					if(!Features.SHARED_PICTURES){
						File noMedia = new File(photoDir, ".nomedia");
						try{
							noMedia.createNewFile();

						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}else {
				if(!appendData) {
					clearPhotoDir(context);
					DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(Present.class));
				}
			}
		}
	}

	@Override
	public void prepareReading() {
		if(!appendData)
			super.prepareReading();
	}

	@Override
	public void onStart() {
		SQLiteDatabase database = DataBaseManager.getDataBase();
		getFolderStmt = database.compileStatement("SELECT folderid from price WHERE id=?");

		super.onStart();
	}

	@Override
	public void onEnd() {
		if(getFolderStmt != null)
			getFolderStmt.close();
		super.onEnd();
	}
	
	private void clearPhotoDir(Context context) {
		File dir = getPhotoDir(context);
		
		for(File file :dir.listFiles())
			if (!file.isHidden()) 
				file.delete();
	}

	protected PricePhoto read(RawObject rawObject) throws RuntimeException {
		return (PricePhoto)rawObject.createDataObject(PricePhoto.class);
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if (enableExternalStore){
			PricePhoto pp = read(rawObject);
			
			if (pp.photo != null && pp.photo.length > 0 && pp.items.size() > 0){
				String fileName = String.format("%s.jpg", UUID.randomUUID().toString().replace("-", ""));
				File file = new File(getPhotoDir(context), fileName);
			   
				FileOutputStream fos = null;
				
				Present present = new Present();
				present.photoPath = file.getAbsolutePath().toString();
				
				for (PricePhotoItem item : pp.items){
					try{
						getFolderStmt.bindString(1, item.id);
						present.folderId = getFolderStmt.simpleQueryForString();
						present.id = item.id;
				
						if(fos == null) {
							fos = new FileOutputStream(file);
							fos.write(pp.photo);
						}
						dbProxy.insertRecord(present);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				pp.photo = null;
				if (fos != null) {
					try {
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
