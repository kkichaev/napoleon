package com.grsoft.napoleon.util;

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.grsoft.napoleon.CameraPreview;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.SrcDataCounter;

public class PhotoClickHandler extends OnClickListenerToNotify {
	
	public static final int CAMERA_ACTIVITY = 0x1812;
	

	public interface EventHandler {
		void prepareBoforeClick();
		void makePhotoFile(File newFile);
	}

	protected PhotoDocument doc;
	EventHandler handler;
	DocType docType;
	
	public PhotoClickHandler(PhotoDocument doc, EventHandler handler, DocType docType) {
		this.doc = doc;
		this.handler = handler;
		this.docType = docType;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		Activity context = (Activity) v.getContext();
		long lim = ((CfgNplW)ConfigManager.getConfig()).max_packet_len;
		int dc = doc.count();
		long sz = doc.size();
		
		long space = dc == 0 ? 0 : sz / dc;
		
		if((sz + space) > lim){
			Toast.makeText(context, R.string.over_limit_photo, Toast.LENGTH_LONG).show();
		}else{
			handler.prepareBoforeClick();
			com.grsoft.napoleon.util.CfgNplW cfg = 
					(com.grsoft.napoleon.util.CfgNplW) ConfigManager.getConfig();
			
			if (cfg.androidPhoto && !Features.ANDROID_CAMERA_PROHIBIT)
				openPhotoActivity(context);
			else {
				CameraPreview.open(context, doc, docType);
			}
		}
	}

	private void openPhotoActivity(Activity context) {
		try{
			File path = new File(Path.getDataDir());
			boolean ret = path.mkdir();
			File file = new File(path, String.format("%d.jpg",SrcDataCounter.getValue()));
			handler.makePhotoFile(file);

			Uri uri = null;

			if (Build.VERSION.SDK_INT >= 24) {
				uri = FileProvider.getUriForFile(context, context.getString(R.string.fileprovider_authorities), file);
			}else
				uri = Uri.fromFile(file);


			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			((Activity)context).startActivityForResult(intent, CAMERA_ACTIVITY);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
