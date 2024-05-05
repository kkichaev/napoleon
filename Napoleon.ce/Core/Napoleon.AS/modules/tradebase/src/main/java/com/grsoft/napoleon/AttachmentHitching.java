package com.grsoft.napoleon;

import java.io.File;
import java.io.FileOutputStream;

import com.grsoft.database.HitchOnSelect;
import com.grsoft.dataobjects.Attachment;
import com.grsoft.network.BytesMember;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.content.Context;
import android.os.Environment;

public class AttachmentHitching extends HitchOnSelect {
	private boolean enableExternalStore = false;
	String ATTACH_DIRECTORY = null;

	public AttachmentHitching(String id, Context context) {
		super(Attachment.class, "Attachment");
		setCondition(String.format("id='%s'", id));
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			enableExternalStore = true;
			ATTACH_DIRECTORY = context.getExternalFilesDir(null) + "/attachments/";

			File photoDir = new File(ATTACH_DIRECTORY);
		
			if (!photoDir.exists()){
				enableExternalStore = photoDir.mkdirs();
			
				if (enableExternalStore){
					if(Features.SHARED_PICTURES == false ){
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

	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if (enableExternalStore){
			Attachment a = (Attachment) rawObject.createDataObject(Attachment.class);
			BytesMember bytes = (BytesMember) rawObject.getMember("data");
			
			String fileName = String.format("%s.%s", a.id, a.name.substring(a.name.lastIndexOf("."), a.name.length()).toLowerCase());
			File file = new File(ATTACH_DIRECTORY, fileName);
		    a.path = file.getAbsolutePath();
		    
			FileOutputStream fos = null;
			
			try{
				fos = new FileOutputStream(file);
				fos.write(bytes.toBytes());
				dbProxy.insertRecord(a);
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
