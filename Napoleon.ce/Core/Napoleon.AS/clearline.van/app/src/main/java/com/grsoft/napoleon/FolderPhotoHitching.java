package com.grsoft.napoleon;

import java.io.File;
import java.io.FileOutputStream;
import com.grsoft.database.DbWriter;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FolderPhoto;
import com.grsoft.dataobjects.FolderPresent;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import android.os.Environment;

public class FolderPhotoHitching extends RcvNewHitching{
	private int index = 1;
	private boolean enableExternalStore = false;
	public static final String PHOTO_DIRECTORY = Environment.getExternalStorageDirectory() + "/Napoleon/files/folders_photo/";
	
	public FolderPhotoHitching() {
		super(FolderPhoto.class, "FolderPhoto");
	
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
				DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(FolderPresent.class));
			}
		}
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
			FolderPhoto fp = (FolderPhoto)rawObject.createDataObject(FolderPhoto.class);
			
			if (fp.pic != null){
				String fileName = String.format("%d.jpg", index++);
				File file = new File(PHOTO_DIRECTORY, fileName);
			   
				FileOutputStream fos = null;
				
				try{
					fos = new FileOutputStream(file);
					fos.write(fp.pic);
					
					FolderPresent p = new FolderPresent();
					p.id = fp.id;
					p.path = file.getAbsolutePath().toString();
					p.color = fp.color;
					p.tsz = fp.tsz;
					
					dbProxy.insertRecord(p);
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
}
