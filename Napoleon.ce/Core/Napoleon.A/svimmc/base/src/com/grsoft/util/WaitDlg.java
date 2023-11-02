package com.grsoft.util;

import com.grsoft.napoleon.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class WaitDlg {
	public static Dialog createDialog(Context context){
		ProgressDialog result = new ProgressDialog(context);
		result.setTitle(context.getString(R.string.wait));
		result.setMessage(context.getString(R.string.print_creating));
		
		return result;
	}
}
