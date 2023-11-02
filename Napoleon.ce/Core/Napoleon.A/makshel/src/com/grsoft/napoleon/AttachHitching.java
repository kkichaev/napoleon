package com.grsoft.napoleon;

import java.io.File;
import java.io.FileOutputStream;
import android.os.Environment;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.dataobjects.ActionFiles;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


public class AttachHitching extends HitchOnSelect {
	private static final String OBJ_NAME = "ActionFiles";
	private String path = "";
	private String file = "";
	
	public AttachHitching(String id) {
		super(ActionFiles.class, OBJ_NAME);
		file = id.substring(id.lastIndexOf("\\") + 1).trim();
		path = WarehouseEx.getAttachPath();
		setCondition("[file]='"+id.trim()+"'");
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File dir = new File(path);
		
			if (!dir.exists()){
				if (dir.mkdirs()){
					File noMedia = new File(dir, ".nomedia");
					
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
	public void onStart() {
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ActionFiles dobj = (ActionFiles)rawObject.createDataObject(dataObject);

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) &&
				dobj.data != null && dobj.data.length > 0){
			File f = new File(path.trim(), file.trim());
		   
			FileOutputStream fos = null;
			
			try{
				fos = new FileOutputStream(f);
				fos.write(dobj.data);
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
			}
		}
		
		
	}
}
