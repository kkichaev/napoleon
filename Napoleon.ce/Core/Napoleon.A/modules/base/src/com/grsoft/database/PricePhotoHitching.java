/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   15/07/2011   creating
 */
package com.grsoft.database;

import java.io.File;
import java.io.FileOutputStream;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PricePhoto;
import com.grsoft.dataobjects.PricePhotoItem;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

/***
 * Обработчик приема фотографий для прайса
 * @author kki
 *
 */
public class PricePhotoHitching extends RcvNewHitching {
	@SuppressWarnings("unused")
	private static final String TAG = "PricePhotoHitching";  
	public static final String PHOTO_DIRECTORY = Environment.getExternalStorageDirectory() +
		"/Napoleon/files/price_photo/";
	/***
	 * Номер для имени фотографии
	 */
	private int index = 1;
	private boolean enableExternalStore = false;
	SQLiteStatement getFolderStmt;
	
	public PricePhotoHitching() {
		super(PricePhoto.class, "PricePhoto");

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			enableExternalStore = true;
			File photoDir = new File(PHOTO_DIRECTORY);
		
			if (!photoDir.exists()){
				enableExternalStore = photoDir.mkdirs();
			
				if (enableExternalStore){
					File noMedia = new File(photoDir, ".nomedia");
					try{
						noMedia.createNewFile();

					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else {
				clearPhotoDir();
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(Present.class));
			}
		}
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
	
	private void clearPhotoDir() {
		File dir = new File(PHOTO_DIRECTORY);
		
		for(File file :dir.listFiles())
			if (!file.isHidden()) 
				file.delete();
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if (enableExternalStore){
			PricePhoto pp = (PricePhoto)rawObject.createDataObject(PricePhoto.class);
			
			if (pp.photo != null && pp.photo.length > 0 && pp.items.size() > 0){
				String fileName = String.format("%d.jpg", index);
				File file = new File(PHOTO_DIRECTORY, fileName);
			   
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
							index++;
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
