package com.grsoft.napoleon;

import java.io.File;

import com.grsoft.dataobjects.DMP;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DMPImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.util.ExtrasConst;

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


public class ManagePhotoDlgEx extends DialogFragment {
	public static final String PIC_ID = "pic_id";
	private String pic_id;
	private long docRowId = -1;
	private DMPImpl doc = new DMPImpl();
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Bundle b = getArguments();
		
		if(b != null){
			pic_id = b.getString(PIC_ID);
			docRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
			doc.read(docRowId);
			CharSequence[] items;
			
			if (doc.isEditable())
				items  =new CharSequence[]{getString(R.string.open), getString(R.string.delete)};
			else
				items = new CharSequence[]{getString(R.string.open)};
			
			
			builder.setTitle(R.string.select_action);
			builder.setItems(items, itemClick());
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
		DMPImpl d = new DMPImpl();
		d.read(docRowId);
		
		if(d.isEditable()) {
			VisitImpl v = new VisitImpl();
			v.read(docRowId);
			
			for(VisitItem i : v.getData().items) {
				VisitItemEx e = (VisitItemEx)i;
				
				if(e.key.equals(pic_id)) {
					v.getData().items.remove(i);
					v.write();
					v.close();
					
					File file = new File(new String(i.id));
					file.delete();
					
					Intent n = new Intent(DMPItemEdit.REFRESH_ACTION);
					getActivity().sendBroadcast(n);
					
					break;
				}
			}
		}
	}

	protected void preview() {
		String path = "";
		
		VisitImpl v = new VisitImpl();
		v.read(docRowId);
		
		for(VisitItem i : v.getData().items) {
			VisitItemEx e = (VisitItemEx)i;
			
			if(e.key.equals(pic_id)) {
				path = new String(i.id);
				break;
			}
		}
		
		
		if(path.length() > 0) {
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
