package com.grsoft.database;

import java.io.File;
import java.io.FileOutputStream;

import android.os.Environment;

import com.grsoft.dataobjects.Report;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class GetReportsHitching extends Hitching {
	public static final String REPORT_EXTENTION = ".html";

	public static final String REPORT_DIRECTORY = Environment.getExternalStorageDirectory() + "/Napoleon/reports/";
	
	boolean saveToCard;
	
	public GetReportsHitching() {
		super(Report.class, "Report");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		saveToCard = ((CfgNpl)ConfigManager.getConfig()).saveReportsToCard && 
				Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		
		if( saveToCard) {
			File photoDir = new File(REPORT_DIRECTORY);
			
			if (!photoDir.exists())
				saveToCard = photoDir.mkdirs();
			
			if (saveToCard){
				File noMedia = new File(photoDir, ".test");
				try{
					if( !noMedia.isFile() )
						saveToCard = noMedia.createNewFile();
				}catch(Exception e){
					e.printStackTrace();
					saveToCard = false;
				}
			}
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Report dobj = (Report) rawObject.createDataObject(dataObject);
		if( saveToCard && dobj.report.length > 0 ) {
			FileOutputStream fos = null;
			try{
				String fileName = dobj.id + REPORT_EXTENTION;
				File file = new File(REPORT_DIRECTORY, fileName);
				
				fos = new FileOutputStream(file);
				fos.write(dobj.report);
				dobj.report = new byte[0];
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
		
		dbProxy.insertRecord(dobj);
	}
}
