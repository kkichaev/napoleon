package com.keeper.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import com.keeper.R;

public class DeleteDialog {
	public static  Dialog create(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.alert));
		builder.setMessage(context.getResources().getString(R.string.ask_to_delete_record));
		builder.setNegativeButton(context.getResources().getString(R.string.cancel), null);
		builder.setPositiveButton(context.getResources().getString(R.string.ok), null);
		return builder.create();
	}
}
