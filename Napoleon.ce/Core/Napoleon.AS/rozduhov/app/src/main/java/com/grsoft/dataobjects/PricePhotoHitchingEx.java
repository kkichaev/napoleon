package com.grsoft.dataobjects;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

public class PricePhotoHitchingEx extends Hitching {
	@SuppressWarnings("unused")
	private static final String TAG = "PricePhotoHitching";  
	public static final String PHOTO_DIRECTORY = Environment.getExternalStorageDirectory() +
		"/Napoleon/files/price_photo/";
	
	Map<String, PresentEx> readed = new HashMap<String, PresentEx>();

	private boolean enableExternalStore = false;
	DbWriter writer = new DbWriter();
	SQLiteStatement stmt;
	
	public PricePhotoHitchingEx() {
		super(PricePhotoEx.class, "PricePhoto");
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			enableExternalStore = true;

			try {
				SQLiteDatabase database = DataBaseManager.getDataBase();
				stmt = database .compileStatement("SELECT folderid from price WHERE id=?");
			} catch(Exception e) {
				e.printStackTrace();
			}
			
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
			}
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		writer.close();
		if(stmt != null)
			stmt.close();
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if(enableExternalStore) {
			PricePhotoEx pp = (PricePhotoEx) rawObject.createDataObject(PricePhotoEx.class);
			if(pp.photo != null && pp.photo.length > 0) {
				FileOutputStream fos = null;
				File file;

				PresentEx pe = readed.get(pp.id);
				if(pe != null) {
					String fileName = String.format("%s_%d.jpg", pp.id, pe.photas.size() + 1);
					file = new File(PHOTO_DIRECTORY, fileName);
					
					PricePhotoName ppn = new PricePhotoName();
					ppn.name = file.getAbsolutePath().toString();
					pe.photas.add(ppn);
				} else {
					String fileName = String.format("%s.jpg", pp.id);
					file = new File(PHOTO_DIRECTORY, fileName);
				   
					pe = new PresentEx();
					readed.put(pp.id, pe);
					pe.id = pp.id;
					pe.photoPath = file.getAbsolutePath().toString(); 
					try {
						stmt.bindString(1, pe.id);
						pe.folderId = stmt.simpleQueryForString();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				try{
					fos = new FileOutputStream(file);
					fos.write(pp.photo);
					
					writer.insertRecord(pe);
				}catch(Exception e){
					e.printStackTrace();
				}
				finally{
					if (fos != null)
						try {
							fos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					fos = null;
				}
			}
		}
	}
}
