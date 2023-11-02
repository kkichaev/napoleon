package com.grsoft.ads;

import java.io.File;
import java.util.List;

import com.grsoft.ads.dataobjects.PicStore;
import com.grsoft.ads.dataobjects.TaskAttachmentHitching;
import com.grsoft.ads.dataobjects.impl.PicStoreImpl;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;

import android.app.Activity;
import android.os.AsyncTask;

public class ClearProcess extends AsyncTask<ClearProcess.Params, Void, Boolean> {
	public static class Params{
		boolean pictures = false;
		boolean attachments = false;
	}
	
	private Activity activity; 
	
	public ClearProcess(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	protected Boolean doInBackground(ClearProcess.Params... params) {
		Params arg = params[0];
		
		if(arg.attachments)
			deleteAttachments();
		
		if(arg.pictures)
			deletePictures();
		
		return true;
	}

	protected void deletePictures() {
		List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(PicStore.class), 
				String.format("(params & %1$d) == %1$d",ParamState.ofExported), null);
		
		PicStoreImpl pc = new PicStoreImpl();
		
		for(long rowid : ids)
			if(pc.read(rowid))
				pc.delete();
		
		pc.close();
	}

	protected void deleteAttachments() {
		File dir = new File(TaskAttachmentHitching.ATTACH_DIRECTORY); 
		
		if (dir.isDirectory()){
		    String[] children = dir.list();

		    for (int i = 0; i < children.length; i++)
		       new File(dir, children[i]).delete();
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		try {
			activity.showDialog(R.id.wait_dlg);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		try {
			activity.dismissDialog(R.id.wait_dlg);
			activity.finish();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
