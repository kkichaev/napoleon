package com.grsoft.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.PricePhotoEx;
import com.grsoft.dataobjects.PricePhotoItem;
import com.grsoft.napoleon.BuildConfig;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;


public class UpdatePrezentHitching extends Hitching {

	public interface Event {
		void picture_received();
	}

	public static Event handler;

	Map<Object, FolderData> folders = new HashMap<>();
	boolean enableExternalStore = true;
	String presentPath = "";
	
	public UpdatePrezentHitching(Context context) {
		super(PricePhotoEx.class, "PricePhoto");

		presentPath = context.getExternalFilesDir(null) + "/price_photos/";

		try {
			File photoDir = new File(presentPath);
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@TableInfo(name="Price")
	public static class FolderData extends DataObject {
		public int folderID;
		public String id;
	}

	@Override
	public void onStart() {
		DbWriter.checkDBTable(dataObject);
		DbWriter.checkDBTable(PresentEx.class);

		folders = DbReader.fetchDic(FolderData.class, "id");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if( !enableExternalStore )
			return;
		
		PricePhotoEx upd = (PricePhotoEx) rawObject.createDataObject(dataObject);
		
		if(upd.photo == null)
			return;

		try {
			if(handler != null) {
				handler.picture_received();
			}

			String name = upd.name.substring(upd.name.replace('\\', '/').lastIndexOf('/') + 1);
			File f = new File(presentPath, name);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
			bos.write(upd.photo);
			bos.flush();
			bos.close();
			
			DbWriter wr = new DbWriter();
			PresentEx p = (PresentEx) new PresentEx();

			for(PricePhotoItem i : upd.items){
				FolderData fd = folders.get(i.id);
				if(fd == null)
					continue;

				p.id = i.id;
				p.crc = upd.crc;
				p.photoPath = f.getAbsolutePath();
				p.name = upd.name;
				p.folderId = Integer.toString(fd.folderID);
				
				wr.insertRecord(p);
			}
			
			wr.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
