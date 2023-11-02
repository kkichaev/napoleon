package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class ReorderDlg extends DialogFragment {
	public static final String REORDER_ACTION = "com.grsoft.napoleon.ReorderDlg.REORDER_ACTION";
	public static final String SIZE = "size";
	public static final String CURRENT = "current";
	protected static final String OLD = "old";
	private int current;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.select_pos);
		Bundle args = getArguments();
		
		if(args != null){
			int sz = args.getInt(SIZE);
			current = args.getInt(CURRENT);
			
			String[] items = new String[sz];
			for(int i = 0; i < sz; i++)
				items[i] = Integer.toString(i+1);
			
			builder.setSingleChoiceItems(items, current, onItemClick());
		}
		
		return builder.create();
	}

	private OnClickListener onItemClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(REORDER_ACTION);
				intent.putExtra(CURRENT, which);
				intent.putExtra(OLD, current);
				getContext().sendBroadcast(intent);
				dismiss();
			}
		};
	}
}
