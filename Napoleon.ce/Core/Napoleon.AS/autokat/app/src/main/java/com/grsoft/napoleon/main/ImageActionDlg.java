package com.grsoft.napoleon.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.napoleon.R;


public class ImageActionDlg extends DialogFragment {
	public static final String PIC_ID = "pic_id";
	private String pic_id;

	public interface ImageActionListener {
		void delete(String id);
	}

	private ImageActionListener imageActionListener;

	public void setImageActionListener(ImageActionListener imageActionListener) {
		this.imageActionListener = imageActionListener;
	}

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
		if (imageActionListener != null)
			imageActionListener.delete(pic_id);
	}

	protected void preview() {
		PicStoreImpl picStore = new PicStoreImpl();
		
		if(picStore.read("id", pic_id))
			picStore.getData().preview(getActivity());
	}
}
