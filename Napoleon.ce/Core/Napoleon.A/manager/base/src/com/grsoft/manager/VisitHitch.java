package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.database.HitchOnSelect;
import com.grsoft.dataobjects.VisitPreview;

public class VisitHitch extends HitchOnSelect{
//	private static final String SHARED_FOLDER = "vispic/%s%d/";
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	public VisitHitch(Class<VisitPreview> class1, String userid, Date created){
		super(VisitPreview.class, "VisitPreview");
		setCondition(String.format(" \"userid\" = '%s' and \"created\" = ToDate('%s')", userid, sdf.format(created)));
	}
	
//	@Override
//	public void onRead(RawObject rawObject) throws RuntimeException {
//		VisitPreview visit = (VisitPreview) rawObject.createDataObject(dataObject);
//		
//		if (Environment.getExternalStorageState()
//				.equals(Environment.MEDIA_MOUNTED)){
//			File folder = createFolder(visit);
//			savePics(visit, folder);
//		}
//	}
//
//	public static File createFolder(CreateDocDataObject cddo) {
//		File folder = new File(Environment.getExternalStorageDirectory(), String.format(SHARED_FOLDER, cddo.userid, cddo.created.getTime()));
//		
//		if (!folder.exists())
//			folder.mkdirs();
//		return folder;
//	}
//
//	private void savePics(VisitPreview visit, File folder) {
//		int index = 1;
//		
//		for(VisitPreviewItem i : visit.items){
//			savePic(folder, index, i);
//			index++;
//		}
//	}
//
//	private void savePic(File folder, int index, VisitPreviewItem i) {
//		String fileName = String.format("%d.jpg", index);
//		File file = new File(folder, fileName);
//   
//		FileOutputStream fos = null;
//		
//		try{
//			fos = new FileOutputStream(file);
//			fos.write(i.smallPhoto);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		finally{
//			if (fos != null)
//				try {
//					fos.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		}
//	}
}