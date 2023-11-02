package com.grsoft.napoleon.dostavka;

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

import androidx.core.content.FileProvider;

import com.grsoft.dataobjects.impl.VisitImpl;

import java.io.File;


public class ManagePhotoDlg extends DialogFragment {
	public static final String PIC_ID = "pic_id";
	public static final String VIS_ID = "vis_id";
	private int pic_id;
	private long created;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Bundle b = getArguments();
		
		if(b != null){
			pic_id = b.getInt(PIC_ID);
			created = b.getLong(VIS_ID);
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
		Intent i = new Intent(AutoWaybillEdit.DEL_PHOTO_ACTION);
		i.putExtra(PIC_ID, pic_id);
		getActivity().sendBroadcast(i); 
	}

	protected void preview() {
		VisitImpl vis = new VisitImpl();

		if(vis.read(created)) {
			if (pic_id <= vis.getData().items.size()) {
				String path = new String(vis.getData().items.get(pic_id).id);
				Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);

				Uri uri = null;

				if (Build.VERSION.SDK_INT >= 24) {
					Context c = getActivity();
					uri = FileProvider.getUriForFile(c, getContext().getString(R.string.fileprovider_authorities), new File(path));
				} else
					uri = Uri.parse("file://" + path);

				i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				i.setDataAndType(uri, "image/*");

				startActivity(i);
			}
		}
	}
}
