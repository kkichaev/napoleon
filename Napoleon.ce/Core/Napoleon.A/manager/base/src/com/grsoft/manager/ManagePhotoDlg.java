package com.grsoft.manager;

import java.io.File;

import com.grsoft.dataobjects.impl.PicStoreImpl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;


public class ManagePhotoDlg extends DialogFragment {
	public static final String PIC_ID = "pic_id";
	public static final String DEL_PHOTO_ACTION = "ManagePhotoDlg.del_photo_action";
	private String pic_id;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Bundle b = getArguments();
		
		if(b != null){
			pic_id = b.getString(PIC_ID);
			builder.setTitle(R.string.select_action);
			builder.setItems(new CharSequence[]{getString(R.string.open), getString(R.string.delete)}, itemClick());
		}
		
		return builder.create();
	}

	private DialogInterface.OnClickListener itemClick() {
		return new OnClickListener() {
			@Override 
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case 0:
					preview();
					break;
				case 1:
					delete();
					break;
				}
			}
		};
	}

	protected void delete() { 
		Intent i = new Intent(DEL_PHOTO_ACTION);
		i.putExtra(PIC_ID, pic_id);
		getActivity().sendBroadcast(i); 
	}

	protected void preview() {
		PicStoreImpl picStore = new PicStoreImpl();
		
		if(picStore.read("id", pic_id)) {
			String path = new String(picStore.getData().picture);
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			
			Uri uri = null;
			
			if (Build.VERSION.SDK_INT >= 24) {
				Context c = getActivity();
				uri = FileProvider.getUriForFile(c,"com.grsoft.napoleon.fileprovider", new File(path)); 
			}else
				uri = Uri.parse("file://" + path);
			
			i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			i.setDataAndType(uri, "image/*");
			
			startActivity(i);
		}
	}
}
