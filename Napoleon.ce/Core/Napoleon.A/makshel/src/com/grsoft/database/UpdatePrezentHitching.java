package com.grsoft.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.PricePhotoEx;
import com.grsoft.dataobjects.PricePhotoItem;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


public class UpdatePrezentHitching extends Hitching {
	SQLiteStatement statement;
	boolean enableExternalStore = true;
	String presentPath = "";
	
	public UpdatePrezentHitching() {
		super(PricePhotoEx.class, "PricePhoto");

		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		presentPath = cfg.presentpath;
		
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
	
	@Override
	public void onStart() {
		DbWriter.checkDBTable(dataObject);

		SQLiteDatabase database = DataBaseManager.getDataBase();
		statement = database.compileStatement("SELECT folderid from price WHERE id=?");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if( !enableExternalStore )
			return;
		
		PricePhotoEx upd = (PricePhotoEx) rawObject.createDataObject(dataObject);
		
		try{
			String name = upd.name.substring(upd.name.replace('\\', '/').lastIndexOf('/') + 1);
			File f = new File(presentPath, name);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
			bos.write(upd.photo);
			bos.flush();
			bos.close();
			
			PresentImpl impl = new PresentImpl();
			PresentEx p = (PresentEx) impl.getData();

			for(PricePhotoItem i : upd.items){
				p.id = i.id;
				p.crc = upd.crc;
				p.photoPath = f.getAbsolutePath();
				
				statement.bindString(1, i.id);
				
				try{
					p.folderId = statement.simpleQueryForString();
				}catch(Exception e){ e.printStackTrace(); }
				
				impl.write();
			}
			impl.close();
		}catch(Exception e) { e.printStackTrace(); }
		
	}
	
	@Override
	public void onEnd() {
		try{
			statement.close();
		}catch(Exception e) {e .printStackTrace();}
	}

}
