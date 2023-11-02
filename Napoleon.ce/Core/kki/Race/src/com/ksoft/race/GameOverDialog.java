package com.ksoft.race;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class GameOverDialog extends DialogFragment {
	public static final int OK = 0;
	public static final int CANCEL = 1;
	
	public interface ResultListener {
		void onResultSelect(int code);
	}
	
	private ResultListener resultListener;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder result = new AlertDialog.Builder(getActivity());
		result.setTitle("Information");
		result.setMessage("Game Over!");
		result.setPositiveButton("Дальше гонять", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				fireResult(OK);
			}
		});
		result.setNegativeButton("Уйти", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				fireResult(CANCEL);
			}
		});
		return result.create();
	}

	protected void fireResult(int code) {
		if(resultListener != null)
			resultListener.onResultSelect(code);
	}

	public void setResultListener(ResultListener listener) {
		resultListener = listener;
	}
	
	
}
