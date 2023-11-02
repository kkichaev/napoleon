package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class DocumentsEx extends Documents {
	@Override
	protected Dialog createWarningStopListDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(((OrgEx) org.getData()).stopMsg);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { doCreate(); }
		});

		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
}
