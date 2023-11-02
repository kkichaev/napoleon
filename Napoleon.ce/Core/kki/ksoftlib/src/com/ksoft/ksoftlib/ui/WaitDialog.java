package com.ksoft.ksoftlib.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ksoft.lib.R;


public class WaitDialog extends DialogFragment {

	protected AsyncTask<?, ?, ?> thread;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog result = new ProgressDialog(getActivity());
		result.setTitle(R.string.wait);
		result.setMessage(getString(R.string.please_wait));
		return result;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if(thread != null)
			thread.cancel(false);
		super.onCancel(dialog);
	}
}
